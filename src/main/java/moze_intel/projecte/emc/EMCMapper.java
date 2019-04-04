package moze_intel.projecte.emc;

import moze_intel.projecte.PECore;
import moze_intel.projecte.api.event.EMCRemapEvent;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.emc.generators.BigFractionToLongGenerator;
import moze_intel.projecte.emc.arithmetics.HiddenBigFractionArithmetic;
import moze_intel.projecte.emc.arithmetics.IValueArithmetic;
import moze_intel.projecte.emc.collector.LongToBigFractionCollector;
import moze_intel.projecte.emc.collector.DumpToFileCollector;
import moze_intel.projecte.emc.collector.IExtendedMappingCollector;
import moze_intel.projecte.emc.collector.WildcardSetValueFixCollector;
import moze_intel.projecte.emc.generators.IValueGenerator;
import moze_intel.projecte.emc.json.NSSItem;
import moze_intel.projecte.emc.json.NSSItemWithNBT;
import moze_intel.projecte.emc.json.NormalizedSimpleStack;
import moze_intel.projecte.emc.mappers.APICustomConversionMapper;
import moze_intel.projecte.emc.mappers.APICustomEMCMapper;
import moze_intel.projecte.emc.mappers.BrewingMapper;
import moze_intel.projecte.emc.mappers.CraftingMapper;
import moze_intel.projecte.emc.mappers.CustomEMCMapper;
import moze_intel.projecte.emc.mappers.IEMCMapper;
import moze_intel.projecte.emc.mappers.OreDictionaryMapper;
import moze_intel.projecte.emc.mappers.SmeltingMapper;
import moze_intel.projecte.emc.mappers.customConversions.CustomConversionMapper;
import moze_intel.projecte.emc.pregenerated.PregeneratedEMC;
import moze_intel.projecte.playerData.Transmutation;
import moze_intel.projecte.utils.PrefixConfiguration;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.oredict.OreDictionary;

import org.apache.commons.math3.fraction.BigFraction;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class EMCMapper 
{
	public static final Map<SimpleStack, Long> emc = new LinkedHashMap<>();
	public static final Map<String, List<NSSItemWithNBT>> nssWithNBTCache = new LinkedHashMap<>();

	public static double covalenceLoss = ProjectEConfig.difficulty.covalenceLoss;

	public static void map()
	{
		List<IEMCMapper<NormalizedSimpleStack, Long>> emcMappers = Arrays.asList(
				new OreDictionaryMapper(),
				APICustomEMCMapper.instance,
				new CustomConversionMapper(),
				new CustomEMCMapper(),
				new CraftingMapper(),
				new moze_intel.projecte.emc.mappers.FluidMapper(),
				new SmeltingMapper(),
				new BrewingMapper(),
				new APICustomConversionMapper()
		);
		SimpleGraphMapper<NormalizedSimpleStack, BigFraction, IValueArithmetic<BigFraction>> mapper = new SimpleGraphMapper<>(new HiddenBigFractionArithmetic());
		IValueGenerator<NormalizedSimpleStack, Long> valueGenerator = new BigFractionToLongGenerator<>(mapper);
		IExtendedMappingCollector<NormalizedSimpleStack, Long, IValueArithmetic<BigFraction>> mappingCollector = new LongToBigFractionCollector<>(mapper);
		mappingCollector = new WildcardSetValueFixCollector<>(mappingCollector);

		Configuration config = new Configuration(new File(PECore.CONFIG_DIR, "mapping.cfg"));
		config.load();

		if (config.getBoolean("dumpEverythingToFile", "general", false,"Want to take a look at the internals of EMC Calculation? Enable this to write all the conversions and setValue-Commands to config/ProjectE/mappingdump.json")) {
			mappingCollector = new DumpToFileCollector<>(new File(PECore.CONFIG_DIR, "mappingdump.json"), mappingCollector);
		}

		boolean shouldUsePregenerated = config.getBoolean("pregenerate", "general", false, "When the next EMC mapping occurs write the results to config/ProjectE/pregenerated_emc.json and only ever run the mapping again" +
						" when that file does not exist, this setting is set to false, or an error occurred parsing that file.");

		Map<NormalizedSimpleStack, Long> graphMapperValues;
		if (shouldUsePregenerated && PECore.PREGENERATED_EMC_FILE.canRead() && PregeneratedEMC.tryRead(PECore.PREGENERATED_EMC_FILE, graphMapperValues = new HashMap<>()))
		{
			PECore.LOGGER.info(String.format("Loaded %d values from pregenerated EMC File", graphMapperValues.size()));
		}
		else
		{


			SimpleGraphMapper.setLogFoundExploits(config.getBoolean("logEMCExploits", "general", true,
					"Log known EMC Exploits. This can not and will not find all possible exploits. " +
							"This will only find exploits that result in fixed/custom emc values that the algorithm did not overwrite. " +
							"Exploits that derive from conversions that are unknown to ProjectE will not be found."
			));

			PECore.debugLog("Starting to collect Mappings...");
			for (IEMCMapper<NormalizedSimpleStack, Long> emcMapper : emcMappers)
			{
				try
				{
					if (config.getBoolean(emcMapper.getName(), "enabledMappers", emcMapper.isAvailable(), emcMapper.getDescription()) && emcMapper.isAvailable())
					{
						DumpToFileCollector.currentGroupName = emcMapper.getName();
						emcMapper.addMappings(mappingCollector, new PrefixConfiguration(config, "mapperConfigurations." + emcMapper.getName()));
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

			graphMapperValues = valueGenerator.generateValues();
			PECore.debugLog("Generated Values...");

			filterEMCMap(graphMapperValues);

			if (shouldUsePregenerated) {
				//Should have used pregenerated, but the file was not read => regenerate.
				try
				{
					PregeneratedEMC.write(PECore.PREGENERATED_EMC_FILE, graphMapperValues);
					PECore.debugLog("Wrote Pregen-file!");
				} catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}


		for (Map.Entry<NormalizedSimpleStack, Long> entry: graphMapperValues.entrySet()) {
			NormalizedSimpleStack normStackItem = entry.getKey();
			String name = (normStackItem instanceof NSSItem) ? ((NSSItem)normStackItem).itemName: 
						  (normStackItem instanceof NSSItemWithNBT) ? ((NSSItemWithNBT)normStackItem).itemName: null;
			Item obj = Item.REGISTRY.getObject(new ResourceLocation(name));
			if (obj != null)
			{
				if (normStackItem instanceof NSSItemWithNBT){
					insertNSSWithNBTintoEMCMap((NSSItemWithNBT) normStackItem, entry.getValue());
				} else if(normStackItem instanceof NSSItem){
					emc.put(new SimpleStack(obj.getRegistryName(), ((NSSItem)normStackItem).damage), entry.getValue());
				}
			} else {
				PECore.LOGGER.warn("Could not add EMC value for {}|{}. Can not get ItemID!", name);
			}
		}

		MinecraftForge.EVENT_BUS.post(new EMCRemapEvent());
		Transmutation.cacheFullKnowledge();
		FuelMapper.loadMap();
		PECore.refreshJEI();
	}

	public static void insertNSSWithNBTintoEMCMap(NSSItemWithNBT normStackItem, long value) {
		SimpleStack itm = new SimpleStack(new ResourceLocation(normStackItem.itemName), normStackItem.damage, normStackItem.nbt);
		putInNSSNBTCache((NSSItemWithNBT) normStackItem);
		emc.put(itm, value);
	}

	public static void insertSimpleStackWithNBTintoEMCMap(SimpleStack itm, long value) {
		putInNSSNBTCache(new NSSItemWithNBT(itm.id.toString(), itm.damage, itm.tag, NSSItemWithNBT.NO_IGNORES));
		emc.put(itm, value);
	}
	
	private static void putInNSSNBTCache(NSSItemWithNBT normStackItem) {
		if(normStackItem.nbt == null || normStackItem.nbt.isEmpty()){
			return;
		}
		String key = normStackItem.itemName;
		if(!nssWithNBTCache.containsKey(key)){
			nssWithNBTCache.put(key, new ArrayList<>());
		}
		for(NSSItemWithNBT itm: nssWithNBTCache.get(key)){
			if(itm.damage == normStackItem.damage && normStackItem.nbt.equals(itm.nbt)){
				return;
			}
		}
		nssWithNBTCache.get(key).add(normStackItem);		
	}

	private static void filterEMCMap(Map<NormalizedSimpleStack, Long> map) {
		map.entrySet().removeIf(e -> !((e.getKey() instanceof NSSItem)||(e.getKey() instanceof NSSItemWithNBT))
										|| ((e.getKey() instanceof NSSItem) && ((NSSItem) e.getKey()).damage == OreDictionary.WILDCARD_VALUE)
										|| e.getValue() <= 0);
	}

	public static boolean mapContains(SimpleStack key)
	{
		if(key.tag != null){
			return mapContainsWithNBT(key) || emc.containsKey(key); 
		}
		return emc.containsKey(key);
	}

	public static long getEmcValue(SimpleStack stack)
	{
		if(stack.tag != null){
			return getEmcValueWithNBT(stack);
		}
		return emc.get(stack);
	}
	
	public static long getEmcValueWithNBT(SimpleStack stack){
		SimpleStack stack2 = stack;
		if(stack.tag != null && !stack.tag.isEmpty()){
			NSSItemWithNBT represent = getRepresentativeTag(stack);
			if(represent != null){
				stack2 = represent.toSimpleStack();
			}
		}
		return emc.get(stack2);
	}

	public static void clearMaps() {
		emc.clear();
		nssWithNBTCache.clear();
	}

	public static boolean mapContainsWithNBT(SimpleStack withNBT) {
		return mapContainsWithNBT(withNBT, true);
	}
	
	public static boolean mapContainsWithNBT(SimpleStack withNBT, boolean partialResults) {
		if(nssWithNBTCache.containsKey(withNBT.id.toString())){
			for(NSSItemWithNBT itm: nssWithNBTCache.get(withNBT.id.toString())){
				if(itm.ignoreDamage || itm.damage == withNBT.damage){
					if((partialResults && NSSItemWithNBT.isNBTContained(itm.nbt,withNBT.tag))||
							itm.nbt.equals(withNBT.tag)){
						return true;	
					}
				}
			}
		}
		return false;
	}
	
	public static NSSItemWithNBT getRepresentativeTag(SimpleStack withNBT){
		NSSItemWithNBT mostSimilar = null;
		int maxSimilarity = 0;
		if(nssWithNBTCache.containsKey(withNBT.id.toString())){
			for(NSSItemWithNBT itm: nssWithNBTCache.get(withNBT.id.toString())){
				if(itm.ignoreDamage || itm.damage == withNBT.damage){
					if(NSSItemWithNBT.isNBTContained(itm.nbt,withNBT.tag)){
						int newSim = NSSItemWithNBT.NBTSimilarity(itm.nbt,withNBT.tag);
						if(maxSimilarity < newSim) {
							maxSimilarity = newSim;
							mostSimilar = itm;
						}
					}
				}
			}
		}
		return mostSimilar;
	}

	
	
}
