package moze_intel.projecte.emc.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import mezz.jei.util.Log;
import moze_intel.projecte.emc.collector.IMappingCollector;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ResourceLocationException;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Map;

public interface NormalizedSimpleStack {

	public static enum Serializer implements JsonSerializer<NormalizedSimpleStack>, JsonDeserializer<NormalizedSimpleStack> {
		INSTANCE;

		@Override
		public NormalizedSimpleStack deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			String s = json.getAsString();
			if (s.startsWith("#")) {
				try
				{
					return NSSTag.create(s.substring(1));
				} catch (ResourceLocationException ex)
				{
					throw new JsonParseException("Malformed tag ID", ex);
				}
			} else if (s.startsWith("FAKE|")) {
				return NSSFake.create(s.substring("FAKE|".length()));
			} else if (s.startsWith("FLUID|")) {
				String fluidName = s.substring("FLUID|".length());
				Fluid fluid = FluidRegistry.getFluid(fluidName);
				if (fluid == null)
					throw new JsonParseException("Tried to identify nonexistent Fluid " + fluidName);
				return NSSFluid.create(fluid);
			} else {
				try
				{
					return new NSSItem(new ResourceLocation(s));
				} catch (ResourceLocationException e)
				{
					throw new JsonParseException("Malformed item ID");
				}
			}
		}

		@Override
		public JsonElement serialize(NormalizedSimpleStack src, Type typeOfSrc, JsonSerializationContext context) {
			return new JsonPrimitive(src.json());
		}
	}

	public static <V extends Comparable<V>> void addMappings(IMappingCollector<NormalizedSimpleStack, V> mapper) {
		// Add conversions for all items <-> NSSTag for tags they belong to
		for (Map.Entry<String, NSSTag> entry: NSSTag.tagStacks.entrySet()) {
			NSSTag nssTag = entry.getValue();
			for (Item i : nssTag.getAllElements()) {
				mapper.addConversion(1, nssTag, Collections.singletonList(new NSSItem(i)));
				mapper.addConversion(1, new NSSItem(i), Collections.singletonList(nssTag));
			}
		}
	}

	@Override
	public boolean equals(Object o);

	public String json();
}
