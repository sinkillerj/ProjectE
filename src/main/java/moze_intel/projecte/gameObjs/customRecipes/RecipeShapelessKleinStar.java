package moze_intel.projecte.gameObjs.customRecipes;

import com.google.gson.JsonObject;
import javax.annotation.Nonnull;
import moze_intel.projecte.gameObjs.items.KleinStar;
import moze_intel.projecte.gameObjs.registries.PERecipeSerializers;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapelessRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class RecipeShapelessKleinStar implements ICraftingRecipe {

	private final ShapelessRecipe compose;

	public RecipeShapelessKleinStar(ShapelessRecipe compose) {
		this.compose = compose;
	}

	@Nonnull
	@Override
	public ResourceLocation getId() {
		return compose.getId();
	}

	@Nonnull
	@Override
	public IRecipeSerializer<?> getSerializer() {
		return PERecipeSerializers.KLEIN.get();
	}

	@Override
	public boolean matches(@Nonnull CraftingInventory inv, @Nonnull World worldIn) {
		return compose.matches(inv, worldIn);
	}

	@Nonnull
	@Override
	public ItemStack assemble(@Nonnull CraftingInventory inv) {
		ItemStack result = compose.assemble(inv);
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

	@Nonnull
	@Override
	public ItemStack getResultItem() {
		return compose.getResultItem();
	}

	@Nonnull
	@Override
	public NonNullList<ItemStack> getRemainingItems(@Nonnull CraftingInventory inv) {
		return compose.getRemainingItems(inv);
	}

	@Nonnull
	@Override
	public NonNullList<Ingredient> getIngredients() {
		return compose.getIngredients();
	}

	@Override
	public boolean isSpecial() {
		//Allow the klein recipes to show up in the recipe book and in JEI
		return false;
	}

	@Nonnull
	@Override
	public String getGroup() {
		return compose.getGroup();
	}

	public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<RecipeShapelessKleinStar> {

		@Nonnull
		@Override
		public RecipeShapelessKleinStar fromJson(@Nonnull ResourceLocation recipeId, @Nonnull JsonObject json) {
			return new RecipeShapelessKleinStar(IRecipeSerializer.SHAPELESS_RECIPE.fromJson(recipeId, json));
		}

		@Nonnull
		@Override
		public RecipeShapelessKleinStar fromNetwork(@Nonnull ResourceLocation recipeId, @Nonnull PacketBuffer buffer) {
			return new RecipeShapelessKleinStar(IRecipeSerializer.SHAPELESS_RECIPE.fromNetwork(recipeId, buffer));
		}

		@Override
		public void toNetwork(@Nonnull PacketBuffer buffer, RecipeShapelessKleinStar recipe) {
			IRecipeSerializer.SHAPELESS_RECIPE.toNetwork(buffer, recipe.compose);
		}
	}
}