package powercrystals.minefactoryreloaded.core;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import powercrystals.core.position.BlockPosition;
import powercrystals.core.util.UtilInventory;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactory;

public abstract class MFRLiquidMover
{
	/**
	 * Attempts to fill tank with the player's current item.
	 * @param	itcb			the tank the liquid is going into
	 * @param	entityplayer	the player trying to fill the tank
	 * @return	True if liquid was transferred to the tank.
	 */
	public static boolean manuallyFillTank(ITankContainerBucketable itcb, EntityPlayer entityplayer)
	{
		ItemStack ci = entityplayer.inventory.getCurrentItem();
		FluidStack liquid = FluidContainerRegistry.getFluidForFilledItem(ci);
		if(liquid != null)
		{
			if(itcb.fill(ForgeDirection.UNKNOWN, liquid, false) == liquid.amount)
			{
				itcb.fill(ForgeDirection.UNKNOWN, liquid, true);
				if(!entityplayer.capabilities.isCreativeMode)
				{
					entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, UtilInventory.consumeItem(ci));					
				}
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Attempts to drain tank into the player's current item.
	 * @param	itcb			the tank the liquid is coming from
	 * @param	entityplayer	the player trying to take liquid from the tank
	 * @return	True if liquid was transferred from the tank.
	 */
	public static boolean manuallyDrainTank(ITankContainerBucketable itcb, EntityPlayer entityplayer)
	{
		ItemStack ci = entityplayer.inventory.getCurrentItem();
		if(FluidContainerRegistry.isEmptyContainer(ci))
		{
			for(FluidTankInfo tank : itcb.getTankInfo(ForgeDirection.UNKNOWN))
			{
				FluidStack tankLiquid = tank.fluid;
				ItemStack filledBucket = FluidContainerRegistry.fillFluidContainer(tankLiquid, ci);
				if(FluidContainerRegistry.isFilledContainer(filledBucket))
				{
					FluidStack bucketLiquid = FluidContainerRegistry.getFluidForFilledItem(filledBucket);
					if(entityplayer.capabilities.isCreativeMode)
					{
						itcb.drain(ForgeDirection.UNKNOWN, bucketLiquid, true);
						return true;
					}
					else if(ci.stackSize == 1)
					{
						itcb.drain(ForgeDirection.UNKNOWN, bucketLiquid, true);
						entityplayer.inventory.setInventorySlotContents(entityplayer.inventory.currentItem, filledBucket);
						return true;
					}
					else if(entityplayer.inventory.addItemStackToInventory(filledBucket))
					{
						itcb.drain(ForgeDirection.UNKNOWN, bucketLiquid, true);
						ci.stackSize -= 1;
						return true;
					}
				}
				
			}
		}
		return false;
	}
	
	public static void pumpLiquid(FluidTank tank, TileEntityFactory from)
	{
		if(tank != null && tank.getFluid() != null && tank.getFluid().amount > 0)
		{
			FluidStack l = tank.getFluid().copy();
			l.amount = Math.min(l.amount, FluidContainerRegistry.BUCKET_VOLUME);
			for(BlockPosition adj : new BlockPosition(from).getAdjacent(true))
			{
				TileEntity tile = from.worldObj.getBlockTileEntity(adj.x, adj.y, adj.z);
				if(tile instanceof IFluidHandler)
				{
					int filled = ((IFluidHandler)tile).fill(adj.orientation.getOpposite(), l, true);
					tank.drain(filled, true);
					l.amount -= filled;
					if(l.amount <= 0)
					{
						break;
					}
				}
			}
		}
	}
	
}
