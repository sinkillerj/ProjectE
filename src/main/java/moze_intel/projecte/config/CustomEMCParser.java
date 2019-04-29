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
import moze_intel.projecte.emc.json.NSSTag;
import moze_intel.projecte.emc.json.NormalizedSimpleStack;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ResourceLocationException;

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
					if(entries.get(k).item.equals(entries.get(i).item)){
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
		public final NormalizedSimpleStack item;
		public long emc;
		

		private CustomEMCEntry(NormalizedSimpleStack nss, long emc)
		{
			this.item = nss;
			this.emc = emc;
		}

		@Override
		public boolean equals(Object o)
		{
			return o == this ||
					o instanceof CustomEMCEntry
							&& item.equals(((CustomEMCEntry) o).item)
							&& emc == ((CustomEMCEntry) o).emc;
		}

		@Override
		public int hashCode() {
			int result = item != null ? item.hashCode() : 0;
			result = 31 * result + (int) (emc ^ (emc >>> 32));
			return result;
		}
	}
	
	public static HashMap<String, List<CustomEMCEntry>> customEMCEntries = new HashMap<>();
	
	private static boolean dirty = false;

	public static void init()
	{
		customEMCEntries.clear();
		if(ProjectEConfig.misc.separateCustomEMC.get()){
			if(!CONFIG_DIR.exists()){
				cloneAndDumpEMCMappings();
			}else{
				for(File f: CONFIG_DIR.listFiles()){
					if(f.getName().endsWith(".json")){
						CustomEMCFile currentEntries = null;
						try (BufferedReader reader = new BufferedReader(new FileReader(f))) {
							currentEntries = GSON.fromJson(reader, CustomEMCFile.class);
							currentEntries.entries.removeIf(e -> e.item == null || e.emc < 0 || !(e.item instanceof NSSItem));
						} catch (IOException | JsonParseException e) {
							PECore.LOGGER.fatal("Couldn't read custom emc file");
							e.printStackTrace();
							currentEntries = new CustomEMCFile(new ArrayList<>());
						}
						currentEntries = currentEntries.withCleanEntries();
						if(f.getName().equalsIgnoreCase("$Tag.json")){
							getMod("$Tag").addAll(currentEntries.entries);
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
			//NSSFluid currently not working
			if (entry.item instanceof NSSFluid){
				NSSFluid fluid = (NSSFluid)entry.item;
				String[] names = fluid.name.split(":");
				if(names.length == 1){
					getMod("$Tag").add(entry);
				}else{
					getMod(names[0]).add(entry);
				}
			}else if (entry.item instanceof NSSItem){
				NSSItem item = (NSSItem)entry.item;
				String[] names = item.itemName.toString().split(":");
				if(names.length == 1){
					getMod("$Tag").add(entry);
				}else{
					getMod(names[0]).add(entry);
				}
			}else if (entry.item instanceof NSSTag){
				getMod("$Tag").add(entry);
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
			currentEntries.entries.removeIf(e -> e.item == null || e.emc < 0 || !(e.item instanceof NSSItem));
		} catch (IOException | JsonParseException e) {
			PECore.LOGGER.fatal("Couldn't read custom emc file");
			e.printStackTrace();
			currentEntries = new CustomEMCFile(new ArrayList<>());
		}
		return currentEntries.withCleanEntries();
	}

	public static boolean addToFile(ItemStack itm, long emc){
		NormalizedSimpleStack nss = new NSSItem(itm);
		return addToFile(nss, emc);
	}
	
	public static boolean addToFile(String name, long emc){
		NormalizedSimpleStack nss;
		if(name.startsWith("#")){
			try
			{
				nss = NSSTag.create(name.substring(1));
			} catch (ResourceLocationException ex)
			{
				throw new JsonParseException("Malformed tag ID", ex);
			}
		}else if(name.split("|").length > 1){
			String name2 = name.substring(0, name.indexOf("|"));
			String tag = name.substring(name.indexOf("|")+1);
			if(name2.split(":").length == 1){
				nss = new NSSItem(new ResourceLocation("$Tag",name2), tag);
			}else{
				nss = new NSSItem(new ResourceLocation(name2), tag);
			}
		}else{
			if(name.split(":").length == 1)
				nss = new NSSItem(new ResourceLocation("$Tag",name));
			else
				nss = new NSSItem(new ResourceLocation(name));
		}
		return addToFile(nss, emc);
	}
	
	public static boolean addToFile(NormalizedSimpleStack nss, long emc) {
		CustomEMCEntry entry = new CustomEMCEntry(nss, emc);
		List<CustomEMCEntry> mod = null;
		if(nss instanceof NSSTag){
			mod = getMod("$Tag");
		}else if (nss instanceof NSSItem){
			String[] s = ((NSSItem)nss).getName().toString().split(":");
			if(s.length == 1)
				mod = getMod("$Tag");
			mod = getMod(s[0]);
		}
		
		for(CustomEMCEntry entry2:mod){
			if(entry2.item.equals(nss)){
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
	public static boolean removeFromFile(ItemStack itm){
		NormalizedSimpleStack nss = new NSSItem(itm);
		return removeFromFile(nss);
	}
	
	public static boolean removeFromFile(String name){
		NormalizedSimpleStack nss;
		if(name.startsWith("#")){
			try
			{
				nss = NSSTag.create(name.substring(1));
			} catch (ResourceLocationException ex)
			{
				throw new JsonParseException("Malformed tag ID", ex);
			}
		}else if(name.split("|").length > 1){
			String name2 = name.substring(0, name.indexOf("|"));
			String tag = name.substring(name.indexOf("|")+1);
			if(name2.split(":").length == 1){
				nss = new NSSItem(new ResourceLocation("$Tag",name2), tag);
			}else{
				nss = new NSSItem(new ResourceLocation(name2), tag);
			}
		}else{
			if(name.split(":").length == 1)
				nss = new NSSItem(new ResourceLocation("$Tag",name));
			else
				nss = new NSSItem(new ResourceLocation(name));
		}
		return removeFromFile(nss);
	}
	
	public static boolean removeFromFile(NormalizedSimpleStack nss) {
		List<CustomEMCEntry> mod = null; 
		String[] s = ((NSSItem)nss).getName().toString().split(":");
		if(s.length == 1)
			mod = getMod("$Tag");
		mod = getMod(s[0]);
		
		int idx = 0;
		for(CustomEMCEntry entry2: mod){
			if(nss.equals(entry2.item)){
				break;
			}
			idx++;
		}
		
		if(idx >= mod.size())
			return false;
		mod.remove(idx);
		return true;
	}
	
	

	private static void flush()
	{
		if(ProjectEConfig.misc.separateCustomEMC.get() && !CONFIG_DIR.exists())
			dirty = true;
		if(!ProjectEConfig.misc.separateCustomEMC.get() && CONFIG_DIR.exists())
			dirty = true;
		if (dirty)
		{
			if(ProjectEConfig.misc.separateCustomEMC.get()){
				if(customEMCEntries.isEmpty())
					return;
				if(!CONFIG_DIR.exists()){
					CONFIG_DIR.mkdir();
					if(!CONFIG_DIR.exists()){
						PECore.LOGGER.fatal("Couldn't create custom emc directory. Dumping to file instead.");
						flush();
						return;
					}
				}
				if(customEMCEntries.containsKey("$Tag")){
					File f = new File(CONFIG_DIR, "$Tag.json");
					try
					{
						Files.write(GSON.toJson(new CustomEMCFile(customEMCEntries.get("$Tag")).withCleanEntries()), f , Charsets.UTF_8);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				for(String k: customEMCEntries.keySet()){
					if(!k.equalsIgnoreCase("$Tag")){
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
		if (stack instanceof NSSItem){
			path = ((NSSItem)stack).itemName.toString().split(":")[0];
		}else if(stack instanceof NSSTag){
			path = "$Tag";
		}
		
		if(customEMCEntries.containsKey(path)){
			for(CustomEMCEntry entry: customEMCEntries.get(path)){
				if(entry.item.equals(stack)){
					return true;
				}
			}
		}
		return false;
		
	}

	
}
