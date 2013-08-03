package powercrystals.minefactoryreloaded.render.entity;

import org.lwjgl.opengl.GL11;

import powercrystals.minefactoryreloaded.entity.EntityRocket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EntityRocketRenderer extends Render
{
	private static final ResourceLocation ROCKET = new ResourceLocation("minefactoryreloaded", "textures/itemmodels/Rocket.png");
	
	private IModelCustom _model;
	
	public EntityRocketRenderer()
	{
		try
		{
			_model = AdvancedModelLoader.loadModel("/assets/minefactoryreloaded/models/Rocket.obj");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
    public void renderRocket(EntityRocket rocket, double x, double y, double z, float yaw, float partialTicks)
    {
		TextureManager renderengine = Minecraft.getMinecraft().renderEngine;
		
		if(renderengine != null)
		{
			renderengine.func_110577_a(ROCKET);
		}
		
		GL11.glPushMatrix();
		
		GL11.glTranslatef((float)x, (float)y, (float)z);
		GL11.glRotatef(rocket.prevRotationYaw + (rocket.rotationYaw - rocket.prevRotationYaw) * partialTicks - 90.0F, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(90, 0, 0, 1);
		GL11.glRotatef(rocket.prevRotationPitch + (rocket.rotationPitch - rocket.prevRotationPitch) * partialTicks, 0.0F, 0.0F, 1.0F);
		GL11.glScalef(0.01F, 0.01F, 0.01F);
		
		_model.renderAll();
		
		GL11.glPopMatrix();
    }

    @Override
	public void doRender(Entity entity, double x, double y, double z, float yaw, float partialTicks)
    {
        this.renderRocket((EntityRocket)entity, x, y, z, yaw, partialTicks);
    }
	
	protected ResourceLocation getTexture(EntityRocket par1Entity)
	{
		return ROCKET;
	}
	
	protected ResourceLocation func_110775_a(Entity par1Entity)
	{
		return this.getTexture((EntityRocket)par1Entity);
	}
}
