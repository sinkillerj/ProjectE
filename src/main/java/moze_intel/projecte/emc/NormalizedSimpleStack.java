package moze_intel.projecte.emc;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import moze_intel.projecte.emc.collector.IMappingCollector;
import moze_intel.projecte.utils.ItemHelper;
import moze_intel.projecte.utils.PELogger;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class NormalizedSimpleStack {
	public static Map<Integer, Set<Integer>> idWithUsedMetaData = Maps.newHashMap();

	public static NormalizedSimpleStack getFor(int id, int damage) {
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

	public static NormalizedSimpleStack getFor(Block block) {
		return getFor(new ItemStack(block));
	}

	public static NormalizedSimpleStack getFor(Item item) {
		return getFor(Item.itemRegistry.getIDForObject(item), 0);
	}

	public static NormalizedSimpleStack getFor(Item item, int meta) {
		return getFor(Item.itemRegistry.getIDForObject(item), meta);
	}

	public static NormalizedSimpleStack getFor(ItemStack stack) {
		if (stack == null || stack.getItem() == null) return null;
		int id = Item.itemRegistry.getIDForObject(stack.getItem());
		if (id < 0) {
			PELogger.logWarn(String.format("Could not get id for stack %s with item %s (Class: %s)", stack, stack.getItem(), stack.getItem().getClass()));
			return null;
		}
		return getFor(id, stack.getItemDamage());
	}

	public static NormalizedSimpleStack getFor(net.minecraftforge.fluids.Fluid fluid) {
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
			List<ItemStack> list = ItemHelper.getODItems(entry.getKey());
			for (ItemStack i: list) {
				mapper.addConversion(1, oreDictStack, Arrays.asList(NormalizedSimpleStack.getFor(i)));
				mapper.addConversion(1, NormalizedSimpleStack.getFor(i), Arrays.asList(oreDictStack));
			}
		}
	}

	public abstract boolean equals(Object o);
	public abstract String json();


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
		public String json()
		{
			Item obj = Item.itemRegistry.getObjectById(id);

			if (obj != null) {
				return String.format("%s|%s", Item.itemRegistry.getNameForObject(obj),  damage == OreDictionary.WILDCARD_VALUE ? "*" : damage);
			}
			throw new IllegalArgumentException("Cannot get json Representation for  " + this.toString());
		}

		@Override
		public String toString() {
			Item obj = Item.itemRegistry.getObjectById(id);

			if (obj != null) {
				return String.format("%s(%s:%s)", Item.itemRegistry.getNameForObject(obj), id, damage == OreDictionary.WILDCARD_VALUE ? "*" : damage);
			}

			return "id:" + id + " damage:" + (damage == OreDictionary.WILDCARD_VALUE ? "*" : damage);
		}
	}

	public static NormalizedSimpleStack createFake(String description) {
		return new NSSFake(description);
	}

	public static class NSSFake extends NormalizedSimpleStack {
		public final String description;
		public final int counter;
		private static int fakeItemCounter = 0;
		public NSSFake(String description)
		{
			this.counter = fakeItemCounter++;
			this.description = description;
		}

		public boolean equals(Object o) {
			if (o instanceof NSSFake) {
				return o == this;
			}
			return false;
		}

		@Override
		public String json()
		{
			return "FAKE|" + this.counter + " " + this.description;
		}

		@Override
		public String toString() {
			return "NSSFAKE" + counter + ": " + description;
		}
	}

	public static class NSSFluid extends NormalizedSimpleStack {

		public final String name;
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
		public String json()
		{
			return "FLUID|"+this.name;
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

		@Override
		public String json()
		{
			return "OD|" + this.od;
		}

		@Override
		public String toString() {
			return "OD: " + od;
		}
	}

	public static NormalizedSimpleStack fromSerializedItem(String serializedItem) throws Exception {
		int pipeIndex = serializedItem.lastIndexOf('|');
		if (pipeIndex < 0)
		{
			throw new IllegalArgumentException(String.format("Cannot parse '%s' as itemstack. Missing | to separate metadata.", serializedItem));
		}
		String itemName = serializedItem.substring(0, pipeIndex);
		String itemDamageString = serializedItem.substring(pipeIndex + 1);
		int itemDamage;
		if (itemDamageString.equals("*"))
		{
			itemDamage = OreDictionary.WILDCARD_VALUE;
		}
		else
		{
			try
			{
				itemDamage = Integer.parseInt(itemDamageString);
			} catch (NumberFormatException e)
			{
				throw new IllegalArgumentException(String.format("Could not parse '%s' to metadata-integer", itemDamageString), e);
			}
		}

		Item itemObject = Item.itemRegistry.getObject(new ResourceLocation(itemName));
		if (itemObject != null)
		{
			int id = Item.itemRegistry.getIDForObject(itemObject);
			return NormalizedSimpleStack.getFor(id, itemDamage);
		}
		PELogger.logWarn(String.format("Could not get Item-Object for Item with name: '%s'", itemName));
		return null;
	}
}
