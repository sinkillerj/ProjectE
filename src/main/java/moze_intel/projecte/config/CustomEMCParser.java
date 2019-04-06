package moze_intel.projecte.config;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.annotations.SerializedName;

import moze_intel.projecte.PECore;
import moze_intel.projecte.emc.json.NSSFluid;
import moze_intel.projecte.emc.json.NSSItem;
import moze_intel.projecte.emc.json.NSSItemWithNBT;
import moze_intel.projecte.emc.json.NSSOreDictionary;
import moze_intel.projecte.emc.json.NormalizedSimpleStack;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public final class CustomEMCParser
{
	private static final Gson GSON = new GsonBuilder().registerTypeAdapter(NormalizedSimpleStack.class, NormalizedSimpleStack.Serializer.INSTANCE).setPrettyPrinting().create();
	private static final File CONFIG = new File(PECore.CONFIG_DIR, "custom_emc.json");

	private static final File CONFIG_DIR = new File(PECore.CONFIG_DIR, "custom_emc");
	
	public static class CustomEMCFile
	{
		public final List<CustomEMCEntry> entries;

		public CustomEMCFile(List<CustomEMCEntry> entries)
		{
			this.entries = entries;
		}
		
		public CustomEMCFile withCleanEntries(){			
			for(int i = 0; i < entries.size(); i++){
				for(int k = 0; k < i; k++){
					if(entries.get(k).nss.equals(entries.get(i).nss)){
						entries.remove(k);
						k--;
						i--;
					}
				}
			}
			return this;
		}
	}

	public static class CustomEMCEntry
	{
		@SerializedName("item")
		public final NormalizedSimpleStack nss;
		public long emc;
		

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
	
	public static HashMap<String, List<CustomEMCEntry>> customEMCEntries = new HashMap<>();
	
	private static boolean dirty = false;

	public static void init()
	{
		if(ProjectEConfig.misc.separateCustomEMC){
			if(!CONFIG_DIR.exists()){
				cloneAndDumpEMCMappings();
			}else{
				for(File f: CONFIG_DIR.listFiles()){
					if(f.getName().endsWith(".json")){
						CustomEMCFile currentEntries = null;
						try (BufferedReader reader = new BufferedReader(new FileReader(f))) {
							currentEntries = GSON.fromJson(reader, CustomEMCFile.class);
							currentEntries.entries.removeIf(e -> e.nss == null || e.emc < 0 || !(e.nss instanceof NSSItem ||e.nss instanceof NSSItemWithNBT || e.nss instanceof NSSOreDictionary));
						} catch (IOException | JsonParseException e) {
							PECore.LOGGER.fatal("Couldn't read custom emc file");
							e.printStackTrace();
							currentEntries = new CustomEMCFile(new ArrayList<>());
						}
						currentEntries = currentEntries.withCleanEntries();
						if(f.getName().equalsIgnoreCase("$OreDict.json")){
							getMod("$OreDict").addAll(currentEntries.entries);
						}else{
							createMapFromEntries(currentEntries.entries);
						}
					}
				}
				return;
			}
		}else{
			createMapFromEntries(readOriginalConfigFile().entries);
		}
		flush();
	}

	private static void cloneAndDumpEMCMappings() {
		CustomEMCFile totalEntries = readOriginalConfigFile();
		createMapFromEntries(totalEntries.entries);
		flush();
		return;
	}

	private static void createMapFromEntries(List<CustomEMCEntry> entries){
		for(CustomEMCEntry entry: entries){
			if(entry.nss instanceof NSSOreDictionary){
				getMod("$OreDict").add(entry);
			}else if (entry.nss instanceof NSSFluid){
				NSSFluid fluid = (NSSFluid)entry.nss;
				String[] names = fluid.name.split(":");
				if(names.length == 1){
					getMod("minecraft").add(entry);
				}else{
					getMod(names[0]).add(entry);
				}
			}else if (entry.nss instanceof NSSItem){
				NSSItem item = (NSSItem)entry.nss;
				String[] names = item.itemName.split(":");
				if(names.length == 1){
					getMod("minecraft").add(entry);
				}else{
					getMod(names[0]).add(entry);
				}
			}else if (entry.nss instanceof NSSItemWithNBT){
				NSSItemWithNBT item = (NSSItemWithNBT)entry.nss;
				String[] names = item.itemName.split(":");
				if(names.length == 1){
					getMod("minecraft").add(entry);
				}else{
					getMod(names[0]).add(entry);
				}
			}
		}
	}
	
	public static List<CustomEMCEntry> getMod(String key) {
		if(customEMCEntries == null){
			customEMCEntries = new HashMap();
		}if(!customEMCEntries.containsKey(key)){
			customEMCEntries.put(key, new ArrayList<CustomEMCEntry>());
		}
		return customEMCEntries.get(key);
	}

	private static CustomEMCFile readOriginalConfigFile() {
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
		CustomEMCFile currentEntries = null;
		try (BufferedReader reader = new BufferedReader(new FileReader(CONFIG))) {
			currentEntries = GSON.fromJson(reader, CustomEMCFile.class);
			currentEntries.entries.removeIf(e -> e.nss == null || e.emc < 0 || !(e.nss instanceof NSSItem ||e.nss instanceof NSSItemWithNBT|| e.nss instanceof NSSOreDictionary));
		} catch (IOException | JsonParseException e) {
			PECore.LOGGER.fatal("Couldn't read custom emc file");
			e.printStackTrace();
			currentEntries = new CustomEMCFile(new ArrayList<>());
		}
		return currentEntries.withCleanEntries();
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
		List<CustomEMCEntry> mod = null;
		if(nss instanceof NSSOreDictionary){
			mod = getMod("$OreDict");
		}else{
			String[] s = toAdd.split(":");
			mod = getMod(s[0]);
		}
		
		for(CustomEMCEntry entry2:mod){
			if(entry2.nss.equals(nss)){
				entry2.emc = emc;
				dirty = true;
				flush();
				return true;
			}
		}
		mod.add(entry);
		dirty = true;
		flush();
		return true;
	}
	
	public static boolean addWithNBTToFile(ItemStack itm, long emc)
	{
		return addWithNBTToFile(itm, NSSItemWithNBT.NO_IGNORES, emc);
	}
	
	public static boolean addWithNBTToFile(ItemStack itm, String[]ignores, long emc)
	{
		NSSItemWithNBT nssNBT = (NSSItemWithNBT) NSSItemWithNBT.create(itm, ignores);
		String mod = itm.getItem().getRegistryName().getNamespace();
		List<CustomEMCEntry> entries = getMod(mod);
		CustomEMCEntry entry = new CustomEMCEntry(nssNBT, emc);
		for(CustomEMCEntry entry2: getMod(mod)){
			if(entry2.nss.equals(nssNBT)){
				entry2.emc = emc;
				dirty = true;
				flush();
				return true;
			}
		}
		getMod(mod).add(entry);
		dirty = true;
		flush();
		return true;
	}

	public static boolean removeWithNBTFromFile(ItemStack toRemove)
	{
		return removeWithNBTFromFile(toRemove, NSSItemWithNBT.NO_IGNORES);
	}
	
	public static boolean removeWithNBTFromFile(ItemStack toRemove,
			String[] ignores) {
		NSSItemWithNBT nssNBT = (NSSItemWithNBT) NSSItemWithNBT.create(toRemove, ignores);
		String mod = toRemove.getItem().getRegistryName().getNamespace();
		List<CustomEMCEntry> entries = getMod(mod);
		if(entries.isEmpty()){
			return false;
		}
		int idx = 0;
		for(CustomEMCEntry entry: entries){
			if(entry.nss instanceof NSSItemWithNBT){
				if(((NSSItemWithNBT)entry.nss).equalsEvenPartially(nssNBT)){
					break;	
				}
			}
			idx++;
		}
		if(idx >= entries.size())
			return false;
		entries.remove(idx);
		return true;
	}
	
	public static boolean removeFromFile(String toRemove, int meta)
	{
		NormalizedSimpleStack nss = getNss(toRemove, meta);
		List<CustomEMCEntry> entries = null; 
		if(nss instanceof NSSOreDictionary){			
			entries = getMod("$OreDict");
		}else{
			String[] s = toRemove.split(":");
			entries = getMod(s[0]);
		}
		int idx = 0;
		for(CustomEMCEntry entry: entries){
			if(nss.equals(entry.nss)){
				break;
			}
			idx++;
		}
		
		if(idx >= entries.size())
			return false;
		entries.remove(idx);
		return true;
	}

	private static void flush()
	{
		if(ProjectEConfig.misc.separateCustomEMC && !CONFIG_DIR.exists())
			dirty = true;
		if(!ProjectEConfig.misc.separateCustomEMC && CONFIG_DIR.exists())
			dirty = true;
		if (dirty)
		{
			if(ProjectEConfig.misc.separateCustomEMC){
				if(customEMCEntries.isEmpty())
					return;
				if(!CONFIG_DIR.exists()){
					CONFIG_DIR.mkdir();
					if(!CONFIG_DIR.exists()){
						PECore.LOGGER.fatal("Couldn't create custom emc directory. Dumping to file instead.");
						ProjectEConfig.misc.separateCustomEMC = false;
						flush();
						return;
					}
				}
				if(customEMCEntries.containsKey("$OreDict")){
					File f = new File(CONFIG_DIR, "$OreDict.json");
					try
					{
						Files.write(GSON.toJson(new CustomEMCFile(customEMCEntries.get("$OreDict")).withCleanEntries()), f , Charsets.UTF_8);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				for(String k: customEMCEntries.keySet()){
					if(!k.equalsIgnoreCase("$OreDict")){
						File f = new File(CONFIG_DIR, k+".json");
						try
						{
							Files.write(GSON.toJson(new CustomEMCFile(customEMCEntries.get(k)).withCleanEntries()), f , Charsets.UTF_8);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				dirty = false;
				return;
			}else{
				if(CONFIG_DIR.exists()){
					delTree(CONFIG_DIR);
				}
				try
				{
					Files.write(GSON.toJson(getAllEntriesInSingleFile().withCleanEntries()), CONFIG, Charsets.UTF_8);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			dirty = false;
		}
	}

	private static void delTree(File dir) {
		for(File f: dir.listFiles()){
			if(f.isDirectory())
				delTree(f);
			else
				f.delete();
		}
		dir.delete();
	}

	private static CustomEMCFile getAllEntriesInSingleFile() {
		if(customEMCEntries.isEmpty())
			return new CustomEMCFile(new ArrayList<>());
		ArrayList<CustomEMCEntry> entries = new ArrayList<>();
		for(String k : customEMCEntries.keySet()){
			entries.addAll(customEMCEntries.get(k));
		}
		return new CustomEMCFile(entries);
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

	public static boolean containsItem(NormalizedSimpleStack stack) {
		String path = null;
		if(stack instanceof NSSOreDictionary){
			path = "$OreDict";
		}else if (stack instanceof NSSItem){
			path = ((NSSItem)stack).itemName.split(":")[0];
		}else if (stack instanceof NSSItemWithNBT){
			path = ((NSSItemWithNBT)stack).itemName.split(":")[0];
		}
		if(customEMCEntries.containsKey(path)){
			for(CustomEMCEntry entry: customEMCEntries.get(path)){
				if(entry.nss.equals(stack)){
					return true;
				}
			}
		}
		return false;
		
	}

	
}
