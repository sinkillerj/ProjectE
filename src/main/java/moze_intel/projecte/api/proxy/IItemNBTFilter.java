package moze_intel.projecte.api.proxy;

import java.util.Collection;

import net.minecraft.item.ItemStack;

/**Interface that allows for filtering certain item NBT components.*/

public interface IItemNBTFilter {
	
	/**If the itemStack provided is one of the items this plugin filters.
	 * @param input The itemstack to be filtered
	 * @return true if the item can be filtered by this plugin */
	public boolean canFilterStack(ItemStack input);
	
	
	/**Removes plugin-defined NBT tags from the ItemStack. The input must not be modified.
	 * Use input.copy() and work on that.
	 * @param input The itemstack to be filtered
	 * @return a new ItemStack with the target NBT Tags removed or added */
	public ItemStack getFilteredItemStack(final ItemStack input);
	
	/**Method used to determine if an ItemStack will be passed on to the plugin
	 * String should be a either:
	 * - A ResourceLocator for an item, optionally followed by "|" and a meta value or "*" for wildcard;
	 * - A mod name followed by ":*" to filter any item from that mod ("minecraft:*" for only vanilla items);
	 * - "*" to filter any item from any mod
	 * @return A colection of the above-formatted strings indicating where the plugin operates.*/
	public Collection<String> allowedItems();
}
