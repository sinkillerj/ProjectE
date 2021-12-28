package moze_intel.projecte.api.nbt;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import moze_intel.projecte.api.ItemInfo;
import net.minecraft.nbt.CompoundTag;

/**
 * Class used for processing what NBT data modifies the EMC value, and what NBT is needed/should be saved when transmuting an item.
 */
public interface INBTProcessor {

	/**
	 * A unique Name for the {@link INBTProcessor}. This is used to identify the {@link INBTProcessor} in the Configuration.
	 *
	 * @return A unique Name
	 */
	String getName();

	/**
	 * A Description, that will be included as a Comment in the Configuration File
	 *
	 * @return A <b>short</b> description
	 */
	String getDescription();

	/**
	 * This method is used to determine the default for enabling/disabling this {@link INBTProcessor}. If this returns {@code false} neither {@link
	 * #getPersistentNBT(ItemInfo)} nor {@link #recalculateEMC(ItemInfo, long)} will not be called.
	 *
	 * @return {@code true} if you want this {@link INBTProcessor} to be part of the EMC calculations, {@code false} otherwise.
	 */
	default boolean isAvailable() {
		return true;
	}

	/**
	 * This method is used to determine if this {@link INBTProcessor} can ever have persistent data. If this returns {@code false} {@link #usePersistentNBT()} will not be
	 * checked.<br/>
	 *
	 * @return {@code true} if you want {@link #usePersistentNBT()} to be checked, {@code false} otherwise.
	 */
	default boolean hasPersistentNBT() {
		return false;
	}

	/**
	 * This method is used to determine if this {@link INBTProcessor} should contribute its persistent data. If this returns {@code false} {@link
	 * #getPersistentNBT(ItemInfo)} will not be called.<br/>
	 *
	 * This method will also be used to determine the default for enabling/disabling of NBT persistence this {@link INBTProcessor}
	 *
	 * @return {@code true} if you want {@link #getPersistentNBT(ItemInfo)} to be called, {@code false} otherwise.
	 */
	default boolean usePersistentNBT() {
		return hasPersistentNBT();
	}

	/**
	 * Calculates any changes to EMC this {@link INBTProcessor} has to make based on the given {@link ItemInfo}
	 *
	 * @param info       The {@link ItemInfo} to attempt to get any NBT specific information this
	 * @param currentEMC The EMC value before this {@link INBTProcessor} has performed any calculations.
	 *
	 * @return The EMC value after this {@link INBTProcessor} has performed its calculations.
	 *
	 * @throws ArithmeticException If an overflow happened or some calculation went really bad and we should just hard exit and return the last successful EMC value
	 *                             calculated.
	 */
	long recalculateEMC(@Nonnull ItemInfo info, long currentEMC) throws ArithmeticException;

	/**
	 * Gets the minimum {@link CompoundTag} that is needed to recreate/get an EMC value from this {@link INBTProcessor} for an {@link ItemInfo}. This is used for building
	 * up the actual {@link ItemInfo} that will be saved to Knowledge/duplication in a condenser.
	 *
	 * @param info The {@link ItemInfo} to get the persistent NBT from.
	 *
	 * @return The minimum {@link CompoundTag} needed to recreate/get an EMC value from this {@link INBTProcessor} for an {@link ItemInfo}
	 */
	@Nullable
	default CompoundTag getPersistentNBT(@Nonnull ItemInfo info) {
		return null;
	}
}