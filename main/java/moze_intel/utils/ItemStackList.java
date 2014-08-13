package moze_intel.utils;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class ItemStackList implements Iterable<String> 
{
	private LinkedList<String> list;
	
	public ItemStackList()
	{
		list = new LinkedList();
	}
	
	public ItemStackList(ItemStack[] array)
	{
		list = new LinkedList();
		
		for (ItemStack stack : array)
		{
			if (stack == null)
			{
				continue;
			}
			
			list.add(getName(stack));
		}
	}
	
	public ItemStackList(Collection<ItemStack> c)
	{
		list = new LinkedList();
		
		for (ItemStack stack : c)
		{
			if (stack == null)
			{
				continue;
			}
			
			list.add(getName(stack));
		}
	}
	
	public boolean add(ItemStack stack)
	{
		return list.add(getName(stack));
	}
	
	public boolean add(String value)
	{
		return list.add(value);
	}
	
	public void add(int index, ItemStack stack)
	{
		list.add(index, getName(stack));
	}
	
	public void add(int index, String value)
	{
		list.add(index, value);
	}
	
	public boolean addAll(Collection <? extends String> c) 
	{
		return list.addAll(c);
	}
	
	public boolean contains(ItemStack stack)
	{
		return list.contains(getName(stack));
	}
	
	public boolean contains(String value)
	{
		return list.contains(value);
	}
	
	public boolean containsAll(Collection<?> c)
	{
		return list.containsAll(c);
	}
	
	public String removeFirstElement()
	{
		return list.remove();
	}
	
	public String remove(int index)
	{
		return list.remove(index);
	}
	
	public boolean remove(ItemStack stack)
	{
		return list.remove(getName(stack));
	}
	
	public boolean remove(String value)
	{
		return list.remove(value);
	}
	
	public boolean removeAll(Collection<?> c)
	{
		return list.removeAll(c);
	}
	
	public int getSize()
	{
		return list.size();
	}
	
	public void clear()
	{
		list.clear();
	}
	
	public Object clone()
	{
		return list.clone();
	}
	
	public String[] toArray()
	{
		return list.toArray(new String[list.size()]);
	}
	
	@Override
	public Iterator<String> iterator() 
	{
		return list.iterator();
	}
	
	@Override
	public boolean equals(Object other)
	{
		return list.equals(other);
	}
	
	@Override
	public int hashCode()
	{
		return list.hashCode();
	}
	
	@Override
	public String toString()
	{
		return list.toString();
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
