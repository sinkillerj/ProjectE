package moze_intel.projecte.emc.nbt;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.ItemInfo;
import moze_intel.projecte.emc.EMCMappingHandler;
import moze_intel.projecte.emc.nbt.processor.DamageProcessor;
import moze_intel.projecte.emc.nbt.processor.EnchantmentProcessor;
import moze_intel.projecte.emc.nbt.processor.INBTProcessor;
import moze_intel.projecte.emc.nbt.processor.StoredEMCProcessor;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;

public class NBTManager {

	private static final Tag<Item> NBT_WHITELIST_TAG = new ItemTags.Wrapper(new ResourceLocation(PECore.MODID, "nbt_whitelist"));
	//TODO: Add a map for keeping track of cached "extra" values that have been found this lookup from querying
	// but do not have actually values added into the actual map
	// NOTE: We still need to figure out how to best handle it so that we don't spam the map with values of things
	// like different damage values
	// This is where the least significant NBT part of the processor comes in
	// Note: unsure how well that will work due to the damage NBT actually being *most* significant
	private static List<INBTProcessor> processors = new ArrayList<>();

	static {
		//TODO: Allow these to be registered via annotations. Also make sure they have some sort of priority system
		processors.add(new DamageProcessor());
		processors.add(new EnchantmentProcessor());
		processors.add(new StoredEMCProcessor());
	}

	//TODO: Figure out what happens if more cleaners get added since someone learns an item so then it doesn't find a match when trying to
	// remove the "overly" cleaned stack

	//TODO: Go back through this, because it actually will give the "incorrect" information because it will trim out things like damage/stored emc
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
		}
		return emcValue;
	}
}