package moze_intel.projecte.integration.crafttweaker.mappers;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import java.util.HashMap;
import java.util.Map;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.mapper.EMCMapper;
import moze_intel.projecte.api.mapper.IEMCMapper;
import moze_intel.projecte.api.mapper.collector.IMappingCollector;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.NotNull;

@EMCMapper(requiredMods = "crafttweaker")
public class CrTCustomEMCMapper implements IEMCMapper<NormalizedSimpleStack, Long> {

	private static final Map<NormalizedSimpleStack, Long> customEmcValues = new HashMap<>();

	public static void registerCustomEMC(@NotNull NormalizedSimpleStack stack, long emcValue) {
		customEmcValues.put(stack, emcValue);
	}

	public static void unregisterNSS(@NotNull NormalizedSimpleStack stack) {
		customEmcValues.remove(stack);
	}

	@Override
	public void addMappings(IMappingCollector<NormalizedSimpleStack, Long> mapper, CommentedFileConfig config, ReloadableServerResources serverResources,
			RegistryAccess registryAccess, ResourceManager resourceManager) {
		customEmcValues.forEach((normStack, value) -> {
			//Note: We set it for each of the values in the tag to make sure it is properly taken into account when calculating the individual EMC values
			normStack.forSelfAndEachElement(nss -> mapper.setValueBefore(nss, value));
			PECore.debugLog("CraftTweaker setting value for {} to {}", normStack, value);
		});
	}

	@Override
	public String getName() {
		return "CrTCustomEMCMapper";
	}

	@Override
	public String getDescription() {
		return "Allows setting EMC values through CraftTweaker. This behaves similarly to if someone used the custom emc file instead.";
	}
}