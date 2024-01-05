package moze_intel.projecte.gameObjs.customRecipes;

import moze_intel.projecte.gameObjs.items.KleinStar;
import moze_intel.projecte.gameObjs.registries.PERecipeSerializers;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import org.jetbrains.annotations.NotNull;

public class RecipeShapelessKleinStar extends WrappedShapelessRecipe {

	public RecipeShapelessKleinStar(ShapelessRecipe internal) {
		super(internal);
	}

	@NotNull
	@Override
	public RecipeSerializer<?> getSerializer() {
		return PERecipeSerializers.KLEIN.get();
	}

	@NotNull
	@Override
	public ItemStack assemble(@NotNull CraftingContainer inv, @NotNull RegistryAccess registryAccess) {
		ItemStack result = getInternal().assemble(inv, registryAccess);
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
	public boolean isSpecial() {
		//Allow the klein recipes to show up in the recipe book and in JEI
		return false;
	}
}