package moze_intel.projecte.emc;

import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

	
public class RecipeInput implements Iterable<Object>
{
	private final List<Object> list;

	public RecipeInput()
	{
		list = new ArrayList<Object>();
	}

	public void addToInputs(ItemStack stack)
	{
		SimpleStack simpleStack = new SimpleStack(stack);

		if (simpleStack.isValid())
		{
			list.add(simpleStack);
		}
	}

	public void addToInput(ArrayList<ItemStack> list)
	{
		ArrayList<SimpleStack> toAdd = new ArrayList<SimpleStack>();

		for (ItemStack stack : list)
		{
			if (stack == null)
			{
				continue;
			}

			SimpleStack s = new SimpleStack(stack);

			if (s.isValid())
			{
				toAdd.add(s);
			}
		}

		if (!toAdd.isEmpty())
		{
			this.list.add(toAdd);
		}
	}

	@Override
	public Iterator<Object> iterator()
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

	/*private LinkedList<SimpleStack> list;
		
	public RecipeInput()
	{
		list = new LinkedList<SimpleStack>();
	}
		
	public RecipeInput(ItemStack[] inputs)
	{
		this();
			
		for (ItemStack stack : inputs)
		{
			if (stack != null)
			{
				list.add(new SimpleStack(stack));
			}
		}
	}
		
	public RecipeInput(List<SimpleStack> inputs)
	{
		this();
			
		list.addAll(inputs);
	}
		
	public void addToInputs(ItemStack stack)
	{
		SimpleStack simpleStack = new SimpleStack(stack);

		if (simpleStack.isValid())
		{
			list.add(new SimpleStack(stack));
		}
	}
		
	public LinkedList<SimpleStack> getInputs()
	{
		return list;
	}
	
	@Override
	public Iterator<SimpleStack> iterator()
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
	}*/
}
