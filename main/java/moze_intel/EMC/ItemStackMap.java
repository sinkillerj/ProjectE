package moze_intel.EMC;

import java.util.LinkedHashMap;
import java.util.Map;

import moze_intel.utils.Utils;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

/**
 *	A modified LinkedHashMap object, for better use with {@link ItemStack}. 
 * @param <K> The type of the entries: must be an {@link ItemStack}
 * @param <V> The type of the value, can be any object
 * @author Moze_Intel
 */

public class ItemStackMap<K, V> extends LinkedHashMap<K, V> implements Map<K, V>
{
	public ItemStackMap()
	{
		super();
	}
	
	/**
	 * Push an entry in the map. Note that the key must be an {@link ItemStack}, or a {@link null} value will be passed.
	 */
	@Override
	public V put(K key, V value)
	{
		if (key instanceof ItemStack)
			return super.put(key, value);
		return null;
	}
	
	/**
	 * Get the value in the map with key equal to the parameter passed. Since the keys can only be ItemStacks, the parameter must be an ItemStack as well.
	 */
	@Override
	public V get(Object key)
	{
		if (key instanceof ItemStack)
		{
			ItemStack toFind = (ItemStack) key;
			for (java.util.Map.Entry<K, V> entry : this.entrySet())
			{
				ItemStack stack = (ItemStack) entry.getKey();
				if (AreStacksEqual(stack, toFind))
					return entry.getValue();
			}
		}
		return null;
	}
	
	public ItemStack getKey(Object obj)
	{
		if (obj instanceof ItemStack)
		{
			ItemStack toFind = (ItemStack) obj;
			for (K key : this.keySet())
			{
				ItemStack stack = (ItemStack) key;
				if (AreStacksEqual(stack, toFind))
					return stack;
			}
		}
		return null;
	}
	
	/**
	 * Searches through the map for the key. To be equal, ItemStacks don't have to have the same stack size.<br>
	 * ItemStacks with {@link OreDictionary#WILDCARD_VALUE} damage will be treated as the same.
	 */
	@Override
	public boolean containsKey(Object obj) 
	{
		if (obj instanceof ItemStack)
		{
			ItemStack toCompare = (ItemStack) obj;
			for (K k : this.keySet())
			{
				ItemStack stack = (ItemStack) k;
				if (AreStacksEqual(stack, toCompare)) return true;
			}
		}
        return false;
    }
	
	private boolean AreStacksEqual(ItemStack stack1, ItemStack stack2)
	{
		//TODO Implement enchantment detection
		//return Utils.AreItemStacksEqual(stack1, stack2);
		return Utils.AreItemStacksEqualIgnoreNBT(stack1, stack2);
	}
}
