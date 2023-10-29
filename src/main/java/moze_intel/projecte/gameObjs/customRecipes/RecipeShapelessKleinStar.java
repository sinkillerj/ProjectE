package moze_intel.projecte.gameObjs.customRecipes;

import com.google.gson.JsonObject;
import moze_intel.projecte.gameObjs.items.KleinStar;
import moze_intel.projecte.gameObjs.registries.PERecipeSerializers;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class RecipeShapelessKleinStar implements CraftingRecipe {

	private final ShapelessRecipe compose;

	public RecipeShapelessKleinStar(ShapelessRecipe compose) {
		this.compose = compose;
	}

	@NotNull
	@Override
	public ResourceLocation getId() {
		return compose.getId();
	}

	@NotNull
	@Override
	public RecipeSerializer<?> getSerializer() {
		return PERecipeSerializers.KLEIN.get();
	}

	@Override
	public boolean matches(@NotNull CraftingContainer inv, @NotNull Level worldIn) {
		return compose.matches(inv, worldIn);
	}

	@NotNull
	@Override
	public ItemStack assemble(@NotNull CraftingContainer inv, @NotNull RegistryAccess registryAccess) {
		ItemStack result = compose.assemble(inv, registryAccess);
		long storedEMC = 0;
		for (int i = 0; i < inv.getContainerSize(); i++) {
			ItemStack stack = inv.getItem(i);
			if (!stack.isEmpty() && stack.getItem() instanceof KleinStar) {
				storedEMC += KleinStar.getEmc(stack);
			}
		}
		if (storedEMC != 0 && result.getItem() instanceof KleinStar) {
			KleinStar.setEmc(result, storedEMC);
		}
		return result;
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return compose.canCraftInDimensions(width, height);
	}

	@NotNull
	@Override
	public ItemStack getResultItem(@NotNull RegistryAccess registryAccess) {
		return compose.getResultItem(registryAccess);
	}

	@NotNull
	@Override
	public NonNullList<ItemStack> getRemainingItems(@NotNull CraftingContainer inv) {
		return compose.getRemainingItems(inv);
	}

	@NotNull
	@Override
	public NonNullList<Ingredient> getIngredients() {
		return compose.getIngredients();
	}

	@Override
	public boolean isSpecial() {
		//Allow the klein recipes to show up in the recipe book and in JEI
		return false;
	}

	@NotNull
	@Override
	public String getGroup() {
		return compose.getGroup();
	}

	@NotNull
	@Override
	public ItemStack getToastSymbol() {
		return compose.getToastSymbol();
	}

	@Override
	public boolean isIncomplete() {
		return compose.isIncomplete();
	}

	@NotNull
	@Override
	public CraftingBookCategory category() {
		return compose.category();
	}

	public static class Serializer implements RecipeSerializer<RecipeShapelessKleinStar> {

		@NotNull
		@Override
		public RecipeShapelessKleinStar fromJson(@NotNull ResourceLocation recipeId, @NotNull JsonObject json) {
			return new RecipeShapelessKleinStar(RecipeSerializer.SHAPELESS_RECIPE.fromJson(recipeId, json));
		}

		@NotNull
		@Override
		public RecipeShapelessKleinStar fromNetwork(@NotNull ResourceLocation recipeId, @NotNull FriendlyByteBuf buffer) {
			return new RecipeShapelessKleinStar(RecipeSerializer.SHAPELESS_RECIPE.fromNetwork(recipeId, buffer));
		}

		@Override
		public void toNetwork(@NotNull FriendlyByteBuf buffer, RecipeShapelessKleinStar recipe) {
			RecipeSerializer.SHAPELESS_RECIPE.toNetwork(buffer, recipe.compose);
		}
	}
}