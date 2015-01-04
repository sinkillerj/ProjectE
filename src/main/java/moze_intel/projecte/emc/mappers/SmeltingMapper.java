package moze_intel.projecte.emc.mappers;

import moze_intel.projecte.emc.IMappingCollector;
import moze_intel.projecte.emc.IngredientMap;
import moze_intel.projecte.emc.NormalizedSimpleStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;

import java.util.Map;

public class SmeltingMapper implements IEMCMapper<NormalizedSimpleStack> {
    @Override
    public void addMappings(IMappingCollector<NormalizedSimpleStack> mapper) {
        Map<ItemStack, ItemStack> smelting = FurnaceRecipes.smelting().getSmeltingList();
        for (Map.Entry<ItemStack, ItemStack> entry: smelting.entrySet()) {
            ItemStack input = entry.getKey();
            ItemStack output = entry.getValue();
            if (input == null || output == null) {
                continue;
            }
            NormalizedSimpleStack normInput = new NormalizedSimpleStack(input);
            NormalizedSimpleStack normOutput = new NormalizedSimpleStack(output);
            IngredientMap<NormalizedSimpleStack> map = new IngredientMap<NormalizedSimpleStack>();
            map.addIngredient(normInput, input.stackSize);
            mapper.addConversionMultiple(output.stackSize, normOutput, map.getMap());
        }
    }
}
