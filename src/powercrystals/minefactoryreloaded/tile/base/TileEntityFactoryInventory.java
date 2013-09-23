package powercrystals.minefactoryreloaded.tile.base;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import powercrystals.core.asm.relauncher.Implementable;
import powercrystals.core.position.BlockPosition;
import powercrystals.minefactoryreloaded.core.BlockNBTManager;
import powercrystals.minefactoryreloaded.core.MFRLiquidMover;
import powercrystals.minefactoryreloaded.setup.Machine;

import buildcraft.api.transport.IPipeConnection.ConnectOverride;
import buildcraft.api.transport.IPipeTile.PipeType;
import buildcraft.api.gates.IAction;

@Implementable("buildcraft.api.transport.IPipeConnection;buildcraft.core.IMachine")
public abstract class TileEntityFactoryInventory extends TileEntityFactory implements ISidedInventory
{
	public enum ConnectType
	{
		CONNECT, DISCONNECT, DEFAULT
	}
	
	protected Machine machine;
	
	protected String _invName;
	protected boolean _hasInvName = false;
	
	protected TileEntityFactoryInventory(Machine machine)
	{
		_inventory = new ItemStack[getSizeInventory()];
		this.machine = machine;
	}
	
	@Override
	public String getInvName()
	{
		return _hasInvName ? _invName : StatCollector.translateToLocal(machine.getInternalName() + ".name");
	}
	
	@Override
	public boolean isInvNameLocalized()
	{
		return _hasInvName;
	}
	
	public void setInvName(String name)
	{
		this._invName = name;
		this._hasInvName = name != null && name.length() > 0;
	}
	
	public void onBlockBroken()
	{
		if (isInvNameLocalized())
		{
			NBTTagCompound tag = new NBTTagCompound();
			NBTTagCompound name = new NBTTagCompound();
			name.setString("Name", getInvName());
			tag.setTag("display", name);
			BlockNBTManager.setForBlock(new BlockPosition(xCoord, yCoord, zCoord), tag);
		}
	}
	
	public FluidTank getTank()
	{
		return null;
	}
	
	protected boolean shouldPumpLiquid()
	{
		return false;
	}
	
	public boolean allowBucketFill()
	{
		return false;
	}
	
	public boolean allowBucketDrain()
	{
		return false;
	}
	
	@Override
	public void updateEntity()
	{
		super.updateEntity();
		
		if(!worldObj.isRemote && shouldPumpLiquid())
		{
			MFRLiquidMover.pumpLiquid(getTank(), this);
		}
	}
	
	protected ItemStack[] _inventory;
	
	@Override
	public ItemStack getStackInSlot(int slot)
	{
		if (_inventory == null || slot < 0 || slot >= _inventory.length)
			return null;
		return _inventory[slot];
	}
	
	@Override
	public void openChest()
	{
	}
	
	@Override
	public void closeChest()
	{
	}
	
	@Override
	public ItemStack decrStackSize(int slot, int size)
	{
		if(_inventory[slot] != null)
		{
			if(_inventory[slot].stackSize <= size)
			{
				ItemStack itemstack = _inventory[slot];
				_inventory[slot] = null;
				onFactoryInventoryChanged();
				return itemstack;
			}
			ItemStack itemstack1 = _inventory[slot].splitStack(size);
			if(_inventory[slot].stackSize == 0)
			{
				_inventory[slot] = null;
			}
			onFactoryInventoryChanged();
			return itemstack1;
		}
		else
		{
			onFactoryInventoryChanged();
			return null;
		}
	}
	
	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack)
	{
		_inventory[i] = itemstack;
		if(itemstack != null && itemstack.stackSize > getInventoryStackLimit())
		{
			itemstack.stackSize = getInventoryStackLimit();
		}
		onFactoryInventoryChanged();
	}
	
	protected void onFactoryInventoryChanged()
	{
	}
	
	@Override
	public int getInventoryStackLimit()
	{
		return 64;
	}
	
	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack)
	{
		return true;
	}
	
	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer)
	{
		if(worldObj.getBlockTileEntity(xCoord, yCoord, zCoord) != this)
		{
			return false;
		}
		return entityplayer.getDistanceSq(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D) <= 64D;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound)
	{
		super.readFromNBT(nbttagcompound);
		NBTTagList nbttaglist = nbttagcompound.getTagList("Items");
		_inventory = new ItemStack[getSizeInventory()];
		for(int i = 0; i < nbttaglist.tagCount(); i++)
		{
			NBTTagCompound nbttagcompound1 = (NBTTagCompound)nbttaglist.tagAt(i);
			int j = nbttagcompound1.getByte("Slot") & 0xff;
			if(j >= 0 && j < _inventory.length)
			{
				ItemStack s = new ItemStack(0, 0, 0);
				s.readFromNBT(nbttagcompound1);
				_inventory[j] = s;
			}
		}
		onFactoryInventoryChanged();
		
		boolean foundLiquid = false;
		FluidTank tank = getTank();
		
		int tankAmount = nbttagcompound.getInteger("tankAmount");
		if (tank != null && tankAmount > 0 && nbttagcompound.hasKey("tankFluidName"))
		{
			FluidStack fluid = FluidRegistry.getFluidStack(nbttagcompound.getString("tankFluidName"), tankAmount);
			if (fluid != null)
			{
				tank.setFluid(fluid);
				foundLiquid = true;
			}
		}
		if (tank != null && tankAmount > 0 && !foundLiquid && nbttagcompound.hasKey("tankFluidID"))
		{
			int fluidId = nbttagcompound.getInteger("tankFluidID");
			FluidStack fluid = FluidRegistry.getFluidStack(FluidRegistry.getFluidName(fluidId), tankAmount);
			
			if(fluid != null)
			{
				tank.setFluid(fluid);
				foundLiquid = true;
			}
		}
		if (tank != null && !foundLiquid)
		{
			tank.setFluid(null);
		}
		
		for(int i = 0; i < getSizeInventory(); i++)
		{
			if(_inventory[i] != null && _inventory[i].getItem() == null)
			{
				_inventory[i] = null;
			}
		}
		
		if (nbttagcompound.hasKey("display"))
		{
			NBTTagCompound display = nbttagcompound.getCompoundTag("display");
			if (display.hasKey("Name"))
			{
				this.setInvName(display.getString("Name"));
			}
		}
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound)
	{
		super.writeToNBT(nbttagcompound);
		NBTTagList nbttaglist = new NBTTagList();
		for(int i = 0; i < _inventory.length; i++)
		{
			if(_inventory[i] != null)
			{
				NBTTagCompound nbttagcompound1 = new NBTTagCompound();
				nbttagcompound1.setByte("Slot", (byte)i);
				_inventory[i].writeToNBT(nbttagcompound1);
				nbttaglist.appendTag(nbttagcompound1);
			}
		}
		FluidTank tank = getTank();
		if(tank != null)
		{
			FluidStack fluid = tank.getFluid();
			if (fluid != null)
			{
				nbttagcompound.setInteger("tankAmount", fluid.amount);
				String name = fluid.getFluid().getName();
				if (name != null)
				{
					nbttagcompound.setString("tankFluidName", name);
				}
				nbttagcompound.setInteger("tankFluidID", fluid.fluidID);
			}
			else
			{
				nbttagcompound.removeTag("tankAmount");
				nbttagcompound.removeTag("tankFluidName");
				nbttagcompound.removeTag("tankFluidID");
			}
		}
		
		if (this.isInvNameLocalized())
		{
			NBTTagCompound display = new NBTTagCompound();
			display.setString("Name", getInvName());
			nbttagcompound.setCompoundTag("display", display);
		}
		
		nbttagcompound.setTag("Items", nbttaglist);
	}
	
	@Override
	public ItemStack getStackInSlotOnClosing(int var1)
	{
		return null;
	}
	
	public boolean shouldDropSlotWhenBroken(int slot)
	{
		return true;
	}
	
	@Override
	public int[] getAccessibleSlotsFromSide(int side)
	{
		int start = getStartInventorySide(ForgeDirection.getOrientation(side));
		int size = getSizeInventorySide(ForgeDirection.getOrientation(side));
		
		int[] slots = new int[size];
		for(int i = 0; i < size; i++)
		{
			slots[i] = i + start;
		}
		return slots;
	}
	
	public int getSizeInventory()
	{
		return 0;
	}
	
	public int getStartInventorySide(ForgeDirection side)
	{
		return 0;
	}
	
	public int getSizeInventorySide(ForgeDirection side)
	{
		return getSizeInventory();
	}
	
	@Override
	public boolean canInsertItem(int slot, ItemStack itemstack, int side)
	{
		return this.isItemValidForSlot(slot, itemstack);
	}
	
	@Override
	public boolean canExtractItem(int slot, ItemStack itemstack, int side)
	{
		return true;
	}

	public float getComparatorOutput(int side)
	{
		int[] slots = getAccessibleSlotsFromSide(side);
		int len = 0;
		float ret = 0;
		for (int i = slots.length; i --> 0; )
		{
			if (canInsertItem(slots[i], null, side))
			{
				ItemStack stack = getStackInSlot(slots[i]);
				if (stack != null)
				{
					float maxStack = Math.min(stack.getMaxStackSize(), getInventoryStackLimit()); 
					ret += Math.max(Math.min(stack.stackSize / maxStack, 1), 0);
				}
				++len;
			}
		}
		return ret / len;
	}
	
	public ConnectType canConnectItemPipe(ForgeDirection with)
	{
		return ConnectType.DEFAULT;
	}
	
	// IPipeConnection
	public ConnectOverride overridePipeConnection(PipeType type, ForgeDirection with)
	{
		if (type != PipeType.ITEM)
			return ConnectOverride.DEFAULT;
		
		switch (canConnectItemPipe(with))
		{
			case CONNECT:
				return ConnectOverride.CONNECT;
			case DISCONNECT:
				return ConnectOverride.DISCONNECT;
		}

		return ConnectOverride.DEFAULT;
	}
	
	// IMachine
	public boolean isActive()
	{
		return false;
	}
	
	public boolean manageFluids()
	{
		return false;
	}
	
	public boolean manageSolids()
	{
		return false;
	}
	
	public boolean allowActions()
	{
		return false;
	}
	
	public boolean allowAction(IAction action)
	{
		return this.allowActions();
	}
}
