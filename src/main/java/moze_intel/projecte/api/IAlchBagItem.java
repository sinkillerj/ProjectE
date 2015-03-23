package moze_intel.projecte.api;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/***
 * This interface should be implemented by items wishing to have special behavior while inside an Alchemical Bag
 * Or want special behavior over item pickups from within the bag (Go directly to bag, change into another item, etc.)
 */
public interface IAlchBagItem
{
	/***
	 * The in-bag callback, called on both sides. Almost always you want to call IOHandler#markDirty on serverside after you are done
	 * @param player The player that is being ticked
	 * @param bag The bag being ticked
	 * @param item Our itemstack being ticked in the bag
	 */
	public void updateInAlchBag(EntityPlayer player, ItemStack bag, ItemStack item);

	/***
	 * The pickup callback. Only called serverside.
	 * @param player The player picking the item up
	 * @param bag The first bag found in the player inv. Will never be null
	 * @param item The item being picked up
	 * @return If the standard Forge pickup event should be canceled or not
	 */
	public boolean onPickUp(EntityPlayer player, ItemStack bag, EntityItem item);

	/***
	 * Unimplemented, in case we decide to add tooltips but don't want to break API compat
	 * @return A string describing what the item does in an Alch Bag
	 */
	public String getAlchBagDesc();
}
