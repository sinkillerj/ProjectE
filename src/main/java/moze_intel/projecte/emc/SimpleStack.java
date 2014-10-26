package moze_intel.projecte.emc;

import moze_intel.projecte.utils.Utils;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class SimpleStack
{
	public int id;
	public int damage;
	public int qnty;
	
	public SimpleStack(int id)
	{
		this.id = id;
	}
	
	public SimpleStack(int id, int damage)
	{
		this(id);
		this.damage = damage;
	}
	
	public SimpleStack(int id, int damage, int qnty)
	{
		this(id, damage);
		this.qnty = qnty;
	}
	
	public SimpleStack(ItemStack stack)
	{
		this(Item.itemRegistry.getIDForObject(stack.getItem()), stack.getItemDamage(), stack.stackSize);
	}
	
	public SimpleStack(ItemStack stack, int qnty)
	{
		this(Item.itemRegistry.getIDForObject(stack.getItem()), stack.getItemDamage(), stack.stackSize);
	}
	
	@Override
	public int hashCode() 
	{
		return id;
	}
	
	@Override
	public boolean equals(Object obj) 
	{
		if (obj instanceof SimpleStack)
		{
			SimpleStack other = (SimpleStack) obj;
			 
			if (this.damage == OreDictionary.WILDCARD_VALUE || other.damage == OreDictionary.WILDCARD_VALUE)
			{
				return this.id == other.id;
			}
			
			return this.id == other.id && this.damage == other.damage;
		}
		
		return false;
	}
	
	@Override
	public String toString() 
	{
		Object obj = Item.itemRegistry.getObjectById(id);
		
		if (obj != null)
		{
			return Item.itemRegistry.getNameForObject(obj);
		}
		
		return "id:" + id + " damage:" + damage + " qnty:" + qnty;
	}
}
