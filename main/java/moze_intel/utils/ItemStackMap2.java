package moze_intel.utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class ItemStackMap2 <T>
{
	private LinkedHashMap<String, T> map;
	
	public ItemStackMap2()
	{
		map = new LinkedHashMap();
	}
	
	public void addMapping(ItemStack key, T value)
	{
		map.put(getName(key), value);
	}
	
	public void addMapping(String key, T value)
	{
		map.put(key, value);
	}
	
	public T removeMapping(ItemStack stack)
	{
		return map.remove(getName(stack));
	}
	
	public T removeMapping(String key)
	{
		return map.remove(key);
	}
	
	public void clearMap()
	{
		map.clear();
	}
	
	public boolean isEmpty()
	{
		return map.isEmpty();
	}
	
	public boolean containsKey(ItemStack stack)
	{
		return map.containsKey(getName(stack));
	}
	
	public boolean containsKey(String key)
	{
		return map.containsKey(key);
	}
	
	public boolean containsValue(T value)
	{
		return map.containsValue(value);
	}
	
	public T getValue(ItemStack stack)
	{
		return map.get(getName(stack));
	}
	
	public T getValue(String key)
	{
		return map.get(key);
	}
	
	public int getSize()
	{
		return map.size();
	}
	
	public Set<String> getKeys()
	{
		return map.keySet();
	}
	
	public String getMatchingKey(String key)
	{
		List list = Arrays.asList(getKeys().toArray());
		return (String) list.get(list.indexOf(key));
	}
	
	public void putAll(Map<? extends String, ? extends T> m)
	{
		map.putAll(m);
	}
	
	public Object clone()
	{
		return map.clone();
	}
	
	public Collection<T> getValues()
	{
		return map.values();
	}
	
	public Set<Entry<String, T>> getEntrySet()
	{
		return map.entrySet();
	}
	
	@Override
	public boolean equals(Object other)
	{
		return map.equals(other);
	}
	
	@Override
	public int hashCode()
	{
		return map.hashCode();
	}
	
	@Override
	public String toString()
	{
		return map.toString();
	}
	
	private String getName(ItemStack stack)
	{
		if (stack.getItemDamage() == OreDictionary.WILDCARD_VALUE)
		{
			stack.setItemDamage(0);
		}
		
		String oreName = Utils.getOreDictionaryName(stack);
		
		if (oreName.equals("Unknown"))
		{
			//return stack.getDisplayName();
			return stack.getUnlocalizedName();
		}
		
		return oreName;
	}
}
