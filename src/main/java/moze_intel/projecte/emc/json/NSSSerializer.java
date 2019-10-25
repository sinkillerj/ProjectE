package moze_intel.projecte.emc.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.imc.IMCMethods;
import moze_intel.projecte.api.imc.NSSCreatorInfo;
import moze_intel.projecte.api.nss.NSSCreator;
import moze_intel.projecte.api.nss.NSSFake;
import moze_intel.projecte.api.nss.NSSFluid;
import moze_intel.projecte.api.nss.NSSItem;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ResourceLocationException;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.registries.ForgeRegistries;

public class NSSSerializer implements JsonSerializer<NormalizedSimpleStack>, JsonDeserializer<NormalizedSimpleStack> {

	public static NSSSerializer INSTANCE = new NSSSerializer();

	private Map<String, NSSCreator> creatorHelper = new HashMap<>();

	private NSSCreator itemCreator = string -> {
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

	public void addCreator(String key, NSSCreator creator) {
		//TODO: Should we check if we already have one registered for the key?
		creatorHelper.put(key, creator);
	}

	@Override
	public NormalizedSimpleStack deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		String s = json.getAsString();
		if (s.contains("|")) {
			String[] parts = s.split("\\|");
			String key = parts[0];
			if (creatorHelper.containsKey(key)) {
				return creatorHelper.get(key).apply(parts[1]);
			}
		}
		return itemCreator.apply(s);
	}

	@Override
	public JsonElement serialize(NormalizedSimpleStack src, Type typeOfSrc, JsonSerializationContext context) {
		return new JsonPrimitive(src.json());
	}

	public static void init() {
		//TODO: Do we also want to register the itemCreator via the ITEM key?
		registerDefault("FAKE", NSSFake::create);
		registerDefault("FLUID", fluidName -> {
			if (fluidName.startsWith("#")) {
				try {
					return NSSFluid.createTag(new ResourceLocation(fluidName.substring(1)));
				} catch (ResourceLocationException ex) {
					throw new JsonParseException("Malformed fluid tag ID", ex);
				}
			}
			Fluid fluid;
			try {
				fluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(fluidName));
			} catch (ResourceLocationException e) {
				throw new JsonParseException("Malformed fluid ID", e);
			}
			if (fluid == null) {
				throw new JsonParseException("Tried to identify nonexistent fluid " + fluidName);
			}
			return NSSFluid.createFluid(fluid);
		});
	}

	private static void registerDefault(String key, NSSCreator creator) {
		InterModComms.sendTo(PECore.MODID, IMCMethods.REGISTER_NSS_SERIALIZER, () -> new NSSCreatorInfo(key, creator));
	}
}