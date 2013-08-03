package powercrystals.minefactoryreloaded.render.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelSlime;
import net.minecraft.client.renderer.entity.RenderSlime;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.entity.EntityPinkSlime;

@SideOnly(Side.CLIENT)
public class RenderPinkSlime extends RenderSlime
{
	private static final ResourceLocation PINKSLIME = MineFactoryReloadedCore.getMobTexture("pinkslime.png");
	
	public RenderPinkSlime()
	{
		super(new ModelSlime(16), new ModelSlime(0), 0.25F);
	}
	
	protected ResourceLocation getTexture(EntityPinkSlime entity)
	{
		return PINKSLIME;
	}
	
	@Override
	protected ResourceLocation func_110775_a(Entity entity)
	{
		return getTexture((EntityPinkSlime)entity);
	}
}
