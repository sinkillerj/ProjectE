package moze_intel.projecte.api.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * This interfaces specifies items that perform a specific function every tick when inside an Alchemical Bag, on a player
 *
 * @author williewillus
 */
public interface IAlchBagItem
{
	/**
	 * Called on both client and server every time the alchemical bag ticks this item
	 *
	 * @param inv The inventory of the bag
	 * @param player The player whose bag is being ticked
	 * @param stack The ItemStack being ticked
	 * @return Whether the inventory was changed by this item ticking
	 */
	boolean updateInAlchBag(ItemStack[] inv, EntityPlayer player, ItemStack stack);
}
