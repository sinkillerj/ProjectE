package moze_intel.EMC;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class IStack
{
	public int id;
	public int damage;
	public int qnty;
	
	public IStack(int id)
	{
		this.id = id;
	}
	
	public IStack(int id, int damage)
	{
		this(id);
		this.damage = damage;
	}
	
	public IStack(int id, int damage, int qnty)
	{
		this(id, damage);
		this.qnty = qnty;
	}
	
	public IStack(ItemStack stack)
	{
		//this(net.minecraft.item.Item.getIdFromItem(stack.getItem()), stack.getItemDamage(), stack.stackSize);
		this(Item.itemRegistry.getIDForObject(stack.getItem()), stack.getItemDamage(), stack.stackSize);
	}
	
	public IStack(ItemStack stack, int qnty)
	{
		//this(net.minecraft.item.Item.getIdFromItem(stack.getItem()), stack.getItemDamage(), qnty);
		this(Item.itemRegistry.getIDForObject(stack.getItem()), stack.getItemDamage(), stack.stackSize);
	}
	
	@Override
	public int hashCode() 
	{
		/*if (this.damage == OreDictionary.WILDCARD_VALUE)
		{
			return id;
		}
		
		return (id + ":" + damage).hashCode();*/
		
		return id;
	}
	
	@Override
	public boolean equals(Object obj) 
	{
		if (obj instanceof IStack)
		{
			IStack other = (IStack) obj;
			 
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
		return "id:"+id +" damage:"+ damage+" qnty: "+qnty;
	}
}
