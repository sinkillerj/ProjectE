package moze_intel.projecte.emc.mappers.customConversions;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.conversion.CustomConversion;
import moze_intel.projecte.api.conversion.CustomConversionFile;
import moze_intel.projecte.api.mapper.EMCMapper;
import moze_intel.projecte.api.mapper.IEMCMapper;
import moze_intel.projecte.api.mapper.collector.IMappingCollector;
import moze_intel.projecte.api.nss.NSSFake;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import moze_intel.projecte.impl.codec.PECodecHelper;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

@EMCMapper
public class CustomConversionMapper implements IEMCMapper<NormalizedSimpleStack, Long> {

	@Override
	public String getName() {
		return "CustomConversionMapper";
	}

	@Override
	public String getDescription() {
		return "Loads json files within datapacks (data/<domain>/pe_custom_conversions/*.json) to add values and conversions";
	}

	@Override
	public void addMappings(IMappingCollector<NormalizedSimpleStack, Long> mapper, CommentedFileConfig config, ReloadableServerResources serverResources,
			RegistryAccess registryAccess, ResourceManager resourceManager) {
		Map<ResourceLocation, CustomConversionFile> files = load(resourceManager);
		for (CustomConversionFile file : files.values()) {
			addMappingsFromFile(file, mapper);
		}
	}

	private static Map<ResourceLocation, CustomConversionFile> load(ResourceManager resourceManager) {
		Map<ResourceLocation, CustomConversionFile> loading = new HashMap<>();

		String folder = "pe_custom_conversions";
		String extension = ".json";
		int folderLength = folder.length();
		int extensionLength = extension.length();

		// Find all data/<domain>/pe_custom_conversions/foo/bar.json
		resourceManager.listResourceStacks(folder, n -> n.getPath().endsWith(extension)).forEach((file /*<domain>:foo/bar*/, resources) -> {
			ResourceLocation conversionId = file.withPath(path -> path.substring(folderLength + 1, path.length() - extensionLength));

			PECore.LOGGER.info("Considering file {}, ID {}", file, conversionId);
			NSSFake.setCurrentNamespace(conversionId.toString());

			// Iterate through all copies of this conversion, from lowest to highest priority datapack, merging the results together
			for (Resource resource : resources) {
				try (Reader reader = resource.openAsReader()) {
					PECodecHelper.read(reader, CustomConversionFile.CODEC, "custom conversion file")
							.ifPresent(result -> loading.merge(conversionId, result, CustomConversionFile::merge));
				} catch (IOException e) {
					PECore.LOGGER.error("Could not load resource {}", file, e);
				}
			}
		});
		NSSFake.resetNamespace();
		return loading;
	}

	private static void addMappingsFromFile(CustomConversionFile file, IMappingCollector<NormalizedSimpleStack, Long> mapper) {
		file.groups().forEach((name, group) -> {
			PECore.debugLog("Adding conversions from group '{}' with comment '{}'", name, group.comment());
			for (CustomConversion conversion : group.conversions()) {
				mapper.addConversion(conversion.count(), conversion.output(), conversion.ingredients());
			}
		});

		//Note: We set it for each of the values in the tag to make sure it is properly taken into account when calculating the individual EMC values
		file.values().setValueBefore().forEach((stack, value) -> stack.forSelfAndEachElement(nss -> mapper.setValueBefore(nss, value)));

		//Note: We set it for each of the values in the tag to make sure it is properly taken into account when calculating the individual EMC values
		file.values().setValueAfter().forEach((stack, value) -> stack.forSelfAndEachElement(nss -> mapper.setValueAfter(nss, value)));

		for (CustomConversion conversion : file.values().conversions()) {
			Consumer<NormalizedSimpleStack> consumer = nss -> mapper.setValueFromConversion(conversion.count(), nss, conversion.ingredients());
			if (conversion.propagateTags()) {
				conversion.output().forSelfAndEachElement(consumer);
			} else {
				consumer.accept(conversion.output());
			}
		}
	}
}