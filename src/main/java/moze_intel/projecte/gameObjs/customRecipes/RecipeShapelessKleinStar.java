package moze_intel.projecte.gameObjs.customRecipes;

import moze_intel.projecte.gameObjs.items.KleinStar;
import moze_intel.projecte.gameObjs.registries.PEDataComponentTypes;
import moze_intel.projecte.gameObjs.registries.PERecipeSerializers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import org.jetbrains.annotations.NotNull;

public class RecipeShapelessKleinStar extends WrappedShapelessRecipe {

	public RecipeShapelessKleinStar(ShapelessRecipe internal) {
		super(internal);
	}

	@NotNull
	@Override
	public RecipeSerializer<? extends CraftingRecipe> getSerializer() {
		return PERecipeSerializers.KLEIN.get();
	}

	@NotNull
	@Override
	public ItemStack assemble(@NotNull CraftingInput inv) {
		ItemStack result = getInternal().assemble(inv);
		if (result.getItem() instanceof KleinStar resultingStar) {
			long maxEmc = resultingStar.getMaximumEmc(result);
			long storedEMC = 0;
			for (ItemStack stack : inv.items()) {
				if (!stack.isEmpty() && stack.getItem() instanceof KleinStar star) {
					long inputEmc = star.getStoredEmc(stack);
					if (inputEmc >= maxEmc - storedEMC) {
						//If the emc stored in the input is more than how much we need to get to the max emc
						// just set it to the max and break out
						storedEMC = maxEmc;
						break;
					} else {
						//Otherwise we have room for the input emc, add it. We know this shouldn't overflow
						storedEMC += inputEmc;
					}
				}
			}
			result.set(PEDataComponentTypes.STORED_EMC, storedEMC);
		}
		return result;
	}
}
