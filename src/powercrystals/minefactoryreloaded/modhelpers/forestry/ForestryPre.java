package powercrystals.minefactoryreloaded.modhelpers.forestry;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidContainerData;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidDictionary;
import net.minecraftforge.fluids.FluidStack;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;

@Mod(modid = "MineFactoryReloaded|CompatForestryPre", name = "MFR Compat: Forestry (2)", version = MineFactoryReloadedCore.version, dependencies = "before:Forestry")
@NetworkMod(clientSideRequired = false, serverSideRequired = false)
public class ForestryPre
{
	@EventHandler
	public static void init(FMLInitializationEvent e)
	{
		if(!Loader.isModLoaded("Forestry"))
		{
			return;
		}
		/*
                FluidContainerRegistry.registerFluidContainer(FluidRegistry.getFluid("milk"), new ItemStack(Item.bucketMilk), FluidContainerRegistry.EMPTY_BUCKET);
                FluidContainerRegistry.registerFluidContainer(FluidRegistry.getFluid("sludge"), new ItemStack(sludgeBucketItem), FluidContainerRegistry.EMPTY_BUCKET);
                FluidContainerRegistry.registerFluidContainer(FluidRegistry.getFluid("sewage"), new ItemStack(sewageBucketItem), FluidContainerRegistry.EMPTY_BUCKET);
                FluidContainerRegistry.registerFluidContainer(FluidRegistry.getFluid("mobessence"), new ItemStack(mobEssenceBucketItem), FluidContainerRegistry.EMPTY_BUCKET);
                FluidContainerRegistry.registerFluidContainer(FluidRegistry.getFluid("biofuel"), new ItemStack(bioFuelBucketItem), FluidContainerRegistry.EMPTY_BUCKET);
                FluidContainerRegistry.registerFluidContainer(FluidRegistry.getFluid("meat"), new ItemStack(meatBucketItem), FluidContainerRegistry.EMPTY_BUCKET);
                FluidContainerRegistry.registerFluidContainer(FluidRegistry.getFluid("pinkslime"), new ItemStack(pinkSlimeBucketItem), FluidContainerRegistry.EMPTY_BUCKET);
                FluidContainerRegistry.registerFluidContainer(FluidRegistry.getFluid("chocolatemilk"), new ItemStack(chocolateMilkBucketItem), FluidContainerRegistry.EMPTY_BUCKET);
                FluidContainerRegistry.registerFluidContainer(FluidRegistry.getFluid("mushroomsoup"), new ItemStack(mushroomSoupBucketItem), FluidContainerRegistry.EMPTY_BUCKET);
                FluidContainerRegistry.registerFluidContainer(FluidRegistry.getFluid("mushroomsoup"), new ItemStack(Item.bowlSoup), new ItemStack(Item.bowlEmpty));
		*/
	}
}
