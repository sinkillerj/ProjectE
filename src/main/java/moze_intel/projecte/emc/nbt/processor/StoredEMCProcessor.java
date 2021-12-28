package moze_intel.projecte.emc.nbt.processor;

import javax.annotation.Nonnull;
import moze_intel.projecte.api.ItemInfo;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.nbt.INBTProcessor;
import moze_intel.projecte.api.nbt.NBTProcessor;
import net.minecraft.world.item.ItemStack;

@NBTProcessor
public class StoredEMCProcessor implements INBTProcessor {

	@Override
	public String getName() {
		return "StoredEMCProcessor";
	}

	@Override
	public String getDescription() {
		return "Increases the EMC value of the item to take into account any EMC the item has stored.";
	}

	@Override
	public long recalculateEMC(@Nonnull ItemInfo info, long currentEMC) throws ArithmeticException {
		ItemStack stack = info.createStack();
		return stack.getCapability(ProjectEAPI.EMC_HOLDER_ITEM_CAPABILITY).map(emcHolder -> Math.addExact(currentEMC, emcHolder.getStoredEmc(stack))).orElse(currentEMC);
	}
}