package powercrystals.minefactoryreloaded.farmables.ranchables;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import powercrystals.minefactoryreloaded.api.IFactoryRanchable;

public class RanchableSquid implements IFactoryRanchable
{
	@Override
	public Class<?> getRanchableEntity()
	{
		return EntitySquid.class;
	}
	
	@Override
	public List<Object> ranch(World world, EntityLivingBase entity, IInventory rancher)
	{
		List<Object> drops = new LinkedList<Object>();
		drops.add(new ItemStack(Item.dyePowder, 1, 0));
		return drops;
	}
	
}
