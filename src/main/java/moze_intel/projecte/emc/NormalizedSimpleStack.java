package moze_intel.projecte.emc;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import moze_intel.projecte.utils.PELogger;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

public abstract class NormalizedSimpleStack {
	public static Map<Integer, Set<Integer>> idWithUsedMetaData = Maps.newHashMap();

	public static NormalizedSimpleStack getNormalizedSimpleStackFor(int id, int damage) {
		if (id < 0) return null;
		NSSItem normStack = new NSSItem(id, damage);
		Set<Integer> usedMetadata;
		if (!idWithUsedMetaData.containsKey(normStack.id)) {
			usedMetadata = Sets.newHashSet();
			idWithUsedMetaData.put(normStack.id, usedMetadata);
		} else {
			usedMetadata = idWithUsedMetaData.get(normStack.id);
		}
		usedMetadata.add(normStack.damage);
		return normStack;
	}

	public static NormalizedSimpleStack getNormalizedSimpleStackFor(Block block) {
		return getNormalizedSimpleStackFor(new ItemStack(block));
	}

	public static NormalizedSimpleStack getNormalizedSimpleStackFor(Item item) {
		return getNormalizedSimpleStackFor(Item.itemRegistry.getIDForObject(item), 0);
	}

	public static NormalizedSimpleStack getNormalizedSimpleStackFor(Item item, int meta ) {
		return getNormalizedSimpleStackFor(Item.itemRegistry.getIDForObject(item), meta);
	}

	public static NormalizedSimpleStack getNormalizedSimpleStackFor(ItemStack stack) {
		if (stack == null || stack.getItem() == null) return null;
		int id = Item.itemRegistry.getIDForObject(stack.getItem());
		if (id < 0) {
			PELogger.logWarn(String.format("Could not get id for stack %s with item %s (Class: %s)", stack, stack.getItem(), stack.getItem().getClass()));
			return null;
		}
		return getNormalizedSimpleStackFor(id, stack.getItemDamage());
	}

	public static NormalizedSimpleStack getNormalizedSimpleStackFor(net.minecraftforge.fluids.Fluid fluid) {
		//TODO cache The fluid normalizedSimpleStacks?
		return new NSSFluid(fluid);
	}

	public static <V extends Comparable<V>> void addMappings(IMappingCollector<NormalizedSimpleStack, V> mapper) {
		for (Map.Entry<Integer, Set<Integer>> entry : idWithUsedMetaData.entrySet()) {
			entry.getValue().remove(OreDictionary.WILDCARD_VALUE);
			entry.getValue().add(0);
			NormalizedSimpleStack stackWildcard = new NSSItem(entry.getKey(), OreDictionary.WILDCARD_VALUE);
			for (int metadata : entry.getValue()) {
				mapper.addConversion(1, stackWildcard, Arrays.asList((NormalizedSimpleStack)new NSSItem(entry.getKey(), metadata)));
			}
		}
	}

	public abstract int hashCode();
	public abstract boolean equals(Object o);

	public static class NSSItem extends NormalizedSimpleStack{
		public int id;
		public int damage;
		private NSSItem(int id, int damage) {
			this.id = id;
			if (this.id == -1) {
				throw new IllegalArgumentException("Invalid Item with getIDForObject() == -1");
			}
			this.damage = damage;
		}

		public ItemStack toItemStack() {
			Item item = Item.getItemById(id);

			if (item != null) {
				return new ItemStack(Item.getItemById(id), 1, damage);
			}
			return null;
		}

		public NormalizedSimpleStack copy() {
			return new NSSItem(id, damage);
		}

		@Override
		public int hashCode() {
			return id;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof NSSItem) {
				NSSItem other = (NSSItem) obj;

				return this.id == other.id && this.damage == other.damage;
			}

			return false;
		}

		@Override
		public String toString() {
			Object obj = Item.itemRegistry.getObjectById(id);

			if (obj != null) {
				return "" + Item.itemRegistry.getNameForObject(obj) + "(" + id + ") " + (damage == OreDictionary.WILDCARD_VALUE ? "*" : damage);
			}

			return "id:" + id + " damage:" + (damage == OreDictionary.WILDCARD_VALUE ? "*" : damage);
		}
	}

	public static Map<Map<NormalizedSimpleStack,Integer>,NSSGroup> groups = Maps.newHashMap();
	public static NormalizedSimpleStack createGroup(Iterable<ItemStack> i) {
		IngredientMap<NormalizedSimpleStack> groupMap = new IngredientMap<NormalizedSimpleStack>();
		for (ItemStack itemStack:i) {
			NormalizedSimpleStack normStack = getNormalizedSimpleStackFor(itemStack);
			if (normStack == null) return null;
			groupMap.addIngredient(normStack, itemStack.stackSize);
		}
		Map<NormalizedSimpleStack,Integer> map = groupMap.getMap();
		NSSGroup g;
		if (groups.containsKey(map)) {
			g = groups.get(map);;
			map.clear();
		} else {
			g = new NSSGroup(map);
			groups.put(map, g);
		}
		return g;
  	}

	public static class NSSGroup extends NormalizedSimpleStack {
		Map<NormalizedSimpleStack, Integer> group;
		public NSSGroup(Map<NormalizedSimpleStack, Integer> g) {
			group = g;
		}

		public boolean equals(Object o) {
			if (o instanceof NSSGroup) {
				return group.equals(((NSSGroup)o).group);
			}
			return false;
		}
		@Override
		public int hashCode() {
			return this.group.hashCode();
		}

		@Override
		public String toString() {
			return this.group.keySet().toString();
		}
	}

	public static class NSSFluid extends NormalizedSimpleStack {

		String name;
		private NSSFluid(net.minecraftforge.fluids.Fluid f) {
			this.name = f.getName();
		}
		public boolean equals(Object o) {
			if (o instanceof NSSFluid) {
				return name.equals(((NSSFluid) o).name);
			}
			return false;
		}
		@Override
		public int hashCode() {
			return this.name.hashCode();
		}

		@Override
		public String toString() {
			return "Fluid: " + this.name;
		}
	}
}
