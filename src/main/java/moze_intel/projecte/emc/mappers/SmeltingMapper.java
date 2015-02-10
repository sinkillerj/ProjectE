package moze_intel.projecte.emc.mappers;

import moze_intel.projecte.emc.IMappingCollector;
import moze_intel.projecte.emc.IngredientMap;
import moze_intel.projecte.emc.NormalizedSimpleStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Map;

public class SmeltingMapper implements IEMCMapper<NormalizedSimpleStack, Integer> {
	@Override
	public void addMappings(IMappingCollector<NormalizedSimpleStack, Integer> mapper) {
		Map<ItemStack, ItemStack> smelting = FurnaceRecipes.smelting().getSmeltingList();
		for (Map.Entry<ItemStack, ItemStack> entry : smelting.entrySet()) {
			ItemStack input = entry.getKey();
			ItemStack output = entry.getValue();
			if (input == null || output == null) {
				continue;
			}
			IngredientMap<NormalizedSimpleStack> map = new IngredientMap<NormalizedSimpleStack>();
			map.addIngredient(NormalizedSimpleStack.getNormalizedSimpleStackFor(input), input.stackSize);
			mapper.addConversionMultiple(output.stackSize, NormalizedSimpleStack.getNormalizedSimpleStackFor(output), map.getMap());

		}
	}
}
