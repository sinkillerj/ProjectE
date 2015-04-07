package moze_intel.projecte.emc.mappers;

import com.google.common.collect.Lists;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.emc.IMappingCollector;
import moze_intel.projecte.emc.NormalizedSimpleStack;
import moze_intel.projecte.gameObjs.tiles.InterdictionTile;
import moze_intel.projecte.utils.PELogger;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;

import java.util.*;

public class APICustomEMCMapper implements IEMCMapper<NormalizedSimpleStack, Integer>, ProjectEAPI.IRegisterCustomEMC {
	public static APICustomEMCMapper instance = new APICustomEMCMapper();
	public static final int PRIORITY_MIN_VALUE = 0;
	public static final int PRIORITY_MAX_VALUE = 512;
	public static final int PRIORITY_DEFAULT_VALUE = 1;
	private APICustomEMCMapper() {}

	Map<String, Map<String, Integer>> customEMCforMod = new HashMap<String, Map<String, Integer>>();

	public void registerCustomEMC(ItemStack stack, int emcValue) {
		if (stack == null || stack.getItem() == null) return;
		if (emcValue < 0) emcValue = 0;
		ModContainer activeMod = Loader.instance().activeModContainer();
		String modId = activeMod == null ? null : activeMod.getModId();
		Map<String, Integer> modMap;
		if (customEMCforMod.containsKey(modId)) {
			modMap = customEMCforMod.get(modId);
		} else {
			modMap = new HashMap<String, Integer>();
			customEMCforMod.put(modId, modMap);
		}
		modMap.put(serializeToString(stack), emcValue);
	}

	protected String serializeToString(ItemStack stack) {
		String name = Item.itemRegistry.getNameForObject(stack.getItem());
		return String.format("%d@%s", stack.getItemDamage(), name);
	}
	protected NormalizedSimpleStack deserializeFromString(String s) {
		String[] splits = s.split("@", 2);
		return NormalizedSimpleStack.getNormalizedSimpleStackFor((Item)Item.itemRegistry.getObject(splits[1]), Integer.parseInt(splits[0]));
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
	public void addMappings(IMappingCollector<NormalizedSimpleStack, Integer> mapper, Configuration config) {
		final Map<String, Integer> priorityMap = new HashMap<String, Integer>();
		for (String modId: customEMCforMod.keySet()) {
			if (modId == null) continue;
			priorityMap.put(modId, config.getInt(modId + "priority", "customEMCPriorities", PRIORITY_DEFAULT_VALUE, PRIORITY_MIN_VALUE, PRIORITY_MAX_VALUE, "Priority for Mod with ModId = " + modId + ". 0 to disable. Values: " + customEMCforMod.get(modId).size()));
		}
		if (customEMCforMod.containsKey(null))
			priorityMap.put(null, config.getInt("modlessCustomEMCPriority", "", PRIORITY_DEFAULT_VALUE, PRIORITY_MIN_VALUE, PRIORITY_MAX_VALUE, "Priority for custom EMC values for which the ModId could not be determined. 0 to disable. Values: " + customEMCforMod.get(null).size()));

		List<String> modIds = new ArrayList<String>(customEMCforMod.keySet());
		Collections.sort(modIds, new Comparator<String>() {
			@Override
			public int compare(String a, String b) {
				//a < b => -1
				//a > b => +1
				//Reverse sorting so high priority comes first
				return -(priorityMap.get(a) - priorityMap.get(b));
			}
		});


		for(String modId : modIds) {
			if (priorityMap.get(modId) != 0) {
				for (Map.Entry<String, Integer> entry : customEMCforMod.get(modId).entrySet()) {
					NormalizedSimpleStack normStack = deserializeFromString(entry.getKey());
					mapper.setValue(normStack, entry.getValue(), IMappingCollector.FixedValue.FixAndInherit);
					PELogger.logInfo(String.format("%s setting value for %s to %s", modId == null ? "unknown mod" : modId, normStack, entry.getValue()));
				}
			}
		}
	}
}
