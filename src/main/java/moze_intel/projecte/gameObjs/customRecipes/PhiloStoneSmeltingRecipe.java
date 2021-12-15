package moze_intel.projecte.gameObjs.customRecipes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import moze_intel.projecte.gameObjs.items.PhilosophersStone;
import moze_intel.projecte.gameObjs.registries.PERecipeSerializers;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class PhiloStoneSmeltingRecipe extends SpecialRecipe {

	public PhiloStoneSmeltingRecipe(ResourceLocation id) {
		super(id);
	}

	@Override
	public boolean matches(@Nonnull CraftingInventory inv, @Nonnull World world) {
		//If we have at least one matching recipe, return that we found a match
		return !getMatchingRecipes(inv, world).isEmpty();
	}

	@Nonnull
	@Override
	public ItemStack assemble(@Nonnull CraftingInventory inv) {
		Set<FurnaceRecipe> matchingRecipes = getMatchingRecipes(inv, ServerLifecycleHooks.getCurrentServer().overworld());
		if (matchingRecipes.isEmpty()) {
			return ItemStack.EMPTY;
		}
		//If we have at least one matching recipe, return the output
		//Note: It is multiplied by seven as we have seven inputs
		ItemStack output = matchingRecipes.stream().findFirst().get().getResultItem().copy();
		output.setCount(output.getCount() * 7);
		return output;
	}

	private Set<FurnaceRecipe> getMatchingRecipes(CraftingInventory inv, @Nonnull World world) {
		List<ItemStack> philoStones = new ArrayList<>();
		List<ItemStack> coals = new ArrayList<>();
		List<ItemStack> allItems = new ArrayList<>();
		for (int i = 0; i < inv.getContainerSize(); ++i) {
			ItemStack stack = inv.getItem(i);
			if (!stack.isEmpty()) {
				Item item = stack.getItem();
				allItems.add(stack);
				if (allItems.size() > 9) {
					//Exit if we have more than 9 items total (for mods that may add larger crafting tables)
					return Collections.emptySet();
				}
				if (item instanceof PhilosophersStone) {
					philoStones.add(stack);
				}
				if (item.is(ItemTags.COALS)) {
					coals.add(stack);
				}
			}
		}
		if (allItems.size() == 9) {
			//If we have exactly 9 items check for a matching recipe
			for (ItemStack philoStone : philoStones) {
				for (ItemStack coal : coals) {
					//Skip if the philosopher's stone is the same stack as the coal stack
					// This may be the case if a pack dev added the philosopher's stone to the coals tag
					if (philoStone != coal) {
						Set<FurnaceRecipe> matchingRecipes = new HashSet<>();
						for (ItemStack stack : allItems) {
							//Ignore checking the piece of coal and the philosopher's stone
							if (stack != philoStone && stack != coal) {
								//And check all the other elements to find any matching recipes
								Inventory furnaceInput = new Inventory(stack);
								if (matchingRecipes.isEmpty()) {
									//If there are no matching recipes yet see if there are any recipes that match the current stack and add them if they are,
									// if we didn't end up adding any elements that means there are no matching recipes so fail
									if (!matchingRecipes.addAll(world.getRecipeManager().getRecipesFor(IRecipeType.SMELTING, furnaceInput, world))) {
										return Collections.emptySet();
									}
								} else if (matchingRecipes.removeIf(recipe -> !recipe.matches(furnaceInput, world))) {
									//If any matching recipes are no longer valid (so got removed), check if our set of matching recipes is now empty now
									if (matchingRecipes.isEmpty()) {
										//If it is exit due to there being no match
										return Collections.emptySet();
									}
								}
							}
						}
						if (!matchingRecipes.isEmpty()) {
							//We have at least one matching recipe, so return the found recipes
							return matchingRecipes;
						}
					}
				}
			}
		}
		return Collections.emptySet();
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return width * height >= 9;
	}

	@Nonnull
	@Override
	public IRecipeSerializer<?> getSerializer() {
		return PERecipeSerializers.PHILO_STONE_SMELTING.get();
	}
}