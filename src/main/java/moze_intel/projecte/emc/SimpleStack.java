package moze_intel.projecte.emc;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Objects;

public class SimpleStack
{
	public final ResourceLocation id;
	public int damage;
	public int qnty;

	public SimpleStack(ResourceLocation id, int qnty, int damage)
	{
		this.id = id;
		this.qnty = qnty;
		this.damage = damage;
	}
	
	public SimpleStack(ItemStack stack)
	{
		if (stack == null)
		{
			id = new ResourceLocation("minecraft", "air");
		}
		else
		{
			id = stack.getItem().getRegistryName();
			damage = stack.getItemDamage();
			qnty = stack.stackSize;
		}
	}

	public boolean isValid()
	{
		return !id.equals(new ResourceLocation("minecraft", "air"));
	}

	public ItemStack toItemStack()
	{
		if (isValid())
		{
			Item item = Item.REGISTRY.getObject(id);

			if (item != null)
			{
				return new ItemStack(item, qnty, damage);
			}
		}

		return null;
	}

	public SimpleStack copy()
	{
		return new SimpleStack(id, qnty, damage);
	}

	@Override
	public int hashCode() 
	{
		int hash = 31 * qnty << 4 ^ id.hashCode();
		if (this.damage == OreDictionary.WILDCARD_VALUE)
			hash = hash * 57 ^ this.damage;
		return hash;
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
				return this.qnty == other.qnty && Objects.equals(this.id, other.id);
			}

			//return this.id == other.id && this.damage == other.damage;
			return Objects.equals(this.id, other.id) && this.qnty == other.qnty && this.damage == other.damage;
		}
		
		return false;
	}
	
	@Override
	public String toString() 
	{
		Item obj = Item.REGISTRY.getObject(id);
		
		if (obj != null)
		{
			return id + " " + qnty + " " + damage;
		}
		
		return "id:" + id + " damage:" + damage + " qnty:" + qnty;
	}
}
