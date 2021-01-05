package moze_intel.projecte.emc.mappers.recipe;

import java.util.Arrays;
import java.util.Collection;
import moze_intel.projecte.api.mapper.collector.IMappingCollector;
import moze_intel.projecte.api.mapper.recipe.RecipeTypeMapper;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import net.minecraft.item.crafting.AbstractCookingRecipe;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.SingleItemRecipe;
import net.minecraft.item.crafting.SmithingRecipe;

@RecipeTypeMapper(priority = Integer.MIN_VALUE)
public class FallbackRecipeTypeMapper extends BaseRecipeTypeMapper {

	@Override
	public String getName() {
		return "FallbackRecipeType";
	}

	@Override
	public String getDescription() {
		return "Fallback for default handling of recipes that extend ICraftingRecipe, AbstractCookingRecipe, SingleItemRecipe, or SmithingRecipe. "
			   + "This will catch modded extensions of the vanilla recipe classes, and if the VanillaRecipeTypes mapper is disabled, "
			   + "this mapper will still catch the vanilla recipes.";
	}

	@Override
	public boolean canHandle(IRecipeType<?> recipeType) {
		//Pretend that we can handle
		return true;
	}

	@Override
	public boolean handleRecipe(IMappingCollector<NormalizedSimpleStack, Long> mapper, IRecipe<?> recipe) {
		if (recipe instanceof ICraftingRecipe || recipe instanceof AbstractCookingRecipe || recipe instanceof SingleItemRecipe || recipe instanceof SmithingRecipe) {
			return super.handleRecipe(mapper, recipe);
		}
		return false;
	}

	@Override
	protected Collection<Ingredient> getIngredients(IRecipe<?> recipe) {
		Collection<Ingredient> ingredients = super.getIngredients(recipe);
		if (recipe instanceof SmithingRecipe && ingredients.isEmpty()) {
			//If the extension of smithing recipe doesn't override getIngredients (just like vanilla doesn't)
			// grab the values from the recipe's object itself
			SmithingRecipe smithingRecipe = (SmithingRecipe) recipe;
			return Arrays.asList(smithingRecipe.base, smithingRecipe.addition);
		}
		return ingredients;
	}
}