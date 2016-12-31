package moze_intel.projecte.config;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import moze_intel.projecte.PECore;
import moze_intel.projecte.emc.NormalizedSimpleStack;
import moze_intel.projecte.emc.mappers.customConversions.CustomConversionMapper;
import moze_intel.projecte.utils.PELogger;
import net.minecraft.util.JsonUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public final class CustomEMCParser
{
	private static final Gson GSON = new GsonBuilder().registerTypeAdapter(CustomEMCEntry.class, new Serializer()).setPrettyPrinting().create();
	private static final File CONFIG = new File(PECore.CONFIG_DIR, "custom_emc.json");

	public static class CustomEMCFile
	{
		public final List<CustomEMCEntry> entries;

		public CustomEMCFile(List<CustomEMCEntry> entries)
		{
			this.entries = entries;
		}
	}

	public static class CustomEMCEntry
	{
		public final NormalizedSimpleStack nss;
		public final int emc;

		private CustomEMCEntry(NormalizedSimpleStack nss, int emc)
		{
			this.nss = nss;
			this.emc = emc;
		}

		@Override
		public boolean equals(Object o)
		{
			return o == this ||
					o instanceof CustomEMCEntry
							&& nss.equals(((CustomEMCEntry) o).nss)
							&& emc == ((CustomEMCEntry) o).emc;
		}

		@Override
		public int hashCode()
		{
			return nss.hashCode() ^ 31 * emc;
		}
	}

	private static class Serializer implements JsonSerializer<CustomEMCEntry>, JsonDeserializer<CustomEMCEntry>
	{
		@Override
		public CustomEMCEntry deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			JsonObject obj = JsonUtils.getJsonObject(json, "custom emc entry");
			if (!JsonUtils.hasField(obj, "item") || !JsonUtils.hasField(obj, "emc"))
			{
				throw new JsonParseException("Missing fields from Custom EMC entry");
			}
			String nss = JsonUtils.getString(obj.get("item"), "item");
			int emc = JsonUtils.getInt(obj.get("emc"), "emc");
			if (emc < 0)
			{
				throw new JsonParseException("Invalid EMC amount: " + emc);
			}
			// todo stop reaching into other code. Pull out, refactor, and unify all json stuff.
			return new CustomEMCEntry(CustomConversionMapper.getNSSfromJsonString(nss, new HashMap<String, NormalizedSimpleStack>()), emc);
		}

		@Override
		public JsonElement serialize(CustomEMCEntry src, Type typeOfSrc, JsonSerializationContext context) {
			JsonObject obj = new JsonObject();
			obj.add("item", new JsonPrimitive(src.nss.json()));
			obj.add("emc", new JsonPrimitive(src.emc));
			return obj;
		}
	}

	public static CustomEMCFile currentEntries;
	private static boolean dirty = false;

	public static void init()
	{
		flush();

		if (!CONFIG.exists())
		{
			try
			{
				if (CONFIG.createNewFile())
				{
					writeDefaultFile();
				}
			}
			catch (IOException e)
			{
				PELogger.logFatal("Exception in file I/O: couldn't create custom configuration files.");
			}
		}

		try (BufferedReader reader = new BufferedReader(new FileReader(CONFIG))){
			currentEntries = GSON.fromJson(reader, CustomEMCFile.class);
			currentEntries.entries.removeIf(e -> e.nss == null);
		} catch (IOException e) {
			PELogger.logFatal("Couldn't read custom emc file");
			currentEntries = new CustomEMCFile(new ArrayList<CustomEMCEntry>());
		}
	}

	private static NormalizedSimpleStack getNss(String str, int meta)
	{
		if (str.contains(":"))
		{
			return NormalizedSimpleStack.getFor(str, meta);
		}
		else
		{
			return NormalizedSimpleStack.forOreDictionary(str);
		}
	}

	public static boolean addToFile(String toAdd, int meta, int emc)
	{
		NormalizedSimpleStack nss = getNss(toAdd, meta);
		CustomEMCEntry entry = new CustomEMCEntry(nss, emc);

		int setAt = -1;

		for (int i = 0; i < currentEntries.entries.size(); i++)
		{
			if (currentEntries.entries.get(i).nss.equals(nss))
			{
				setAt = i;
				break;
			}
		}

		if (setAt == -1)
		{
			currentEntries.entries.add(entry);
		} else
		{
			currentEntries.entries.set(setAt, entry);
		}

		dirty = true;
		return true;
	}

	public static boolean removeFromFile(String toRemove, int meta)
	{
		NormalizedSimpleStack nss = getNss(toRemove, meta);
		Iterator<CustomEMCEntry> iter = currentEntries.entries.iterator();

		boolean removed = false;
		while (iter.hasNext())
		{
			if (iter.next().nss.equals(nss))
			{
				iter.remove();
				dirty = true;
				removed = true;
			}
		}

		return removed;
	}

	private static void flush()
	{
		if (dirty)
		{
			try
			{
				Files.write(GSON.toJson(currentEntries), CONFIG, Charsets.UTF_8);
			} catch (IOException e) {
				e.printStackTrace();
			}

			dirty = false;
		}
	}

	private static void writeDefaultFile()
	{
		JsonObject elem = (JsonObject) GSON.toJsonTree(new CustomEMCFile(new ArrayList<CustomEMCEntry>()));
		elem.add("__comment", new JsonPrimitive("Use the in-game commands to edit this file"));
		try
		{
			Files.write(GSON.toJson(elem), CONFIG, Charsets.UTF_8);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
