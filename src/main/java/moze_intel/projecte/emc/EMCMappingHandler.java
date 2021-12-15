package moze_intel.projecte.emc;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.ItemInfo;
import moze_intel.projecte.api.event.EMCRemapEvent;
import moze_intel.projecte.api.mapper.IEMCMapper;
import moze_intel.projecte.api.mapper.arithmetic.IValueArithmetic;
import moze_intel.projecte.api.mapper.collector.IExtendedMappingCollector;
import moze_intel.projecte.api.mapper.generator.IValueGenerator;
import moze_intel.projecte.api.nss.NSSItem;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.emc.arithmetic.HiddenBigFractionArithmetic;
import moze_intel.projecte.emc.collector.DumpToFileCollector;
import moze_intel.projecte.emc.collector.LongToBigFractionCollector;
import moze_intel.projecte.emc.generator.BigFractionToLongGenerator;
import moze_intel.projecte.emc.mappers.TagMapper;
import moze_intel.projecte.emc.pregenerated.PregeneratedEMC;
import moze_intel.projecte.network.packets.to_client.SyncEmcPKT.EmcPKTInfo;
import moze_intel.projecte.utils.AnnotationHelper;
import net.minecraft.resources.DataPackRegistries;
import net.minecraft.resources.IResourceManager;
import net.minecraftforge.common.MinecraftForge;
import org.apache.commons.math3.fraction.BigFraction;

public final class EMCMappingHandler {

	private static final List<IEMCMapper<NormalizedSimpleStack, Long>> mappers = new ArrayList<>();
	private static final Map<ItemInfo, Long> emc = new HashMap<>();

	public static void loadMappers() {
		//If we don't have any mappers loaded try to load them
		if (mappers.isEmpty()) {
			//Add all the EMC mappers we have encountered
			mappers.addAll(AnnotationHelper.getEMCMappers());
			//Manually register the Tag Mapper to ensure that it is registered last so that it can "fix" all the tags used in any of the other mappers
			// This also has the side effect to make sure that we can use EMC_MAPPERS.isEmpty to check if we have attempted to initialize our cache yet
			mappers.add(new TagMapper());
		}
	}

	public static <T> T getOrSetDefault(CommentedFileConfig config, String key, String comment, T defaultValue) {
		T val = config.get(key);
		if (val == null) {
			val = defaultValue;
			config.set(key, val);
			config.setComment(key, comment);
		}
		return val;
	}

	public static void map(DataPackRegistries dataPackRegistries, IResourceManager resourceManager) {
		//Start by clearing the cached map so if values are removed say by setting EMC to zero then we respect the change
		clearEmcMap();
		SimpleGraphMapper<NormalizedSimpleStack, BigFraction, IValueArithmetic<BigFraction>> mapper = new SimpleGraphMapper<>(new HiddenBigFractionArithmetic());
		IValueGenerator<NormalizedSimpleStack, Long> valueGenerator = new BigFractionToLongGenerator<>(mapper);
		IExtendedMappingCollector<NormalizedSimpleStack, Long, IValueArithmetic<BigFraction>> mappingCollector = new LongToBigFractionCollector<>(mapper);

		Path path = ProjectEConfig.CONFIG_DIR.resolve("mapping.toml");
		try {
			if (path.toFile().createNewFile()) {
				PECore.debugLog("Created mapping.toml");
			}
		} catch (IOException ex) {
			PECore.LOGGER.error("Couldn't create mapping.toml", ex);
		}

		CommentedFileConfig config = CommentedFileConfig.builder(path).build();
		config.load();

		boolean dumpToFile = getOrSetDefault(config, "general.dumpEverythingToFile", "Want to take a look at the internals of EMC Calculation? Enable this to write all the conversions and setValue-Commands to config/ProjectE/mappingdump.json", false);
		boolean shouldUsePregenerated = getOrSetDefault(config, "general.pregenerate", "When the next EMC mapping occurs write the results to config/ProjectE/pregenerated_emc.json and only ever run the mapping again" +
																					   " when that file does not exist, this setting is set to false, or an error occurred parsing that file.", false);
		boolean logFoundExploits = getOrSetDefault(config, "general.logEMCExploits", "Log known EMC Exploits. This can not and will not find all possible exploits. " +
																					 "This will only find exploits that result in fixed/custom emc values that the algorithm did not overwrite. " +
																					 "Exploits that derive from conversions that are unknown to ProjectE will not be found.", true);

		if (dumpToFile) {
			mappingCollector = new DumpToFileCollector<>(ProjectEConfig.CONFIG_DIR.resolve("mappingdump.json").toFile(), mappingCollector);
		}

		File pregeneratedEmcFile = Paths.get("config", PECore.MODNAME, "pregenerated_emc.json").toFile();
		Map<NormalizedSimpleStack, Long> graphMapperValues;
		if (shouldUsePregenerated && pregeneratedEmcFile.canRead() && PregeneratedEMC.tryRead(pregeneratedEmcFile, graphMapperValues = new HashMap<>())) {
			PECore.LOGGER.info("Loaded {} values from pregenerated EMC File", graphMapperValues.size());
		} else {
			SimpleGraphMapper.setLogFoundExploits(logFoundExploits);

			PECore.debugLog("Starting to collect Mappings...");
			for (IEMCMapper<NormalizedSimpleStack, Long> emcMapper : mappers) {
				try {
					if (getOrSetDefault(config, "enabledMappers." + emcMapper.getName(), emcMapper.getDescription(), emcMapper.isAvailable())) {
						DumpToFileCollector.currentGroupName = emcMapper.getName();
						emcMapper.addMappings(mappingCollector, config, dataPackRegistries, resourceManager);
						PECore.debugLog("Collected Mappings from " + emcMapper.getClass().getName());
					}
				} catch (Exception e) {
					PECore.LOGGER.fatal("Exception during Mapping Collection from Mapper {}. PLEASE REPORT THIS! EMC VALUES MIGHT BE INCONSISTENT!", emcMapper.getClass().getName(), e);
				}
			}
			DumpToFileCollector.currentGroupName = "NSSHelper";

			PECore.debugLog("Mapping Collection finished");
			mappingCollector.finishCollection();

			PECore.debugLog("Starting to generate Values:");

			config.save();
			config.close();

			graphMapperValues = valueGenerator.generateValues();
			PECore.debugLog("Generated Values...");

			filterEMCMap(graphMapperValues);

			if (shouldUsePregenerated) {
				//Should have used pregenerated, but the file was not read => regenerate.
				try {
					PregeneratedEMC.write(pregeneratedEmcFile, graphMapperValues);
					PECore.debugLog("Wrote Pregen-file!");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		for (Map.Entry<NormalizedSimpleStack, Long> entry : graphMapperValues.entrySet()) {
			NSSItem normStackItem = (NSSItem) entry.getKey();
			ItemInfo obj = ItemInfo.fromNSS(normStackItem);
			if (obj != null) {
				emc.put(obj, entry.getValue());
			} else {
				PECore.LOGGER.warn("Could not add EMC value for {}, item does not exist!", normStackItem.getResourceLocation());
			}
		}

		MinecraftForge.EVENT_BUS.post(new EMCRemapEvent());
		FuelMapper.loadMap(dataPackRegistries.getTags());
	}

	private static void filterEMCMap(Map<NormalizedSimpleStack, Long> map) {
		map.entrySet().removeIf(e -> !(e.getKey() instanceof NSSItem) || ((NSSItem) e.getKey()).representsTag() || e.getValue() <= 0);
	}

	public static int getEmcMapSize() {
		return emc.size();
	}

	public static boolean hasEmcValue(@Nonnull ItemInfo info) {
		return emc.containsKey(info);
	}

	/**
	 * Gets the stored emc value or zero if there is no entry in the map for the given value.
	 */
	public static long getStoredEmcValue(@Nonnull ItemInfo info) {
		return emc.getOrDefault(info, 0L);
	}

	public static void clearEmcMap() {
		emc.clear();
	}

	/**
	 * Returns a modifiable set of all the mapped {@link ItemInfo}
	 */
	public static Set<ItemInfo> getMappedItems() {
		return new HashSet<>(emc.keySet());
	}

	public static void fromPacket(EmcPKTInfo[] data) {
		emc.clear();
		for (EmcPKTInfo info : data) {
			emc.put(ItemInfo.fromItem(info.getItem(), info.getNbt()), info.getEmc());
		}
	}

	public static EmcPKTInfo[] createPacketData() {
		EmcPKTInfo[] ret = new EmcPKTInfo[emc.size()];
		int i = 0;
		for (Map.Entry<ItemInfo, Long> entry : emc.entrySet()) {
			ItemInfo info = entry.getKey();
			ret[i] = new EmcPKTInfo(info.getItem(), info.getNBT(), entry.getValue());
			i++;
		}
		return ret;
	}
}