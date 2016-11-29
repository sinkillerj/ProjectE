package moze_intel.projecte.api.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
	byte getCharge(@Nonnull ItemStack stack);

	/**
	 * Called serverside when the player presses the charge keybinding; reading sneaking state is up to you
	 * @param player The player
	 * @param stack The item being charged
	 * @param hand The hand this stack was in, or null if the call was not from the player's hands
	 * @return Whether the operation succeeded
	 */
	boolean changeCharge(@Nonnull EntityPlayer player, @Nonnull ItemStack stack, @Nullable EnumHand hand);
}
