package moze_intel.projecte.api.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * Used by items that have different modes
 */
public interface IModeChanger 
{
	/**
	 * Gets the mode from this ItemStack
	 * @param stack The stack we want the mode of
	 * @return The mode of this ItemStack
	 */
	byte getMode(ItemStack stack);

	/**
	 * Called serverside when the player presses change mode (G by default)
	 * @param player The player pressing the change mode key
	 * @param stack The stack whose mode we are changing
	 */
	void changeMode(EntityPlayer player, ItemStack stack);
}
