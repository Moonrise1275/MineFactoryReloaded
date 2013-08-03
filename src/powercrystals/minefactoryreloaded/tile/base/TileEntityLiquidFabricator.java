package powercrystals.minefactoryreloaded.tile.base;

import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import powercrystals.minefactoryreloaded.core.ITankContainerBucketable;
import powercrystals.minefactoryreloaded.setup.Machine;

public abstract class TileEntityLiquidFabricator extends TileEntityFactoryPowered implements ITankContainerBucketable
{
	private Fluid _fluid;
	private int _fabPerTick;
	
	private FluidTank _tank;
	
	protected TileEntityLiquidFabricator(Fluid fluid, int fabPerTick, Machine machine)
	{
		super(machine, machine.getActivationEnergyMJ() * fabPerTick);
		_fluid = fluid;
		_fabPerTick = fabPerTick;
		_tank = new FluidTank(FluidContainerRegistry.BUCKET_VOLUME);
	}
	
	@Override
	protected boolean activateMachine()
	{
		if(_fluid == null)
		{
			setIdleTicks(getIdleTicksMax());
			return false;
		}
		
		if(_tank.getFluid() != null && _tank.getCapacity() - _tank.getFluid().amount < _fabPerTick)
		{
			return false;
		}
		
		_tank.fill(new FluidStack(_fluid, _fabPerTick), true);
		
		return true;
	}
	
	@Override
	public int getEnergyStoredMax()
	{
		return 16000;
	}
	
	@Override
	public int getWorkMax()
	{
		return 0;
	}
	
	@Override
	public int getIdleTicksMax()
	{
		return 200;
	}
	
	@Override
	public FluidTank getTank()
	{
		return _tank;
	}
	
	@Override
	protected boolean shouldPumpLiquid()
	{
		return true;
	}
	
	@Override
	public int getSizeInventory()
	{
		return 0;
	}
	
	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill)
	{
		return 0;
	}
	/*
	@Override
	public int fill(int tankIndex, FluidStack resource, boolean doFill)
	{
		return 0;
	}
	*/
	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid)
	{
		return false;
	}
	
	@Override
	public boolean allowBucketDrain()
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
		return _tank;
	}
	*/
}
