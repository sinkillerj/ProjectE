package moze_intel.projecte.emc.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import moze_intel.projecte.emc.collector.IMappingCollector;
import moze_intel.projecte.utils.ItemHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.oredict.OreDictionary;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface NormalizedSimpleStack {

	public static enum Serializer implements JsonSerializer<NormalizedSimpleStack>, JsonDeserializer<NormalizedSimpleStack> {
		INSTANCE;

		@Override
		public NormalizedSimpleStack deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			String s = json.getAsString();
			if (s.startsWith("OD|")) {
				return NSSOreDictionary.create(s.substring("OD|".length()));
			} else if (s.startsWith("FAKE|")) {
				return NSSFake.create(s.substring("FAKE|".length()));
			} else if (s.startsWith("FLUID|")) {
				String fluidName = s.substring("FLUID|".length());
				Fluid fluid = FluidRegistry.getFluid(fluidName);
				if (fluid == null)
					throw new JsonParseException("Tried to identify nonexistent Fluid " + fluidName);
				return NSSFluid.create(fluid);
			} else {
				int pipeIndex = s.lastIndexOf('|');
				if (pipeIndex < 0)
				{
					throw new JsonParseException(String.format("Cannot parse '%s' as itemstack. Missing | to separate metadata.", s));
				}
				String itemName = s.substring(0, pipeIndex);
				String itemDamageString = s.substring(pipeIndex + 1);
				int itemDamage;
				if (itemDamageString.equals("*")) {
					itemDamage = OreDictionary.WILDCARD_VALUE;
				}
				else {
					try {
						itemDamage = Integer.parseInt(itemDamageString);
					} catch (NumberFormatException e) {
						throw new JsonParseException(String.format("Could not parse '%s' to metadata-integer", itemDamageString), e);
					}
				}

				return NSSItem.create(itemName, itemDamage);
			}
		}

		@Override
		public JsonElement serialize(NormalizedSimpleStack src, Type typeOfSrc, JsonSerializationContext context) {
			return new JsonPrimitive(src.json());
		}
	}

	public static <V extends Comparable<V>> void addMappings(IMappingCollector<NormalizedSimpleStack, V> mapper) {
		for (Map.Entry<String, Set<Integer>> entry : NSSItem.idWithUsedMetaData.entrySet()) {
			entry.getValue().remove(OreDictionary.WILDCARD_VALUE);
			entry.getValue().add(0);
			NormalizedSimpleStack stackWildcard = new NSSItem(entry.getKey(), OreDictionary.WILDCARD_VALUE);
			for (int metadata : entry.getValue()) {
				mapper.addConversion(1, stackWildcard, Collections.singletonList(new NSSItem(entry.getKey(), metadata)));
			}
		}

		for (Map.Entry<String, NormalizedSimpleStack> entry: NSSOreDictionary.oreDictStacks.entrySet()) {
			NormalizedSimpleStack oreDictStack = entry.getValue();
			List<ItemStack> list = ItemHelper.getODItems(entry.getKey());
			for (ItemStack i: list) {
				mapper.addConversion(1, oreDictStack, Collections.singletonList(NSSItem.create(i)));
				mapper.addConversion(1, NSSItem.create(i), Collections.singletonList(oreDictStack));
			}
		}
	}

	@Override
	public boolean equals(Object o);

	public String json();
}
