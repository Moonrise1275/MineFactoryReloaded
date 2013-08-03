package powercrystals.minefactoryreloaded.item;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.api.ISyringe;
import powercrystals.minefactoryreloaded.gui.MFRCreativeTab;

public abstract class ItemSyringe extends ItemFactory implements ISyringe
{
	public ItemSyringe(int id)
	{
		super(id);
		setMaxStackSize(1);
		setCreativeTab(MFRCreativeTab.tab);
	}
	// itemInteractionForEntity
	@Override
	public boolean func_111207_a(ItemStack s, EntityPlayer player, EntityLivingBase e)
	{
		if(!e.worldObj.isRemote && canInject(e.worldObj, e, s))
		{
			if(inject(e.worldObj, e, s))
			{
				s.itemID = MineFactoryReloadedCore.syringeEmptyItem.itemID;
				return true;
			}
		}
		
		return false;
	}
}
