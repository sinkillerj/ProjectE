package moze_intel.projecte.emc;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.event.EMCRemapEvent;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.emc.generators.BigFractionToLongGenerator;
import moze_intel.projecte.emc.arithmetics.HiddenBigFractionArithmetic;
import moze_intel.projecte.emc.arithmetics.IValueArithmetic;
import moze_intel.projecte.emc.collector.LongToBigFractionCollector;
import moze_intel.projecte.emc.collector.DumpToFileCollector;
import moze_intel.projecte.emc.collector.IExtendedMappingCollector;
import moze_intel.projecte.emc.generators.IValueGenerator;
import moze_intel.projecte.emc.json.NSSItem;
import moze_intel.projecte.emc.json.NormalizedSimpleStack;
import moze_intel.projecte.emc.mappers.APICustomConversionMapper;
import moze_intel.projecte.emc.mappers.APICustomEMCMapper;
import moze_intel.projecte.emc.mappers.CraftingMapper;
import moze_intel.projecte.emc.mappers.CustomEMCMapper;
import moze_intel.projecte.emc.mappers.IEMCMapper;
import moze_intel.projecte.emc.mappers.customConversions.CustomConversionMapper;
import moze_intel.projecte.emc.pregenerated.PregeneratedEMC;
import moze_intel.projecte.playerData.Transmutation;
import net.minecraft.item.Item;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.math3.fraction.BigFraction;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class EMCMapper 
{
	public static final Map<Item, Long> emc = new LinkedHashMap<>();
	public static double covalenceLoss = ProjectEConfig.difficulty.covalenceLoss.get();
	public static boolean covalenceLossRounding = ProjectEConfig.difficulty.covalenceLossRounding.get();

	public static <T> T getOrSetDefault(CommentedFileConfig config, String key, String comment, T defaultValue)
	{
		T val = config.get(key);
		if (val == null)
		{
			val = defaultValue;
			config.set(key, val);
			config.setComment(key, comment);
		}
		return val;
	}

	public static void map(IResourceManager resourceManager)
	{
		List<IEMCMapper<NormalizedSimpleStack, Long>> emcMappers = Arrays.asList(
				APICustomEMCMapper.instance,
				new CustomConversionMapper(),
				new CustomEMCMapper(),
				new CraftingMapper(),
				// todo 1.13 new moze_intel.projecte.emc.mappers.FluidMapper(),
				APICustomConversionMapper.instance
		);
		SimpleGraphMapper<NormalizedSimpleStack, BigFraction, IValueArithmetic<BigFraction>> mapper = new SimpleGraphMapper<>(new HiddenBigFractionArithmetic());
		IValueGenerator<NormalizedSimpleStack, Long> valueGenerator = new BigFractionToLongGenerator<>(mapper);
		IExtendedMappingCollector<NormalizedSimpleStack, Long, IValueArithmetic<BigFraction>> mappingCollector = new LongToBigFractionCollector<>(mapper);

		Path path = Paths.get("config", PECore.MODNAME, "mapping.toml");
		try {
			path.toFile().createNewFile();
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
			mappingCollector = new DumpToFileCollector<>(new File(PECore.CONFIG_DIR, "mappingdump.json"), mappingCollector);
		}

		File pregeneratedEmcFile = Paths.get("config", PECore.MODNAME, "pregenerated_emc.json").toFile();
		Map<NormalizedSimpleStack, Long> graphMapperValues;
		if (shouldUsePregenerated && pregeneratedEmcFile.canRead() && PregeneratedEMC.tryRead(pregeneratedEmcFile, graphMapperValues = new HashMap<>()))
		{
			PECore.LOGGER.info(String.format("Loaded %d values from pregenerated EMC File", graphMapperValues.size()));
		}
		else
		{
			SimpleGraphMapper.setLogFoundExploits(logFoundExploits);

			PECore.debugLog("Starting to collect Mappings...");
			for (IEMCMapper<NormalizedSimpleStack, Long> emcMapper : emcMappers)
			{
				try
				{
					if (getOrSetDefault(config, "enabledMappers." + emcMapper.getName(), emcMapper.getDescription(), emcMapper.isAvailable()))
					{
						DumpToFileCollector.currentGroupName = emcMapper.getName();
						emcMapper.addMappings(mappingCollector, config, resourceManager);
						PECore.debugLog("Collected Mappings from " + emcMapper.getClass().getName());
					}
				} catch (Exception e)
				{
					PECore.LOGGER.fatal("Exception during Mapping Collection from Mapper {}. PLEASE REPORT THIS! EMC VALUES MIGHT BE INCONSISTENT!", emcMapper.getClass().getName());
					e.printStackTrace();
				}
			}
			DumpToFileCollector.currentGroupName = "NSSHelper";
			NormalizedSimpleStack.addMappings(mappingCollector);

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
				try
				{
					PregeneratedEMC.write(pregeneratedEmcFile, graphMapperValues);
					PECore.debugLog("Wrote Pregen-file!");
				} catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}


		for (Map.Entry<NormalizedSimpleStack, Long> entry: graphMapperValues.entrySet()) {
			NSSItem normStackItem = (NSSItem)entry.getKey();
			Item obj = ForgeRegistries.ITEMS.getValue(normStackItem.itemName);
			if (obj != null)
			{
				emc.put(obj, entry.getValue());
			} else {
				PECore.LOGGER.warn("Could not add EMC value for {}, item does not exist!", normStackItem.itemName);
			}
		}

		MinecraftForge.EVENT_BUS.post(new EMCRemapEvent());
		Transmutation.cacheFullKnowledge();
		FuelMapper.loadMap();
	}

	private static void filterEMCMap(Map<NormalizedSimpleStack, Long> map) {
		map.entrySet().removeIf(e -> !(e.getKey() instanceof NSSItem)
										|| e.getValue() <= 0);
	}

	public static long getEmcValue(IItemProvider item)
	{
		return emc.get(item.asItem());
	}

	public static void clearMaps() {
		emc.clear();
	}
}
