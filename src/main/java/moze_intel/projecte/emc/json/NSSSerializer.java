package moze_intel.projecte.emc.json;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Map;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.imc.IMCMethods;
import moze_intel.projecte.api.imc.NSSCreatorInfo;
import moze_intel.projecte.api.nss.NSSCreator;
import moze_intel.projecte.api.nss.NSSFake;
import moze_intel.projecte.api.nss.NSSFluid;
import moze_intel.projecte.api.nss.NSSItem;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ResourceLocationException;
import net.minecraftforge.fml.InterModComms;

public class NSSSerializer implements JsonSerializer<NormalizedSimpleStack>, JsonDeserializer<NormalizedSimpleStack> {

	public static NSSSerializer INSTANCE = new NSSSerializer();

	public static final NSSCreator fakeCreator = NSSFake::create;

	public static final NSSCreator itemCreator = string -> {
		//TODO: Should we verify that the item/item tag exists?
		if (string.startsWith("#")) {
			try {
				return NSSItem.createTag(new ResourceLocation(string.substring(1)));
			} catch (ResourceLocationException ex) {
				throw new JsonParseException("Malformed item tag ID", ex);
			}
		}
		try {
			return NSSItem.createItem(new ResourceLocation(string));
		} catch (ResourceLocationException e) {
			throw new JsonParseException("Malformed item ID", e);
		}
	};

	public static final NSSCreator fluidCreator = fluidName -> {
		//TODO: Should we verify that the fluid/fluid tag exists?
		if (fluidName.startsWith("#")) {
			try {
				return NSSFluid.createTag(new ResourceLocation(fluidName.substring(1)));
			} catch (ResourceLocationException ex) {
				throw new JsonParseException("Malformed fluid tag ID", ex);
			}
		}
		try {
			return NSSFluid.createFluid(new ResourceLocation(fluidName));
		} catch (ResourceLocationException e) {
			throw new JsonParseException("Malformed fluid ID", e);
		}
	};

	private Map<String, NSSCreator> creators = Collections.emptyMap();

	public void setCreators(Map<String, NSSCreator> creators) {
		//Make the map be immutable
		this.creators = ImmutableMap.copyOf(creators);
	}

	@Override
	public NormalizedSimpleStack deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		return deserialize(json.getAsString());
	}

	public NormalizedSimpleStack deserialize(String s) {
		//TODO: Add tests to support the different ones we add
		if (s.contains("|")) {
			String[] parts = s.split("\\|");
			String key = parts[0];
			if (creators.containsKey(key)) {
				return creators.get(key).create(parts[1]);
			}
		}
		//Fallback to the item creator
		return itemCreator.create(s);
	}

	@Override
	public JsonElement serialize(NormalizedSimpleStack src, Type typeOfSrc, JsonSerializationContext context) {
		return new JsonPrimitive(src.json());
	}

	public static void init() {
		registerDefault("FAKE", fakeCreator);
		registerDefault("ITEM", itemCreator);
		registerDefault("FLUID", fluidCreator);
	}

	private static void registerDefault(String key, NSSCreator creator) {
		InterModComms.sendTo(PECore.MODID, IMCMethods.REGISTER_NSS_SERIALIZER, () -> new NSSCreatorInfo(key, creator));
	}
}