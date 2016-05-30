package moze_intel.projecte.api.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
	byte getMode(@Nonnull ItemStack stack);

	/**
	 * Called serverside when the player presses change mode
	 * @param player The player pressing the change mode key
	 * @param stack The stack whose mode we are changing
	 * @param hand The hand this stack was in, or null if the call was not from the player's hands
	 * @return Whether the operation succeeded
	 */
	boolean changeMode(@Nonnull EntityPlayer player, @Nonnull ItemStack stack, @Nullable EnumHand hand);
}
