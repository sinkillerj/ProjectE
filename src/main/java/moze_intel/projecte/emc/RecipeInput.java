package moze_intel.projecte.emc;

import net.minecraft.item.ItemStack;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

	
public class RecipeInput implements Iterable<SimpleStack>
{
	private LinkedList<SimpleStack> list;
		
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
	}
}
