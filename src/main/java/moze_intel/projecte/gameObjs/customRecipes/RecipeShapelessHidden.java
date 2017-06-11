package moze_intel.projecte.gameObjs.customRecipes;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.util.NonNullList;

public class RecipeShapelessHidden extends ShapelessRecipes
{
	public RecipeShapelessHidden(String group, ItemStack result, NonNullList<Ingredient> ingredients) {
		super(group, result, ingredients);
	}

	// TODO 1.12 - ensure JEI respects this or bother them to do so
	@Override
	public boolean func_192399_d() {
		return true;
	}
}