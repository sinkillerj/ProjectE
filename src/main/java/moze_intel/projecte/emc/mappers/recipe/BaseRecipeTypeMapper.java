package moze_intel.projecte.emc.mappers.recipe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.mapper.collector.IMappingCollector;
import moze_intel.projecte.api.mapper.recipe.IRecipeTypeMapper;
import moze_intel.projecte.api.nss.NSSFake;
import moze_intel.projecte.api.nss.NSSItem;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import moze_intel.projecte.emc.IngredientMap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;

public abstract class BaseRecipeTypeMapper implements IRecipeTypeMapper {

	@Override
	public boolean handleRecipe(IMappingCollector<NormalizedSimpleStack, Long> mapper, IRecipe<?> recipe) {
		ItemStack recipeOutput = recipe.getRecipeOutput();
		if (recipeOutput.isEmpty()) {
			//If there is no output (for example a special recipe), don't mark it that we handled it
			return false;
		}
		ResourceLocation recipeID = recipe.getId();
		List<Tuple<NormalizedSimpleStack, List<IngredientMap<NormalizedSimpleStack>>>> dummyGroupInfos = new ArrayList<>();
		IngredientMap<NormalizedSimpleStack> ingredientMap = new IngredientMap<>();
		for (Ingredient recipeItem : getIngredients(recipe)) {
			ItemStack[] matches = getMatchingStacks(recipeItem, recipeID);
			if (matches == null) {
				//Failed to get matching stacks ingredient, bail but mark that we handled it as there is a 99% chance a later
				// mapper would fail as well due to it being an invalid recipe
				return false;
			} else if (matches.length == 1) {
				//Handle this ingredient as a direct representation of the stack it represents
				if (addIngredient(ingredientMap, matches[0].copy(), recipeID)) {
					//Failed to add ingredient, bail but mark that we handled it as there is a 99% chance a later
					// mapper would fail as well due to it being an invalid recipe
					return true;
				}
			} else if (matches.length > 0) {
				List<ItemStack> multiIngredient = Arrays.stream(matches).filter(stack -> !stack.isEmpty()).map(ItemStack::copy)
						.collect(Collectors.toCollection(LinkedList::new));
				//Handle this ingredient as the representation of all the stacks it supports
				//TODO: Try and improve how this NSSFake is creates to better match across different recipes
				NormalizedSimpleStack dummy = NSSFake.create(multiIngredient.toString());
				List<IngredientMap<NormalizedSimpleStack>> groupIngredientMaps = new ArrayList<>();
				ingredientMap.addIngredient(dummy, 1);
				for (ItemStack stack : multiIngredient) {
					IngredientMap<NormalizedSimpleStack> groupIngredientMap = new IngredientMap<>();
					if (addIngredient(groupIngredientMap, stack, recipeID)) {
						//Failed to add ingredient, bail but mark that we handled it as there is a 99% chance a later
						// mapper would fail as well due to it being an invalid recipe
						return true;
					}
					groupIngredientMaps.add(groupIngredientMap);
				}
				if (!groupIngredientMaps.isEmpty()) {
					dummyGroupInfos.add(new Tuple<>(dummy, groupIngredientMaps));
				}
			}
		}
		for (Tuple<NormalizedSimpleStack, List<IngredientMap<NormalizedSimpleStack>>> dummyGroupInfo : dummyGroupInfos) {
			for (IngredientMap<NormalizedSimpleStack> groupIngredientMap : dummyGroupInfo.getB()) {
				mapper.addConversion(1, dummyGroupInfo.getA(), groupIngredientMap.getMap());
			}
		}
		//TODO: Evaluate using a fake crafting inventory and then calling recipe#getRemainingItems? May not be worthwhile to do
		mapper.addConversion(recipeOutput.getCount(), NSSItem.createItem(recipeOutput), ingredientMap.getMap());
		return true;
	}

	@Nullable
	private ItemStack[] getMatchingStacks(Ingredient ingredient, ResourceLocation recipeID) {
		try {
			return ingredient.getMatchingStacks();
		} catch (Exception e) {
			if (isTagException(e)) {
				PECore.LOGGER.fatal("Error mapping recipe {}. Ingredient of type: {} crashed when getting the matching stacks "
									+ "due to not properly deserializing and handling tags. Please report this to the ingredient's creator.",
						recipeID, ingredient.getClass().getName(), e);
			} else {
				PECore.LOGGER.fatal("Error mapping recipe {}. Ingredient of type: {} crashed when getting the matching stacks. "
									+ "Please report this to the ingredient's creator.", recipeID, ingredient.getClass().getName(), e);
			}
			return null;
		}
	}

	//Returns true if it failed and is invalid
	private boolean addIngredient(IngredientMap<NormalizedSimpleStack> ingredientMap, ItemStack stack, ResourceLocation recipeID) {
		Item item = stack.getItem();
		boolean hasContainerItem = false;
		try {
			//Note: We include the hasContainerItem check in the try catch, as if a mod is handling tags incorrectly
			// there is a chance their hasContainerItem is checking something about tags, and
			hasContainerItem = item.hasContainerItem(stack);
			if (hasContainerItem) {
				//If this item has a container for the stack, we remove the cost of the container itself
				ingredientMap.addIngredient(NSSItem.createItem(item.getContainerItem(stack)), -1);
			}
		} catch (Exception e) {
			ResourceLocation itemName = item.getRegistryName();
			if (hasContainerItem) {
				if (isTagException(e)) {
					PECore.LOGGER.fatal("Error mapping recipe {}. Item: {} reported that it has a container item, "
										+ "but errors when trying to get the container item due to not properly deserializing and handling tags. "
										+ "Please report this to {}.", recipeID, itemName, itemName.getNamespace(), e);
				} else {
					PECore.LOGGER.fatal("Error mapping recipe {}. Item: {} reported that it has a container item, "
										+ "but errors when trying to get the container item based on the stack in the recipe. "
										+ "Please report this to {}.", recipeID, itemName, itemName.getNamespace(), e);
				}
			} else if (isTagException(e)) {
				PECore.LOGGER.fatal("Error mapping recipe {}. Item: {} crashed when checking if the stack has a container item, "
									+ "due to not properly deserializing and handling tags. Please report this to {}.", recipeID, itemName,
						itemName.getNamespace(), e);
			} else {
				PECore.LOGGER.fatal("Error mapping recipe {}. Item: {} crashed when checking if the stack in the recipe has a container item. "
									+ "Please report this to {}.", recipeID, itemName, itemName.getNamespace(), e);
			}
			//If something failed because the recipe errored, return that we did handle it so that we don't try to handle it later
			// as there is a 99% chance it will just fail again anyways
			return true;
		}
		ingredientMap.addIngredient(NSSItem.createItem(stack), 1);
		return false;
	}

	private boolean isTagException(Exception e) {
		return e instanceof IllegalStateException && e.getMessage().matches("Tag \\S*:\\S* used before it was bound");
	}

	//Allow overwriting the ingredients list because Smithing recipes don't override it themselves
	protected Collection<Ingredient> getIngredients(IRecipe<?> recipe) {
		return recipe.getIngredients();
	}
}