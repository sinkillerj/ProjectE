package moze_intel.projecte.api.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;

/**
 * This interface specifies items that switch between modes when the mode switch keybind is activated (default G)
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
	 * Called serverside when the player presses change mode
	 * @param player The player pressing the change mode key
	 * @param stack The stack whose mode we are changing
	 * @param hand The hand this stack was in
	 */
	void changeMode(EntityPlayer player, ItemStack stack, EnumHand hand);
}
