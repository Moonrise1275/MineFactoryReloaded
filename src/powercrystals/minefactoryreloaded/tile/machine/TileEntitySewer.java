package powercrystals.minefactoryreloaded.tile.machine;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import powercrystals.minefactoryreloaded.util.Area;
import powercrystals.minefactoryreloaded.util.BlockPosition;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.core.HarvestAreaManager;
import powercrystals.minefactoryreloaded.core.IHarvestAreaContainer;
import powercrystals.minefactoryreloaded.core.ITankContainerBucketable;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryInventory;
import powercrystals.minefactoryreloaded.gui.client.GuiSewer;
import powercrystals.minefactoryreloaded.gui.container.ContainerSewer;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryInventory;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntitySewer extends TileEntityFactoryInventory implements ITankContainerBucketable, IHarvestAreaContainer
{
	private FluidTank _tank;
	
	private HarvestAreaManager _areaManager;
	
	private int _tick;
	
	private long _nextSewerCheckTick;
	
	private boolean _jammed;
	
	public TileEntitySewer()
	{
		super(Machine.Sewer);
		_tank = new FluidTank(1 * FluidContainerRegistry.BUCKET_VOLUME);
		_areaManager = new HarvestAreaManager(this, 0, 1, 0);
		_areaManager.setOverrideDirection(ForgeDirection.UP);
	}
	
	@Override
	public String getGuiBackground()
	{
		return "sewagecollector.png";
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public GuiFactoryInventory getGui(InventoryPlayer inventoryPlayer)
	{
		return new GuiSewer(getContainer(inventoryPlayer), this);
	}
	
	@Override
	public ContainerSewer getContainer(InventoryPlayer inventoryPlayer)
	{
		return new ContainerSewer(this, inventoryPlayer);
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
	protected void onFactoryInventoryChanged()
	{
		_areaManager.updateUpgradeLevel(_inventory[0]);
	}
	
	@Override
	public HarvestAreaManager getHAM()
	{
		return _areaManager;
	}
	
	@Override
	public void updateEntity()
	{
		super.updateEntity();
		if(worldObj.isRemote)
		{
			return;
		}
		_tick++;
		
		if(_nextSewerCheckTick <= worldObj.getTotalWorldTime())
		{
			Area a = new Area(BlockPosition.fromFactoryTile(this), _areaManager.getRadius(), 0, 0);
			_jammed = false;
			for(BlockPosition bp : a.getPositionsBottomFirst())
			{
				if(worldObj.getBlockId(bp.x, bp.y, bp.z) == MineFactoryReloadedCore.machineBlocks.get(0).blockID &&
						worldObj.getBlockMetadata(bp.x, bp.y, bp.z) == Machine.Sewer.getMeta() &&
						!(bp.x == xCoord && bp.y == yCoord && bp.z == zCoord))
				{
					_jammed = true;
					break;
				}
			}
			
			_nextSewerCheckTick = worldObj.getTotalWorldTime() + 800 + worldObj.rand.nextInt(800);
		}
		
		if(_tick >= 31 && !_jammed)
		{
			_tick = 0;
			List<?> entities = worldObj.getEntitiesWithinAABB(EntityLivingBase.class, _areaManager.getHarvestArea().toAxisAlignedBB());
			double massFound = 0;
			for(Object o : entities)
			{
				if(o instanceof EntityAnimal || o instanceof EntityVillager)
				{
					massFound += Math.pow(((EntityLivingBase)o).boundingBox.getAverageEdgeLength(), 2);
				}
				else if(o instanceof EntityPlayer && ((EntityPlayer)o).isSneaking())
				{
					massFound += Math.pow(((EntityLivingBase)o).boundingBox.getAverageEdgeLength(), 2);
				}
			}
			if (massFound > 0)
			{
				_tank.fill(FluidRegistry.getFluidStack("sewage", (int)(25 * massFound)), true);
			}
			// TODO: add a second tank to the sewer for essence
			else if (_tank.getFluid() == null || _tank.getFluid().isFluidEqual(FluidRegistry.getFluidStack("mobEssence", 1)))
			{
				int maxAmount = Math.max(_tank.getCapacity() - _tank.getFluidAmount(), 0);
				if (maxAmount < 0)
				{
					return;
				}
				entities = worldObj.getEntitiesWithinAABB(EntityXPOrb.class, _areaManager.getHarvestArea().toAxisAlignedBB());
				for (Object o : entities)
				{
					Entity e = (Entity)o;
					if (e != null & e instanceof EntityXPOrb && !e.isDead)
					{
						EntityXPOrb orb = (EntityXPOrb)o;
						int found = Math.min(orb.xpValue, maxAmount);
						orb.xpValue -= found;
						if (orb.xpValue <= 0)
						{
							orb.setDead();
							found = Math.max(found, 0);
						}
						if (found > 0)
						{
							found = (int)(found * 66.66666667f);
							maxAmount -= found;
							_tank.fill(FluidRegistry.getFluidStack("mobEssence", found), true);
							if (maxAmount <= 0)
							{
								break;
							}
						}
					}
				}
			}
		}
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
	@Override
	public int getSizeInventory()
	{
		return 1;
	}
}
