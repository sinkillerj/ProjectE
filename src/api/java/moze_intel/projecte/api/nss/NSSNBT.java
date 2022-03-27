package moze_intel.projecte.api.nss;

import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

/**
 * An extension of {@link NormalizedSimpleStack} that allows for representing stacks that are both "simple" and can have a {@link CompoundTag} attached.
 */
public interface NSSNBT extends NormalizedSimpleStack {

	/**
	 * Gets the {@link CompoundTag} that this {@link NSSNBT} has.
	 *
	 * @return The {@link CompoundTag} that this {@link NSSNBT} has.
	 */
	@Nullable
	CompoundTag getNBT();

	/**
	 * Checks if this {@link NSSNBT} has any NBT.
	 *
	 * @return True if this {@link NSSNBT} has NBT, false otherwise.
	 */
	default boolean hasNBT() {
		return getNBT() != null;
	}
}