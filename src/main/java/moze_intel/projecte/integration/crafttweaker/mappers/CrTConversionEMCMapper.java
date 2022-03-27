package moze_intel.projecte.integration.crafttweaker.mappers;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.mapper.EMCMapper;
import moze_intel.projecte.api.mapper.IEMCMapper;
import moze_intel.projecte.api.mapper.collector.IMappingCollector;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.NotNull;

@EMCMapper(requiredMods = "crafttweaker")
public class CrTConversionEMCMapper implements IEMCMapper<NormalizedSimpleStack, Long> {

	private static final List<CrTConversion> storedConversions = new ArrayList<>();

	public static void addConversion(@NotNull CrTConversion conversion) {
		storedConversions.add(conversion);
	}

	public static void removeConversion(@NotNull CrTConversion conversion) {
		storedConversions.remove(conversion);
	}

	@Override
	public void addMappings(IMappingCollector<NormalizedSimpleStack, Long> mapper, CommentedFileConfig config, ReloadableServerResources serverResources,
			ResourceManager resourceManager) {
		for (CrTConversion apiConversion : storedConversions) {
			mapper.addConversion(apiConversion.amount, apiConversion.output, apiConversion.ingredients);
			PECore.debugLog("CraftTweaker setting value for {}", apiConversion.output);
		}
	}

	@Override
	public String getName() {
		return "CrTConversionEMCMapper";
	}

	@Override
	public String getDescription() {
		return "Allows adding custom conversions through CraftTweaker. This behaves similarly to if someone used a custom conversion file instead.";
	}

	public record CrTConversion(NormalizedSimpleStack output, int amount, Map<NormalizedSimpleStack, Integer> ingredients) {}
}