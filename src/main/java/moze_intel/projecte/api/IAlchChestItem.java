package moze_intel.projecte.api;

import moze_intel.projecte.gameObjs.tiles.AlchChestTile;
import net.minecraft.item.ItemStack;

/***
 * This interface should be implemented by items that wish to receive a callback each TE tick, on both sides,
 * to give their item special functions while in an alchemical chest.
 * Any synchronizations or side-checks needed are up to the addon developer.
 */
public interface IAlchChestItem
{
	/***
	 * The callback
	 * @param chest The chest that received the callback
	 * @param stack The itemStack that received the callback
	 */
	public void updateInAlchChest(AlchChestTile chest, ItemStack stack);
}
