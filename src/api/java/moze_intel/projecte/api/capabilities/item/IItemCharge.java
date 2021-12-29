package moze_intel.projecte.api.capabilities.item;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import moze_intel.projecte.api.PESounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;

/**
 * This interface specifies items that have a charge that changes when the respective keybinding is activated (default V)
 *
 * This is exposed through the Capability system.
 *
 * Acquire an instance of this using {@link ItemStack#getCapability(Capability, net.minecraft.core.Direction)}.
 */
public interface IItemCharge {

	String KEY = "Charge";

	int getNumCharges(@Nonnull ItemStack stack);

	/**
	 * Gets the current percent charge on the given ItemStack. Should be a value between 0 and 1 inclusive.
	 *
	 * @param stack Stack whose charge percent we want
	 *
	 * @return The percent charge on the stack
	 */
	default float getChargePercent(@Nonnull ItemStack stack) {
		return (float) getCharge(stack) / getNumCharges(stack);
	}

	/**
	 * Returns the current charge on the given ItemStack
	 *
	 * @param stack Stack whose charge we want
	 *
	 * @return The charge on the stack
	 */
	default int getCharge(@Nonnull ItemStack stack) {
		return stack.getOrCreateTag().getInt(KEY);
	}

	/**
	 * Called serverside when the player presses the charge keybinding; reading sneaking state is up to you
	 *
	 * @param player The player
	 * @param stack  The item being charged
	 * @param hand   The hand this stack was in, or null if the call was not from the player's hands
	 *
	 * @return Whether the operation succeeded
	 */
	default boolean changeCharge(@Nonnull Player player, @Nonnull ItemStack stack, @Nullable InteractionHand hand) {
		int currentCharge = getCharge(stack);
		int numCharges = getNumCharges(stack);

		if (player.isShiftKeyDown()) {
			if (currentCharge > 0) {
				player.level.playSound(null, player.getX(), player.getY(), player.getZ(), PESounds.UNCHARGE, SoundSource.PLAYERS, 1.0F,
						0.5F + ((0.5F / (float) numCharges) * currentCharge));
				stack.getOrCreateTag().putInt(KEY, currentCharge - 1);
				return true;
			}
		} else if (currentCharge < numCharges) {
			player.level.playSound(null, player.getX(), player.getY(), player.getZ(), PESounds.CHARGE, SoundSource.PLAYERS, 1.0F,
					0.5F + ((0.5F / (float) numCharges) * currentCharge));
			stack.getOrCreateTag().putInt(KEY, currentCharge + 1);
			return true;
		}
		return false;
	}
}