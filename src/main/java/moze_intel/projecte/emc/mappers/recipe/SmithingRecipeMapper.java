package moze_intel.projecte.emc.mappers.recipe;

import java.util.Arrays;
import java.util.Collection;
import moze_intel.projecte.api.mapper.recipe.RecipeTypeMapper;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.UpgradeRecipe;

@RecipeTypeMapper
public class SmithingRecipeMapper extends BaseRecipeTypeMapper {

	@Override
	public String getName() {
		return "Smithing";
	}

	@Override
	public String getDescription() {
		return "Maps smithing recipes.";
	}

	@Override
	public boolean canHandle(RecipeType<?> recipeType) {
		return recipeType == RecipeType.SMITHING;
	}

	@Override
	protected Collection<Ingredient> getIngredients(Recipe<?> recipe) {
		UpgradeRecipe smithingRecipe = (UpgradeRecipe) recipe;
		//Smithing recipes don't implement getIngredient with the inputs so we need to fake it
		return Arrays.asList(smithingRecipe.base, smithingRecipe.addition);
	}
}