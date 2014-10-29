package moze_intel.projecte.config;

import moze_intel.projecte.MozeCore;
import moze_intel.projecte.emc.EMCMapper;
import moze_intel.projecte.utils.PELogger;
import moze_intel.projecte.utils.Utils;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public final class FileParser
{
    private static final String VERSION = "#0.1";
    private static File EMC_CONFIG;

    public static void init()
    {
        EMC_CONFIG = new File(MozeCore.CONFIG_DIR, "custom_emc.cfg");

        if (!EMC_CONFIG.exists())
        {
            try
            {
                EMC_CONFIG.createNewFile();
            }
            catch (IOException e)
            {
                PELogger.logFatal("Exception in file I/O");
                e.printStackTrace();
            }

            PrintWriter writer = null;

            try
            {
                writer = new PrintWriter(EMC_CONFIG);

                writer.println(VERSION);
                writer.println("Custom EMC file");
                writer.println("This file is used for custom EMC registration: do NOT modify it manually.");
                writer.println("Use the in-game commands (projecte_addEMC/removeEMC/resetEMC).");
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            finally
            {
                Utils.closeStream(writer);
            }
        }

        BufferedReader reader = null;
        PrintWriter writer = null;

        try
        {
            reader = new BufferedReader(new FileReader(EMC_CONFIG));

            if (!reader.readLine().equals(VERSION))
            {
                PELogger.logFatal("Old custom_emc.cfg detected, resetting.");

                writer = new PrintWriter(EMC_CONFIG);

                writer.println(VERSION);
                writer.println("Custom EMC file");
                writer.println("This file is used for custom EMC registration: do NOT modify it manually.");
                writer.println("Use the in-game commands (projecte_addEMC/removeEMC/resetEMC).");
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            Utils.closeStream(reader);
            Utils.closeStream(writer);
        }
    }

    public static void readUserData()
    {
        Entry entry;
        LineNumberReader reader = null;

        try
        {
            reader = new LineNumberReader(new FileReader(EMC_CONFIG));

            while ((entry = getNextEntry(reader)) != null)
            {
                if (entry.name.contains(":"))
                {
                    ItemStack stack = Utils.getStackFromString(entry.name, entry.meta);

                    if (stack == null)
                    {
                        PELogger.logFatal("Error in custom EMC file: couldn't find item: " + entry.name);
                        continue;
                    }

                    EMCMapper.addMapping(stack, entry.emc);
                    PELogger.logInfo("Registered custom emc for: " + entry.name + "(" + entry.emc + ")");
                }
                else
                {
                    if (OreDictionary.getOres(entry.name).isEmpty())
                    {
                        PELogger.logFatal("Error in custom EMC file: no OD entry for " + entry.name);
                        continue;
                    }

                    EMCMapper.addMapping(entry.name, entry.emc);
                    PELogger.logInfo("Registered custom emc for: " + entry.name + "(" + entry.emc + ")");
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            Utils.closeStream(reader);
        }
    }

    public static boolean addToFile(String toAdd, int meta, int emc)
    {
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
                writer = new PrintWriter(new FileOutputStream(EMC_CONFIG, false));

                for (String s : file)
                {
                    writer.println(s);
                }

                result = true;
            }
            else
            {
                writer = new PrintWriter(new FileOutputStream(EMC_CONFIG, true));

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
            Utils.closeStream(writer);
        }

        return result;
    }

    public static boolean removeFromFile(String toRemove, int meta)
    {
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
                writer = new PrintWriter(new FileOutputStream(EMC_CONFIG, false));

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
            Utils.closeStream(writer);
        }

        return result;
    }

    private static List<String> readAllFile()
    {
        List<String> list = new ArrayList<String>();
        BufferedReader reader = null;

        try
        {
            reader = new BufferedReader(new FileReader(EMC_CONFIG));

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
            Utils.closeStream(reader);
        }

        return new ArrayList<String>();
    }

    private static List<Entry> getAllEntries()
    {
        List<Entry> list = new ArrayList<Entry>();
        LineNumberReader reader = null;

        try
        {
            reader = new LineNumberReader(new FileReader(EMC_CONFIG));

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
            Utils.closeStream(reader);
        }

        return new ArrayList<Entry>();
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
            else
            {
                continue;
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
