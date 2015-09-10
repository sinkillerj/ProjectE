package moze_intel.projecte.emc.pregenerated;

import moze_intel.projecte.emc.NormalizedSimpleStack;
import moze_intel.projecte.utils.PELogger;

import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToInstanceMap;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import scala.Int;

import java.io.*;
import java.lang.reflect.Type;
import java.util.Map;

public class PregeneratedEMC
{
	public static class CreateDestroyValueBag {
		public Map<String, Integer> create;
		public Map<String, Integer> destroy;
	}
	static final Gson gson =  new GsonBuilder().setPrettyPrinting().create();

	public static boolean tryRead(File f, Map<NormalizedSimpleStack, Integer> mapForCreation, Map<NormalizedSimpleStack, Integer> mapForDestruction)
	{

		try {
			if (readMultiValueFile(f, mapForCreation, mapForDestruction))
			{
				return true;
			}
			 else
			{
				PELogger.logFatal("Could not read %s as multi value file!", f);
			}
		} catch (Exception e) {
			throw new RuntimeException("Could not parse Multi Value Pregen File!", e);
		}
		PELogger.logFatal("Fallback to old (same value) file format", f);
		mapForCreation.clear();
		mapForDestruction.clear();
		try {
			Map<NormalizedSimpleStack, Integer> m = readSameValue(f);
			mapForCreation.putAll(m);
			mapForDestruction.putAll(m);
			return true;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static boolean readMultiValueFile(File file, Map<NormalizedSimpleStack, Integer> mapForCreation, Map<NormalizedSimpleStack, Integer> mapForDestruction) throws FileNotFoundException {
		CreateDestroyValueBag map = gson.fromJson(new FileReader(file), CreateDestroyValueBag.class);
		if (map.create == null || map.destroy == null) return false;
		deserializeMap(map.create, mapForCreation);
		deserializeMap(map.destroy, mapForDestruction);
		return true;
	}

	private static void deserializeMap(Map<String, Integer> from, Map<NormalizedSimpleStack, Integer> to) {
		for (Map.Entry<String, Integer> entry: from.entrySet())
		{
			try
			{
				NormalizedSimpleStack normalizedSimpleStack = NormalizedSimpleStack.fromSerializedItem(entry.getKey());
				to.put(normalizedSimpleStack, entry.getValue());
			} catch (Exception e)
			{
				PELogger.logWarn("Could not create NormalizedSimpleStack from '%s' when reading pregen file!", entry.getKey());
			}
		}
	}

	public static Map<NormalizedSimpleStack, Integer> readSameValue(File file) throws FileNotFoundException
	{
		Type type = new TypeToken<Map<String, Integer>>() {}.getType();
		Map<String, Integer> map = gson.fromJson(new FileReader(file), type);
		Map<NormalizedSimpleStack, Integer> out = Maps.newHashMap();
		deserializeMap(map, out);
		return out;
	}

	public static void write(File file, Map<NormalizedSimpleStack, Integer> mapForCreation, Map<NormalizedSimpleStack, Integer> mapForDestruction) throws IOException
	{
		CreateDestroyValueBag bag = new CreateDestroyValueBag();
		bag.create = serializeMap(mapForCreation);
		bag.destroy = serializeMap(mapForDestruction);
		FileWriter writer = new FileWriter(file);
		gson.toJson(bag, CreateDestroyValueBag.class, writer);
		writer.close();
	}

	private static Map<String, Integer> serializeMap(Map<NormalizedSimpleStack, Integer> map) {
		Map<String, Integer> out = Maps.newHashMap();
		for (Map.Entry<NormalizedSimpleStack, Integer> entry: map.entrySet()) {
			out.put(entry.getKey().json(), entry.getValue());
		}
		return out;
	}
}
