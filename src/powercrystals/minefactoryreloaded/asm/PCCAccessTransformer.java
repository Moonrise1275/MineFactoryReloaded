package powercrystals.minefactoryreloaded.asm;

import java.io.IOException;

import cpw.mods.fml.common.asm.transformers.AccessTransformer;
import cpw.mods.fml.relauncher.FMLInjectionData;

public class PCCAccessTransformer extends AccessTransformer
{
	public PCCAccessTransformer() throws IOException
	{
		super("pcc_at_" + FMLInjectionData.data()[4]/*MCVersion*/ + ".cfg");
	}
}
