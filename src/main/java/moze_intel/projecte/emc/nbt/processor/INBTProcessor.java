package moze_intel.projecte.emc.nbt.processor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import moze_intel.projecte.api.ItemInfo;
import net.minecraft.nbt.CompoundNBT;

//TODO: Move to the API and add an annotation for registering it
public interface INBTProcessor {

	@Nullable
	CompoundNBT getPersistentNBT(@Nonnull ItemInfo info);

	long recalculateEMC(@Nonnull ItemInfo info, long currentEMC) throws ArithmeticException;
}