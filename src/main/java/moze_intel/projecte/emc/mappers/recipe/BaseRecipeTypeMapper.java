package moze_intel.projecte.emc.mappers.recipe;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import moze_intel.projecte.api.mapper.collector.IMappingCollector;
import moze_intel.projecte.api.mapper.recipe.IRecipeTypeMapper;
import moze_intel.projecte.api.nss.NSSFake;
import moze_intel.projecte.api.nss.NSSItem;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import moze_intel.projecte.emc.IngredientMap;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;

public abstract class BaseRecipeTypeMapper implements IRecipeTypeMapper {

	@Override
	public boolean handleRecipe(IMappingCollector<NormalizedSimpleStack, Long> mapper, IRecipe<?> recipe) {
		ItemStack recipeOutput = recipe.getRecipeOutput();
		if (recipeOutput.isEmpty()) {
			//If there is no output (for example a special recipe), don't mark it that we handled it
			return false;
		}
		IngredientMap<NormalizedSimpleStack> ingredientMap = new IngredientMap<>();
		for (Ingredient recipeItem : getIngredients(recipe)) {
			ItemStack[] matches = recipeItem.getMatchingStacks();
			if (matches.length == 1) {
				//Handle this ingredient as a direct representation of the stack it represents
				ItemStack stack = matches[0].copy();
				if (stack.getItem().hasContainerItem(stack)) {
					//If this item has a container for the stack, we remove the cost of the container itself
					ingredientMap.addIngredient(NSSItem.createItem(stack.getItem().getContainerItem(stack)), -1);
				}
				ingredientMap.addIngredient(NSSItem.createItem(stack), 1);
			} else if (matches.length > 0) {
				List<ItemStack> multiIngredient = Arrays.stream(matches).map(ItemStack::copy).collect(Collectors.toCollection(LinkedList::new));
				//Handle this ingredient as the representation of all the stacks it supports
				NormalizedSimpleStack dummy = NSSFake.create(multiIngredient.toString());
				ingredientMap.addIngredient(dummy, 1);
				for (ItemStack stack : multiIngredient) {
					if (!stack.isEmpty()) {
						IngredientMap<NormalizedSimpleStack> groupIngredientMap = new IngredientMap<>();
						if (stack.getItem().hasContainerItem(stack)) {
							//If this item has a container for the stack, we remove the cost of the container itself
							groupIngredientMap.addIngredient(NSSItem.createItem(stack.getItem().getContainerItem(stack)), -1);
						}
						groupIngredientMap.addIngredient(NSSItem.createItem(stack), 1);
						mapper.addConversion(1, dummy, groupIngredientMap.getMap());
					}
				}
			}
		}
		mapper.addConversion(recipeOutput.getCount(), NSSItem.createItem(recipeOutput), ingredientMap.getMap());
		return true;
	}

	//Allow overwriting the ingredients list because Smithing recipes don't override it themselves
	protected Collection<Ingredient> getIngredients(IRecipe<?> recipe) {
		return recipe.getIngredients();
	}
}