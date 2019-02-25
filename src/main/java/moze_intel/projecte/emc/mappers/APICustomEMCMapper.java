package moze_intel.projecte.emc.mappers;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import moze_intel.projecte.PECore;
import moze_intel.projecte.emc.json.NSSItem;
import moze_intel.projecte.emc.json.NormalizedSimpleStack;
import moze_intel.projecte.emc.collector.IMappingCollector;
import moze_intel.projecte.impl.ConversionProxyImpl;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class APICustomEMCMapper implements IEMCMapper<NormalizedSimpleStack, Long> {
	public static final APICustomEMCMapper instance = new APICustomEMCMapper();
	private static final int PRIORITY_MIN_VALUE = 0;
	private static final int PRIORITY_MAX_VALUE = 512;
	private static final int PRIORITY_DEFAULT_VALUE = 1;
	private APICustomEMCMapper() {}

	//Need a special Map for Items and Blocks because the ItemID-mapping might change, so we need to store modid:unlocalizedName instead of the NormalizedSimpleStack which only holds itemid and metadata
	private final Map<String, Map<NormalizedSimpleStack, Long>> customEMCforMod = new HashMap<>();
	private final Map<String, Map<NormalizedSimpleStack, Long>> customNonItemEMCforMod = new HashMap<>();

	public void registerCustomEMC(ItemStack stack, long emcValue) {
		if (stack.isEmpty()) return;
		if (emcValue < 0) emcValue = 0;
		ModContainer activeMod = Loader.instance().activeModContainer();
		String modId = activeMod == null ? null : activeMod.getModId();
		Map<NormalizedSimpleStack, Long> modMap;
		if (customEMCforMod.containsKey(modId)) {
			modMap = customEMCforMod.get(modId);
		} else {
			modMap = new HashMap<>();
			customEMCforMod.put(modId, modMap);
		}
		modMap.put(NSSItem.create(stack), emcValue);
	}

	public void registerCustomEMC(Object o, long emcValue) {
		NormalizedSimpleStack stack = ConversionProxyImpl.instance.objectToNSS(o);
		if (stack == null) return;
		if (emcValue < 0) emcValue = 0;
		ModContainer activeMod = Loader.instance().activeModContainer();
		String modId = activeMod == null ? null : activeMod.getModId();
		Map<NormalizedSimpleStack, Long> modMap;
		if (customNonItemEMCforMod.containsKey(modId)) {
			modMap = customNonItemEMCforMod.get(modId);
		} else {
			modMap = new HashMap<>();
			customNonItemEMCforMod.put(modId, modMap);
		}
		modMap.put(stack, emcValue);
	}

	@Override
	public String getName() {
		return "APICustomEMCMapper";
	}

	@Override
	public String getDescription() {
		return "Allows other mods to set EMC values using the ProjectEAPI";
	}

	@Override
	public boolean isAvailable() {
		return true;
	}

	@Override
	public void addMappings(IMappingCollector<NormalizedSimpleStack, Long> mapper, Configuration config) {
		Map<String, Integer> priorityMap = new HashMap<>();
		Set<String> modIdSet = new HashSet<>();
		modIdSet.addAll(customEMCforMod.keySet());
		modIdSet.addAll(customNonItemEMCforMod.keySet());

		for (String modId: modIdSet) {
			if (modId == null) continue;
			int valueCount = 0;
			if (customEMCforMod.containsKey(modId))
			{
				valueCount += customEMCforMod.get(modId).size();
			}
			if (customNonItemEMCforMod.containsKey(modId))
			{
				valueCount += customNonItemEMCforMod.get(modId).size();
			}
			priorityMap.put(modId, config.getInt(modId + "priority", "customEMCPriorities", PRIORITY_DEFAULT_VALUE, PRIORITY_MIN_VALUE, PRIORITY_MAX_VALUE, "Priority for Mod with ModId = " + modId + ". Values: " + valueCount));
		}
		if (modIdSet.contains(null))
		{
			int valueCount = 0;
			if (customEMCforMod.containsKey(null))
			{
				valueCount += customEMCforMod.get(null).size();
			}
			if (customNonItemEMCforMod.containsKey(null))
			{
				valueCount += customNonItemEMCforMod.get(null).size();
			}
			priorityMap.put(null, config.getInt("modlessCustomEMCPriority", "", PRIORITY_DEFAULT_VALUE, PRIORITY_MIN_VALUE, PRIORITY_MAX_VALUE, "Priority for custom EMC values for which the ModId could not be determined. 0 to disable. Values: " + valueCount));
		}

		List<String> modIds = new ArrayList<>(modIdSet);
		modIds.sort(Comparator.comparingInt(priorityMap::get).reversed());

		for(String modId : modIds) {
			String modIdOrUnknown = modId == null ? "unknown mod" : modId;
			if (customEMCforMod.containsKey(modId))
			{
				for (Map.Entry<NormalizedSimpleStack, Long> entry : customEMCforMod.get(modId).entrySet())
				{
					NormalizedSimpleStack normStack = entry.getKey();
					if (isAllowedToSet(modId, normStack, entry.getValue(), config))
					{
						mapper.setValueBefore(normStack, entry.getValue());
						PECore.debugLog("{} setting value for {} to {}", modIdOrUnknown, normStack, entry.getValue());
					}
					else
					{
						PECore.debugLog("Disallowed {} to set the value for {} to {}", modIdOrUnknown, normStack, entry.getValue());
					}
				}
			}
			if (customNonItemEMCforMod.containsKey(modId))
			{
				for(Map.Entry<NormalizedSimpleStack, Long> entry: customNonItemEMCforMod.get(modId).entrySet()) {
					NormalizedSimpleStack normStack = entry.getKey();
					if (isAllowedToSet(modId, normStack, entry.getValue(), config))
					{
						mapper.setValueBefore(normStack, entry.getValue());
						PECore.debugLog("{} setting value for {} to {}", modIdOrUnknown, normStack, entry.getValue());
					}
					else
					{
						PECore.debugLog("Disallowed {} to set the value for {} to {}", modIdOrUnknown, normStack, entry.getValue());
					}
				}
			}
		}
	}

	private boolean isAllowedToSet(String modId, NormalizedSimpleStack stack, Long value, Configuration config) {
		String itemName;
		if (stack instanceof NSSItem)
		{
			NSSItem item = (NSSItem)stack;
			itemName = item.itemName;
		} else {
			itemName = "IntermediateFakeItemsUsedInRecipes:";
		}
		String modForItem = itemName.substring(0, itemName.indexOf(':'));
		String permission = config.getString(modForItem,"permissions."+modId,"both", String.format("Allow '%s' to set and or remove values for '%s'. Options: [both, set, remove, none]", modId, modForItem), new String[]{"both", "set", "remove", "none"});
		if (permission.equals("both"))
		{
			return true;
		}
		if (value == 0)
		{
			return permission.equals("remove");
		}
		else
		{
			return permission.equals("set");
		}
	}
}