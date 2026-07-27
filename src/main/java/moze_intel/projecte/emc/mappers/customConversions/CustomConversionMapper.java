package moze_intel.projecte.emc.mappers.customConversions;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongSortedMaps;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.conversion.ConversionGroup;
import moze_intel.projecte.api.conversion.CustomConversion;
import moze_intel.projecte.api.conversion.CustomConversionFile;
import moze_intel.projecte.api.mapper.EMCMapper;
import moze_intel.projecte.api.mapper.IEMCMapper;
import moze_intel.projecte.api.mapper.collector.IMappingCollector;
import moze_intel.projecte.api.nss.NSSFake;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import moze_intel.projecte.config.PEConfigTranslations;
import moze_intel.projecte.impl.codec.PECodecHelper;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.apache.logging.log4j.util.TriConsumer;

@EMCMapper
public class CustomConversionMapper implements IEMCMapper<NormalizedSimpleStack, Long> {

	private static final FileToIdConverter CONVERSION_LISTER = FileToIdConverter.json("pe_custom_conversions");
	private static final TriConsumer<IMappingCollector<NormalizedSimpleStack, Long>, NormalizedSimpleStack, CustomConversion> CONVERSION_CONSUMER =
			(collector, nss, conversion) ->
			collector.setValueFromConversion(conversion.count(), nss, conversion.ingredients());

	@Override
	public String getName() {
		return PEConfigTranslations.MAPPING_CUSTOM_CONVERSION_MAPPER.title();
	}

	@Override
	public String getTranslationKey() {
		return PEConfigTranslations.MAPPING_CUSTOM_CONVERSION_MAPPER.getTranslationKey();
	}

	@Override
	public String getDescription() {
		return PEConfigTranslations.MAPPING_CUSTOM_CONVERSION_MAPPER.tooltip();
	}

	@Override
	public void addMappings(IMappingCollector<NormalizedSimpleStack, Long> mapper, ReloadableServerResources serverResources,
			RegistryAccess registryAccess, ResourceManager resourceManager) {
		Map<ResourceLocation, CustomConversionFile> files = load(registryAccess, resourceManager);
		addMappingsFromFiles(files, mapper);
	}

	private static Map<ResourceLocation, CustomConversionFile> load(RegistryAccess registryAccess, ResourceManager resourceManager) {
		Map<ResourceLocation, CustomConversionFile> loading = new HashMap<>();

		RegistryOps<JsonElement> serializationContext = registryAccess.createSerializationContext(JsonOps.INSTANCE);
		try {
			// Find all data/<domain>/pe_custom_conversions/foo/bar.json
			for (Map.Entry<ResourceLocation, List<Resource>> entry : CONVERSION_LISTER.listMatchingResourceStacks(resourceManager).entrySet()) {
				ResourceLocation file = entry.getKey();//<domain>:foo/bar
				ResourceLocation conversionId = CONVERSION_LISTER.fileToId(file);

				PECore.debugLog("Considering file {}, ID {}", file, conversionId);
				NSSFake.setCurrentNamespace(conversionId.toString());

				// Iterate through all copies of this conversion, from lowest to highest priority datapack, merging the results together
				for (Resource resource : entry.getValue()) {
					try (Reader reader = resource.openAsReader()) {
						Optional<CustomConversionFile> fileOptional = PECodecHelper.read(serializationContext, reader, CustomConversionFile.CODEC, "custom conversion file");
						//noinspection OptionalIsPresent - Capturing lambda
						if (fileOptional.isPresent()) {
							loading.merge(conversionId, fileOptional.get(), CustomConversionFile::merge);
						}
					} catch (IOException e) {
						PECore.LOGGER.error("Could not load resource {}", file, e);
					}
				}
			}
		} finally {
			NSSFake.resetNamespace();
		}
		return loading;
	}

	static void addMappingsFromFiles(Map<ResourceLocation, CustomConversionFile> files, IMappingCollector<NormalizedSimpleStack, Long> mapper) {
		//FileToIdConverter returns a Map without an ordering contract. Apply files by resource ID so conflicting fixed values and forced
		//conversions have stable precedence that cannot change when an unrelated file changes the backing map's iteration order.
		List<ResourceLocation> orderedIds = new ArrayList<>(files.keySet());
		orderedIds.sort(ResourceLocation::compareNamespaced);
		for (ResourceLocation id : orderedIds) {
			PECore.debugLog("Adding mappings from custom conversion file {}", id);
			addMappingsFromFile(files.get(id), mapper);
		}
	}

	private static void addMappingsFromFile(CustomConversionFile file, IMappingCollector<NormalizedSimpleStack, Long> mapper) {
		for (Map.Entry<String, ConversionGroup> entry : file.groups().entrySet()) {
			ConversionGroup group = entry.getValue();
			PECore.debugLog("Adding conversions from group '{}' with comment '{}'", entry.getKey(), group.comment());
			for (CustomConversion conversion : group.conversions()) {
				mapper.addConversion(conversion.count(), conversion.output(), conversion.ingredients());
			}
		}

		//Note: We set it for each of the values in the tag to make sure it is properly taken into account when calculating the individual EMC values
		for (Iterator<Object2LongMap.Entry<NormalizedSimpleStack>> iterator = Object2LongSortedMaps.fastIterator(file.values().setValueBefore()); iterator.hasNext(); ) {
			Object2LongMap.Entry<NormalizedSimpleStack> entry = iterator.next();
			entry.getKey().forSelfAndEachElement(mapper, entry.getLongValue(), IMappingCollector::setValueBefore);
		}

		//Note: We set it for each of the values in the tag to make sure it is properly taken into account when calculating the individual EMC values
		for (Iterator<Object2LongMap.Entry<NormalizedSimpleStack>> iterator = Object2LongSortedMaps.fastIterator(file.values().setValueAfter()); iterator.hasNext(); ) {
			Object2LongMap.Entry<NormalizedSimpleStack> entry = iterator.next();
			entry.getKey().forSelfAndEachElement(mapper, entry.getLongValue(), IMappingCollector::setValueAfter);
		}

		for (CustomConversion customConversion : file.values().conversions()) {
			if (customConversion.propagateTags()) {
				customConversion.output().forSelfAndEachElement(mapper, customConversion, CONVERSION_CONSUMER);
			} else {
				CONVERSION_CONSUMER.accept(mapper, customConversion.output(), customConversion);
			}
		}
	}
}
