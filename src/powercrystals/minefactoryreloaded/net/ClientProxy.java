package powercrystals.minefactoryreloaded.net;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.fluids.FluidRegistry;
import powercrystals.minefactoryreloaded.MineFactoryReloadedClient;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;

public class ClientProxy implements IMFRProxy
{
	@Override
	public void init()
	{
		MineFactoryReloadedClient.init();
	}
	
	@Override
	public void movePlayerToCoordinates(EntityPlayer e, double x, double y, double z)
	{
		e.setPosition(x, y, z);
	}
	
	@Override
	@ForgeSubscribe
	public void onPostTextureStitch(TextureStitchEvent.Post e)
	{
		FluidRegistry.getFluid("milk").setIcons(MineFactoryReloadedCore.milkLiquid.getBlockTextureFromSide(1));
		FluidRegistry.getFluid("sludge").setIcons(MineFactoryReloadedCore.sludgeLiquid.getBlockTextureFromSide(1));
		FluidRegistry.getFluid("sewage").setIcons(MineFactoryReloadedCore.sewageLiquid.getBlockTextureFromSide(1));
		FluidRegistry.getFluid("mobessence").setIcons(MineFactoryReloadedCore.essenceLiquid.getBlockTextureFromSide(1));
		FluidRegistry.getFluid("biofuel").setIcons(MineFactoryReloadedCore.biofuelLiquid.getBlockTextureFromSide(1));
		FluidRegistry.getFluid("meat").setIcons(MineFactoryReloadedCore.meatLiquid.getBlockTextureFromSide(1));
		FluidRegistry.getFluid("pinkslime").setIcons(MineFactoryReloadedCore.pinkSlimeLiquid.getBlockTextureFromSide(1));
		FluidRegistry.getFluid("chocolatemilk").setIcons(MineFactoryReloadedCore.chocolateMilkLiquid.getBlockTextureFromSide(1));
		FluidRegistry.getFluid("mushroomsoup").setIcons(MineFactoryReloadedCore.mushroomSoupLiquid.getBlockTextureFromSide(1));
	}
}
