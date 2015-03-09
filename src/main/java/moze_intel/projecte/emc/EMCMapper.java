package moze_intel.projecte.emc;

import moze_intel.projecte.PECore;
import moze_intel.projecte.emc.arithmetics.IntArithmetic;
import moze_intel.projecte.emc.mappers.*;
import moze_intel.projecte.playerData.Transmutation;
import moze_intel.projecte.utils.PELogger;
import moze_intel.projecte.utils.PrefixConfiguration;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.oredict.OreDictionary;

import java.io.File;
import java.util.*;

public final class EMCMapper 
{
	public static Map<SimpleStack, Integer> emc = new LinkedHashMap<SimpleStack, Integer>();
	public static Map<NormalizedSimpleStack, Integer> graphMapperValues;

	public static void map()
	{
		List<IEMCMapper<NormalizedSimpleStack, Integer>> emcMappers = Arrays.asList(new OreDictionaryMapper(), new LazyMapper(), new CustomEMCMapper(), new CraftingMapper(), new moze_intel.projecte.emc.mappers.FluidMapper(), new SmeltingMapper());
		GraphMapper<NormalizedSimpleStack, Integer> graphMapper = new SimpleGraphMapper<NormalizedSimpleStack, Integer>(new IntArithmetic());

		Configuration config = new Configuration(new File(PECore.CONFIG_DIR, "mapping.cfg"));
		config.load();

		PELogger.logInfo("Starting to collect Mappings...");
		for (IEMCMapper<NormalizedSimpleStack, Integer> emcMapper: emcMappers) {
			if (config.getBoolean(emcMapper.getName(), "enabledMappers",emcMapper.isAvailable(), emcMapper.getDescription()) && emcMapper.isAvailable()) {
				emcMapper.addMappings(graphMapper, new PrefixConfiguration(config, "mapperConfigurations." + emcMapper.getName()));
				PELogger.logInfo("Collected Mappings from " + emcMapper.getClass().getName());
			}
		}
		NormalizedSimpleStack.addMappings(graphMapper);
		PELogger.logInfo("Starting to generate Values:");
		config.save();

		graphMapperValues =  graphMapper.generateValues();
		PELogger.logInfo("Generated Values...");

		for(Iterator<Map.Entry<NormalizedSimpleStack, Integer>> iter = graphMapperValues.entrySet().iterator(); iter.hasNext();) {
			Map.Entry<NormalizedSimpleStack, Integer> entry = iter.next();
			NormalizedSimpleStack normStack = entry.getKey();
			if (normStack.damage != OreDictionary.WILDCARD_VALUE && !( normStack instanceof NormalizedSimpleStack.Group)) {
				emc.put(new SimpleStack(normStack.id, 1, normStack.damage), entry.getValue());
			}
		}

		Transmutation.loadCompleteKnowledge();
		FuelMapper.loadMap();
	}

	public static boolean mapContains(SimpleStack key)
	{
		SimpleStack copy = key.copy();
		copy.qnty = 1;

		return emc.containsKey(copy);
	}

	public static int getEmcValue(SimpleStack stack)
	{
		SimpleStack copy = stack.copy();
		copy.qnty = 1;

		return emc.get(copy);
	}

	public static void clearMaps() {
		emc.clear();
	}
}
