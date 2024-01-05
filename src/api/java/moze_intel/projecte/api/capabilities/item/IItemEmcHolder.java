package moze_intel.projecte.api.capabilities.item;

import moze_intel.projecte.api.capabilities.block_entity.IEmcStorage.EmcAction;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.ItemCapability;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

/**
 * This interface defines the contract for items that wish to expose their internal EMC storage for external manipulation
 * <p>
 * This is exposed through the Capability system.
 * <p>
 * Acquire an instance of this using {@link ItemStack#getCapability(ItemCapability)}.
 *
 * @author williewillus
 */
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
	long insertEmc(@NotNull ItemStack stack, long toInsert, EmcAction action);

	/**
	 * Extracts EMC from the itemstack
	 *
	 * @param stack     The itemstack to remove from
	 * @param toExtract The maximum amount to remove
	 * @param action    The action to perform, either {@link EmcAction#EXECUTE} or {@link EmcAction#SIMULATE}
	 *
	 * @return The amount that was actually extracted
	 */
	long extractEmc(@NotNull ItemStack stack, long toExtract, EmcAction action);

	/**
	 * Gets the current EMC this stack is showing to the public
	 *
	 * @param stack The stack to query
	 *
	 * @return The current publicly-accessible EMC stored in this stack
	 */
	@Range(from = 0, to = Long.MAX_VALUE)
	long getStoredEmc(@NotNull ItemStack stack);

	/**
	 * Gets the maximum EMC that is allowed to be stored in this stack
	 *
	 * @param stack The stack to query
	 *
	 * @return The maximum amount of publicly-accessible EMC that can be stored in this stack
	 *
	 * @implNote This value should never be zero
	 */
	@Range(from = 1, to = Long.MAX_VALUE)
	long getMaximumEmc(@NotNull ItemStack stack);

	/**
	 * Helper method to get the amount of EMC this {@link IItemEmcHolder} needs to become full.
	 *
	 * @param stack The stack to query
	 *
	 * @return The amount of EMC this {@link IItemEmcHolder} needs.
	 */
	@Range(from = 0, to = Long.MAX_VALUE)
	default long getNeededEmc(@NotNull ItemStack stack) {
		return Math.max(0, getMaximumEmc(stack) - getStoredEmc(stack));
	}
}