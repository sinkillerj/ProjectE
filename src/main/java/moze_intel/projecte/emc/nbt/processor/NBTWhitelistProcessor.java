package moze_intel.projecte.emc.nbt.processor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import moze_intel.projecte.api.ItemInfo;
import moze_intel.projecte.utils.ItemHelper;
import net.minecraft.nbt.CompoundNBT;

public class NBTWhitelistProcessor implements INBTProcessor {

	@Nullable
	@Override
	public CompoundNBT getPersistentNBT(@Nonnull ItemInfo info) {
		if (info.getItem().isIn(ItemHelper.NBT_WHITELIST_TAG)) {
			//The item is whitelisted for keeping its NBT so just mark all of the NBT as persistent
			return info.getNBT();
		}
		return null;
	}

	@Override
	public long recalculateEMC(@Nonnull ItemInfo info, long currentEMC) throws ArithmeticException {
		//NO-OP
		return currentEMC;
	}
}