package moze_intel.projecte.gameObjs.customRecipes;

import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.items.KleinStar;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

import javax.annotation.Nonnull;

// TODO verify
public class RecipeShapedKleinStar extends ShapedRecipes
{
	private ItemStack recipeOutput;

	public RecipeShapedKleinStar(int width, int height, NonNullList<Ingredient> ingredients, ItemStack output) {
		super("projecte:klein_expansion", width, height, ingredients, output);
		recipeOutput = output;
	}

	@Override
	public boolean matches(@Nonnull InventoryCrafting inv, @Nonnull World world)
	{
		if (super.matches(inv, world)) {
			long storedEMC = 0;
			for (int i = 0; i < inv.getSizeInventory(); i++)
			{
				ItemStack stack = inv.getStackInSlot(i);
				if(!stack.isEmpty() && stack.getItem() == ObjHandler.kleinStars)
				{
					storedEMC += KleinStar.getEmc(stack);
				}
			}

			if (storedEMC != 0 && recipeOutput.getItem() == ObjHandler.kleinStars)
			{
				KleinStar.setEmc(recipeOutput, storedEMC);
			}
		}

		return false;
	}

	@Override
	@Nonnull
	public ItemStack getCraftingResult(@Nonnull InventoryCrafting inv)
	{
		return recipeOutput;
	}
}