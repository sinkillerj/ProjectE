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
		public Map<NormalizedSimpleStack, Integer> create;
		public Map<NormalizedSimpleStack, Integer> destroy;
	}
	static final Gson gson =  new GsonBuilder().registerTypeAdapter(NormalizedSimpleStack.class, new NSSJsonTypeAdapter().nullSafe()).enableComplexMapKeySerialization().setPrettyPrinting().create();

	public static boolean tryRead(File f, Map<NormalizedSimpleStack, Integer> mapForCreation, Map<NormalizedSimpleStack, Integer> mapForDestruction)
	{

		try {
			readMultiValueFile(f, mapForCreation, mapForDestruction);
			return true;
		} catch (Exception e) {
			PELogger.logFatal("Could not read %s as multi value file!", f);
			e.printStackTrace();
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

	public static void readMultiValueFile(File file, Map<NormalizedSimpleStack, Integer> mapForCreation, Map<NormalizedSimpleStack, Integer> mapForDestruction) throws FileNotFoundException {
		CreateDestroyValueBag map = gson.fromJson(new FileReader(file), CreateDestroyValueBag.class);
		mapForCreation.putAll(map.create);
		mapForCreation.remove(null);
		mapForDestruction.putAll(map.destroy);
		mapForDestruction.remove(null);
	}

	public static Map<NormalizedSimpleStack, Integer> readSameValue(File file) throws FileNotFoundException
	{
		Type type = new TypeToken<Map<NormalizedSimpleStack, Integer>>() {}.getType();
		Map<NormalizedSimpleStack, Integer> map = gson.fromJson(new FileReader(file), type);
		map.remove(null);
		return map;
	}

	public static void write(File file, Map<NormalizedSimpleStack, Integer> mapForCreation, Map<NormalizedSimpleStack, Integer> mapForDestruction) throws IOException
	{
		CreateDestroyValueBag bag = new CreateDestroyValueBag();
		bag.create = mapForCreation;
		bag.destroy = mapForDestruction;
		FileWriter writer = new FileWriter(file);
		gson.toJson(bag, CreateDestroyValueBag.class, writer);
		writer.close();
	}
}
