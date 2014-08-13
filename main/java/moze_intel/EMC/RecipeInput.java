package moze_intel.EMC;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.item.ItemStack;

	
public class RecipeInput implements Iterable<IStack>
{
	private LinkedList<IStack> list;
		
	public RecipeInput()
	{
		list = new LinkedList<IStack>();
	}
		
	public RecipeInput(ItemStack[] inputs)
	{
		this();
			
		for (ItemStack stack : inputs)
		{
			if (stack != null)
			{
				list.add(new IStack(stack));
			}
		}
	}
		
	public RecipeInput(List<IStack> inputs)
	{
		this();
			
		list.addAll(inputs);
	}
		
	public void addToInputs(ItemStack stack)
	{
		list.add(new IStack(stack));
	}
		
	public LinkedList<IStack> getInputs()
	{
		return list;
	}

	@Override
	public Iterator<IStack> iterator()
	{
		return list.iterator();
	}
		
	@Override
	public String toString()
	{
		return list.toString();
	}

	@Override
	public int hashCode() 
	{
		return list.hashCode();
	}
		
	@Override
	public boolean equals(Object obj) 
	{
		if (obj instanceof RecipeInput)
		{
			return list.equals(((RecipeInput) obj).list);
		}
			
		return false;
	}
}
