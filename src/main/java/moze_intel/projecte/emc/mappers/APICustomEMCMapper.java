package moze_intel.projecte.emc.mappers;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.ToIntFunction;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.imc.CustomEMCRegistration;
import moze_intel.projecte.api.mapper.EMCMapper;
import moze_intel.projecte.api.mapper.IEMCMapper;
import moze_intel.projecte.api.mapper.collector.IMappingCollector;
import moze_intel.projecte.api.nss.NSSItem;
import moze_intel.projecte.api.nss.NSSTag;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import moze_intel.projecte.emc.EMCMappingHandler;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.ResourceManager;

@EMCMapper
public class APICustomEMCMapper implements IEMCMapper<NormalizedSimpleStack, Long> {

	@EMCMapper.Instance
	public static final APICustomEMCMapper INSTANCE = new APICustomEMCMapper();
	private static final int PRIORITY_MIN_VALUE = 0;
	private static final int PRIORITY_MAX_VALUE = 512;
	private static final int PRIORITY_DEFAULT_VALUE = 1;

	private APICustomEMCMapper() {
	}

	private final Map<String, Map<NormalizedSimpleStack, Long>> customEMCforMod = new HashMap<>();

	public void registerCustomEMC(String modid, CustomEMCRegistration customEMCRegistration) {
		NormalizedSimpleStack stack = customEMCRegistration.stack();
		if (stack == null) {
			return;
		}
		long emcValue = customEMCRegistration.value();
		if (emcValue < 0) {
			emcValue = 0;
		}
		PECore.debugLog("Mod: '{}' registered a custom EMC value of: '{}' for the NormalizedSimpleStack: '{}'", modid, emcValue, stack);
		customEMCforMod.computeIfAbsent(modid, k -> new HashMap<>()).put(stack, emcValue);
	}

	@Override
	public String getName() {
		return "APICustomEMCMapper";
	}

	@Override
	public String getDescription() {
		return "Allows other mods to easily set EMC values using the ProjectEAPI";
	}

	@Override
	public void addMappings(IMappingCollector<NormalizedSimpleStack, Long> mapper, CommentedFileConfig config, ReloadableServerResources serverResources,
			RegistryAccess registryAccess, ResourceManager resourceManager) {
		Map<String, Integer> priorityMap = new HashMap<>();

		for (String modId : customEMCforMod.keySet()) {
			String configKey = getName() + ".priority." + (modId == null ? "__no_modid" : modId);
			int priority = EMCMappingHandler.getOrSetDefault(config, configKey, "Priority for this mod", PRIORITY_DEFAULT_VALUE);
			priorityMap.put(modId, priority);
		}

		List<String> modIds = new ArrayList<>(customEMCforMod.keySet());
		modIds.sort(Comparator.comparingInt((ToIntFunction<String>) priorityMap::get).reversed());

		for (String modId : modIds) {
			String modIdOrUnknown = modId == null ? "unknown mod" : modId;
			if (customEMCforMod.containsKey(modId)) {
				for (Map.Entry<NormalizedSimpleStack, Long> entry : customEMCforMod.get(modId).entrySet()) {
					NormalizedSimpleStack normStack = entry.getKey();
					long emc = entry.getValue();
					if (isAllowedToSet(modId, normStack, emc, config)) {
						mapper.setValueBefore(normStack, emc);
						if (normStack instanceof NSSTag nssTag) {
							//Note: We set it for each of the values in the tag to make sure it is properly taken into account when calculating the individual EMC values
							nssTag.forEachElement(normalizedSimpleStack -> mapper.setValueBefore(normalizedSimpleStack, emc));
						}
						PECore.debugLog("{} setting value for {} to {}", modIdOrUnknown, normStack, emc);
					} else {
						PECore.debugLog("Disallowed {} to set the value for {} to {}", modIdOrUnknown, normStack, emc);
					}
				}
			}
		}
	}

	private boolean isAllowedToSet(String modId, NormalizedSimpleStack stack, Long value, CommentedFileConfig config) {
		String resourceLocation;
		if (stack instanceof NSSItem nssItem) {
			//Allow both item names and tag locations
			resourceLocation = nssItem.getResourceLocation().toString();
		} else {
			resourceLocation = "IntermediateFakeItemsUsedInRecipes:";
		}
		String modForItem = resourceLocation.substring(0, resourceLocation.indexOf(':'));
		String configPath = String.format("permissions.%s.%s", modId, modForItem);
		String comment = String.format("Allow mod '%s' to set and or remove values for mod '%s'. Options: [both, set, remove, none]", modId, modForItem);
		String permission = EMCMappingHandler.getOrSetDefault(config, configPath, comment, "both");
		if (permission.equals("both")) {
			return true;
		}
		if (value == 0) {
			return permission.equals("remove");
		}
		return permission.equals("set");
	}
}