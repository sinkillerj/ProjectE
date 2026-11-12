package moze_intel.projecte.emc.mappers.recipe.special;

import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.TransmuteRecipe;
import net.minecraft.world.item.ItemStack;
import java.util.Collections;
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
public class ShulkerRecoloringMapper implements IRecipeTypeMapper {
    @Override
    public boolean handleRecipe(IMappingCollector<NormalizedSimpleStack, Long> mapper, RecipeHolder<?> recipeHolder,
            RegistryAccess registryAccess, INSSFakeGroupManager fakeGroupManager) {

        if (!(recipeHolder.value() instanceof TransmuteRecipe recipe)) return false;
        var ingredients = recipe.placementInfo().ingredients();
        if (ingredients.size() < 2) return false;
        for (var source : ingredients.getFirst().items().toList()) {
            for (var material : ingredients.get(1).items().toList()) {
                for (int count = 1; count < ingredients.size(); count++) {
                    var stacks = new ArrayList<ItemStack>(Collections.nCopies(9, ItemStack.EMPTY));
                    stacks.set(0, new ItemStack(source));
                    for (int slot = 1; slot <= count; slot++) stacks.set(slot, new ItemStack(material));
                    var input = CraftingInput.of(3, 3, stacks);
                    if (!recipe.matches(input, null)) continue;
                    var result = recipe.assemble(input);
                    if (!result.isEmpty()) mapper.addConversion(result.getCount(), NSSItem.createItem(result),
                            EMCHelper.intMapOf(NSSItem.createItem(source), 1, NSSItem.createItem(material), count));
                }
            }
        }
        return true;

    }
    @Override public boolean canHandle(RecipeType<?> type) { return type == RecipeType.CRAFTING; }
    @Override public String getName() { return PEConfigTranslations.MAPPING_CRAFTING_MAPPER_SHULKER_RECOLORING.title(); }
    @Override public String getTranslationKey() { return PEConfigTranslations.MAPPING_CRAFTING_MAPPER_SHULKER_RECOLORING.getTranslationKey(); }
    @Override public String getDescription() { return PEConfigTranslations.MAPPING_CRAFTING_MAPPER_SHULKER_RECOLORING.tooltip(); }
}
