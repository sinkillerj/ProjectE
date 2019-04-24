package moze_intel.projecte.api.item;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

/**
 * This interface defines the contract for items that wish to expose their internal EMC storage for external manipulation
 *
 * @author williewillus
 */
public interface IItemEmc
{
	/**
	 * Adds EMC to the itemstack
	 * @param stack The itemstack to add to
	 * @param toAdd The maximum amount to add
	 * @return The amount that was actually added
	 */
	long addEmc(@Nonnull ItemStack stack, long toAdd);

	/**
	 * Adds EMC to the itemstack
	 * @param stack The itemstack to add to
	 * @param toAdd The maximum amount to add
	 * @return The amount that was actually added
	 * @deprecated Since ProjectE API version 1.2.0
	 */
	@Deprecated
	default double addEmc(@Nonnull ItemStack stack, double toAdd) {
		return addEmc(stack, (long) toAdd);
	}

	/**
	 * Extracts EMC from the itemstack
	 * @param stack The itemstack to remove from
	 * @param toRemove The maximum amount to remove
	 * @return The amount that was actually extracted
	 */
	long extractEmc(@Nonnull ItemStack stack, long toRemove);

	/**
	 * Extracts EMC from the itemstack
	 * @param stack The itemstack to remove from
	 * @param toRemove The maximum amount to remove
	 * @return The amount that was actually extracted
	 * @deprecated Since ProjectE API version 1.2.0
	 */
	@Deprecated
	default double extractEmc(@Nonnull ItemStack stack, double toRemove) {
		return extractEmc(stack, (long) toRemove);
	}

	/**
	 * Gets the current EMC this stack is showing to the public
	 * @param stack The stack to query
	 * @return The current publicly-accessible EMC stored in this stack
	 */
	long getStoredEmc(@Nonnull ItemStack stack);

	/**
	 * Gets the maximum EMC that is allowed to be stored in this stack
	 * @param stack The stack to query
	 * @return The maximum amount of publicly-accessible EMC that can be stored in this stack
	 */
	long getMaximumEmc(@Nonnull ItemStack stack);
}
