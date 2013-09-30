package powercrystals.minefactoryreloaded.asm;

import cpw.mods.fml.relauncher.FMLLaunchHandler;
import net.minecraft.launchwrapper.IClassTransformer;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;

import powercrystals.minefactoryreloaded.asm.relauncher.Implementable;

public class PCCASMTransformer implements IClassTransformer
{
	private static String desc;
	
	public PCCASMTransformer()
	{
		desc = Type.getDescriptor(Implementable.class);
	}
	
	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes)
	{
		if (name.equals("powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryPowered") ||
			name.equals("powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryInventory"))
		{
			ClassReader cr = new ClassReader(bytes);
			ClassNode cn = new ClassNode();
			cr.accept(cn, 0);
			
			if (this.implement(cn))
			{
				System.out.println("Adding runtime interfaces to " + transformedName);
				ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
				cn.accept(cw);
				bytes = cw.toByteArray();
			}
		}
		
		return bytes;
	}
	
	private boolean implement(ClassNode cn)
	{
		if (cn.visibleAnnotations == null)
		{
			return false;
		}
		
		boolean interfaces = false;
		for (AnnotationNode node : (List<AnnotationNode>)cn.visibleAnnotations)
		{
			if (!node.desc.equals(desc) || node.values == null)
				continue;
			
			List<Object> values = node.values;
			for (int i = 0, e = values.size(); i < e; )
			{
				Object k = values.get(i++);
				Object v = values.get(i++);
				if (k instanceof String && k.equals("value") && v instanceof String)
				{
					String[] value = ((String)v).split(";");
					for (int j = 0, l = value.length; j < l; ++j)
					{
						String clazz = value[j].trim();
						String cz = clazz.replace('.', '/');
						if (cn.interfaces.contains(cz))
							continue;
						
						Class<?> intf;
						try {
							intf = Class.forName(clazz);
						} catch (Throwable _) {
							intf = null;
						}
						if (intf == null)
							continue;
						
						cn.interfaces.add(cz);
						interfaces = true;
					}
				}
			}
		}
		return interfaces;
	}

}
