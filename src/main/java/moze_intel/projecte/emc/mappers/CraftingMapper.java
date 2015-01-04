package moze_intel.projecte.emc.mappers;

import moze_intel.projecte.emc.IMappingCollector;
import moze_intel.projecte.emc.IngredientMap;
import moze_intel.projecte.emc.NormalizedSimpleStack;
import moze_intel.projecte.utils.PELogger;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;

import java.util.*;

public class CraftingMapper implements IEMCMapper<NormalizedSimpleStack> {

    List<IRecipeMapper> recipeMappers = new LinkedList<IRecipeMapper>();
    Set<Class> canNotMap = new HashSet<Class>();

    @Override
    public void addMappings(IMappingCollector<NormalizedSimpleStack> mapper) {
        Iterator<IRecipe> iter = CraftingManager.getInstance().getRecipeList().iterator();
        while (iter.hasNext()) {
            IRecipe recipe = iter.next();
            boolean handled = false;
            ItemStack recipeOutput = recipe.getRecipeOutput();
            if (recipeOutput == null) continue;
            for (IRecipeMapper recipeMapper: recipeMappers) {
                if (recipeMapper.canHandle(recipe)) {
                    handled = true;
                    mapper.addConversionMultiple(recipeOutput.stackSize, new NormalizedSimpleStack(recipeOutput), recipeMapper.getIngredientsFor(recipe).getMap());
                    break;
                }
            }
            if (!handled) {
                if (!canNotMap.contains(recipe.getClass())) {
                    canNotMap.add(recipe.getClass());
                    PELogger.logWarn("Can not map Crafting Recipes with Type: " + recipe.getClass().getName());
                }
            }
        }
    }


    protected static interface IRecipeMapper {
        public boolean canHandle(IRecipe recipe);
        public IngredientMap<NormalizedSimpleStack> getIngredientsFor(IRecipe recipe);
    }

    //TODO implement IRecipeMapper for ShapedRecipes, ShapelessRecipes, ShapedOreRecipe, ShapelessOreRecipe
}
