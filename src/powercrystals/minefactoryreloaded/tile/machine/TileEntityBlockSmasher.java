package powercrystals.minefactoryreloaded.tile.machine;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import powercrystals.core.util.UtilInventory;
import powercrystals.minefactoryreloaded.core.ITankContainerBucketable;
import powercrystals.minefactoryreloaded.gui.client.GuiBlockSmasher;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryInventory;
import powercrystals.minefactoryreloaded.gui.container.ContainerBlockSmasher;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryPowered;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityBlockSmasher extends TileEntityFactoryPowered implements ITankContainerBucketable
{
	private FluidTank _tank;
	
	private int _fortune = 0;
	
	private ItemStack _lastInput;
	private ItemStack _lastOutput;
	
	private Random _rand = new Random();
	
	public TileEntityBlockSmasher()
	{
		super(Machine.BlockSmasher);
		_tank = new FluidTank(FluidContainerRegistry.BUCKET_VOLUME * 4);
	}
	
	@Override
	public void setWorldObj(World world)
	{
		super.setWorldObj(world);
	}
	
	@Override
	public int getSizeInventory()
	{
		return 2;
	}
	
	@Override
	public String getGuiBackground()
	{
		return "blocksmasher.png";
	}
	
	@Override
	public ContainerBlockSmasher getContainer(InventoryPlayer inventoryPlayer)
	{
		return new ContainerBlockSmasher(this, inventoryPlayer);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public GuiFactoryInventory getGui(InventoryPlayer inventoryPlayer)
	{
		return new GuiBlockSmasher(getContainer(inventoryPlayer), this);
	}
	
	@Override
	protected boolean activateMachine()
	{
		if(_inventory[0] == null)
		{
			setWorkDone(0);
			return false;
		}
		if(_lastInput == null || !UtilInventory.stacksEqual(_lastInput, _inventory[0]))
		{
			_lastInput = _inventory[0];
			_lastOutput = getOutput(_lastInput);
		}
		if(_lastOutput == null)
		{
			setWorkDone(0);
			return false;
		}
		if(_fortune > 0 && (_tank.getFluid() == null || _tank.getFluid().amount < _fortune))
		{
			return false;
		}
		if(_inventory[1] != null && !UtilInventory.stacksEqual(_lastOutput, _inventory[1]))
		{
			setWorkDone(0);
			return false;
		}
		if(_inventory[1] != null && _inventory[1].getMaxStackSize() - _inventory[1].stackSize < _lastOutput.stackSize)
		{
			return false;
		}
		
		setWorkDone(getWorkDone() + 1);
		_tank.drain(_fortune, true);
		
		if(getWorkDone() >= getWorkMax())
		{
			setWorkDone(0);
			_lastInput = null;
			if(_inventory[1] == null)
			{
				_inventory[1] = _lastOutput.copy();
			}
			else
			{
				_inventory[1].stackSize += _lastOutput.stackSize;
			}
			
			_inventory[0].stackSize--;
			if(_inventory[0].stackSize == 0)
			{
				_inventory[0] = null;
			}
		}
		return true;
	}
	
	private ItemStack getOutput(ItemStack input)
	{
		if(!(input.getItem() instanceof ItemBlock))
		{
			return null;
		}
		int blockId = ((ItemBlock)input.getItem()).getBlockID();
		Block b = Block.blocksList[blockId];
		if(b == null)
		{
			return null;
		}
		
		ArrayList drops = b.getBlockDropped(this.worldObj, xCoord, 0, zCoord, input.getItemDamage(), _fortune);
		// TODO: support multiple-output
		if (drops != null && drops.size() > 0)
		{
			// HACK: randomly return one of the drops
			return (ItemStack)drops.get(drops.size() > 1 ? _rand.nextInt(drops.size()) : 0);
		}
		return null;
	}
	
	public int getFortune()
	{
		return _fortune;
	}
	
	public void setFortune(int fortune)
	{
		if(fortune >= 0 && fortune <= 3)
		{
			if(_fortune < fortune)
			{
				setWorkDone(0);
			}
			_fortune = fortune;
		}
	}
	
	@Override
	public int getEnergyStoredMax()
	{
		return 16000;
	}
	
	@Override
	public int getWorkMax()
	{
		return 60;
	}
	
	@Override
	public int getIdleTicksMax()
	{
		return 1;
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
	public boolean allowBucketFill()
	{
		return true;
	}
	
	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill)
	{
		if(resource == null || (resource.fluidID != FluidRegistry.getFluidID("mobessence")))
		{
			return 0;
		}
		
		return _tank.fill(resource, doFill);
	}
	/*
	@Override
	public int fill(int tankIndex, FluidStack resource, boolean doFill)
	{
		return fill(ForgeDirection.UNKNOWN, resource, doFill);
	}
	*/
	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid)
	{
		return true;
	}
	
	@Override
	public FluidStack drain(ForgeDirection from, FluidStack fluid, boolean doDrain)
	{
		return null;
	}
	
	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain)
	{
		return null;
	}
	/*
	@Override
	public FluidStack drain(int tankIndex, int maxDrain, boolean doDrain)
	{
		return null;
	}
	*/
	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid)
	{
		return false;
	}
	
	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection direction)
	{
		return new FluidTankInfo[] { _tank.getInfo() };
	}
	/*
	@Override
	public FluidTank getTank(ForgeDirection direction, FluidStack type)
	{
		if(type != null && type.itemID == FluidRegistry.getCanonicalLiquid("mobEssence").itemID)
		{
			return _tank;
		}
		return null;
	}
	*/
	@Override
	public FluidTank getTank()
	{
		return _tank;
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound)
	{
		super.writeToNBT(nbttagcompound);
		nbttagcompound.setInteger("fortune", _fortune);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound)
	{
		super.readFromNBT(nbttagcompound);
		_fortune = nbttagcompound.getInteger("fortune");
	}
}
