package moze_intel.projecte.emc.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ResourceLocationException;
import net.minecraftforge.registries.ForgeRegistries;

public interface NormalizedSimpleStack {

	enum Serializer implements JsonSerializer<NormalizedSimpleStack>, JsonDeserializer<NormalizedSimpleStack> {
		INSTANCE;

		@Override
		public NormalizedSimpleStack deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			//TODO: Make it so the NSS types get registered and then it queries them for creating
			String s = json.getAsString();
			if (s.startsWith("#")) {
				try {
					return NSSItem.createTag(new ResourceLocation(s.substring(1)));
				} catch (ResourceLocationException ex) {
					throw new JsonParseException("Malformed item tag ID", ex);
				}
			} else if (s.startsWith("FAKE|")) {
				return NSSFake.create(s.substring("FAKE|".length()));
			} else if (s.startsWith("FLUID|")) {
				String fluidName = s.substring("FLUID|".length());
				if (fluidName.startsWith("#")) {
					try {
						return NSSFluid.createTag(new ResourceLocation(s.substring("FLUID|#".length())));
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
			} else {
				try {
					return NSSItem.createItem(new ResourceLocation(s));
				} catch (ResourceLocationException e) {
					throw new JsonParseException("Malformed item ID", e);
				}
			}
		}

		@Override
		public JsonElement serialize(NormalizedSimpleStack src, Type typeOfSrc, JsonSerializationContext context) {
			return new JsonPrimitive(src.json());
		}
	}

	@Override
	boolean equals(Object o);

	String json();
}
