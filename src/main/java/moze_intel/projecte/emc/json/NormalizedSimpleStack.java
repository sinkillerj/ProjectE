package moze_intel.projecte.emc.json;

import moze_intel.projecte.emc.collector.IMappingCollector;
import moze_intel.projecte.utils.ItemHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface NormalizedSimpleStack {

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

	public static NormalizedSimpleStack fromSerializedItem(String serializedItem) {
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

		return NSSItem.create(itemName, itemDamage);
	}
}
