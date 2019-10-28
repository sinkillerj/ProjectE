package moze_intel.projecte.api.capabilities.item;

import javax.annotation.Nonnull;
import moze_intel.projecte.api.capabilities.tile.IEmcStorage.EmcAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

/**
 * This interface defines the contract for items that wish to expose their internal EMC storage for external manipulation
 *
 * This is exposed through the Capability system.
 *
 * Acquire an instance of this using {@link ItemStack#getCapability(Capability, Direction)}.
 *
 * @author williewillus
 */
//TODO: Convert things over to simulating insertion/extraction?
public interface IItemEmcHolder {

	/**
	 * Adds EMC to the itemstack
	 *
	 * @param stack    The itemstack to add to
	 * @param toInsert The maximum amount to add
	 * @param action   The action to perform, either {@link EmcAction#EXECUTE} or {@link EmcAction#SIMULATE}
	 *
	 * @return The amount that was actually added
	 */
	long insertEmc(@Nonnull ItemStack stack, long toInsert, EmcAction action);

	/**
	 * Extracts EMC from the itemstack
	 *
	 * @param stack     The itemstack to remove from
	 * @param toExtract The maximum amount to remove
	 * @param action    The action to perform, either {@link EmcAction#EXECUTE} or {@link EmcAction#SIMULATE}
	 *
	 * @return The amount that was actually extracted
	 */
	long extractEmc(@Nonnull ItemStack stack, long toExtract, EmcAction action);

	/**
	 * Gets the current EMC this stack is showing to the public
	 *
	 * @param stack The stack to query
	 *
	 * @return The current publicly-accessible EMC stored in this stack
	 */
	long getStoredEmc(@Nonnull ItemStack stack);

	/**
	 * Gets the maximum EMC that is allowed to be stored in this stack
	 *
	 * @param stack The stack to query
	 *
	 * @return The maximum amount of publicly-accessible EMC that can be stored in this stack
	 *
	 * @implNote This value should never be zero
	 */
	long getMaximumEmc(@Nonnull ItemStack stack);

	/**
	 * Helper method to get the amount of EMC this {@link IItemEmcHolder} needs to become full.
	 *
	 * @param stack The stack to query
	 *
	 * @return The amount of EMC this {@link IItemEmcHolder} needs.
	 */
	default long getNeededEmc(@Nonnull ItemStack stack) {
		return Math.max(0, getMaximumEmc(stack) - getStoredEmc(stack));
	}
}