package moze_intel.projecte.api;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/***
 * This interface should be implemented by items wishing to have special behavior while inside an Alchemical Bag
 * Or want special behavior while being picked up (Go directly to bag, etc.)
 * Any synchronizations or side-checks needed are up to the addon developer.
 */
public interface IAlchBagItem
{
	/***
	 * The in-bag callback. Almost always you want to call IOHandler#markDirty serverside after you are done
	 * @param player The player that is being ticked
	 * @param bag The bag being ticked
	 * @param item Our itemstack being ticked in the bag
	 */
	public void updateInAlchBag(EntityPlayer player, ItemStack bag, ItemStack item);

	/***
	 * The pickup callback. Only called serverside.
	 * @param player
	 * @return If the Forge event should be canceled or not.
	 */
	public boolean onPickUp(EntityPlayer player, EntityItem item);
}
