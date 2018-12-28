package moze_intel.projecte.emc;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Objects;

public class SimpleStack
{
	public final ResourceLocation id;

	public SimpleStack(ResourceLocation id)
	{
		this.id = id;
	}
	
	public SimpleStack(ItemStack stack)
	{
		if (stack.isEmpty())
		{
			id = new ResourceLocation("minecraft", "air");
		}
		else
		{
			id = stack.getItem().getRegistryName();
		}
	}

	public boolean isValid()
	{
		return !id.equals(new ResourceLocation("minecraft", "air"));
	}

	public ItemStack toItemStack()
	{
		Item item = Item.REGISTRY.get(id);

		if (item != null)
		{
			return new ItemStack(item);
		}

		return ItemStack.EMPTY;
	}

	@Override
	public int hashCode() 
	{
		return id.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) 
	{
		if (obj instanceof SimpleStack)
		{
			SimpleStack other = (SimpleStack) obj;
			return Objects.equals(this.id, other.id);
		}
		
		return false;
	}
	
	@Override
	public String toString() 
	{
		return id.toString();
	}
}
