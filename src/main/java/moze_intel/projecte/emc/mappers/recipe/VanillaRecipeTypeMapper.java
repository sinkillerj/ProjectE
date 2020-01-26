package moze_intel.projecte.emc.mappers.recipe;

import moze_intel.projecte.api.mapper.recipe.RecipeTypeMapper;
import net.minecraft.item.crafting.IRecipeType;

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
	public boolean canHandle(IRecipeType<?> recipeType) {
		return recipeType == IRecipeType.CRAFTING || recipeType == IRecipeType.SMELTING || recipeType == IRecipeType.BLASTING || recipeType == IRecipeType.SMOKING
			   || recipeType == IRecipeType.CAMPFIRE_COOKING || recipeType == IRecipeType.STONECUTTING;
	}
}