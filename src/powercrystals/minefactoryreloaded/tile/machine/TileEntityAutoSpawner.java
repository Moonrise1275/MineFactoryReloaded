package powercrystals.minefactoryreloaded.tile.machine;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.ILiquidTank;
import net.minecraftforge.liquids.LiquidContainerRegistry;
import net.minecraftforge.liquids.LiquidDictionary;
import net.minecraftforge.liquids.LiquidStack;
import net.minecraftforge.liquids.LiquidTank;
import powercrystals.minefactoryreloaded.core.ITankContainerBucketable;
import powercrystals.minefactoryreloaded.gui.client.GuiAutoSpawner;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryInventory;
import powercrystals.minefactoryreloaded.gui.container.ContainerAutoSpawner;
import powercrystals.minefactoryreloaded.item.ItemSafariNet;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryPowered;

public class TileEntityAutoSpawner extends TileEntityFactoryPowered implements ITankContainerBucketable
{
	private static final int _spawnRange = 4;
	private LiquidTank _tank;
	private boolean _spawnExact = false;
	
	public TileEntityAutoSpawner()
	{
		super(512);
		_tank = new LiquidTank(LiquidContainerRegistry.BUCKET_VOLUME * 4);
	}
	
	public boolean getSpawnExact()
	{
		return _spawnExact;
	}
	
	public void setSpawnExact(boolean spawnExact)
	{
		_spawnExact = spawnExact;
	}
	
	@Override
	public String getGuiBackground()
	{
		return "autospawner.png";
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public GuiFactoryInventory getGui(InventoryPlayer inventoryPlayer)
	{
		return new GuiAutoSpawner(getContainer(inventoryPlayer), this);
	}
	
	@Override
	public ContainerAutoSpawner getContainer(InventoryPlayer inventoryPlayer)
	{
		return new ContainerAutoSpawner(this, inventoryPlayer);
	}
	
	@Override
	public int getSizeInventory()
	{
		return 1;
	}

	@Override
	public String getInvName()
	{
		return "Auto-Spawner";
	}

	@Override
	protected boolean activateMachine()
	{
		if(_inventory[0] == null || !(_inventory[0].getItem() instanceof ItemSafariNet) || _inventory[0].getTagCompound() == null || ItemSafariNet.isSingleUse(_inventory[0]))
		{
			setWorkDone(0);
			return false;	
		}
		if(getWorkDone() < getWorkMax())
		{
			if(_tank.getLiquid() != null && _tank.getLiquid().amount >= 10)
			{
				_tank.getLiquid().amount -= 10;
				setWorkDone(getWorkDone() + 1);
				return true;
			}
			else
			{
				return false;
			}
		}
		else
		{
			Entity spawnedEntity = null;
			try
			{
				spawnedEntity = EntityList.createEntityByName((String)EntityList.classToStringMapping.get(Class.forName(_inventory[0].getTagCompound().getString("_class"))), worldObj);
			}
			catch(ClassNotFoundException e)
			{
				e.printStackTrace();
				return false;
			}
			
			if(!(spawnedEntity instanceof EntityLiving))
			{
				return false;
			}
			
			EntityLiving spawnedLiving = (EntityLiving)spawnedEntity;
			spawnedLiving.initCreature();

			if(_spawnExact)
			{
				NBTTagCompound tag = (NBTTagCompound)_inventory[0].getTagCompound().copy();
				tag.removeTag("Equipment");
				spawnedLiving.readEntityFromNBT(tag);
			}
			
			double x = xCoord + (worldObj.rand.nextDouble() - worldObj.rand.nextDouble()) * _spawnRange;
			double y = yCoord + worldObj.rand.nextInt(3) - 1;
			double z = zCoord + (worldObj.rand.nextDouble() - worldObj.rand.nextDouble()) * _spawnRange;
			
			spawnedLiving.setLocationAndAngles(x, y, z, worldObj.rand.nextFloat() * 360.0F, 0.0F);
			
			if(!this.worldObj.checkIfAABBIsClear(spawnedLiving.boundingBox) ||
					!this.worldObj.getCollidingBoundingBoxes(spawnedLiving, spawnedLiving.boundingBox).isEmpty() ||
					this.worldObj.isAnyLiquid(spawnedLiving.boundingBox))
			{
				return false;
			}

			worldObj.spawnEntityInWorld(spawnedLiving);
			worldObj.playAuxSFX(2004, this.xCoord, this.yCoord, this.zCoord, 0);

			spawnedLiving.spawnExplosionParticle();
			setWorkDone(0);
			return true;
		}
	}

	@Override
	public int getEnergyStoredMax()
	{
		return 32000;
	}

	@Override
	public int getWorkMax()
	{
		return _spawnExact ? 50 : 15;
	}

	@Override
	public int getIdleTicksMax()
	{
		return 200;
	}
	
	@Override
	public ILiquidTank getTank()
	{
		return _tank;
	}

	@Override
	public int fill(ForgeDirection from, LiquidStack resource, boolean doFill)
	{
		if(resource == null || resource.itemID != LiquidDictionary.getLiquid("mobEssence", 1000).itemID)
		{
			return 0;
		}
		return _tank.fill(resource, doFill);
	}

	@Override
	public int fill(int tankIndex, LiquidStack resource, boolean doFill)
	{
		return fill(ForgeDirection.UNKNOWN, resource, doFill);
	}

	@Override
	public boolean allowBucketFill()
	{
		return true;
	}
	
	@Override
	public LiquidStack drain(ForgeDirection from, int maxDrain, boolean doDrain)
	{
		return null;
	}

	@Override
	public LiquidStack drain(int tankIndex, int maxDrain, boolean doDrain)
	{
		return null;
	}

	@Override
	public ILiquidTank[] getTanks(ForgeDirection direction)
	{
		return new ILiquidTank[] { _tank };
	}

	@Override
	public ILiquidTank getTank(ForgeDirection direction, LiquidStack type)
	{
		return _tank;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound)
	{
		super.readFromNBT(nbttagcompound);
		_spawnExact = nbttagcompound.getBoolean("spawnExact");
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound)
	{
		super.writeToNBT(nbttagcompound);
		nbttagcompound.setBoolean("spawnExact", _spawnExact);
	}
}