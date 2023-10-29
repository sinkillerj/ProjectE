package moze_intel.projecte.emc.mappers.recipe;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import moze_intel.projecte.api.mapper.recipe.RecipeTypeMapper;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;
import net.minecraft.world.item.crafting.SmithingTrimRecipe;

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
		//Smithing recipes don't implement getIngredient with the inputs, so we need to fake it
		if (recipe instanceof SmithingTransformRecipe transformRecipe) {
			return List.of(transformRecipe.base, transformRecipe.addition, transformRecipe.template);
		} else if (recipe instanceof SmithingTrimRecipe trimRecipe) {
			return List.of(trimRecipe.base, trimRecipe.addition, trimRecipe.template);
		}
		return Collections.emptyList();
	}
}