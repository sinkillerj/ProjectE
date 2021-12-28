package moze_intel.projecte.emc.mappers.recipe;

import moze_intel.projecte.api.mapper.recipe.RecipeTypeMapper;
import net.minecraft.world.item.crafting.RecipeType;

@RecipeTypeMapper
public class VanillaRecipeTypeMapper extends BaseRecipeTypeMapper {

	@Override
	public String getName() {
		return "VanillaRecipeTypes";
	}

	@Override
	public String getDescription() {
		return "Maps the different vanilla recipe types.";
	}

	@Override
	public boolean canHandle(RecipeType<?> recipeType) {
		return recipeType == RecipeType.CRAFTING || recipeType == RecipeType.SMELTING || recipeType == RecipeType.BLASTING || recipeType == RecipeType.SMOKING
			   || recipeType == RecipeType.CAMPFIRE_COOKING || recipeType == RecipeType.STONECUTTING;
	}
}