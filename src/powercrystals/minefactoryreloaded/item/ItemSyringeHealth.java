package powercrystals.minefactoryreloaded.item;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import powercrystals.minefactoryreloaded.setup.MFRConfig;

public class ItemSyringeHealth extends ItemSyringe
{
	public ItemSyringeHealth()
	{
		super(MFRConfig.syringeHealthItemId.getInt());
	}
	
	@Override
	public boolean canInject(World world, EntityLivingBase entity, ItemStack syringe)
	{
		return entity.func_110143_aJ() < entity.func_110138_aP();
	}
	
	@Override
	public boolean inject(World world, EntityLivingBase entity, ItemStack syringe)
	{
		entity.heal(5);
		return true;
	}
}
