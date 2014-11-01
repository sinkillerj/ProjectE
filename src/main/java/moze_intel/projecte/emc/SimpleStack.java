package moze_intel.projecte.emc;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class SimpleStack
{
	public int id;
	public int damage;
	public int qnty;

	public SimpleStack(int id, int damage, int qnty)
	{
		this.id = id;
        this.damage = damage;
		this.qnty = qnty;
	}
	
	public SimpleStack(ItemStack stack)
	{
        if (stack == null)
        {
            id = -1;
        }
        else
        {
            id = Item.itemRegistry.getIDForObject(stack.getItem());
            damage = stack.getItemDamage();
            qnty = stack.stackSize;
        }
	}

    public boolean isValid()
    {
        return id != -1;
    }

    public ItemStack toItemStack()
    {
        if (isValid())
        {
            Item item = Item.getItemById(id);

            if (item != null)
            {
                return new ItemStack(Item.getItemById(id), qnty, damage);
            }
        }

        return null;
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
