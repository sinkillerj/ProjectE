package moze_intel.projecte.emc.mappers.recipe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.mapper.collector.IMappingCollector;
import moze_intel.projecte.api.mapper.recipe.INSSFakeGroupManager;
import moze_intel.projecte.api.mapper.recipe.IRecipeTypeMapper;
import moze_intel.projecte.api.nss.NSSItem;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import moze_intel.projecte.emc.IngredientMap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;

//TODO: Fix recipe mapping for things containing EMC not working properly? (aka full klein stars)
// We probably could do it with a set value before, make it a grouping of a fake stack that has
// a specific emc value, and it, and then use that? We probably should check the capability for
// it though it might be enough for now to just use an instanceof?
//TODO: Evaluate using a fake crafting inventory and then calling recipe#getRemainingItems? May not be worthwhile to do
// The bigger question is how would the "fake group" stuff work for it? Maybe have an NSSFake called "inverted" that
// gets thrown in with a bucket? Or conversion NSSFake # = inverted + thing
// Alternatively we should have the fake group manager keep track of an intermediary object that says what kind
// of transformations actually is happening so that we can then basically compare sets/easier allow for custom objects
// to do things
public abstract class BaseRecipeTypeMapper implements IRecipeTypeMapper {

	@Override
	public boolean handleRecipe(IMappingCollector<NormalizedSimpleStack, Long> mapper, IRecipe<?> recipe, INSSFakeGroupManager fakeGroupManager) {
		ItemStack recipeOutput = recipe.getResultItem();
		if (recipeOutput.isEmpty()) {
			//If there is no output (for example a special recipe), don't mark it that we handled it
			return false;
		}
		Collection<Ingredient> ingredientsChecked = getIngredientsChecked(recipe);
		if (ingredientsChecked == null) {
			//Failed to get matching ingredients, bail but mark that we handled it as there is a 99% chance a later
			// mapper would fail as well due to it being an invalid recipe
			return true;
		}
		ResourceLocation recipeID = recipe.getId();
		List<Tuple<NormalizedSimpleStack, List<IngredientMap<NormalizedSimpleStack>>>> dummyGroupInfos = new ArrayList<>();
		IngredientMap<NormalizedSimpleStack> ingredientMap = new IngredientMap<>();
		for (Ingredient recipeItem : ingredientsChecked) {
			ItemStack[] matches = getMatchingStacks(recipeItem, recipeID);
			if (matches == null) {
				//Failed to get matching stacks ingredient, bail but mark that we handled it as there is a 99% chance a later
				// mapper would fail as well due to it being an invalid recipe
				return addConversionsAndReturn(mapper, dummyGroupInfos, true);
			} else if (matches.length == 1) {
				//Handle this ingredient as a direct representation of the stack it represents
				if (addIngredient(ingredientMap, matches[0].copy(), recipeID)) {
					//Failed to add ingredient, bail but mark that we handled it as there is a 99% chance a later
					// mapper would fail as well due to it being an invalid recipe
					return addConversionsAndReturn(mapper, dummyGroupInfos, true);
				}
			} else if (matches.length > 0) {
				Set<NormalizedSimpleStack> rawNSSMatches = new HashSet<>();
				List<ItemStack> stacks = new ArrayList<>();
				for (ItemStack match : matches) {
					if (!match.isEmpty()) {
						//Validate it is not an empty stack in case mods do weird things in custom ingredients
						rawNSSMatches.add(NSSItem.createItem(match));
						stacks.add(match);
					}
				}
				int count = stacks.size();
				if (count == 1) {
					//There is only actually one non empty ingredient
					if (addIngredient(ingredientMap, stacks.get(0).copy(), recipeID)) {
						//Failed to add ingredient, bail but mark that we handled it as there is a 99% chance a later
						// mapper would fail as well due to it being an invalid recipe
						return addConversionsAndReturn(mapper, dummyGroupInfos, true);
					}
				} else if (count > 1) {
					//Handle this ingredient as the representation of all the stacks it supports
					Tuple<NormalizedSimpleStack, Boolean> group = fakeGroupManager.getOrCreateFakeGroup(rawNSSMatches);
					NormalizedSimpleStack dummy = group.getA();
					ingredientMap.addIngredient(dummy, 1);
					if (group.getB()) {
						//Only lookup the matching stacks for the group with conversion if we don't already have
						// a group created for this dummy ingredient
						// Note: We soft ignore cases where it fails/there are no matching group ingredients
						// as then our fake ingredient will never actually have an emc value assigned with it
						// so the recipe won't either
						List<IngredientMap<NormalizedSimpleStack>> groupIngredientMaps = new ArrayList<>();
						for (ItemStack stack : stacks) {
							IngredientMap<NormalizedSimpleStack> groupIngredientMap = new IngredientMap<>();
							//Copy the stack to ensure a mod that is implemented poorly doesn't end up changing
							// the source stack in the recipe
							if (addIngredient(groupIngredientMap, stack.copy(), recipeID)) {
								//Failed to add ingredient, bail but mark that we handled it as there is a 99% chance a later
								// mapper would fail as well due to it being an invalid recipe
								return addConversionsAndReturn(mapper, dummyGroupInfos, true);
							}
							groupIngredientMaps.add(groupIngredientMap);
						}
						dummyGroupInfos.add(new Tuple<>(dummy, groupIngredientMaps));
					}
				}
			}
		}
		mapper.addConversion(recipeOutput.getCount(), NSSItem.createItem(recipeOutput), ingredientMap.getMap());
		return addConversionsAndReturn(mapper, dummyGroupInfos, true);
	}

	/**
	 * This method can be used as a helper method to return a specific value and add any existing group conversions. It is important that we add any valid group
	 * conversions that we have, regardless of whether the recipe as a whole is valid, because we only create one instance of our group's NSS representation so even if
	 * parts of the recipe are not valid, the conversion may be valid and exist in another recipe.
	 */
	private boolean addConversionsAndReturn(IMappingCollector<NormalizedSimpleStack, Long> mapper,
			List<Tuple<NormalizedSimpleStack, List<IngredientMap<NormalizedSimpleStack>>>> dummyGroupInfos, boolean returnValue) {
		//If we have any conversions make sure to add them even if we are returning early
		for (Tuple<NormalizedSimpleStack, List<IngredientMap<NormalizedSimpleStack>>> dummyGroupInfo : dummyGroupInfos) {
			for (IngredientMap<NormalizedSimpleStack> groupIngredientMap : dummyGroupInfo.getB()) {
				mapper.addConversion(1, dummyGroupInfo.getA(), groupIngredientMap.getMap());
			}
		}
		return returnValue;
	}

	@Nullable
	private ItemStack[] getMatchingStacks(Ingredient ingredient, ResourceLocation recipeID) {
		try {
			return ingredient.getItems();
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

	@Nullable
	private Collection<Ingredient> getIngredientsChecked(IRecipe<?> recipe) {
		try {
			return getIngredients(recipe);
		} catch (Exception e) {
			ResourceLocation recipeID = recipe.getId();
			if (isTagException(e)) {
				PECore.LOGGER.fatal("Error mapping recipe {}. Failed to get ingredients due to the recipe not properly deserializing and handling tags. "
									+ "Please report this to {}.", recipeID, recipeID.getNamespace(), e);
			} else {
				PECore.LOGGER.fatal("Error mapping recipe {}. Failed to get ingredients. Please report this to {}.", recipeID, recipeID.getNamespace(), e);
			}
		}
		return null;
	}

	//Allow overwriting the ingredients list because Smithing recipes don't override it themselves
	protected Collection<Ingredient> getIngredients(IRecipe<?> recipe) {
		return recipe.getIngredients();
	}
}