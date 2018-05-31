package moze_intel.projecte.emc.pregenerated;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import moze_intel.projecte.emc.json.NormalizedSimpleStack;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;

public class PregeneratedEMC
{
	private static final Gson gson =  new GsonBuilder()
			.registerTypeAdapter(NormalizedSimpleStack.class, NormalizedSimpleStack.Serializer.INSTANCE)
			.enableComplexMapKeySerialization().setPrettyPrinting().create();

	public static boolean tryRead(File f, Map<NormalizedSimpleStack, Integer> map)
	{
		try {
			Map<NormalizedSimpleStack, Integer> m = read(f);
			map.clear();
			map.putAll(m);
			return true;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static Map<NormalizedSimpleStack, Integer> read(File file) throws IOException
	{
		Type type = new TypeToken<Map<NormalizedSimpleStack, Integer>>() {}.getType();
		try (BufferedReader reader = new BufferedReader(new FileReader(file)))
		{
			Map<NormalizedSimpleStack, Integer> map = gson.fromJson(reader, type);
			map.remove(null);
			return map;
		}
	}

	public static void write(File file, Map<NormalizedSimpleStack, Integer> map) throws IOException
	{
		Type type = new TypeToken<Map<NormalizedSimpleStack, Integer>>() {}.getType();
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(file)))
		{
			gson.toJson(map, type, writer);
		}
	}
}
