package moze_intel.projecte.emc.mappers;

import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.fml.ModList;

public class RecipeStagesRecipeMapper implements CraftingMapper.IRecipeMapper {
    private boolean rsCompat;
    
    public RecipeStagesRecipeMapper() {
        rsCompat = ModList.get().isLoaded("recipestages");
    }
    
    @Override
    public String getName() {
        return "RecipeStagesRecipeMapper";
    }
    
    @Override
    public String getDescription() {
        return "Maps `RecipeStage` implementation of `IRecipe` from Recipe Stages";
    }
    
    @Override
    public boolean canHandle(IRecipe recipe) {
            return rsCompat && (recipe.getClass().getName().equals("com.blamejared.recipestages.recipes.RecipeStage"));
    }
}
