package moze_intel.projecte.emc.nbt.processor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.ItemInfo;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;

public class NBTWhitelistProcessor implements INBTProcessor {

	private static final Tag<Item> NBT_WHITELIST_TAG = new ItemTags.Wrapper(new ResourceLocation(PECore.MODID, "nbt_whitelist"));

	@Nullable
	@Override
	public CompoundNBT getPersistentNBT(@Nonnull ItemInfo info) {
		if (info.getItem().isIn(NBT_WHITELIST_TAG)) {
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