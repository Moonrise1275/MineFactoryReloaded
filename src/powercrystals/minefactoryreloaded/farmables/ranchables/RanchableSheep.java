package powercrystals.minefactoryreloaded.farmables.ranchables;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import powercrystals.minefactoryreloaded.api.IFactoryRanchable;

public class RanchableSheep implements IFactoryRanchable
{
	@Override
	public Class<?> getRanchableEntity()
	{
		return EntitySheep.class;
	}
	
	@Override
	public List<Object> ranch(World world, EntityLivingBase entity, IInventory rancher)
	{
		EntitySheep s = (EntitySheep)entity;
		
		if(s.getSheared() || s.getGrowingAge() < 0)
		{
			return null;
		}
		
		List<Object> stacks = new LinkedList<Object>();
		stacks.add(new ItemStack(Block.cloth, 1, s.getFleeceColor()));
		s.setSheared(true);
		
		return stacks;
	}
}
