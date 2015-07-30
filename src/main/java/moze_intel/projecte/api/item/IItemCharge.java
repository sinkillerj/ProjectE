package moze_intel.projecte.api.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * This interface specifies items that have a charge that changes when the respective keybinding is activated (default V)
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
	 * Called serverside when the player presses the charge keybinding; reading sneaking state is up to you
	 * @param player The player
	 * @param stack The item being charged
	 */
	void changeCharge(EntityPlayer player, ItemStack stack);
}
