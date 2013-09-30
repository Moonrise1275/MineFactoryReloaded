package powercrystals.minefactoryreloaded.util;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import powercrystals.minefactoryreloaded.util.IInventoryManager;
import powercrystals.minefactoryreloaded.util.InventoryManager;
import powercrystals.minefactoryreloaded.util.BlockPosition;

public abstract class UtilInventory
{
	private static boolean bcIntegration = true;
	private static Class<?> bcIPipeTile = null;
	private static Method bcIPipeTileGetPipeType = null;
	private static Method bcIPipeTileInjectItem = null;
	private static Method bcIPipeTileIsPipeConnected = null;
	
	/**
	 * Searches from position x, y, z, checking for BC-compatible pipes in all directions.
	 * @return Map<ForgeDirection, TileEntry> specifying all found pipes and their directions.
	 */
	
	public static Map<ForgeDirection, TileEntity> findPipes(World world, int x, int y, int z)
	{
		return findPipes(world, x, y, z, ForgeDirection.VALID_DIRECTIONS);
	}
	
	/**
	 * Searches from position x, y, z, checking for BC-compatible pipes in each directiontocheck.
	 * @return Map<ForgeDirection, TileEntry> specifying all found pipes and their directions.
	 */
	
	public static Map<ForgeDirection, TileEntity> findPipes(World world, int x, int y, int z, ForgeDirection[] directionstocheck)
	{
		Map<ForgeDirection, TileEntity> pipes = new LinkedHashMap<ForgeDirection, TileEntity>();
		if (!bcIntegration) return pipes;
		
		try
		{
			if (bcIPipeTile == null)
			{
				bcIPipeTile = Class.forName("buildcraft.api.transport.IPipeTile");
				bcIPipeTileGetPipeType = bcIPipeTile.getMethod("getPipeType");
				bcIPipeTileInjectItem = bcIPipeTile.getMethod("injectItem", ItemStack.class, boolean.class, ForgeDirection.class);
				bcIPipeTileIsPipeConnected = bcIPipeTile.getMethod("isPipeConnected", ForgeDirection.class);
			}
			
			if (bcIPipeTile == null || bcIPipeTileGetPipeType == null || bcIPipeTileInjectItem == null || bcIPipeTileIsPipeConnected == null)
			{
				throw new Exception();
			}
			
			for(ForgeDirection direction : directionstocheck)
			{
				BlockPosition bp = new BlockPosition(x, y, z);
				bp.orientation = direction;
				bp.moveForwards(1);
				TileEntity te = world.getBlockTileEntity(bp.x, bp.y, bp.z);
				if(bcIPipeTile.isInstance(te))
				{
					Enum type = (Enum)bcIPipeTileGetPipeType.invoke(te);
					Boolean connect = (Boolean)bcIPipeTileIsPipeConnected.invoke(te, direction.getOpposite());
					if (type.name().equals("ITEM") && connect.booleanValue())
					{
						pipes.put(direction, te);
					}
				}
			}
		}
		catch (Exception e)
		{
			bcIPipeTile = null;
		}
		finally
		{			
			if (bcIPipeTile == null)
			{
				bcIPipeTile = null;
				bcIPipeTileGetPipeType = null;
				bcIPipeTileInjectItem = null;
				bcIPipeTileIsPipeConnected = null;
				bcIntegration = false;
				pipes.clear();
			}
			return pipes;
		}
	}
	
	/**
	 * Searches from position x, y, z, checking for inventories in all directions.
	 * @return Map<ForgeDirection, IInventory> specifying all found inventories and their directions.
	 */
	public static Map<ForgeDirection, IInventory> findChests(World world, int x, int y, int z)
	{
		return findChests(world, x, y, z, ForgeDirection.VALID_DIRECTIONS);
	}
	
	/**
	 * Searches from position x, y, z, checking for inventories in each directiontocheck.
	 * @return Map<ForgeDirection, IInventory> specifying all found inventories and their directions.
	 */
	public static Map<ForgeDirection, IInventory> findChests(World world, int x, int y, int z, ForgeDirection[] directionstocheck)
	{
		Map<ForgeDirection, IInventory> chests = new LinkedHashMap<ForgeDirection, IInventory>();
		for(ForgeDirection direction : directionstocheck)
		{
			BlockPosition bp = new BlockPosition(x, y, z);
			bp.orientation = direction;
			bp.moveForwards(1);
			TileEntity te = world.getBlockTileEntity(bp.x, bp.y, bp.z);
			if(te instanceof IInventory)
			{
				chests.put(direction, checkForDoubleChest(world, te, bp));
			}
		}
		return chests;
	}
	
	private static IInventory checkForDoubleChest(World world, TileEntity te, BlockPosition chestloc)
	{
		if(world.getBlockId(chestloc.x, chestloc.y, chestloc.z) == Block.chest.blockID)
		{
			for(BlockPosition bp : chestloc.getAdjacent(false))
			{
				if(world.getBlockId(bp.x, bp.y, bp.z) == Block.chest.blockID)
				{
					return new InventoryLargeChest("Large Chest", ((IInventory)te), ((IInventory)world.getBlockTileEntity(bp.x, bp.y, bp.z)));
				}
			}
		}
		return ((IInventory)te);
	}
	
	/**
	 * Drops an ItemStack, checking all directions for pipes > chests. DOESN'T drop items into the world.
	 * Example of this behavior: Cargo dropoff rail, item collector.
	 * @return	The remainder of the ItemStack. Whatever -wasn't- successfully dropped. 
	 */
	public static ItemStack dropStack(TileEntity from, ItemStack stack)
	{
		return dropStack(from.worldObj, new BlockPosition(from.xCoord, from.yCoord, from.zCoord), stack, ForgeDirection.VALID_DIRECTIONS, ForgeDirection.UNKNOWN);
	}

	/**
	 * Drops an ItemStack, checking all directions for pipes > chests. Drops items into the world.
	 * Example of this behavior: Harvesters, sludge boilers, etc.
	 * @param	airdropdirection	the direction that the stack may be dropped into air.
	 * @return	The remainder of the ItemStack. Whatever -wasn't- successfully dropped. 
	 */
	public static ItemStack dropStack(TileEntity from, ItemStack stack, ForgeDirection airdropdirection)
	{
		return dropStack(from.worldObj, new BlockPosition(from.xCoord, from.yCoord, from.zCoord), stack, ForgeDirection.VALID_DIRECTIONS, airdropdirection);
	}
	
	/**
	 * Drops an ItemStack, into chests > pipes > the world, but only in a single direction.
	 * Example of this behavior: Item Router, Ejector
	 * @param	dropdirection a -single- direction in which to check for pipes/chests
	 * @param	airdropdirection	the direction that the stack may be dropped into air.
	 * @return	The remainder of the ItemStack. Whatever -wasn't- successfully dropped. 
	 */
	public static ItemStack dropStack(TileEntity from, ItemStack stack, ForgeDirection dropdirection, ForgeDirection airdropdirection)
	{
		ForgeDirection[] dropdirections = {dropdirection};
		return dropStack(from.worldObj, new BlockPosition(from.xCoord, from.yCoord, from.zCoord), stack, dropdirections, airdropdirection);
	}
	
	/**
	 * Drops an ItemStack, checks pipes > chests > world in that order.
	 * @param	from				the TileEntity doing the dropping
	 * @param	stack				the ItemStack being dropped
	 * @param	dropdirections		directions in which stack may be dropped into chests or pipes
	 * @param	airdropdirection	the direction that the stack may be dropped into air. ForgeDirection.UNKNOWN or other invalid directions indicate that stack shouldn't be dropped into the world.
	 * @return	The remainder of the ItemStack. Whatever -wasn't- successfully dropped. 
	 */
	public static ItemStack dropStack(TileEntity from, ItemStack stack, ForgeDirection[] dropdirections, ForgeDirection airdropdirection)
	{
		return dropStack(from.worldObj, new BlockPosition(from.xCoord, from.yCoord, from.zCoord), stack, dropdirections, airdropdirection);
	}
	
	/**
	 * Drops an ItemStack, checks pipes > chests > world in that order.
	 * It generally shouldn't be necessary to call this explicitly. 
	 * @param	world				the worldObj
	 * @param	bp					the BlockPosition to drop from
	 * @param	stack				the ItemStack being dropped
	 * @param	dropdirections		directions in which stack may be dropped into chests or pipes
	 * @param	airdropdirection	the direction that the stack may be dropped into air. ForgeDirection.UNKNOWN or other invalid directions indicate that stack shouldn't be dropped into the world.
	 * @return	The remainder of the ItemStack. Whatever -wasn't- successfully dropped. 
	 */
	public static ItemStack dropStack(World world, BlockPosition bp, ItemStack stack, ForgeDirection[] dropdirections, ForgeDirection airdropdirection)
	{
		// (0) Sanity check. Don't bother dropping if there's nothing to drop, and never try to drop items on the client.
		if(stack == null || stack.stackSize == 0 || world.isRemote || stack.getItem() == null)
		{
			return stack;
		}
		stack = stack.copy();		
		
		// (1) Try to put stack in pipes that are in valid directions
		for(Entry<ForgeDirection, TileEntity> pipe : findPipes(world, bp.x, bp.y, bp.z, dropdirections).entrySet())
		{
			try
			{
				Integer amount = (Integer)bcIPipeTileInjectItem.invoke(pipe.getValue(), stack, true, pipe.getKey().getOpposite());
				stack.stackSize -= amount.intValue();
			}
			catch (Exception e)
			{
				bcIPipeTile = null;
				bcIPipeTileInjectItem = null;
				bcIntegration = false;
				break;
			}
			
			if (stack.stackSize == 0)
			{
				return null;
			}
		}
		
		// (2) Try to put stack in chests that are in valid directions
		for(Entry<ForgeDirection, IInventory> chest : findChests(world, bp.x, bp.y, bp.z, dropdirections).entrySet())
		{
			IInventoryManager manager = InventoryManager.create((IInventory)chest.getValue(), chest.getKey().getOpposite());
			stack = manager.addItem(stack);
			if(stack == null || stack.stackSize == 0)
			{
				return null;
			}
		}
		// (3) Having failed to put it in a chest or a pipe, drop it in the air if airdropdirection is a valid direction.
		bp.orientation = airdropdirection;
		bp.moveForwards(1);
		if(Arrays.asList(ForgeDirection.VALID_DIRECTIONS).contains(airdropdirection) && world.isAirBlock(bp.x, bp.y, bp.z))
		{
			bp.moveBackwards(1);
			dropStackInAir(stack, bp, world, airdropdirection);
			return null;
		}
		// (4) Is the stack still here? :( Better give it back.
		return stack;
	}
	
	private static void dropStackInAir(ItemStack stack, BlockPosition bp, World world, ForgeDirection towards)
	{
		float dropOffsetX = 0.0F;
		float dropOffsetY = 0.0F;
		float dropOffsetZ = 0.0F;
		
		switch(towards)
		{
			case UNKNOWN:
			case UP:
				dropOffsetX = 0.5F;
				dropOffsetY = 1.5F;
				dropOffsetZ = 0.5F;
				break;
			case DOWN:
				dropOffsetX = 0.5F;
				dropOffsetY = -0.75F;
				dropOffsetZ = 0.5F;
				break;
			case NORTH:
				dropOffsetX = 0.5F;
				dropOffsetY = 0.5F;
				dropOffsetZ = -0.5F;
				break;
			case SOUTH:
				dropOffsetX = 0.5F;
				dropOffsetY = 0.5F;
				dropOffsetZ = 1.5F;
				break;
			case EAST:
				dropOffsetX = 1.5F;
				dropOffsetY = 0.5F;
				dropOffsetZ = 0.5F;
				break;
			case WEST:
				dropOffsetX = -0.5F;
				dropOffsetY = 0.5F;
				dropOffsetZ = 0.5F;
				break;
			default:
				break;
				
		}
		
		EntityItem entityitem = new EntityItem(world, bp.x + dropOffsetX, bp.y + dropOffsetY, bp.z + dropOffsetZ, stack.copy());
		entityitem.motionX = 0.0D;
		if(towards != ForgeDirection.DOWN) entityitem.motionY = 0.3D;
		entityitem.motionZ = 0.0D;
		entityitem.delayBeforeCanPickup = 20;
		world.spawnEntityInWorld(entityitem);
		}

	public static ItemStack consumeItem(ItemStack stack)
	{
		if(stack.stackSize == 1)
		{
			if(stack.getItem().hasContainerItem())
			{
				return stack.getItem().getContainerItemStack(stack);
			}
			else
			{
				return null;
			}
		}
		else
		{
			return stack.splitStack(1);
		}	
	}

	public static void mergeStacks(ItemStack to, ItemStack from)
	{
		if(to == null || from == null) return;
		
		if(to.itemID != from.itemID || to.getItemDamage() != from.getItemDamage()) return;
		if(to.getTagCompound() != null || from.getTagCompound() != null) return;
		
		int amountToCopy = Math.min(to.getMaxStackSize() - to.stackSize, from.stackSize);
		to.stackSize += amountToCopy;
		from.stackSize -= amountToCopy;
	}

	public static boolean stacksEqual(ItemStack s1, ItemStack s2)
	{
		return stacksEqual(s1, s2, true);
	}

	public static boolean stacksEqual(ItemStack s1, ItemStack s2, boolean nbtSensitive)
	{
		if(s1 == null || s2 == null) return false;
		if(!s1.isItemEqual(s2)) return false;
		
		if(nbtSensitive)
		{
			if(s1.getTagCompound() == null && s2.getTagCompound() == null) return true;
			if(s1.getTagCompound() == null || s2.getTagCompound() == null) return false;
			return s1.getTagCompound().equals(s2.getTagCompound());
		}
		
		return true;
	}
}
