package moze_intel.projecte.emc.json;

import moze_intel.projecte.PECore;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.HashSet;
import java.util.Set;

public class NSSItem implements NormalizedSimpleStack {
	static final Set<String> seenIds = new HashSet<>();

	public final String itemName;

	NSSItem(String itemName) {
		this.itemName = itemName;
	}

	public static NormalizedSimpleStack create(Block block) {
		return create(block.getRegistryName());
	}

	public static NormalizedSimpleStack create(ItemStack stack) {
		if (stack.isEmpty()) return null;
		return create(stack.getItem());
	}

	public static NormalizedSimpleStack create(Item item) {
		return create(item.getRegistryName());
	}

	private static NormalizedSimpleStack create(ResourceLocation uniqueIdentifier) {
		return create(uniqueIdentifier.toString());
	}

	public static NormalizedSimpleStack create(String itemName) {
		NSSItem normStack;
		try {
			normStack = new NSSItem(itemName);
		} catch (Exception e) {
			PECore.LOGGER.fatal("Could not create NSSItem: {}", e.getMessage());
			return null;
		}
		seenIds.add(itemName);
		return normStack;
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
		return itemName;
	}

	@Override
	public String toString() {
		return itemName;
	}
}
