package moze_intel.projecte.api.nss;

import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundNBT;

/**
 * An extension of {@link NormalizedSimpleStack} that allows for representing stacks that are both "simple" and can have a {@link CompoundNBT} attached.
 */
public interface NSSNBT extends NormalizedSimpleStack {//TODO: Add tests for NBT based stuff

	/**
	 * Gets the {@link CompoundNBT} that this {@link NSSNBT} has.
	 *
	 * @return The {@link CompoundNBT} that this {@link NSSNBT} has.
	 */
	@Nullable
	CompoundNBT getNBT();

	/**
	 * Checks if this {@link NSSNBT} has any NBT.
	 *
	 * @return True if this {@link NSSNBT} has NBT, false otherwise.
	 */
	default boolean hasNBT() {
		return getNBT() != null;
	}
}