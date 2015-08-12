package moze_intel.projecte.emc.mappers.customConversions;

import moze_intel.projecte.PECore;
import moze_intel.projecte.emc.collector.IMappingCollector;
import moze_intel.projecte.emc.NormalizedSimpleStack;
import moze_intel.projecte.emc.mappers.IEMCMapper;
import moze_intel.projecte.emc.mappers.customConversions.json.ConversionGroup;
import moze_intel.projecte.emc.mappers.customConversions.json.CustomConversion;
import moze_intel.projecte.emc.mappers.customConversions.json.CustomConversionDeserializer;
import moze_intel.projecte.emc.mappers.customConversions.json.CustomConversionFile;
import moze_intel.projecte.emc.mappers.customConversions.json.FixedValues;
import moze_intel.projecte.emc.mappers.customConversions.json.FixedValuesDeserializer;
import moze_intel.projecte.utils.PELogger;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.util.Map;

public class CustomConversionMapper implements IEMCMapper<NormalizedSimpleStack, Integer>
{
	public static final ImmutableList<String> defaultfilenames = ImmutableList.of("metals", "example", "ODdefaults");


	@Override
	public String getName()
	{
		return "CustomConversionMapper";
	}

	@Override
	public String getDescription()
	{
		return "";
	}

	@Override
	public boolean isAvailable()
	{
		return true;
	}

	@Override
	public void addMappings(IMappingCollector<NormalizedSimpleStack, Integer> mapper, Configuration config)
	{
		File customConversionFolder = getCustomConversionFolder();
		if (customConversionFolder.isDirectory() || customConversionFolder.mkdir()) {
			if (config.getBoolean("writeDefaultFiles", "", true, "Create the default files if they are not present, yet. Will not overwrite them, only create them when they are not present."))
			{
				tryToWriteDefaultFiles();
			}
			for (File f: customConversionFolder.listFiles()) {
				if (f.isFile() && f.canRead()) {
					if (f.getName().toLowerCase().endsWith(".json")) {
						if (config.getBoolean(f.getName().substring(0, f.getName().length() - 5), "", true, String.format("Read file: %s?", f.getName()))) {
							try
							{
								addMappingsFromFile(new FileReader(f), mapper);
								PELogger.logInfo("Collected Mappings from " + f.getName());
							} catch (Exception e) {
								PELogger.logFatal("Exception when reading file: " + f);
								e.printStackTrace();
							}
						}
					}
				}
			}
		} else {
			PELogger.logFatal("COULD NOT CREATE customConversions FOLDER IN config/ProjectE");
		}
	}

	public static File getCustomConversionFolder()
	{
		return new File(PECore.CONFIG_DIR, "customConversions");
	}

	public static void addMappingsFromFile(Reader json, IMappingCollector<NormalizedSimpleStack, Integer> mapper) {
		addMappingsFromFile(parseJson(json), mapper);
	}

	public static void addMappingsFromFile(CustomConversionFile file, IMappingCollector<NormalizedSimpleStack, Integer> mapper) {
		Map<String, NormalizedSimpleStack> fakes = Maps.newHashMap();
		//TODO implement buffered IMappingCollector to recover from failures
		for (Map.Entry<String, ConversionGroup> entry : file.groups.entrySet())
		{
			PELogger.logDebug(String.format("Adding conversions from group '%s' with comment '%s'", entry.getKey(), entry.getValue().comment));
			try
			{
				for (CustomConversion conversion : entry.getValue().conversions)
				{
					NormalizedSimpleStack output = getNSSfromJsonString(conversion.output, fakes);
					mapper.addConversion(conversion.count, output, convertToNSSMap(conversion.ingredients, fakes));
				}
			} catch (Exception e) {
				PELogger.logFatal(String.format("ERROR reading custom conversion from group %s!", entry.getKey()));
				e.printStackTrace();
			}
		}

		try
		{
			if (file.values != null)
			{
				if (file.values.setValueBefore != null) {
					for (Map.Entry<String, Integer> entry : file.values.setValueBefore.entrySet())
					{
						NormalizedSimpleStack something = getNSSfromJsonString(entry.getKey(), fakes);
						mapper.setValueBefore(something, entry.getValue());
						if (something instanceof NormalizedSimpleStack.NSSOreDictionary)
						{
							String odName = ((NormalizedSimpleStack.NSSOreDictionary) something).od;
							for (ItemStack itemStack : OreDictionary.getOres(odName))
							{
								mapper.setValueBefore(NormalizedSimpleStack.getFor(itemStack), entry.getValue());
							}
						}
					}
				}
				if (file.values.setValueAfter != null)
				{
					for (Map.Entry<String, Integer> entry : file.values.setValueAfter.entrySet())
					{
						NormalizedSimpleStack something = getNSSfromJsonString(entry.getKey(), fakes);
						mapper.setValueAfter(something, entry.getValue());
						if (something instanceof NormalizedSimpleStack.NSSOreDictionary)
						{
							String odName = ((NormalizedSimpleStack.NSSOreDictionary) something).od;
							for (ItemStack itemStack : OreDictionary.getOres(odName))
							{
								mapper.setValueAfter(NormalizedSimpleStack.getFor(itemStack), entry.getValue());
							}
						}
					}
				}
				if (file.values.conversion != null)
				{
					for (CustomConversion conversion : file.values.conversion)
					{
						NormalizedSimpleStack out = getNSSfromJsonString(conversion.output, fakes);
						if (conversion.evalOD && out instanceof NormalizedSimpleStack.NSSOreDictionary)
						{
							String odName = ((NormalizedSimpleStack.NSSOreDictionary) out).od;
							for (ItemStack itemStack : OreDictionary.getOres(odName))
							{
								mapper.setValueFromConversion(conversion.count, NormalizedSimpleStack.getFor(itemStack), convertToNSSMap(conversion.ingredients, fakes));
							}
						}
						mapper.setValueFromConversion(conversion.count, out, convertToNSSMap(conversion.ingredients, fakes));
					}
				}
			}
		} catch (Exception e) {
			PELogger.logFatal("ERROR reading custom conversion values!");
			e.printStackTrace();
		}
	}


	private static NormalizedSimpleStack getNSSfromJsonString(String s, Map<String, NormalizedSimpleStack> fakes) throws Exception
	{
		if (s.startsWith("OD|")) {
			return NormalizedSimpleStack.forOreDictionary(s.substring(3));
		} else if (s.startsWith("FAKE|"))
		{
			String fakeIdentifier = s.substring(5);
			if (fakes.containsKey(fakeIdentifier))
			{
				return fakes.get(fakeIdentifier);
			}
			else
			{
				NormalizedSimpleStack nssFake = NormalizedSimpleStack.createFake(fakeIdentifier);
				fakes.put(fakeIdentifier, nssFake);
				return nssFake;
			}
		} else if (s.startsWith("FLUID|")) {
			String fluidName = s.substring("FLUID|".length());
			Fluid fluid = FluidRegistry.getFluid(fluidName);
			if (fluid == null) return null;
			return NormalizedSimpleStack.getFor(fluid);
		} else {
			return NormalizedSimpleStack.fromSerializedItem(s);
		}
	}

	private static<V> Map<NormalizedSimpleStack, V> convertToNSSMap(Map<String, V> m, Map<String, NormalizedSimpleStack> fakes) throws Exception{
		Map<NormalizedSimpleStack, V> out = Maps.newHashMap();
		for (Map.Entry<String, V> e: m.entrySet()) {
			NormalizedSimpleStack nssItem = getNSSfromJsonString(e.getKey(), fakes);
			out.put(nssItem, e.getValue());
		}
		return out;
	}

	public static CustomConversionFile parseJson(Reader json) {
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(CustomConversion.class, new CustomConversionDeserializer());
		builder.registerTypeAdapter(FixedValues.class, new FixedValuesDeserializer());
		Gson gson = builder.create();
		return gson.fromJson(json, CustomConversionFile.class);
	}


	public static void tryToWriteDefaultFiles() {
		for (String filename: defaultfilenames) {
			writeDefaultFile(filename);
		}
	}

	private static void writeDefaultFile(String filename) {
		File customConversionFolder = getCustomConversionFolder();
		File f = new File(customConversionFolder, filename + ".json");
		if (f.exists()) {
			return;
		}
		try {
		if (f.createNewFile() && f.canWrite())
		{
			InputStream stream = CustomConversionMapper.class.getClassLoader().getResourceAsStream("defaultCustomConversions/" + filename + ".json");
			OutputStream outputStream = new FileOutputStream(f);
			IOUtils.copy(stream, outputStream);
			stream.close();
			outputStream.close();
		}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
