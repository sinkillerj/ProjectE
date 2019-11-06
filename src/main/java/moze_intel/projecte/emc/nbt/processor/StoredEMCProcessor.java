package moze_intel.projecte.emc.nbt.processor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import moze_intel.projecte.api.ItemInfo;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.nbt.INBTProcessor;
import moze_intel.projecte.api.nbt.NBTProcessor;
import moze_intel.projecte.utils.LazyOptionalHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

@NBTProcessor
public class StoredEMCProcessor implements INBTProcessor {

	@Nullable
	@Override
	public CompoundNBT getPersistentNBT(@Nonnull ItemInfo info) {
		return null;
	}

	@Override
	public long recalculateEMC(@Nonnull ItemInfo info, long currentEMC) throws ArithmeticException {
		//TODO: Improve on this (maybe making it so if the info is actually from the stack it stores a reference)
		// and can then use that for checking capabilities?
		ItemStack stack = info.createStack();
		return LazyOptionalHelper.toOptional(stack.getCapability(ProjectEAPI.EMC_HOLDER_ITEM_CAPABILITY))
				.map(emcHolder -> Math.addExact(currentEMC, emcHolder.getStoredEmc(stack))).orElse(currentEMC);
	}

	@Override
	public String getName() {
		return "StoredEMCProcessor";
	}
}