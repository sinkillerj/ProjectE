package moze_intel.projecte.gameObjs.customRecipes;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * Composes a ShapelessRecipes to hide it from JEI
 */
public class RecipeShapelessHidden extends net.minecraftforge.registries.IForgeRegistryEntry.Impl<IRecipe> implements IRecipe
{
	private final ShapelessRecipes compose;

	public RecipeShapelessHidden(String group, ItemStack result, NonNullList<Ingredient> ingredients) {
		this.compose = new ShapelessRecipes(group, result, ingredients);
	}

	@Override
	public boolean matches(@Nonnull InventoryCrafting inv, @Nonnull World worldIn) {
		return compose.matches(inv, worldIn);
	}

	@Nonnull
	@Override
	public ItemStack getCraftingResult(@Nonnull InventoryCrafting inv) {
		return compose.getCraftingResult(inv);
	}

	@Override
	public boolean canFit(int width, int height) {
		return compose.canFit(width, height);
	}

	@Nonnull
	@Override
	public ItemStack getRecipeOutput() {
		return compose.getRecipeOutput();
	}

	@Nonnull
	@Override
	public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv)
	{
		return compose.getRemainingItems(inv);
	}

	@Nonnull
	@Override
	public NonNullList<Ingredient> getIngredients()
	{
		return compose.getIngredients();
	}

	@Override
	public boolean isDynamic()
	{
		return true;
	}

	@Nonnull
	@Override
	public String getGroup()
	{
		return compose.getGroup();
	}
}