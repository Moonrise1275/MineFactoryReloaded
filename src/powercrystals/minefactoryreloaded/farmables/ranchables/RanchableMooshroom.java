package powercrystals.minefactoryreloaded.farmables.ranchables;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityMooshroom;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import powercrystals.minefactoryreloaded.util.IInventoryManager;
import powercrystals.minefactoryreloaded.util.InventoryManager;
import powercrystals.minefactoryreloaded.api.IFactoryRanchable;

public class RanchableMooshroom implements IFactoryRanchable {
	
	@Override
	public Class<?> getRanchableEntity()
	{
		return EntityMooshroom.class;
	}
	
	@Override
	public List<Object> ranch(World world, EntityLivingBase entity, IInventory rancher)
	{
		List<Object> drops = new LinkedList<Object>();
		
		IInventoryManager manager = InventoryManager.create(rancher, ForgeDirection.UP);
		int bowlIndex = manager.findItem(new ItemStack(Item.bowlEmpty));
		if(bowlIndex >= 0)
		{
			drops.add(new ItemStack(Item.bowlSoup));
			rancher.decrStackSize(bowlIndex, 1);
			return drops;
		}
		
		int bucketIndex = manager.findItem(new ItemStack(Item.bucketEmpty));
		if(bucketIndex >= 0)
		{
			drops.add(new ItemStack(Item.bucketMilk));
			rancher.decrStackSize(bucketIndex, 1);
		}
		else
		{
			drops.add(FluidRegistry.getFluidStack("mushroomsoup", FluidContainerRegistry.BUCKET_VOLUME));
		}
		
		return drops;
	}
}
