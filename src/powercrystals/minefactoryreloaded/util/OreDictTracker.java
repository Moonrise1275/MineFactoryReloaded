package powercrystals.minefactoryreloaded.util;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.minecraft.item.ItemStack;

public final class OreDictTracker
{
	private static Map<ItemIdentifier, List<String>> _oreDictEntries = new HashMap<ItemIdentifier, List<String>>();
	
	public static void registerOreDictEntry(ItemStack stack, String name)
	{
		ItemIdentifier ii = ItemIdentifier.fromItemStack(stack);
		if(_oreDictEntries.get(ii) == null)
		{
			_oreDictEntries.put(ii, new LinkedList<String>());
		}
		_oreDictEntries.get(ii).add(name);
	}
	
	public static List<String> getNamesFromItem(ItemStack stack)
	{
		return _oreDictEntries.get(ItemIdentifier.fromItemStack(stack));
	}
}
