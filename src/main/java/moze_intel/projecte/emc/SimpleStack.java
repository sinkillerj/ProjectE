package moze_intel.projecte.emc;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;

public class SimpleStack
{
	public int id;
	public int damage;
	public int qnty;
	public NBTTagCompound nbt;

	public SimpleStack(int id, int qnty, int damage, NBTTagCompound nbt)
	{
		this.id = id;
		this.qnty = qnty;
		this.damage = damage;
		this.nbt = (nbt == null ? null : (NBTTagCompound) nbt.copy());
	}
	
	public SimpleStack(ItemStack stack)
	{
		if (stack == null)
		{
			id = -1;
			nbt = null;
		}
		else
		{
			id = Item.itemRegistry.getIDForObject(stack.getItem());
			damage = stack.getItemDamage();
			qnty = stack.stackSize;
			nbt = (stack.stackTagCompound == null ? null : (NBTTagCompound) stack.stackTagCompound.copy());
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
				ItemStack stack = new ItemStack(Item.getItemById(id), qnty, damage);
				stack.stackTagCompound = nbt;
				return stack;
			}
		}

		return null;
	}

	public SimpleStack copy()
	{
		return new SimpleStack(id, qnty, damage, nbt);
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
				//return this.id == other.id;
				return this.qnty == other.qnty && this.id == other.id;
			}

			//return this.id == other.id && this.damage == other.damage;
			if(this.nbt != null && other.nbt != null){
				return this.id == other.id && this.qnty == other.qnty && this.damage == other.damage && this.nbt.equals(other.nbt);
			} else if (this.nbt == null && other.nbt == null){ 
				return this.id == other.id && this.qnty == other.qnty && this.damage == other.damage;
			} else {
				return false;
			}
		}
		
		return false;
	}
	
	@Override
	public String toString() 
	{
		Object obj = Item.itemRegistry.getObjectById(id);
		
		if (obj != null)
		{
			return Item.itemRegistry.getNameForObject(obj) + " " + qnty + " " + damage + " " + nbt.toString();
		}
		
		return "id:" + id + " damage:" + damage + " qnty:" + qnty + " nbt:" + nbt.toString();
	}
}
