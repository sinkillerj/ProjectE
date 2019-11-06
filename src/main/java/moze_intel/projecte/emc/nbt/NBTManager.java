package moze_intel.projecte.emc.nbt;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.ItemInfo;
import moze_intel.projecte.api.nbt.INBTProcessor;
import moze_intel.projecte.emc.EMCMappingHandler;
import moze_intel.projecte.utils.AnnotationHelper;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;

public class NBTManager {

	private static final Tag<Item> NBT_WHITELIST_TAG = new ItemTags.Wrapper(new ResourceLocation(PECore.MODID, "nbt_whitelist"));
	private static final List<INBTProcessor> processors = new ArrayList<>();
	//TODO: Eventually maybe add some form of cache for calculated EMC values. There isn't a great way to do it memory wise
	// for how long we should keep it in memory so for now we don't bother.

	public static void loadProcessors() {
		if (processors.isEmpty()) {
			processors.addAll(AnnotationHelper.getNBTProcessors());
		}
	}

	//TODO: Figure out what happens if more cleaners get added since someone learns an item so then it doesn't find a match when trying to
	// remove the "overly" cleaned stack

	public static ItemInfo getPersistentInfo(ItemInfo info) {
		if (info.getNBT() == null || info.getItem().isIn(NBT_WHITELIST_TAG) || EMCMappingHandler.emc.containsKey(info)) {
			//If we have no NBT, we want to allow the tag to be kept, or we have an exact match to a stored value just go with it
			return info;
		}

		//Cleans up the tag in info to reduce it as much as possible
		List<CompoundNBT> persistentNBT = processors.stream().map(processor -> processor.getPersistentNBT(info)).filter(Objects::nonNull).collect(Collectors.toList());
		if (persistentNBT.isEmpty()) {
			return ItemInfo.fromItem(info.getItem());
		}
		//TODO: TEST-ME
		CompoundNBT combinedNBT = persistentNBT.get(0);
		for (int i = 1; i < persistentNBT.size(); i++) {
			combinedNBT = combinedNBT.merge(persistentNBT.get(i));
		}
		return ItemInfo.fromItem(info.getItem(), combinedNBT);
	}

	//TODO: Would a good way to handle support for items that have a NBT in their value but then have extra NBT also be to
	// make each registered NSS that has NBT also get registered into a custom INBTProcessor so that we can do some sort of partial NBT match??

	public static long getEmcValue(ItemInfo info) {
		//TODO: think about - if there is no EMC value matching the nbt exactly then try to get it without the NBT
		// And then it can process any extra NBT it has through the processors
		// Otherwise if it has an exact match already take it.
		// This doesn't catch the edge case that we have an exact match and then there is random added NBT on top of it
		// but that can be thought about more once we have the first pass complete.
		long emcValue = EMCMappingHandler.emc.getOrDefault(info, 0L);
		if (info.getNBT() == null) {
			//If our info has no NBT anyways just return based on the
			return emcValue;
		}
		if (emcValue == 0) {
			//Try getting a base emc value from the NBT less variant if we don't have one matching our NBT
			emcValue = EMCMappingHandler.emc.getOrDefault(ItemInfo.fromItem(info.getItem()), 0L);
			if (emcValue == 0) {
				//The base item doesn't have an EMC value either so just exit
				return 0;
			}
		}

		//Note: We continue to use our initial ItemInfo so that we are calculating based on the NBT
		for (INBTProcessor processor : processors) {
			try {
				emcValue = processor.recalculateEMC(info, emcValue);
			} catch (ArithmeticException e) {
				//Return the last successfully calculated EMC value
				return emcValue;
			}
			if (emcValue <= 0) {
				//Exit if it gets to zero (also safety check for less than zero in case a mod didn't bother sanctifying their data)
				return 0;
			}
		}
		return emcValue;
	}
}