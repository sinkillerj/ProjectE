package moze_intel.projecte.emc.json;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class NSSItem implements NormalizedSimpleStack {
	public final ResourceLocation itemName;

	public NSSItem(ItemStack stack)
	{
		this(stack.getItem().getRegistryName());
		if (stack.isEmpty())
		{
			throw new IllegalArgumentException("Can't make NSSItem with empty stack");
		}
	}

	public NSSItem(IForgeRegistryEntry e)
	{
		this(e.getRegistryName());
	}

	public NSSItem(ResourceLocation itemName) {
		this.itemName = itemName;
	}

	@Override
	public int hashCode() {
		return itemName.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof NSSItem) {
			NSSItem other = (NSSItem) obj;

			return this.itemName.equals(other.itemName);
		}

		return false;
	}

	@Override
	public String json() {
		return itemName.toString();
	}

	@Override
	public String toString() {
		return itemName.toString();
	}
}
