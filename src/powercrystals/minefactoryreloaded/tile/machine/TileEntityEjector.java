package powercrystals.minefactoryreloaded.tile.machine;

import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeDirection;
import powercrystals.minefactoryreloaded.util.IInventoryManager;
import powercrystals.minefactoryreloaded.util.InventoryManager;
import powercrystals.minefactoryreloaded.util.Util;
import powercrystals.minefactoryreloaded.util.UtilInventory;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactory;

public class TileEntityEjector extends TileEntityFactory
{
	private boolean _lastRedstoneState;
	
	@Override
	public void updateEntity()
	{
		super.updateEntity();
		if(worldObj.isRemote)
		{
			return;
		}
		boolean redstoneState = Util.isRedstonePowered(this);
		if(redstoneState && !_lastRedstoneState)
		{
			Map<ForgeDirection, IInventory> chests = UtilInventory.findChests(worldObj, xCoord, yCoord, zCoord);
			inv:		for(Entry<ForgeDirection, IInventory> chest : chests.entrySet())
			{
				if(chest.getKey() == getDirectionFacing())
				{
					continue;
				}
				
				IInventoryManager inventory = InventoryManager.create(chest.getValue(), chest.getKey());
				Map<Integer, ItemStack> contents = inventory.getContents();
				
				for(Entry<Integer, ItemStack> stack : contents.entrySet())
				{
					if(stack == null || stack.getValue() == null)
					{
						continue;
					}
					
					if(chest.getValue() instanceof ISidedInventory)
					{
						ISidedInventory sided = (ISidedInventory)chest.getValue();
						if(!sided.canExtractItem(stack.getKey(), stack.getValue(), chest.getKey().ordinal()))
						{
							continue;
						}
					}
					
					ItemStack stackToDrop = stack.getValue().copy();
					stackToDrop.stackSize = 1;
					ItemStack remaining = UtilInventory.dropStack(this, stackToDrop, this.getDirectionFacing(), this.getDirectionFacing());
					
					// remaining == null if dropped successfully.
					if(remaining == null)
					{
						inventory.removeItem(1, stackToDrop);
						break inv;
					}
				}
			}
		}
		_lastRedstoneState = redstoneState;
	}
	
	@Override
	public boolean canRotate()
	{
		return true;
	}
}
