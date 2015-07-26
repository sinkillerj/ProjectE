package moze_intel.projecte.emc;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import moze_intel.projecte.utils.PELogger;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Arrays;
import java.util.List;
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

		for (Map.Entry<String, NormalizedSimpleStack> entry: oreDictStacks.entrySet()) {
			NormalizedSimpleStack oreDictStack = entry.getValue();
			List<ItemStack> list = OreDictionary.getOres(entry.getKey());
			for (ItemStack i: list) {
				mapper.addConversion(1, oreDictStack, Arrays.asList(NormalizedSimpleStack.getNormalizedSimpleStackFor(i)));
			}
		}
	}

	public abstract boolean equals(Object o);


	private static Map<String, NormalizedSimpleStack> oreDictStacks = Maps.newHashMap();
	public static NormalizedSimpleStack forOreDictionary(String oreDictionaryName)
	{
		if (oreDictStacks.containsKey(oreDictionaryName))
			return oreDictStacks.get(oreDictionaryName);
		List<ItemStack> list = OreDictionary.getOres(oreDictionaryName);
		if (list == null || list.size() == 0) {
			return null;
		}
		NormalizedSimpleStack nss = new NSSOreDictionary(oreDictionaryName);
		oreDictStacks.put(oreDictionaryName, nss);
		return nss;
	}

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
				return String.format("%s(%s:%s)", Item.itemRegistry.getNameForObject(obj), id, damage == OreDictionary.WILDCARD_VALUE ? "*" : damage);
			}

			return "id:" + id + " damage:" + (damage == OreDictionary.WILDCARD_VALUE ? "*" : damage);
		}
	}

	public static NormalizedSimpleStack createFake() {
		return new NSSFake();
  	}

	public static class NSSFake extends NormalizedSimpleStack {
		public boolean equals(Object o) {
			if (o instanceof NSSFake) {
				return o == this;
			}
			return false;
		}

		@Override
		public String toString() {
			return "NSSFAKE: " + super.toString();
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

	public static class NSSOreDictionary extends NormalizedSimpleStack {

		public final String od;
		private NSSOreDictionary(String od) {
			this.od = od;
		}

		@Override
		public int hashCode()
		{
			return od.hashCode();
		}

		@Override
		public boolean equals(Object o)
		{
			if (o instanceof NSSOreDictionary) {
				return this.od.equals(((NSSOreDictionary) o).od);
			}
			return false;
		}
	}

	public static NormalizedSimpleStack fromSerializedItem(String serializedItem) throws Exception {
		int pipeIndex = serializedItem.lastIndexOf('|');
		if (pipeIndex < 0)
		{
			return null;
		}
		String itemName = serializedItem.substring(0, pipeIndex);
		String itemDamageString = serializedItem.substring(pipeIndex + 1);
		int itemDamage;
		try {
			itemDamage = Integer.parseInt(itemDamageString);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(String.format("Could not parse '%s' to metadata-integer", itemDamageString), e);
		}

		Object itemObject = Item.itemRegistry.getObject(itemName);
		if (itemObject != null)
		{
			int id = Item.itemRegistry.getIDForObject(itemObject);
			return NormalizedSimpleStack.getNormalizedSimpleStackFor(id, itemDamage);
		}
		PELogger.logWarn(String.format("Could not get Item-Object for Item with name: '%s'", itemName));
		return null;
	}
}
