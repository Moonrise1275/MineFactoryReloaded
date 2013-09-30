package powercrystals.minefactoryreloaded.util;

import net.minecraft.item.ItemStack;

public class ItemIdentifier
{
	public int itemId;
	public int itemMeta;
	
	@Override
	public boolean equals(Object obj)
	{
		if(!(obj instanceof ItemIdentifier))
		{
			return false;
		}
		ItemIdentifier ii = (ItemIdentifier)obj;
		return ii.itemId == this.itemId && ii.itemMeta == this.itemMeta;
	}
	
	@Override
	public int hashCode()
	{
		return (itemId & 0xFFFF) | (itemMeta << 16);
	}
	
	public static ItemIdentifier fromItemStack(ItemStack stack)
	{
		ItemIdentifier ii = new ItemIdentifier();
		ii.itemId = stack.itemID;
		ii.itemMeta = stack.getItemDamage();
		return ii;
	}
}
