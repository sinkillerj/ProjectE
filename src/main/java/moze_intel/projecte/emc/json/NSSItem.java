package moze_intel.projecte.emc.json;

import moze_intel.projecte.PECore;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;

import java.util.HashSet;
import java.util.Set;

public class NSSItem implements NormalizedSimpleStack {
	static final Set<String> seenIds = new HashSet<>();

	public final String itemName;
	public final int damage;

	NSSItem(String itemName, int damage) {
		this.itemName = itemName;
		this.damage = damage;
	}

	public static NormalizedSimpleStack create(Block block) {
		return create(block, 0);
	}

	public static NormalizedSimpleStack create(Block block, int meta) {
		return create(block.getRegistryName(), meta);
	}

	public static NormalizedSimpleStack create(ItemStack stack) {
		if (stack.isEmpty()) return null;
		return create(stack.getItem(), stack.getItemDamage());
	}

	public static NormalizedSimpleStack create(Item item) {
		return create(item, 0);
	}

	private static NormalizedSimpleStack create(Item item, int meta) {

		return create(item.getRegistryName(), meta);
	}

	private static NormalizedSimpleStack create(ResourceLocation uniqueIdentifier, int damage) {
		if (uniqueIdentifier == null) return null;
		return create(uniqueIdentifier.toString(), damage);
	}

	public static NormalizedSimpleStack create(String itemName, int damage) {
		NSSItem normStack;
		try {
			normStack = new NSSItem(itemName, damage);
		} catch (Exception e) {
			PECore.LOGGER.fatal("Could not create NSSItem: {}", e.getMessage());
			return null;
		}
		seenIds.add(itemName);
		return normStack;
	}

	@Override
	public int hashCode() {
		return itemName.hashCode() ^ damage;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof NSSItem) {
			NSSItem other = (NSSItem) obj;

			return this.itemName.equals(other.itemName) && this.damage == other.damage;
		}

		return false;
	}

	@Override
	public String json() {
		return String.format("%s|%s", itemName, damage == OreDictionary.WILDCARD_VALUE ? "*" : damage);
	}

	@Override
	public String toString() {
		return String.format("%s:%s", itemName, damage == OreDictionary.WILDCARD_VALUE ? "*" : damage);
	}
}
