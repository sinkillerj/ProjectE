package moze_intel.projecte.gameObjs.customRecipes;

import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.ItemHelper;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.util.Tuple;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RecipesCovalenceRepair extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe
{
    private Tuple<ItemStack, List<ItemStack>> findIngredients(InventoryCrafting inv) {
		List<ItemStack> dust = new ArrayList<>();
		ItemStack tool = ItemStack.EMPTY;
		boolean foundItem = false;

		for (int i = 0; i < inv.getSizeInventory(); i++)
		{
			ItemStack input = inv.getStackInSlot(i);

			if (input.isEmpty())
			{
				continue;
			}

			if (ItemHelper.isItemRepairable(input))
			{
				if (!foundItem)
				{
					tool = input;
					foundItem = true;
				}
				else
				{
				    // Duplicate item
				    return new Tuple<>(ItemStack.EMPTY, Collections.emptyList());
				}
			}
			else if (input.getItem() == ObjHandler.covalence)
			{
				dust.add(input);
			}
			else
			{
				// Non-dust non-tool
				return new Tuple<>(ItemStack.EMPTY, Collections.emptyList());
			}
		}

		return new Tuple<>(tool, dust);
	}

	@Override
	public boolean matches(@Nonnull InventoryCrafting inv, @Nonnull World world)
	{
		Tuple<ItemStack, List<ItemStack>> ingredients = findIngredients(inv);
		if (ingredients.getFirst().isEmpty() || ingredients.getSecond().isEmpty())
			return false;

		long emcPerDurability = EMCHelper.getEMCPerDurability(ingredients.getFirst());
		long dustEmc = 0;
		for (ItemStack stack : ingredients.getSecond()) {
			dustEmc += EMCHelper.getEmcValue(stack);
		}
		return dustEmc >= emcPerDurability;
	}
	
	@Nonnull
	@Override
	public ItemStack getCraftingResult(@Nonnull InventoryCrafting inv)
	{
	    Tuple<ItemStack, List<ItemStack>> ingredients = findIngredients(inv);
	    long emcPerDurability = EMCHelper.getEMCPerDurability(ingredients.getFirst());
		long dustEmc = 0;
		for (ItemStack stack : ingredients.getSecond()) {
			dustEmc += EMCHelper.getEmcValue(stack);
		}

	    ItemStack output = ingredients.getFirst().copy();
		output.setItemDamage((int) Math.max(output.getItemDamage() - (dustEmc / emcPerDurability), 0));
		return output;
	}

	@Override
	public boolean canFit(int width, int height) {
		return width > 1 || height > 1;
	}

	@Nonnull
	@Override
	public ItemStack getRecipeOutput() 
	{
		return ItemStack.EMPTY;
	}

	@Override
	public boolean isDynamic() {
		return true;
	}
}
