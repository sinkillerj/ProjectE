package moze_intel.projecte.gameObjs.customRecipes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import moze_intel.projecte.gameObjs.registries.PEItems;
import moze_intel.projecte.gameObjs.registries.PERecipeSerializers;
import moze_intel.projecte.utils.EMCHelper;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.World;

//TODO - 1.16: Validate this, it seems that we didn't have it as a recipe in 1.14/15, we now do again, but we need to review it works properly
public class RecipesCovalenceRepair implements ICraftingRecipe {

	private final ResourceLocation id;

	public RecipesCovalenceRepair(ResourceLocation id) {
		this.id = id;
	}

	private Tuple<ItemStack, List<ItemStack>> findIngredients(IInventory inv) {
		List<ItemStack> dust = new ArrayList<>();
		ItemStack tool = ItemStack.EMPTY;
		boolean foundItem = false;
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack input = inv.getStackInSlot(i);
			if (input.isEmpty()) {
				continue;
			}
			if (input.isDamageable()) {
				if (!foundItem) {
					tool = input;
					foundItem = true;
				} else {
					// Duplicate item
					return new Tuple<>(ItemStack.EMPTY, Collections.emptyList());
				}
			} else if (input.getItem() == PEItems.LOW_COVALENCE_DUST.get() || input.getItem() == PEItems.MEDIUM_COVALENCE_DUST.get() || input.getItem() == PEItems.HIGH_COVALENCE_DUST.get()) {
				dust.add(input);
			} else {
				// Non-dust non-tool
				return new Tuple<>(ItemStack.EMPTY, Collections.emptyList());
			}
		}
		return new Tuple<>(tool, dust);
	}

	@Override
	public boolean matches(@Nonnull CraftingInventory inv, @Nonnull World world) {
		Tuple<ItemStack, List<ItemStack>> ingredients = findIngredients(inv);
		if (ingredients.getA().isEmpty() || ingredients.getB().isEmpty()) {
			return false;
		}
		long emcPerDurability = EMCHelper.getEMCPerDurability(ingredients.getA());
		long dustEmc = 0;
		for (ItemStack stack : ingredients.getB()) {
			dustEmc += EMCHelper.getEmcValue(stack);
		}
		return dustEmc >= emcPerDurability;
	}

	@Nonnull
	@Override
	public ItemStack getCraftingResult(@Nonnull CraftingInventory inv) {
		Tuple<ItemStack, List<ItemStack>> ingredients = findIngredients(inv);
		long emcPerDurability = EMCHelper.getEMCPerDurability(ingredients.getA());
		long dustEmc = 0;
		for (ItemStack stack : ingredients.getB()) {
			dustEmc += EMCHelper.getEmcValue(stack);
		}
		ItemStack output = ingredients.getA().copy();
		output.setDamage((int) Math.max(output.getDamage() - dustEmc / emcPerDurability, 0));
		return output;
	}

	@Override
	public boolean canFit(int width, int height) {
		return width > 1 || height > 1;
	}

	@Nonnull
	@Override
	public ItemStack getRecipeOutput() {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean isDynamic() {
		return true;
	}

	@Nonnull
	@Override
	public ResourceLocation getId() {
		return id;
	}

	@Nonnull
	@Override
	public IRecipeSerializer<?> getSerializer() {
		return PERecipeSerializers.COVALENCE_REPAIR.get();
	}
}