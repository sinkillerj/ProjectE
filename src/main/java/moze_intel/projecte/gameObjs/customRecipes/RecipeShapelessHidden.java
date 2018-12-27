package moze_intel.projecte.gameObjs.customRecipes;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapelessRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * Composes a ShapelessRecipes to hide it from JEI
 */
public class RecipeShapelessHidden implements IRecipe
{
	private final ResourceLocation id;
	private final ShapelessRecipe compose;

	public RecipeShapelessHidden(ResourceLocation id, String group, ItemStack result, NonNullList<Ingredient> ingredients) {
		this.id = id;
		this.compose = new ShapelessRecipe(id, group, result, ingredients);
	}

	@Override
	public boolean matches(@Nonnull IInventory inv, @Nonnull World worldIn) {
		return compose.matches(inv, worldIn);
	}

	@Nonnull
	@Override
	public ItemStack getCraftingResult(@Nonnull IInventory inv) {
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
	public NonNullList<ItemStack> getRemainingItems(IInventory inv)
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

	@Override
	public ResourceLocation getId()
	{
		return compose.getId();
	}

	@Override
	public IRecipeSerializer<?> getSerializer()
	{
		// todo 1.13
		return null;
	}
}