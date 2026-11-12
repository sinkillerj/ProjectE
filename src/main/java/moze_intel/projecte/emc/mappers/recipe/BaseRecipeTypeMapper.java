package moze_intel.projecte.emc.mappers.recipe;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.mapper.collector.IMappingCollector;
import moze_intel.projecte.api.mapper.recipe.INSSFakeGroupManager;
import moze_intel.projecte.api.mapper.recipe.INSSFakeGroupManager.FakeGroupData;
import moze_intel.projecte.api.mapper.recipe.IRecipeTypeMapper;
import moze_intel.projecte.api.nss.NSSItem;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import moze_intel.projecte.emc.MappingHelper;
import moze_intel.projecte.utils.Constants;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.Nullable;

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
//TODO: Do we want to somehow expose this class as a helper in the api?
public abstract class BaseRecipeTypeMapper implements IRecipeTypeMapper {

	@Override
	public boolean handleRecipe(IMappingCollector<NormalizedSimpleStack, Long> mapper, RecipeHolder<?> recipeHolder, RegistryAccess registryAccess,
			INSSFakeGroupManager fakeGroupManager) {
		Recipe<?> recipe = recipeHolder.value();
		if (recipe.isSpecial()) {
			//Skip handling of any "special" recipes as they might have issues if we try to handle them
			return false;
		}
		ItemStack recipeOutput = MappingHelper.getRecipeOutput(recipe);
		if (recipeOutput == null || recipeOutput.isEmpty()) {
			//If there is no output (for example a special recipe), don't mark it that we handled it
			return false;
		}
		Collection<Ingredient> ingredientsChecked = getIngredientsChecked(recipeHolder);
		if (ingredientsChecked == null) {
			//Failed to get matching ingredients, bail but mark that we handled it as there is a 99% chance a later
			// mapper would fail as well due to it being an invalid recipe
			return true;
		}
		Identifier recipeID = MappingHelper.recipeId(recipeHolder);
		Object2IntMap<NormalizedSimpleStack> ingredientMap = new Object2IntOpenHashMap<>();
		for (Ingredient recipeItem : ingredientsChecked) {
			if (recipeItem.isEmpty()) {
				//Skip any explicitly empty ingredients as they are just used for spacing
				continue;
			}
			ItemStack[] matches = getMatchingStacks(recipeItem, recipeID);
			if (matches.length == 0) {
				//If we don't have any matches for the ingredient just return that we handled it, as if it is an accidentally empty ingredient,
				// or we failed to get the matching ingredients, nothing will be able to handle it. If it was explicitly empty,
				// then it will be skipped above
				return true;
			} else if (matches.length == 1) {
				//Handle this ingredient as a direct representation of the stack it represents
				ItemStack match = matches[0];
				if (match.isEmpty()) {
					//If we don't have any matches for the ingredient just return that we couldn't handle it,
					// given a later recipe might be able to
					return false;
				} else if (representsEmptyTag(match)) {
					//Note: Similar to Ingredient#hasNoItems, we also check if the singular stack that matches is a barrier representing the tag is empty
					// Return that we handled it, as if it is an accidentally empty ingredient, nothing will be able to handle it
					return true;
				} else if (addIngredient(ingredientMap, match, recipeID)) {
					//Failed to add ingredient, bail but mark that we handled it as there is a 99% chance a later
					// mapper would fail as well due to it being an invalid recipe
					return true;
				}
			} else {
				Object2IntMap<NormalizedSimpleStack> rawNSSMatches = new Object2IntOpenHashMap<>(matches.length);
				List<ItemStack> stacks = new ArrayList<>(matches.length);
				for (ItemStack match : matches) {
					if (!match.isEmpty() && !representsEmptyTag(match)) {
						//Validate it is not an empty stack in case mods do weird things in custom ingredients
						// Note: We don't have to worry about duplicates, as Ingredient#getItems, returns a distinct set of items
						rawNSSMatches.put(NSSItem.createItem(match), 1);
						stacks.add(match);
					}
				}
				int count = stacks.size();
				if (count == 0) {
					//If we don't have any matches for the ingredient just return that we handled it, as if it is an accidentally empty ingredient,
					// nothing will be able to handle it. If it was explicitly empty, then it will be skipped above
					return true;
				} else if (count > 1) {
					//Handle this ingredient as the representation of all the stacks it supports
					FakeGroupData group = fakeGroupManager.getOrCreateFakeGroupDirect(rawNSSMatches, true, true);
					NormalizedSimpleStack dummy = group.dummy();
					ingredientMap.mergeInt(dummy, 1, Constants.INT_SUM);
					if (group.created()) {
						//Only lookup the matching stacks for the group with conversion if we don't already have
						// a group created for this dummy ingredient
						// Note: We soft ignore cases where it fails/there are no matching group ingredients
						// as then our fake ingredient will never actually have an emc value assigned with it
						// so the recipe won't either
						boolean success = false;
						for (ItemStack stack : stacks) {
							//Note: We use a capacity of two as it will only contain the stack itself and potentially a container
							Object2IntMap<NormalizedSimpleStack> groupIngredientMap = new Object2IntArrayMap<>(2);
							//Copy the stack to ensure a mod that is implemented poorly doesn't end up changing
							// the source stack in the recipe
							if (!addIngredient(groupIngredientMap, stack, recipeID)) {
								mapper.addConversion(1, dummy, groupIngredientMap);
								success = true;
							}
						}
						if (!success) {
							//Failed to add any of the ingredients, bail but mark that we handled it as there is a 99% chance a later
							// mapper would fail as well due to it being an invalid recipe
							return true;
						}
					}
				} else if (addIngredient(ingredientMap, stacks.getFirst(), recipeID)) {//There is only actually one non-empty ingredient
					//Failed to add ingredient, bail but mark that we handled it as there is a 99% chance a later
					// mapper would fail as well due to it being an invalid recipe
					return true;
				}
			}
		}
		mapper.addConversion(recipeOutput.getCount(), NSSItem.createItem(recipeOutput), ingredientMap);
		return true;
	}

	private static boolean representsEmptyTag(ItemStack stack) {
		return stack.getItem() == Items.BARRIER && stack.getHoverName() instanceof MutableComponent hoverName && hoverName.getString().startsWith("Empty Tag: ");
	}

	private ItemStack[] getMatchingStacks(Ingredient ingredient, Identifier recipeID) {
		return MappingHelper.getMatchingStacks(ingredient, recipeID);
	}

	/**
	 * Returns true if it failed and is invalid
	 */
	private boolean addIngredient(Object2IntMap<NormalizedSimpleStack> ingredientMap, ItemStack stack, Identifier recipeID) {
		stack = stack.copy();
		Item item = stack.getItem();
		boolean hasContainerItem = false;
		try {
			//Note: We include the hasContainerItem check in the try catch, as if a mod is handling tags incorrectly
			// there is a chance their hasContainerItem is checking something about tags, and
			ItemStackTemplate remainder = stack.getCraftingRemainder();
			hasContainerItem = remainder != null;
			if (hasContainerItem) {
				//If this item has a container for the stack, we remove the cost of the container itself
				ingredientMap.mergeInt(NSSItem.createItem(remainder.create()), -1, Constants.INT_SUM);
			}
		} catch (Exception e) {
			Identifier itemName = BuiltInRegistries.ITEM.getKey(item);
			if (hasContainerItem) {
				PECore.LOGGER.error(LogUtils.FATAL_MARKER, "Error mapping recipe {}. Item: {} reported that it has a container item, but errors when trying to get "
														   + "the container item based on the stack in the recipe. Please report this to {}.", recipeID, itemName,
						itemName.getNamespace(), e);
			} else {
				PECore.LOGGER.error(LogUtils.FATAL_MARKER, "Error mapping recipe {}. Item: {} crashed when checking if the stack in the recipe has a container item. "
														   + "Please report this to {}.", recipeID, itemName, itemName.getNamespace(), e);
			}
			//If something failed because the recipe errored, return that we did handle it so that we don't try to handle it later
			// as there is a 99% chance it will just fail again anyways
			return true;
		}
		ingredientMap.mergeInt(NSSItem.createItem(stack), 1, Constants.INT_SUM);
		return false;
	}

	@Nullable
	private Collection<Ingredient> getIngredientsChecked(RecipeHolder<?> recipeHolder) {
		try {
			return getIngredients(recipeHolder.value());
		} catch (Exception e) {
			Identifier recipeID = MappingHelper.recipeId(recipeHolder);
			PECore.LOGGER.error(LogUtils.FATAL_MARKER, "Error mapping recipe {}. Failed to get ingredients. Please report this to {}.", recipeID, recipeID.getNamespace(), e);
		}
		return null;
	}

	//Allow overwriting the ingredients list because Smithing recipes don't override it themselves
	protected Collection<Ingredient> getIngredients(Recipe<?> recipe) {
		return recipe.placementInfo().ingredients();
	}
}
