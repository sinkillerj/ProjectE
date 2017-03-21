package moze_intel.projecte.config;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import moze_intel.projecte.PECore;
import moze_intel.projecte.utils.NBTWhitelist;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public final class NBTWhitelistParser
{
	private static final Gson GSON = new GsonBuilder().registerTypeAdapter(NBTWhiteList.class, new Serializer()).setPrettyPrinting().create();
	private static final File CONFIG = new File(PECore.CONFIG_DIR, "nbt_whitelist.json");

	private static class NBTWhiteList
	{
		public final List<Item> items;

		private NBTWhiteList(List<Item> items)
		{
			this.items = items;
		}
	}

	private static class Serializer implements JsonSerializer<NBTWhiteList>, JsonDeserializer<NBTWhiteList>
	{
		@Override
		public NBTWhiteList deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
		{
			JsonObject obj = JsonUtils.getJsonObject(json, "nbt whitelist");
			JsonArray arr = JsonUtils.getJsonArray(obj, "items");
			List<String> itemNames = context.deserialize(arr, new TypeToken<List<String>>() {}.getType());

			List<Item> items = new ArrayList<>();

			for (String s : itemNames)
			{
				Item i = Item.REGISTRY.getObject(new ResourceLocation(s));
				if (i != null)
				{
					items.add(i);
				}
				else
				{
					PECore.LOGGER.warn("Could not find Item {} specified in nbt_whitelist.cfg", s);
				}
			}

			return new NBTWhiteList(items);
		}

		@Override
		public JsonElement serialize(NBTWhiteList src, Type typeOfSrc, JsonSerializationContext context)
		{
			List<String> registryNames = new ArrayList<>();

			for (Item item : src.items)
			{
				registryNames.add(item.getRegistryName().toString());
			}

			JsonObject ret = new JsonObject();
			ret.add("items", context.serialize(registryNames));
			return ret;
		}
	}

	public static void init()
	{
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
				e.printStackTrace();
			}
		}

		try (BufferedReader reader = new BufferedReader(new FileReader(CONFIG)))
		{
			NBTWhiteList whitelist = GSON.fromJson(reader, NBTWhiteList.class);
			for (Item i : whitelist.items)
            {
                NBTWhitelist.register(new ItemStack(i));
            }
		} catch (IOException e)
		{
			PECore.LOGGER.fatal("Couldn't read nbt whitelist file");
		}

	}

	private static void writeDefaultFile()
	{
		List<Item> defaults = new ArrayList<>();

		if (Loader.isModLoaded("tconstruct"))
		{
			defaults.add(Item.getByNameOrId("tconstruct:pickaxe"));
		}

		if (Loader.isModLoaded("botania"))
		{
			defaults.add(Item.getByNameOrId("botania:specialFlower"));
		}

		JsonObject obj = (JsonObject) GSON.toJsonTree(new NBTWhiteList(defaults));
		obj.add("__comment", new JsonPrimitive("To add items to NBT Whitelist, simply add its registry name as a String to the above array"));

		try
		{
			Files.write(GSON.toJson(obj), CONFIG, Charsets.UTF_8);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
