package moze_intel.projecte.api.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This capability interface specifies items that switch between modes when the mode switch keybind is activated (default G)
 */
public interface IModeChanger 
{
	/**
	 * Gets the mode
	 * @return The mode of the inner ItemStack
	 */
	byte getMode();

	/**
	 * Called serverside when the player presses change mode
	 * @param player The player pressing the change mode key
	 * @param hand The hand the inner stack was in, or null if the call was not from the player's hands
	 * @return Whether the operation succeeded
	 */
	boolean changeMode(@Nonnull EntityPlayer player, @Nullable EnumHand hand);
}
