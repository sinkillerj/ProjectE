package moze_intel.projecte.api.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * Used by items that have a "charge".
 */
public interface IItemCharge 
{
	/**
	 * Returns the current charge on the given ItemStack
	 * @param stack Stack whose charge we want
	 * @return The charge on the stack
	 */
	byte getCharge(ItemStack stack);

	/**
	 * Called serverside when the player presses the change mode button (V and Shift-V by default)
	 * @param player The player
	 * @param stack The item whose mode we are changing
	 */
	void changeCharge(EntityPlayer player, ItemStack stack);
}
