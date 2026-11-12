package moze_intel.projecte.emc.mappers.recipe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import moze_intel.projecte.api.mapper.recipe.RecipeTypeMapper;
import moze_intel.projecte.config.PEConfigTranslations;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;
import net.minecraft.world.item.crafting.SmithingTrimRecipe;

@RecipeTypeMapper
public class SmithingRecipeMapper extends BaseRecipeTypeMapper {

	@Override
	public String getName() {
		return PEConfigTranslations.MAPPING_CRAFTING_MAPPER_SMITHING.title();
	}

	@Override
	public String getTranslationKey() {
		return PEConfigTranslations.MAPPING_CRAFTING_MAPPER_SMITHING.getTranslationKey();
	}

	@Override
	public String getDescription() {
		return PEConfigTranslations.MAPPING_CRAFTING_MAPPER_SMITHING.tooltip();
	}

	@Override
	public boolean canHandle(RecipeType<?> recipeType) {
		return recipeType == RecipeType.SMITHING;
	}

	@Override
	protected Collection<Ingredient> getIngredients(Recipe<?> recipe) {
		//Smithing recipes don't implement getIngredient with the inputs, so we need to fake it
		if (recipe instanceof SmithingTransformRecipe transformRecipe) {
			return smithingIngredients(transformRecipe);
		} else if (recipe instanceof SmithingTrimRecipe trimRecipe) {
			return smithingIngredients(trimRecipe);
		}
		return Collections.emptyList();
	}

	private static List<Ingredient> smithingIngredients(SmithingRecipe recipe) {
		List<Ingredient> ingredients = new ArrayList<>(3);
		recipe.templateIngredient().ifPresent(ingredients::add);
		ingredients.add(recipe.baseIngredient());
		recipe.additionIngredient().ifPresent(ingredients::add);
		return ingredients;
	}
}
