package moze_intel.projecte.emc;

import moze_intel.projecte.PECore;
import moze_intel.projecte.emc.collector.DumpToFileCollector;
import moze_intel.projecte.api.event.EMCRemapEvent;
import moze_intel.projecte.emc.collector.IMappingCollector;
import moze_intel.projecte.emc.collector.IntToFractionCollector;
import moze_intel.projecte.emc.generators.FractionToIntGenerator;
import moze_intel.projecte.emc.generators.IMultiValueGenerator;
import moze_intel.projecte.emc.generators.IValueGenerator;
import moze_intel.projecte.emc.generators.SameValueMultiGenerator;
import moze_intel.projecte.emc.mappers.Chisel2Mapper;
import moze_intel.projecte.emc.arithmetics.HiddenFractionArithmetic;
import moze_intel.projecte.emc.mappers.APICustomEMCMapper;
import moze_intel.projecte.emc.mappers.CraftingMapper;
import moze_intel.projecte.emc.mappers.CustomEMCMapper;
import moze_intel.projecte.emc.mappers.IEMCMapper;
import moze_intel.projecte.emc.mappers.LazyMapper;
import moze_intel.projecte.emc.mappers.OreDictionaryMapper;
import moze_intel.projecte.emc.mappers.SmeltingMapper;
import moze_intel.projecte.emc.mappers.customConversions.CustomConversionMapper;
import moze_intel.projecte.emc.pregenerated.PregeneratedEMC;
import moze_intel.projecte.playerData.Transmutation;
import moze_intel.projecte.utils.PELogger;
import moze_intel.projecte.utils.PrefixConfiguration;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.math.Fraction;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public final class EMCMapper 
{
	public static Map<SimpleStack, Integer> emcForCreation = Maps.newHashMap();
	public static Map<SimpleStack, Integer> emcForDestruction = Maps.newHashMap();

	public static void map()
	{
		List<IEMCMapper<NormalizedSimpleStack, Integer>> emcMappers = Arrays.asList(
				new OreDictionaryMapper(),
				new LazyMapper(),
				new Chisel2Mapper(),
				APICustomEMCMapper.instance,
				new CustomConversionMapper(),
				new CustomEMCMapper(),
				new CraftingMapper(),
				new moze_intel.projecte.emc.mappers.FluidMapper(),
				new SmeltingMapper()
		);
		SimpleGraphMapper<NormalizedSimpleStack, Fraction> mapper = new SimpleGraphMapper<NormalizedSimpleStack, Fraction>(new HiddenFractionArithmetic());
		IValueGenerator<NormalizedSimpleStack, Integer> valueGenerator = new FractionToIntGenerator(mapper);
		IMultiValueGenerator<NormalizedSimpleStack, Integer> multiValueGenerator = new SameValueMultiGenerator<NormalizedSimpleStack, Integer>(valueGenerator);
		IMappingCollector<NormalizedSimpleStack, Integer> mappingCollector = new IntToFractionCollector<NormalizedSimpleStack>(mapper);

		Map<NormalizedSimpleStack, Integer> graphMapperValuesForCreation = Maps.newHashMap();
		Map<NormalizedSimpleStack, Integer> graphMapperValuesForDestruction = Maps.newHashMap();


		Configuration config = new Configuration(new File(PECore.CONFIG_DIR, "mapping.cfg"));
		config.load();

		if (config.getBoolean("dumpEverythingToFile", "general", false,"Want to take a look at the internals of EMC Calculation? Enable this to write all the conversions and setValue-Commands to config/ProjectE/mappingdump.json")) {
			mappingCollector = new DumpToFileCollector(new File(PECore.CONFIG_DIR, "mappingdump.json"), mappingCollector);
		}

		boolean shouldUsePregenerated = config.getBoolean("pregenerate", "general", false, "When the next EMC mapping occurs write the results to config/ProjectE/pregenerated_emc.json and only ever run the mapping again" +
						" when that file does not exist, this setting is set to false, or an error occurred parsing that file.");

		if (shouldUsePregenerated && PECore.PREGENERATED_EMC_FILE.canRead() && PregeneratedEMC.tryRead(PECore.PREGENERATED_EMC_FILE, graphMapperValuesForCreation, graphMapperValuesForDestruction))
		{
			PELogger.logInfo(String.format("Loaded %d create-values and %d-destroy from pregenerated EMC File", graphMapperValuesForCreation.size(), graphMapperValuesForDestruction.size()));
		}
		else
		{


			SimpleGraphMapper.setLogFoundExploits(config.getBoolean("logEMCExploits", "general", true,
					"Log known EMC Exploits. This can not and will not find all possible exploits. " +
							"This will only find exploits that result in fixed/custom emc values that the algorithm did not overwrite. " +
							"Exploits that derive from conversions that are unknown to ProjectE will not be found."
			));

			PELogger.logInfo("Starting to collect Mappings...");
			for (IEMCMapper<NormalizedSimpleStack, Integer> emcMapper : emcMappers)
			{
				try
				{
					if (config.getBoolean(emcMapper.getName(), "enabledMappers", emcMapper.isAvailable(), emcMapper.getDescription()) && emcMapper.isAvailable())
					{
						DumpToFileCollector.currentGroupName = emcMapper.getName();
						emcMapper.addMappings(mappingCollector, new PrefixConfiguration(config, "mapperConfigurations." + emcMapper.getName()));
						PELogger.logInfo("Collected Mappings from " + emcMapper.getClass().getName());
					}
				} catch (Exception e)
				{
					PELogger.logFatal(String.format("Exception during Mapping Collection from Mapper %s. PLEASE REPORT THIS! EMC VALUES MIGHT BE INCONSISTENT!", emcMapper.getClass().getName()));
					e.printStackTrace();
				}
			}
			DumpToFileCollector.currentGroupName = "NSSHelper";
			NormalizedSimpleStack.addMappings(mappingCollector);

			PELogger.logInfo("Mapping Collection finished");
			mappingCollector.finishCollection();

			PELogger.logInfo("Starting to generate Values:");

			config.save();

			multiValueGenerator.generateValues(graphMapperValuesForCreation, graphMapperValuesForDestruction);
			PELogger.logInfo("Generated Values...");

			if (shouldUsePregenerated) {
				//Should have used pregenerated, but the file was not read => regenerate.
				try
				{
					PregeneratedEMC.write(PECore.PREGENERATED_EMC_FILE, graphMapperValuesForCreation, graphMapperValuesForDestruction);
					PELogger.logInfo("Wrote Pregen-file!");
				} catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}

		emcForCreation = Maps.newHashMap();
		for (Map.Entry<NormalizedSimpleStack, Integer> entry: graphMapperValuesForCreation.entrySet()) {
			if (!shouldBeFiltered(entry))
			{
				NormalizedSimpleStack.NSSItem normStackItem = (NormalizedSimpleStack.NSSItem)entry.getKey();
				emcForCreation.put(new SimpleStack(normStackItem.id, 1, normStackItem.damage), entry.getValue());
			}
		}
		emcForCreation = ImmutableMap.copyOf(emcForCreation);

		emcForDestruction = Maps.newHashMap();
		for (Map.Entry<NormalizedSimpleStack, Integer> entry: graphMapperValuesForDestruction.entrySet()) {
			if (!shouldBeFiltered(entry))
			{
				NormalizedSimpleStack.NSSItem normStackItem = (NormalizedSimpleStack.NSSItem)entry.getKey();
				emcForDestruction.put(new SimpleStack(normStackItem.id, 1, normStackItem.damage), entry.getValue());
			}
		}
		emcForDestruction = ImmutableMap.copyOf(emcForDestruction);

		MinecraftForge.EVENT_BUS.post(new EMCRemapEvent());
		Transmutation.cacheFullKnowledge();
		FuelMapper.loadMap();
	}

	static boolean shouldBeFiltered(Map.Entry<NormalizedSimpleStack, Integer> entry) {
		NormalizedSimpleStack normStack = entry.getKey();
		if (normStack instanceof NormalizedSimpleStack.NSSItem && entry.getValue() > 0) {
			NormalizedSimpleStack.NSSItem normStackItem = (NormalizedSimpleStack.NSSItem)normStack;
			if (normStackItem.damage != OreDictionary.WILDCARD_VALUE) {
				return false;
			}
		}
		return true;
	}

	public static void clearMaps()
	{
		emcForCreation = ImmutableMap.of();
		emcForDestruction = ImmutableMap.of();
	}
}
