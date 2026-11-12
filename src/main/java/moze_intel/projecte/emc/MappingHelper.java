package moze_intel.projecte.emc;

import moze_intel.projecte.PECore;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.SingleItemRecipe;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;
import net.minecraft.world.item.crafting.SmithingTrimRecipe;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.NotNull;

public final class MappingHelper {

	private MappingHelper() {
	}

	@NotNull
	public static Identifier recipeId(@NotNull RecipeHolder<?> recipeHolder) {
		return recipeHolder.id().identifier();
	}

	@NotNull
	public static ItemStack getRecipeOutput(@NotNull Recipe<?> recipe) {
		if (recipe instanceof SingleItemRecipe singleItemRecipe) {
			return singleItemRecipe.assemble(new SingleRecipeInput(ItemStack.EMPTY));
		}
		if (recipe instanceof ShapelessRecipe shapelessRecipe) {
			ItemStackTemplate result = shapelessRecipe.result();
			return result == null ? ItemStack.EMPTY : result.create();
		}
		if (recipe instanceof ShapedRecipe shapedRecipe) {
			try {
				return shapedRecipe.assemble(net.minecraft.world.item.crafting.CraftingInput.EMPTY);
			} catch (RuntimeException e) {
				return ItemStack.EMPTY;
			}
		}
		if (recipe instanceof SmithingTransformRecipe transformRecipe) {
			return transformRecipe.assemble(new SmithingRecipeInput(ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY));
		}
		if (recipe instanceof SmithingTrimRecipe trimRecipe) {
			return trimRecipe.assemble(new SmithingRecipeInput(ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY));
		}
		return ItemStack.EMPTY;
	}

	@NotNull
	public static ItemStack[] getMatchingStacks(@NotNull Ingredient ingredient, @NotNull Identifier recipeId) {
		try {
			return ingredient.items().map(holder -> new ItemStack(holder.value())).toArray(ItemStack[]::new);
		} catch (Exception e) {
			ICustomIngredient customIngredient = ingredient.getCustomIngredient();
			if (customIngredient != null) {
				Identifier name = NeoForgeRegistries.INGREDIENT_TYPES.getKey(customIngredient.getType());
				if (name == null) {
					PECore.LOGGER.error("Error mapping recipe {}. Ingredient of type: {} crashed when getting the matching stacks. Please report this to the ingredient's creator.",
							recipeId, customIngredient.getClass(), e);
				} else {
					PECore.LOGGER.error("Error mapping recipe {}. Ingredient of type: {} crashed when getting the matching stacks. Please report this to the ingredient's creator ({}).",
							recipeId, name, name.getNamespace(), e);
				}
			} else {
				PECore.LOGGER.error("Error mapping recipe {}. Crashed when getting the matching stacks.", recipeId, e);
			}
			return new ItemStack[0];
		}
	}
}
