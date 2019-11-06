package moze_intel.projecte.api.nbt;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import moze_intel.projecte.api.ItemInfo;
import net.minecraft.nbt.CompoundNBT;

/**
 * Class used for processing what NBT data modifies the EMC value, and what NBT is needed/should be saved when transmuting an item.
 */
public interface INBTProcessor {

	/**
	 * Gets the minimum {@link CompoundNBT} that is needed to recreate/get an EMC value from this {@link INBTProcessor} for an {@link ItemInfo}. This is used for building
	 * up the actual {@link ItemInfo} that will be saved to Knowledge/duplication in a condenser.
	 *
	 * @param info The {@link ItemInfo} to get the persistent NBT from.
	 *
	 * @return The minimum {@link CompoundNBT} needed to recreate/get an EMC value from this {@link INBTProcessor} for an {@link ItemInfo}
	 */
	@Nullable
	CompoundNBT getPersistentNBT(@Nonnull ItemInfo info);

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
	 * A unique Name for the INBTProcessor. This is used to identify the INBTProcessor in the Configuration.
	 *
	 * @return A unique Name
	 *
	 * @apiNote This currently is only actually used for printing when a processor got found, but eventually this may be used for allowing the config to disable
	 * processors.
	 */
	String getName();
}