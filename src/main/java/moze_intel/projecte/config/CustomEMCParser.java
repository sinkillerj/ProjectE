package moze_intel.projecte.config;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import moze_intel.projecte.PECore;
import moze_intel.projecte.emc.NormalizedSimpleStack;
import moze_intel.projecte.utils.FileHelper;
import moze_intel.projecte.utils.ItemHelper;
import moze_intel.projecte.utils.PELogger;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

public final class CustomEMCParser
{
	private static final String VERSION = "#0.2";
	private static File CONFIG;
	private static boolean loaded;

	public static void init()
	{
		CONFIG = new File(PECore.CONFIG_DIR, "custom_emc.cfg");
		loaded = false;

		if (!CONFIG.exists())
		{
			try
			{
				if (CONFIG.createNewFile())
				{
					writeDefaultFile();
					loaded = true;
				}
			}
			catch (IOException e)
			{
				PELogger.logFatal("Exception in file I/O: couldn't create custom configuration files.");
				e.printStackTrace();
				return;
			}
		}
		else
		{
			BufferedReader reader = null;

			try
			{
				reader = new BufferedReader(new FileReader(CONFIG));

				String line = reader.readLine();

				if (line == null || !line.equals(VERSION))
				{
					PELogger.logFatal("Found old custom EMC file: resetting.");
					writeDefaultFile();
				}
			}
			catch (IOException e)
			{
				PELogger.logFatal("Exception in file I/O: couldn't create custom configuration files.");
				e.printStackTrace();
			}
			finally
			{
				FileHelper.closeStream(reader);
			}

			loaded = true;
		}
	}

	public static Map<NormalizedSimpleStack, Integer> userValues = Maps.newHashMap();

	public static void readUserData()
	{
		if (!loaded)
		{
			PELogger.logFatal("ERROR: configurations files are not loaded!");
			return;
		}

		Entry entry;
		LineNumberReader reader = null;
		userValues.clear();
		try
		{
			reader = new LineNumberReader(new FileReader(CONFIG));

			while ((entry = getNextEntry(reader)) != null)
			{
				if (entry.name.contains(":"))
				{
					ItemStack stack = ItemHelper.getStackFromString(entry.name, entry.meta);

					if (stack == null)
					{
						PELogger.logFatal("Error in custom EMC file: couldn't find item: " + entry.name);
						PELogger.logFatal("At line number: " + reader.getLineNumber());
						continue;
					}

					if (entry.emc <= 0)
					{
						PELogger.logInfo("Removed " + entry.name + " from EMC mapping");
					}
					else
					{
						PELogger.logInfo("Registered custom EMC for: " + entry.name + "(" + entry.emc + ")");
					}
					userValues.put(NormalizedSimpleStack.getFor(stack), entry.emc > 0 ? entry.emc  : 0);
				}
				else
				{
					if (OreDictionary.getOres(entry.name).isEmpty())
					{
						PELogger.logFatal("Error in custom EMC file: no OD entry for " + entry.name);
						PELogger.logFatal("At line number: " + reader.getLineNumber());
						continue;
					}

					if (entry.emc <= 0)
					{
						PELogger.logInfo("Removed " + entry.name + " from EMC mapping");
					}
					else
					{
						PELogger.logInfo("Registered custom EMC for: " + entry.name + "(" + entry.emc + ")");
					}
					for (ItemStack stack : ItemHelper.getODItems(entry.name))
					{
						userValues.put(NormalizedSimpleStack.getFor(stack), entry.emc > 0 ? entry.emc  : 0);
					}
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			FileHelper.closeStream(reader);
		}
	}

	public static boolean addToFile(String toAdd, int meta, int emc)
	{
		if (!loaded)
		{
			PELogger.logFatal("ERROR: configurations files are not loaded!");
			return false;
		}

		PrintWriter writer = null;
		boolean result = false;

		try
		{
			List<String> file = readAllFile();
			List<Entry> entries = getAllEntries();

			boolean hasFound = false;
			boolean isOD = !toAdd.contains(":");

			for (Entry e : entries)
			{
				if (!e.name.equals(toAdd) || (!isOD && e.meta != meta))
				{
					continue;
				}

				file.set(e.emcIndex - 1, "E:" + emc);
				hasFound = true;
				break;
			}

			if (hasFound)
			{
				writer = new PrintWriter(new FileOutputStream(CONFIG, false));

				for (String s : file)
				{
					writer.println(s);
				}

				result = true;
			}
			else
			{
				writer = new PrintWriter(new FileOutputStream(CONFIG, true));

				writer.append("\n");
				writer.append("S:" + toAdd + "\n");

				if (toAdd.contains(":"))
				{
					writer.append("M:" + meta + "\n");
				}

				writer.append("E:" + emc + "\n");

				result = true;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			FileHelper.closeStream(writer);
		}

		return result;
	}

	public static boolean removeFromFile(String toRemove, int meta)
	{
		if (!loaded)
		{
			PELogger.logFatal("ERROR: configurations files are not loaded!");
			return false;
		}

		PrintWriter writer = null;
		boolean result = false;

		try
		{
			List<String> file = readAllFile();
			List<Entry> entries = getAllEntries();

			boolean isOD = !toRemove.contains(":");

			for (Entry e : entries)
			{
				if (!e.name.equals(toRemove) || (!isOD && e.meta != meta))
				{
					continue;
				}

				file.remove(e.emcIndex - 1);

				if (!isOD)
				{
					file.remove(e.metaIndex - 1);
				}

				file.remove(e.nameIndex - 1);

				result = true;
				break;
			}

			if (result)
			{
				writer = new PrintWriter(new FileOutputStream(CONFIG, false));

				for (String s : file)
				{
					writer.println(s);
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			FileHelper.closeStream(writer);
		}

		return result;
	}

	private static List<String> readAllFile()
	{
		List<String> list = Lists.newArrayList();
		BufferedReader reader = null;

		try
		{
			reader = new BufferedReader(new FileReader(CONFIG));

			String s;

			while ((s = reader.readLine()) != null)
			{
				list.add(s);
			}

			return list;
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			FileHelper.closeStream(reader);
		}

		return Lists.newArrayList();
	}

	private static List<Entry> getAllEntries()
	{
		List<Entry> list = Lists.newArrayList();
		LineNumberReader reader = null;

		try
		{
			reader = new LineNumberReader(new FileReader(CONFIG));

			Entry e;

			while ((e = getNextEntry(reader)) != null)
			{
				list.add(e);
			}

			return list;
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			FileHelper.closeStream(reader);
		}

		return Lists.newArrayList();
	}

	private static Entry getNextEntry(LineNumberReader reader) throws IOException
	{
		String line;

		while ((line = getNextLine(reader)) != null)
		{
			if (line.charAt(0) == 'S')
			{
				String name = line.substring(2);
				int nameIndex = reader.getLineNumber();

				line = getNextLine(reader);

				int meta = -1;
				int metaIndex = -1;

				if (name.contains(":"))
				{
					if (line == null || line.charAt(0) != 'M')
					{
						continue;
					}

					meta = 0;
					metaIndex = reader.getLineNumber();

					try
					{
						meta = Integer.valueOf(line.substring(2));
					}
					catch (NumberFormatException e)
					{
						e.printStackTrace();
						continue;
					}

					line = getNextLine(reader);
				}

				if (line == null || line.charAt(0) != 'E')
				{
					continue;
				}

				int emc = 0;
				int emcIndex = reader.getLineNumber();

				try
				{
					emc = Integer.valueOf(line.substring(2));
				}
				catch (NumberFormatException e)
				{
					e.printStackTrace();
					continue;
				}

				return new Entry(name, meta, emc, nameIndex, metaIndex, emcIndex);
			}
		}

		return null;
	}

	private static String getNextLine(LineNumberReader reader) throws IOException
	{
		String line;

		while ((line = reader.readLine()) != null)
		{
			line = line.trim();

			if (line.isEmpty() || line.length() < 3 || line.charAt(0) == '#' || line.charAt(1) != ':')
			{
				continue;
			}

			return line;
		}

		return null;
	}

	private static void writeDefaultFile()
	{
		PrintWriter writer = null;

		try
		{
			writer = new PrintWriter(CONFIG);

			writer.println(VERSION);
			writer.println("Custom EMC file");
			writer.println("This file is used for custom EMC registration, it is recommended that you do not modify it manually.");
			writer.println("In game commands are avaliable to set custom values. Type /projecte in game for usage info.");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			FileHelper.closeStream(writer);
		}
	}

	private static class Entry
	{
		public String name;
		public int meta;
		public int emc;
		public int nameIndex;
		public int metaIndex;
		public int emcIndex;

		public Entry(String name, int meta, int emc, int nameIndex, int metaIndex, int emcIndex)
		{
			this.name = name;
			this.meta = meta;
			this.emc = emc;
			this.nameIndex = nameIndex;
			this.metaIndex = metaIndex;
			this.emcIndex = emcIndex;
		}
	}
}
