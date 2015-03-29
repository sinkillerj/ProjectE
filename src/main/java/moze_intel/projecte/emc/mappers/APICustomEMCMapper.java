package moze_intel.projecte.emc.mappers;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.emc.IMappingCollector;
import moze_intel.projecte.emc.NormalizedSimpleStack;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;

import java.util.HashMap;
import java.util.Map;

public class APICustomEMCMapper implements IEMCMapper<NormalizedSimpleStack, Integer>, ProjectEAPI.IRegisterCustomEMC {
	public static APICustomEMCMapper instance = new APICustomEMCMapper();
	private APICustomEMCMapper() {}

	Map<String, Map<NormalizedSimpleStack, Integer>> customEMCforMod = new HashMap<String, Map<NormalizedSimpleStack, Integer>>();
	Map<NormalizedSimpleStack, Integer> customEMCs = new HashMap<NormalizedSimpleStack,Integer>();

	public void registerCustomEMC(ItemStack stack, int emcValue) {
		if (emcValue < 0) emcValue = 0;
		ModContainer activeMod = Loader.instance().activeModContainer();
		if (activeMod == null) {
			customEMCs.put(NormalizedSimpleStack.getNormalizedSimpleStackFor(stack), emcValue);
		} else {
			String modId = activeMod.getModId();
			Map<NormalizedSimpleStack, Integer> modMap;
			if (customEMCforMod.containsKey(modId)) {
				modMap = customEMCforMod.get(modId);
			} else {
				modMap = new HashMap<NormalizedSimpleStack, Integer>();
				customEMCforMod.put(modId, modMap);
			}
			modMap.put(NormalizedSimpleStack.getNormalizedSimpleStackFor(stack), emcValue);
		}
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
		if (config.getBoolean("enableModlessCustomEMC", "", true, "Custom EMC values for which the ModID could not be determined. Values: " + customEMCs.size())) {
			for (Map.Entry<NormalizedSimpleStack, Integer> entry : customEMCs.entrySet()) {
				mapper.setValue(entry.getKey(), entry.getValue(), IMappingCollector.FixedValue.FixAndInherit);
			}
		}
		for(Map.Entry<String, Map<NormalizedSimpleStack, Integer>> outerentry : customEMCforMod.entrySet()) {
			if (config.getBoolean("enable" + outerentry.getKey(), "enableCustomEMCforMod", true, "Custom EMC for Mod with ModId = " + outerentry.getKey() + ". Values: " + outerentry.getValue().size())) {
				for (Map.Entry<NormalizedSimpleStack, Integer> entry : outerentry.getValue().entrySet()) {
					mapper.setValue(entry.getKey(), entry.getValue(), IMappingCollector.FixedValue.FixAndInherit);
				}
			}
		}
	}
}
