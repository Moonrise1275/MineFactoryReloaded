package powercrystals.minefactoryreloaded.modhelpers.forestry;

import java.lang.reflect.Field;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.modhelpers.forestry.fertilizer.FertilizerForestry;
import powercrystals.minefactoryreloaded.modhelpers.forestry.leaves.FertilizableForestryLeaves;
import powercrystals.minefactoryreloaded.modhelpers.forestry.leaves.FruitForestry;
import powercrystals.minefactoryreloaded.modhelpers.forestry.trees.FertilizableForestryTree;
import powercrystals.minefactoryreloaded.modhelpers.forestry.trees.HarvestableForestryTree;
import powercrystals.minefactoryreloaded.modhelpers.forestry.trees.PlantableForestryTree;
import powercrystals.minefactoryreloaded.modhelpers.forestry.utils.ForestryUtils;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import powercrystals.minefactoryreloaded.modhelpers.forestry.pods.FertilizableForestryPods;
import powercrystals.minefactoryreloaded.modhelpers.forestry.pods.FruitForestryPod;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityUnifier;

@Mod(modid = "MineFactoryReloaded|CompatForestry", name = "MFR Compat: Forestry", version = MineFactoryReloadedCore.version, dependencies = "after:MineFactoryReloaded;after:Forestry")
@NetworkMod(clientSideRequired = false, serverSideRequired = false)
public class Forestry
{
	@EventHandler
	public static void load(FMLInitializationEvent e)
	{
		if(!Loader.isModLoaded("Forestry"))
		{
			FMLLog.warning("Forestry missing - MFR Forestry Compat not loading");
			return;
		}
		try
		{
			Class<?> forestryItems = Class.forName("forestry.core.config.ForestryItem");
			if(forestryItems != null)
			{
				Item peat = (Item)forestryItems.getField("peat").get(null);
				MFRRegistry.registerSludgeDrop(5, new ItemStack(peat));
			}
			
			MFRRegistry.registerPlantable(new PlantableForestryTree());
			MFRRegistry.registerFertilizable(new FertilizableForestryTree());
			
			for(Field f : Class.forName("forestry.core.config.ForestryBlock").getDeclaredFields())
			{
				if(f.getName().contains("log"))
				{
					Block log = ((Block)f.get(null));
					if(log != null)
					{
						MFRRegistry.registerHarvestable(new HarvestableForestryTree(log.blockID));
						MFRRegistry.registerFruitLogBlockId(log.blockID);
					}
				}
				else if(f.getName().contains("leaves"))
				{
					Block leaves = ((Block)f.get(null));
					if(leaves != null)
					{
						MFRRegistry.registerFruit(new FruitForestry(leaves.blockID));
						MFRRegistry.registerFertilizable(new FertilizableForestryLeaves(leaves.blockID));
					}
				}
				else if(f.getName().contains("pods"))
				{
					Block pods = ((Block)f.get(null));
					if(pods != null)
					{
						MFRRegistry.registerFruit(new FruitForestryPod(pods.blockID));
						MFRRegistry.registerFertilizable(new FertilizableForestryPods(pods.blockID));
					}
				}
			}
			MFRRegistry.registerFertilizer(new FertilizerForestry(ForestryUtils.getItem("fertilizerCompound")));
		}
		catch(Exception x)
		{
			x.printStackTrace();
		}
	}
	
	@EventHandler
	public static void postInit(FMLPostInitializationEvent e)
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
		*/
		MineFactoryReloadedCore.proxy.onPostTextureStitch(null);
		ForestryUtils.setTreeRoot();
		
		TileEntityUnifier.updateUnifierLiquids();
	}
}
