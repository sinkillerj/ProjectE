package moze_intel.projecte.api.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This capability interface specifies items that have a charge that changes when the respective keybinding is activated (default V)
 */
public interface IItemCharge 
{
	int getNumCharges();

	/**
	 * Returns the current charge on the given ItemStack
	 * @return The charge on the stack
	 */
	int getCharge();

	/**
	 * Called serverside when the player presses the charge keybinding; reading sneaking state is up to you
	 * @param player The player
	 * @param hand The hand this stack was in, or null if the call was not from the player's hands
	 * @return Whether the operation succeeded
	 */
	boolean changeCharge(@Nonnull EntityPlayer player, @Nullable EnumHand hand);
}
