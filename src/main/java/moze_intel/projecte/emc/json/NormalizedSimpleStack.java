package moze_intel.projecte.emc.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import mezz.jei.util.Log;
import moze_intel.projecte.PECore;
import moze_intel.projecte.emc.collector.IMappingCollector;
import moze_intel.projecte.utils.ItemHelper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.oredict.OreDictionary;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

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

	// "Explode" a wildcarded item into all of its variants
	static Iterable<NormalizedSimpleStack> getVariants(String id) {
		Item i = Item.getByNameOrId(id);
		if (i == null) {
			PECore.LOGGER.error("null item in getVariants");
			return Collections.emptyList();
		}

		// Adapted from JEI StackHelper.getSubtypes
		NonNullList<ItemStack> variants = NonNullList.create();
		for (CreativeTabs group : i.getCreativeTabs()) {
			if (group == null) {
				variants.add(new ItemStack(i));
			} else {
				NonNullList<ItemStack> subItems = NonNullList.create();
				try {
					i.getSubItems(group, subItems);
				} catch (RuntimeException | LinkageError e) {
					PECore.LOGGER.warn("couldn't get variants of {}: {}", i, e);
				}

				for (ItemStack sub : subItems) {
					if (!sub.isEmpty()) {
						variants.add(sub);
					}
				}
			}
		}

		// collapse by metadata
		Set<NormalizedSimpleStack> ret = new HashSet<>();
		for (ItemStack variant : variants) {
			ret.add(new NSSItem(variant.getItem().getRegistryName().toString(), variant.getItemDamage()));
		}
		return ret;
	}

	public static <V extends Comparable<V>> void addMappings(IMappingCollector<NormalizedSimpleStack, V> mapper) {
		// Add conversions for all variants -> wildcard variant
		for (String id : NSSItem.seenIds) {
			NormalizedSimpleStack stackWildcard = new NSSItem(id, OreDictionary.WILDCARD_VALUE);
			for (NormalizedSimpleStack variant : getVariants(id)) {
				mapper.addConversion(1, stackWildcard, Collections.singletonList(variant));
			}
		}

		// Add conversions for all variants <-> NSSOreDict
		for (Map.Entry<String, NormalizedSimpleStack> entry: NSSOreDictionary.oreDictStacks.entrySet()) {
			NormalizedSimpleStack oreDictStack = entry.getValue();
			for (ItemStack i : ItemHelper.getODItems(entry.getKey())) {
				mapper.addConversion(1, oreDictStack, Collections.singletonList(NSSItem.create(i)));
				mapper.addConversion(1, NSSItem.create(i), Collections.singletonList(oreDictStack));
			}
		}
	}

	@Override
	public boolean equals(Object o);

	public String json();
}
