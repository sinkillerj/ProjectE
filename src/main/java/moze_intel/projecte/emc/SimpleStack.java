package moze_intel.projecte.emc;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Objects;

public class SimpleStack
{
	public final ResourceLocation id;
	public final int damage;

	public SimpleStack(ResourceLocation id, int damage)
	{
		this.id = id;
		this.damage = damage;
	}
	
	public SimpleStack(ItemStack stack)
	{
		if (stack.isEmpty())
		{
			id = new ResourceLocation("minecraft", "air");
			damage = 0;
		}
		else
		{
			id = stack.getItem().getRegistryName();
			damage = stack.getItemDamage();
		}
	}

	public SimpleStack withMeta(int meta)
	{
		return new SimpleStack(id, meta);
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
				return new ItemStack(item, 1, damage);
			}
		}

		return ItemStack.EMPTY;
	}

	@Override
	public int hashCode() 
	{
		int hash = 31 * id.hashCode();
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
				return Objects.equals(this.id, other.id);
			}

			return Objects.equals(this.id, other.id) && this.damage == other.damage;
		}
		
		return false;
	}
	
	@Override
	public String toString() 
	{
		Item obj = Item.REGISTRY.getObject(id);
		
		if (obj != null)
		{
			return id + " " + damage;
		}
		
		return "id:" + id + " damage:" + damage;
	}
}
