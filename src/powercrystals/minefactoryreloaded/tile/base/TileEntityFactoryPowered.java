package powercrystals.minefactoryreloaded.tile.base;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.MinecraftForge;
import powercrystals.core.asm.relauncher.Implementable;
import powercrystals.core.util.Util;
import powercrystals.core.util.UtilInventory;
import powercrystals.minefactoryreloaded.setup.Machine;
import buildcraft.api.power.PowerHandler;
import buildcraft.api.power.PowerHandler.PowerReceiver;
import buildcraft.api.power.PowerHandler.Type;
//import universalelectricity.core.block.IElectrical;
//import universalelectricity.core.electricity.ElectricityHelper;
//import universalelectricity.core.electricity.ElectricityPack;

/*
 * There are three pieces of information tracked - energy, work, and idle ticks.
 * 
 * Energy is stored and used when the machine activates. The energy stored must be >= energyActivation for the activateMachine() method to be called.
 * If activateMachine() returns true, energy will be drained.
 * 
 * Work is built up and then when at 100% something happens. This is tracked/used entirely by the derived class. If not used (f.ex. harvester), return max 1.
 * 
 * Idle ticks cause an artificial delay before activateMachine() is called again. Max should be the highest value the machine will use, to draw the
 * progress bar correctly.
 */

@Implementable("ic2.api.energy.tile.IEnergySink;buildcraft.api.power.IPowerReceptor")
public abstract class TileEntityFactoryPowered extends TileEntityFactoryInventory
{
	public static boolean needModCheck = true;
	public static boolean bcLoaded = false;
	public static boolean ic2Loaded = false;
	
	public static final int energyPerEU = 4;
	public static final int energyPerMJ = 10;
	public static final int wPerEnergy = 7;
	
	private int _energyStored;
	protected int _energyActivation;
	
	private int _workDone;
	
	private int _idleTicks;
	
	protected List<ItemStack> failedDrops = null;
	private List<ItemStack> missedDrops = new ArrayList<ItemStack>();
	
	protected int _failedDropTicksMax = 20;
	private int _failedDropTicks = 0;
	
	// buildcraft-related fields
	private PowerHandler _powerHandler;
	
	// IC2-related fields
	private boolean _isAddedToIC2EnergyNet;
	private boolean _addToNetOnNextTick;
	
	// constructors
	protected TileEntityFactoryPowered(Machine machine)
	{
		this(machine, machine.getActivationEnergyMJ());
	}
	
	protected TileEntityFactoryPowered(Machine machine, int activationCostMJ)
	{
		super(machine);
		if (needModCheck)
		{
			try
			{
				bcLoaded = (Class.forName("buildcraft.api.power.IPowerReceptor") != null);
			}
			catch (Exception e)
			{
				bcLoaded = false;
			}
			
			try
			{
				ic2Loaded = (Class.forName("ic2.api.energy.tile.IEnergySink") != null);
			}
			catch (Exception e)
			{
				ic2Loaded = false;
			}
			needModCheck = false;
		}
		_energyActivation = activationCostMJ * energyPerMJ;
		if (bcLoaded)
		{
			_powerHandler = new PowerHandler((buildcraft.api.power.IPowerReceptor)this, Type.MACHINE);
			_powerHandler.configure(0, 100, 0, 100);
			_powerHandler.configurePowerPerdition(0, 0);
		}
		setIsActive(false);
	}
	
	@Override
	public void updateEntity()
	{
		super.updateEntity();
		
		if(worldObj.isRemote)
		{
			return;
		}
		
		if(ic2Loaded && _addToNetOnNextTick)
		{
			try
			{
				Object event = Class.forName("ic2.api.energy.event.EnergyTileLoadEvent").getConstructors()[0].newInstance(this);
				MinecraftForge.EVENT_BUS.post((net.minecraftforge.event.Event)event);
			} catch (Throwable _) {}
			_addToNetOnNextTick = false;
			_isAddedToIC2EnergyNet = true;
		}
		
		if(!bcLoaded && !ic2Loaded)
		{
			int inject = Math.min(10, getEnergyStoredMax() - getEnergyStored());
			if (inject > 0)
				setEnergyStored(getEnergyStored() + inject);
		}
		
		_energyStored = Math.min(_energyStored, getEnergyStoredMax());
		setIsActive(_energyStored >= _energyActivation * 2);
		
		if (failedDrops != null)
		{
			if (_failedDropTicks < _failedDropTicksMax)
			{
				_failedDropTicks++;
				return;
			}
			_failedDropTicks = 0;
			if (!doDrop(failedDrops))
			{
				setIdleTicks(getIdleTicksMax());
				return;
			}
			failedDrops = null;
		}
		
		if(Util.isRedstonePowered(this))
		{
			setIdleTicks(getIdleTicksMax());
		}
		else if(_idleTicks > 0)
		{
			_idleTicks--;
		}
		else if(_energyStored >= _energyActivation)
		{
			if(activateMachine())
			{
				_energyStored -= _energyActivation;
			}
		}
	}

	public boolean doDrop(ItemStack drop)
	{
		drop = UtilInventory.dropStack(this, drop, this.getDropDirection());
		if (drop != null && drop.stackSize > 0)
		{
			if (failedDrops == null)
			{
				failedDrops = new ArrayList<ItemStack>();
			}
			failedDrops.add(drop);
		}
		return true;
	}
	
	public boolean doDrop(List<ItemStack> drops)
	{
		if (drops == null || drops.size() <= 0)
		{
			return true;
		}
		List<ItemStack> missed = missedDrops;
		missed.clear();
		for (int i = drops.size(); i --> 0; )
		{
			ItemStack dropStack = drops.get(i);
			dropStack = UtilInventory.dropStack(this, dropStack, this.getDropDirection());
			if (dropStack != null && dropStack.stackSize > 0)
			{
				missed.add(dropStack);
			}
		}
		
		if (missed.size() != 0)
		{
			if (drops != failedDrops)
			{
				if (failedDrops == null)
				{
					failedDrops = new ArrayList<ItemStack>();
				}
				failedDrops.addAll(missed);
			}
			else
			{
				failedDrops.clear();
				failedDrops.addAll(missed);
			}
			return false;
		}
		
		return true;
	}
	
	@Override
	public void validate()
	{
		super.validate();
		if(ic2Loaded && !_isAddedToIC2EnergyNet)
		{
			_addToNetOnNextTick = true;
		}
	}
	
	@Override
	public void invalidate()
	{
		if(ic2Loaded && _isAddedToIC2EnergyNet)
		{
			if(!worldObj.isRemote)
			{
				try{
					Object event = Class.forName("ic2.api.energy.event.EnergyTileUnloadEvent").getConstructors()[0].newInstance(this);
					MinecraftForge.EVENT_BUS.post((net.minecraftforge.event.Event)event);
				} catch (Throwable _) {}
			}
			_isAddedToIC2EnergyNet = false;
		}
		super.invalidate();
	}
	
	@Override
	public void onChunkUnload()
	{
		if(ic2Loaded && _isAddedToIC2EnergyNet)
		{
			if(!worldObj.isRemote)
			{
				try{
					Object event = Class.forName("ic2.api.energy.event.EnergyTileUnloadEvent").getConstructors()[0].newInstance(this);
					MinecraftForge.EVENT_BUS.post((net.minecraftforge.event.Event)event);
				} catch (Throwable _) {}
			}
			_isAddedToIC2EnergyNet = false;
		}
		super.onChunkUnload();
	}
	
	protected abstract boolean activateMachine();
	
	@Override
	public void onBlockBroken()
	{
		super.onBlockBroken();
		if(ic2Loaded && _isAddedToIC2EnergyNet)
		{
			_isAddedToIC2EnergyNet = false;
			try
			{
				Object event = Class.forName("ic2.api.energy.event.EnergyTileUnloadEvent").getConstructors()[0].newInstance(this);
				MinecraftForge.EVENT_BUS.post((net.minecraftforge.event.Event)event);
			} catch (Throwable _) {}
		}
	}
	
	public int getMaxEnergyPerTick()
	{
		return _energyActivation;
	}
	
	public int getEnergyStored()
	{
		return _energyStored;
	}
	
	public abstract int getEnergyStoredMax();
	
	public void setEnergyStored(int energy)
	{
		_energyStored = energy;
	}
	
	public int getWorkDone()
	{
		return _workDone;
	}
	
	public abstract int getWorkMax();
	
	public void setWorkDone(int work)
	{
		_workDone = work;
	}
	
	public int getIdleTicks()
	{
		return _idleTicks;
	}
	
	public abstract int getIdleTicksMax();
	
	public void setIdleTicks(int ticks)
	{
		_idleTicks = ticks;
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag)
	{
		super.writeToNBT(tag);
		
		tag.setInteger("energyStored", _energyStored);
		tag.setInteger("workDone", _workDone);
		if (bcLoaded)
			_powerHandler.writeToNBT(tag);
		
		if (failedDrops != null)
		{
			NBTTagList nbttaglist = new NBTTagList();
			for (ItemStack item : failedDrops)
			{
				NBTTagCompound nbttagcompound1 = new NBTTagCompound();
				item.writeToNBT(nbttagcompound1);
				nbttaglist.appendTag(nbttagcompound1);
			}
			tag.setTag("DropItems", nbttaglist);
		}
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		super.readFromNBT(tag);
		
		_energyStored = Math.min(tag.getInteger("energyStored"), getEnergyStoredMax());
		_workDone = Math.min(tag.getInteger("workDone"), getWorkMax());
		if (bcLoaded)
			_powerHandler.readFromNBT(tag);
		
		if (tag.hasKey("DropItems"))
		{
			List<ItemStack> drops = new ArrayList<ItemStack>();
			NBTTagList nbttaglist = tag.getTagList("DropItems");
			for (int i = nbttaglist.tagCount(); i --> 0; )
			{
				NBTTagCompound nbttagcompound1 = (NBTTagCompound)nbttaglist.tagAt(i);
				ItemStack item = ItemStack.loadItemStackFromNBT(nbttagcompound1);
				if (item != null && item.stackSize > 0)
				{
					drops.add(item);
				}
			}
			if (drops.size() != 0)
			{
				failedDrops = drops;
			}
		}
	}
	
	public int getEnergyRequired()
	{
		return Math.max(getEnergyStoredMax() - getEnergyStored(), 0);
	}
	
	// BC methods
	public PowerReceiver getPowerReceiver(ForgeDirection side)
	{
		return _powerHandler.getPowerReceiver();
	}

	public void doWork(PowerHandler workProvider)
	{
		float use = workProvider.useEnergy(0, (float)getEnergyRequired() / energyPerMJ, true);
		setEnergyStored(getEnergyStored() + (int)Math.floor(use * energyPerMJ));
	}

	public World getWorld()
	{
		return worldObj;
	}
	
	// IC2 methods
	public double demandedEnergyUnits()
	{
		return (double)getEnergyRequired() / energyPerEU;
	}
	
	public double injectEnergyUnits(ForgeDirection directionFrom, double amount)
	{
		int euInjected = (int)Math.max(Math.min(this.demandedEnergyUnits(), amount), 0);
		setEnergyStored(getEnergyStored() + (euInjected * energyPerEU));
		return amount - euInjected;
	}
	
	public boolean acceptsEnergyFrom(TileEntity emitter, ForgeDirection direction)
	{
		return true;
	}
	
	public int getMaxSafeInput()
	{
		return 128;
	}
	
	// UE Methods
	/*
	public float receiveElectricity(ForgeDirection from, ElectricityPack receive, boolean doReceive)
	{
		if (receive == null)
			return 0;
		
		int require = Math.max(Math.min(getEnergyRequired(), (int)(receive.getWatts() / wPerEnergy)), 0);

		if (doReceive)
		{
			this.setEnergyStored(this.getEnergyStored() + require);
		}

		return require * wPerEnergy;
	}

	public ElectricityPack provideElectricity(ForgeDirection from, ElectricityPack request, boolean doProvide)
	{
		return null;
	}

	public float getRequest(ForgeDirection direction)
	{
		return Math.max(getEnergyRequired() * wPerEnergy, 0);
	}

	public float getProvide(ForgeDirection direction)
	{
		return 0;
	}

	public float getVoltage()
	{
		return 0.120F;
	}

	public boolean canConnect(ForgeDirection direction)
	{
		return true;
	}
	*/
}
