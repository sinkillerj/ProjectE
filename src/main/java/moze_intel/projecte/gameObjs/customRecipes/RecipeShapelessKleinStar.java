package moze_intel.projecte.gameObjs.customRecipes;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.items.KleinStar;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;


import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

@Optional.Interface(iface = "mezz.jei.api.recipe.IRecipeWrapper", modid = "jei")
public class RecipeShapelessKleinStar extends net.minecraftforge.registries.IForgeRegistryEntry.Impl<IRecipe> implements IRecipe, IRecipeWrapper {

	private final ShapelessRecipes compose;

	public RecipeShapelessKleinStar(String group, ItemStack result, NonNullList<Ingredient> ingredients) {
		this.compose = new ShapelessRecipes(group, result, ingredients);
	}

	@Override
	public boolean matches(@Nonnull InventoryCrafting inv, @Nonnull World worldIn) {
		return compose.matches(inv, worldIn);
	}

	@Nonnull
	@Override
	public ItemStack getCraftingResult(@Nonnull InventoryCrafting inv) {
		ItemStack result = compose.getCraftingResult(inv);
		long storedEMC = 0;
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (!stack.isEmpty() && stack.getItem() == ObjHandler.kleinStars) {
				storedEMC += KleinStar.getEmc(stack);
			}
		}

		if (storedEMC != 0 && result.getItem() == ObjHandler.kleinStars) {
			KleinStar.setEmc(result, storedEMC);
		}
		return result;
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
	public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
		return compose.getRemainingItems(inv);
	}

	@Nonnull
	@Override
	public NonNullList<Ingredient> getIngredients() {
		return compose.getIngredients();
	}

	@Override
	public boolean isDynamic() {
		return true;
	}

	@Nonnull
	@Override
	public String getGroup() {
		return compose.getGroup();
	}

	@Override
	@Optional.Method(modid = "jei")
	public void getIngredients(IIngredients ingredients) {
		List<ItemStack> stacks = new ArrayList<>();

		for(Ingredient ing : this.compose.getIngredients()){
			stacks.add(ing.getMatchingStacks()[0]);
		}

		ingredients.setInputs(ItemStack.class, stacks);
		ingredients.setOutput(ItemStack.class, this.compose.getRecipeOutput());
	}
}