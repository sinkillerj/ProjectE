package moze_intel.projecte.config;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.annotations.SerializedName;
import moze_intel.projecte.PECore;
import moze_intel.projecte.emc.json.NSSItem;
import moze_intel.projecte.emc.json.NSSOreDictionary;
import moze_intel.projecte.emc.json.NormalizedSimpleStack;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class CustomEMCParser
{
	private static final Gson GSON = new GsonBuilder().registerTypeAdapter(NormalizedSimpleStack.class, NormalizedSimpleStack.Serializer.INSTANCE).setPrettyPrinting().create();
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
		@SerializedName("item")
		public final NormalizedSimpleStack nss;
		public final long emc;

		private CustomEMCEntry(NormalizedSimpleStack nss, long emc)
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
		public int hashCode() {
			int result = nss != null ? nss.hashCode() : 0;
			result = 31 * result + (int) (emc ^ (emc >>> 32));
			return result;
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
				PECore.LOGGER.fatal("Exception in file I/O: couldn't create custom configuration files.");
			}
		}

		try (BufferedReader reader = new BufferedReader(new FileReader(CONFIG))) {
			currentEntries = GSON.fromJson(reader, CustomEMCFile.class);
			currentEntries.entries.removeIf(e -> e.nss == null || e.emc < 0 || !(e.nss instanceof NSSItem || e.nss instanceof NSSOreDictionary));
		} catch (IOException | JsonParseException e) {
			PECore.LOGGER.fatal("Couldn't read custom emc file");
			e.printStackTrace();
			currentEntries = new CustomEMCFile(new ArrayList<>());
		}
	}

	private static NormalizedSimpleStack getNss(String str, int meta)
	{
		if (str.contains(":"))
		{
			return NSSItem.create(str, meta);
		}
		else
		{
			return NSSOreDictionary.create(str);
		}
	}

	public static boolean addToFile(String toAdd, int meta, long emc)
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
		JsonObject elem = (JsonObject) GSON.toJsonTree(new CustomEMCFile(new ArrayList<>()));
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
