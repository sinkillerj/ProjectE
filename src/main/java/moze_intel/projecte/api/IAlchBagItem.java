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
	 * The in-bag callback, called on both sides.
	 * @param player The player that is being ticked
	 * @param invBag The bag inventory
	 * @param item Your itemstack being ticked in the bag
	 */
	public void updateInAlchBag(EntityPlayer player, ItemStack[] invBag, ItemStack item);

	/***
	 * The pickup callback. Only called serverside.
	 * @param player The player picking the item up
	 * @param invBag The inventory of the first bag found. Modify this if you need to access the bag. Never null.
	 * @param item The item being picked up
	 * @return If the standard Forge pickup event should be canceled or not
	 */
	public boolean onPickUp(EntityPlayer player, ItemStack[] invBag, EntityItem item);

	/***
	 * Unimplemented, in case we decide to add tooltips but don't want to break API compat
	 * @return A string describing what the item does in an Alch Bag
	 */
	public String getAlchBagDesc();
}
