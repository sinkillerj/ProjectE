package moze_intel.projecte.emc.mappers.recipe.special;

import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.ImbueRecipe;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.component.DataComponents;
import java.util.ArrayList;
import moze_intel.projecte.api.mapper.collector.IMappingCollector;
import moze_intel.projecte.api.mapper.recipe.INSSFakeGroupManager;
import moze_intel.projecte.api.mapper.recipe.IRecipeTypeMapper;
import moze_intel.projecte.api.mapper.recipe.RecipeTypeMapper;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import moze_intel.projecte.api.nss.NSSItem;
import moze_intel.projecte.config.PEConfigTranslations;
import moze_intel.projecte.utils.EMCHelper;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;

@RecipeTypeMapper
public class TippedArrowMapper implements IRecipeTypeMapper {
    @Override
    public boolean handleRecipe(IMappingCollector<NormalizedSimpleStack, Long> mapper, RecipeHolder<?> recipeHolder,
            RegistryAccess registryAccess, INSSFakeGroupManager fakeGroupManager) {

        if (!(recipeHolder.value() instanceof ImbueRecipe recipe)) return false;
        var ingredients = recipe.placementInfo().ingredients();
        var sources = new ArrayList<ItemStack>();
        ingredients.get(4).items().forEach(item -> {
            if (item.value().components().has(DataComponents.POTION_CONTENTS)) {
                registryAccess.lookupOrThrow(Registries.POTION).listElements().forEach(potion -> {
                    var stack = new ItemStack(item);
                    stack.set(DataComponents.POTION_CONTENTS,
                            new PotionContents(potion));
                    sources.add(stack);
                });
            } else sources.add(new ItemStack(item));
        });
        for (var source : sources) {
            for (var material : ingredients.getFirst().items().toList()) {
                var stacks = new ArrayList<ItemStack>();
                for (int slot = 0; slot < 9; slot++) stacks.add(slot == 4 ? source : new ItemStack(material));
                var input = CraftingInput.of(3, 3, stacks);
                if (!recipe.matches(input, null)) continue;
                var result = recipe.assemble(input);
                if (!result.isEmpty()) mapper.addConversion(result.getCount(), NSSItem.createItem(result),
                        EMCHelper.intMapOf(NSSItem.createItem(source), 1, NSSItem.createItem(material), 8));
            }
        }
        return true;

    }
    @Override public boolean canHandle(RecipeType<?> type) { return type == RecipeType.CRAFTING; }
    @Override public String getName() { return PEConfigTranslations.MAPPING_CRAFTING_MAPPER_TIPPED_ARROW.title(); }
    @Override public String getTranslationKey() { return PEConfigTranslations.MAPPING_CRAFTING_MAPPER_TIPPED_ARROW.getTranslationKey(); }
    @Override public String getDescription() { return PEConfigTranslations.MAPPING_CRAFTING_MAPPER_TIPPED_ARROW.tooltip(); }
}
