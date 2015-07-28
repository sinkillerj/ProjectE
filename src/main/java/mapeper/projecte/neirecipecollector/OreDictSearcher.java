package mapeper.projecte.neirecipecollector;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.primitives.Ints;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class OreDictSearcher
{
	public static String tryToFindOreDict(ItemStack[] stacks) {
		Set<Integer> intersectionOfAllOreIDs = Sets.newHashSet();
		int[] oreIdsForFirstItem = OreDictionary.getOreIDs(stacks[0]);
		intersectionOfAllOreIDs.addAll(Ints.asList(oreIdsForFirstItem));
		for (ItemStack stack: stacks)
		{
			int[] ids = OreDictionary.getOreIDs(stack);
			intersectionOfAllOreIDs.retainAll(Ints.asList(ids));
		}
		if (intersectionOfAllOreIDs.size() > 0) {
			List<String> oreNames = Lists.newArrayList();
			//We found some OreIds that all the itemstacks had.
			//Lets check if we can find one that does not contain more items than it should.
			for (int oreID: intersectionOfAllOreIDs) {
				List<ItemStack> ores = OreDictionary.getOres(OreDictionary.getOreName(oreID));
				boolean thisOreIDvalid = true;
				for (final ItemStack ore: ores) {
					//Check if 'ore' is present in 'stacks'
					if (!Iterators.any(Arrays.asList(stacks).iterator(), new Predicate<ItemStack>()
					{
						@Override
						public boolean apply(ItemStack input)
						{
							return ore.getItem() == input.getItem() && ore.getItemDamage() == input.getItemDamage();
						}
					})) {
						//Found an 'ore' that is not in 'stacks'
						thisOreIDvalid = false;
						break;
					}
				}
				if (thisOreIDvalid) {
					oreNames.add(OreDictionary.getOreName(oreID));
				}
			}
			if (oreNames.size() == 1) {
				return oreNames.get(0);
			} else if (oreNames.size() == 0) {

			} else {

			}
		}
		return null;
	}
}
