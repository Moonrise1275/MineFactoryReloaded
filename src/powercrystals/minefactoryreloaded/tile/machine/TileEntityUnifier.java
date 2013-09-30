package powercrystals.minefactoryreloaded.tile.machine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.oredict.OreDictionary;
import powercrystals.minefactoryreloaded.util.OreDictTracker;
import powercrystals.minefactoryreloaded.core.ITankContainerBucketable;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryInventory;
import powercrystals.minefactoryreloaded.gui.client.GuiUnifier;
import powercrystals.minefactoryreloaded.gui.container.ContainerUnifier;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryInventory;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityUnifier extends TileEntityFactoryInventory implements ITankContainerBucketable
{
	private FluidTank _tank;
	
	private static FluidStack _biofuel;
	private static FluidStack _ethanol;
	private static FluidStack _essence;
	private static FluidStack _liquidxp;
	private int _roundingCompensation;
	
	private Map<String, ItemStack> _preferredOutputs = new HashMap<String, ItemStack>();
	
	public TileEntityUnifier()
	{
		super(Machine.Unifier);
		_tank = new FluidTank(FluidContainerRegistry.BUCKET_VOLUME * 4);
		_roundingCompensation = 1;
	}

	public static void updateUnifierLiquids()
	{
		_biofuel = FluidRegistry.getFluidStack("biofuel", 1);
		_ethanol = FluidRegistry.getFluidStack("ethanol", 1);
		_essence = FluidRegistry.getFluidStack("mobessence", 1);
		_liquidxp = FluidRegistry.getFluidStack("immibis.liquidxp", 1);
	}
	
	@Override
	public String getGuiBackground()
	{
		return "unifier.png";
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public GuiFactoryInventory getGui(InventoryPlayer inventoryPlayer)
	{
		return new GuiUnifier(getContainer(inventoryPlayer), this);
	}
	
	@Override
	public ContainerUnifier getContainer(InventoryPlayer inventoryPlayer)
	{
		return new ContainerUnifier(this, inventoryPlayer);
	}
	
	@Override
	public void updateEntity()
	{
		super.updateEntity();
		if(!worldObj.isRemote)
		{
			ItemStack output = null;
			if(_inventory[0] != null)
			{
				List<String> names = OreDictTracker.getNamesFromItem(_inventory[0]);
				
				if(names == null || names.size() != 1)
				{
					output = _inventory[0].copy();
				}
				else if(_preferredOutputs.containsKey(names.get(0)))
				{
					output = _preferredOutputs.get(names.get(0)).copy();
					output.stackSize = _inventory[0].stackSize;
				}
				else
				{
					output = OreDictionary.getOres(names.get(0)).get(0).copy();
					output.stackSize = _inventory[0].stackSize;
				}
				
				moveItemStack(output);
			}
		}
	}
	
	private void moveItemStack(ItemStack source)
	{
		if(source == null)
		{
			return;
		}
		
		int amt = source.stackSize;
		
		if(_inventory[1] == null)
		{
			amt = Math.min(getInventoryStackLimit(), source.getMaxStackSize());
		}
		else if(source.itemID != _inventory[1].itemID || source.getItemDamage() != _inventory[1].getItemDamage())
		{
			return;
		}
		else if(source.getTagCompound() != null || _inventory[1].getTagCompound() != null)
		{
			return;
		}
		else
		{
			amt = Math.min(_inventory[0].stackSize, _inventory[1].getMaxStackSize() - _inventory[1].stackSize);
		}
		
		if(_inventory[1] == null)
		{
			_inventory[1] = source.copy();
			_inventory[0].stackSize -= source.stackSize;
		}
		else
		{
			_inventory[1].stackSize += amt;
			_inventory[0].stackSize -= amt;
		}
		
		if(_inventory[0].stackSize == 0)
		{
			_inventory[0] = null;
		}
	}
	
	@Override
	protected void onFactoryInventoryChanged()
	{
		_preferredOutputs.clear();
		for(int i = 2; i < 11; i++)
		{
			if(_inventory[i] == null)
			{
				continue;
			}
			List<String> names = OreDictTracker.getNamesFromItem(_inventory[i]);
			if(names != null)
			{
				for(String name : names)
				{
					_preferredOutputs.put(name, _inventory[i].copy());
				}
			}
		}
	}
	
	@Override
	public int getSizeInventory()
	{
		return 11;
	}
	
	@Override
	public boolean shouldDropSlotWhenBroken(int slot)
	{
		return slot < 2;
	}
	
	@Override
	public int getInventoryStackLimit()
	{
		return 64;
	}
	
	@Override
	public boolean canInsertItem(int slot, ItemStack stack, int sideordinal)
	{
		if(slot == 0) return true;
		return false;
	}
	
	@Override
	public boolean canExtractItem(int slot, ItemStack itemstack, int sideordinal)
	{
		if(slot == 1) return true;
		return false;
	}
	
	@Override
	public FluidTank getTank()
	{
		return _tank;
	}
	
	@Override
	public boolean allowBucketFill()
	{
		return true;
	}
	
	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill)
	{
		if(resource == null || resource.amount == 0) return 0;
		
		FluidStack converted = unifierTransformLiquid(resource, doFill);
		
		if(converted == null || converted.amount == 0) return 0;
		
		int filled = _tank.fill(converted, doFill);
		
		if(filled == converted.amount)
		{
			return resource.amount;
		}
		else
		{
			return filled * resource.amount / converted.amount + (resource.amount & _roundingCompensation);
		}
	}
	
	private FluidStack unifierTransformLiquid(FluidStack resource, boolean doFill)
	{
		if(_ethanol != null && _biofuel != null &&
				resource.fluidID == _ethanol.fluidID)
		{
			return new FluidStack(_biofuel.fluidID, resource.amount);
		}
		else if(_ethanol != null && _biofuel != null &&
				resource.fluidID == _biofuel.fluidID)
		{
			return new FluidStack(_ethanol.fluidID, resource.amount);
		}
		else if(_essence != null && _liquidxp != null &&
				resource.fluidID == _essence.fluidID)
		{
			return new FluidStack(_liquidxp.fluidID, resource.amount * 2);
		}
		else if(_essence != null && _liquidxp != null &&
				resource.fluidID == _liquidxp.fluidID)
		{
			if(doFill)
			{
				_roundingCompensation ^= (resource.amount & 1);
			}
			return new FluidStack(_essence.fluidID, resource.amount / 2 + (resource.amount & _roundingCompensation));
		}
		
		return null;
	}
	
	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid)
	{
		return true;
	}
	
	@Override
	public boolean allowBucketDrain()
	{
		return true;
	}
	
	@Override
	public FluidStack drain(ForgeDirection from, FluidStack fluid, boolean doDrain)
	{
		if (_tank.getFluid() != null && fluid != null && _tank.getFluid().fluidID == fluid.fluidID)
			return _tank.drain(fluid.amount, doDrain);
		return null;
	}
	
	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain)
	{
		return _tank.drain(maxDrain, doDrain);
	}
	
	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid)
	{
		return true;
	}
	
	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection direction)
	{
		return new FluidTankInfo[] { _tank.getInfo() };
	}
}
