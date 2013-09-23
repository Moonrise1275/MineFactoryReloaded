package powercrystals.minefactoryreloaded.tile.machine;

import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.core.GrindingDamage;
import powercrystals.minefactoryreloaded.core.HarvestAreaManager;
import powercrystals.minefactoryreloaded.core.IHarvestAreaContainer;
import powercrystals.minefactoryreloaded.core.ITankContainerBucketable;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryInventory;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryPowered;
import powercrystals.minefactoryreloaded.gui.container.ContainerFactoryPowered;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryPowered;

public class TileEntityGrinder extends TileEntityFactoryPowered implements ITankContainerBucketable, IHarvestAreaContainer
{
	public static final int DAMAGE = 500000; 
	private static Field recentlyHit;
	
	static
	{
		ArrayList<String> q = new ArrayList<String>();
		q.add("recentlyHit");
		q.addAll(Arrays.asList(ObfuscationReflectionHelper.remapFieldNames("net.minecraft.entity.EntityLivingBase", new String[] { "field_70718_bc" })));
		recentlyHit = ReflectionHelper.findField(EntityLivingBase.class, q.toArray(new String[q.size()]));
	}
	
	protected HarvestAreaManager _areaManager;
	protected FluidTank _tank;
	protected Random _rand;
	protected GrindingDamage _damageSource;
	
	protected TileEntityGrinder(Machine machine)
	{
		super(machine);
		_areaManager = new HarvestAreaManager(this, 2, 2, 1);
		_tank = new FluidTank(4 * FluidContainerRegistry.BUCKET_VOLUME);
		_rand = new Random();
	}
	
	public TileEntityGrinder()
	{
		this(Machine.Grinder);
		_damageSource = new GrindingDamage(this);
	}
	
	@Override
	public String getGuiBackground()
	{
		return "grinder.png";
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public GuiFactoryInventory getGui(InventoryPlayer inventoryPlayer)
	{
		return new GuiFactoryPowered(getContainer(inventoryPlayer), this);
	}
	
	@Override
	public ContainerFactoryPowered getContainer(InventoryPlayer inventoryPlayer)
	{
		return new ContainerFactoryPowered(this, inventoryPlayer);
	}

	public Random getRandom()
	{
		return _rand;
	}
	
	@Override
	protected boolean shouldPumpLiquid()
	{
		return true;
	}
	
	@Override
	public int getEnergyStoredMax()
	{
		return 32000;
	}
	
	@Override
	public int getWorkMax()
	{
		return 1;
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
	public HarvestAreaManager getHAM()
	{
		return _areaManager;
	}
	
	@Override
	public boolean activateMachine()
	{
		List<?> entities = worldObj.getEntitiesWithinAABB(EntityLivingBase.class, _areaManager.getHarvestArea().toAxisAlignedBB());
		
		entityList: for(Object o : entities)
		{
			EntityLivingBase e = (EntityLivingBase)o;
			if(e.isChild() || e.isEntityInvulnerable() || e.func_110143_aJ() <= 0)
			{
				continue;
			}

			for(Class<?> t : MFRRegistry.getGrinderBlacklist())
			{
				if(t.isInstance(e))
				{
					continue entityList;
				}
			}
			
			damageEntity(e);
			if(e.func_110143_aJ() <= 0)
			{
				_tank.fill(FluidRegistry.getFluidStack("mobessence", 100), true);
				setIdleTicks(20);
			}
			else
			{
				setIdleTicks(10);
			}
			return true;
		}
		setIdleTicks(getIdleTicksMax());
		return false;
	}
	
	protected void setRecentlyHit(EntityLivingBase entity, int t)
	{
		try
		{
			recentlyHit.set(entity, t);
		}
		catch(Throwable e)
		{
		}
	}
	
	protected void damageEntity(EntityLivingBase entity)
	{
		setRecentlyHit(entity, 100);
		entity.attackEntityFrom(_damageSource, DAMAGE);
	}
	
	public void dropsEntity(EntityLivingBase entity, ArrayList<EntityItem> drops)
	{
		setRecentlyHit(entity, 0);
		for (EntityItem item: drops)
		{
			doDrop(item.getEntityItem());
		}
	}
	
	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill)
	{
		return 0;
	}

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
	
	@Override
	public ConnectType canConnectItemPipe(ForgeDirection with)
	{
		return ConnectType.CONNECT;
	}
	
	@Override
	public boolean isItemValidForSlot(int slot, ItemStack itemstack)
	{
		return false;
	}
	
	@Override
	public boolean manageSolids()
	{
		return true;
	}
	
	@Override
	public boolean canRotate()
	{
		return true;
	}
}
