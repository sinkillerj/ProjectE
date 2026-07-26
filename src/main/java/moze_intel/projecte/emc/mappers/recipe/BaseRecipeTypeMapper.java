package moze_intel.projecte.emc.mappers.recipe;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.mapper.collector.IMappingCollector;
import moze_intel.projecte.api.mapper.recipe.INSSFakeGroupManager;
import moze_intel.projecte.api.mapper.recipe.INSSFakeGroupManager.FakeGroupData;
import moze_intel.projecte.api.mapper.recipe.IRecipeTypeMapper;
import moze_intel.projecte.api.nss.NSSItem;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import moze_intel.projecte.utils.Constants;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
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
		ItemStack recipeOutput = recipe.getResultItem(registryAccess);
		if (recipeOutput.isEmpty()) {
			//If there is no output (for example a special recipe), don't mark it that we handled it
			return false;
		}
		Collection<Ingredient> ingredientsChecked = getIngredientsChecked(recipeHolder);
		if (ingredientsChecked == null) {
			//Failed to get matching ingredients, bail but mark that we handled it as there is a 99% chance a later
			// mapper would fail as well due to it being an invalid recipe
			return true;
		}
		ResourceLocation recipeID = recipeHolder.id();
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
				List<ItemStack> stacks = getNormalizableMatches(matches, recipeID, rawNSSMatches);
				int count = stacks.size();
				if (count == 0) {
					//All alternatives were empty, represented an empty tag, or could not be normalized. No later generic mapper can safely use them.
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
				} else if (addIngredient(ingredientMap, stacks.getFirst(), recipeID)) {
					//There is only one valid alternative. Failed to add it, so later generic mappers would encounter the same invalid state.
					return true;
				}
			}
		}
		NormalizedSimpleStack normalizedOutput = normalizeStack(recipeOutput, recipeID, "recipe output");
		if (normalizedOutput == null) {
			//The recipe is malformed and later generic mappers would encounter the same invalid output.
			return true;
		}
		mapper.addConversion(recipeOutput.getCount(), normalizedOutput, ingredientMap);
		return true;
	}

	static List<ItemStack> getNormalizableMatches(ItemStack[] matches, ResourceLocation recipeID,
			Object2IntMap<NormalizedSimpleStack> rawNSSMatches) {
		return getNormalizableMatches(matches, recipeID, rawNSSMatches, NSSItem::createItem);
	}

	static List<ItemStack> getNormalizableMatches(ItemStack[] matches, ResourceLocation recipeID,
			Object2IntMap<NormalizedSimpleStack> rawNSSMatches, Function<ItemStack, NormalizedSimpleStack> normalizer) {
		List<ItemStack> normalizableMatches = new ArrayList<>(matches.length);
		for (ItemStack match : matches) {
			if (!match.isEmpty() && !representsEmptyTag(match)) {
				NormalizedSimpleStack normalizedStack = normalizeStack(match, recipeID, "ingredient alternative", normalizer);
				if (normalizedStack != null) {
					rawNSSMatches.put(normalizedStack, 1);
					normalizableMatches.add(match);
				}
			}
		}
		return normalizableMatches;
	}

	@Nullable
	static NormalizedSimpleStack normalizeStack(ItemStack stack, ResourceLocation recipeID, String stackRole) {
		return normalizeStack(stack, recipeID, stackRole, NSSItem::createItem);
	}

	@Nullable
	static NormalizedSimpleStack normalizeStack(ItemStack stack, ResourceLocation recipeID, String stackRole,
			Function<ItemStack, NormalizedSimpleStack> normalizer) {
		try {
			return normalizer.apply(stack);
		} catch (RuntimeException e) {
			ResourceLocation itemName = BuiltInRegistries.ITEM.getKey(stack.getItem());
			PECore.LOGGER.error(LogUtils.FATAL_MARKER, "Error mapping recipe {}. Failed to normalize the {} stack for item {} ({}). "
													 + "Ignoring this stack so malformed recipe data cannot abort the entire EMC remap.", recipeID, stackRole,
					itemName, stack.getItem().getClass().getName(), e);
			return null;
		}
	}

	private static boolean representsEmptyTag(ItemStack stack) {
		return stack.getItem() == Items.BARRIER && stack.getHoverName() instanceof MutableComponent hoverName && hoverName.getString().startsWith("Empty Tag: ");
	}

	private ItemStack[] getMatchingStacks(Ingredient ingredient, ResourceLocation recipeID) {
		try {
			return ingredient.getItems();
		} catch (Exception e) {
			ICustomIngredient customIngredient = ingredient.getCustomIngredient();
			if (customIngredient != null) {//Should basically always be the case
				ResourceLocation name = NeoForgeRegistries.INGREDIENT_TYPES.getKey(customIngredient.getType());
				if (name == null) {
					PECore.LOGGER.error(LogUtils.FATAL_MARKER, "Error mapping recipe {}. Ingredient of type: {} crashed when getting the matching stacks. "
															   + "Please report this to the ingredient's creator.", recipeID, customIngredient.getClass(), e);
				} else {
					PECore.LOGGER.error(LogUtils.FATAL_MARKER, "Error mapping recipe {}. Ingredient of type: {} crashed when getting the matching stacks. "
															   + "Please report this to the ingredient's creator ({}).", recipeID, name, name.getNamespace(), e);
				}
			} else {
				PECore.LOGGER.error(LogUtils.FATAL_MARKER, "Error mapping recipe {}. Crashed when getting the matching stacks.", recipeID, e);
			}
			return new ItemStack[0];
		}
	}

	/**
	 * Returns true if it failed and is invalid
	 */
	private boolean addIngredient(Object2IntMap<NormalizedSimpleStack> ingredientMap, ItemStack stack, ResourceLocation recipeID) {
		stack = stack.copy();
		Item item = stack.getItem();
		boolean hasContainerItem = false;
		try {
			//Note: We include the hasContainerItem check in the try catch, as if a mod is handling tags incorrectly
			// there is a chance their hasContainerItem is checking something about tags, and
			hasContainerItem = item.hasCraftingRemainingItem(stack);
			if (hasContainerItem) {
				//If this item has a container for the stack, remove the full returned stack cost. Most vanilla remainders have a count of one,
				//but the API returns an ItemStack and third-party items may legitimately return more than one item.
				ItemStack craftingRemainingItem = item.getCraftingRemainingItem(stack);
				if (craftingRemainingItem.isEmpty()) {
					throw new IllegalStateException("Item reported a crafting remainder but returned an empty stack");
				}
				subtractCraftingRemainder(ingredientMap, NSSItem.createItem(craftingRemainingItem), craftingRemainingItem.getCount());
			}
		} catch (Exception e) {
			ResourceLocation itemName = BuiltInRegistries.ITEM.getKey(item);
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
		NormalizedSimpleStack normalizedStack = normalizeStack(stack, recipeID, "ingredient");
		if (normalizedStack == null) {
			return true;
		}
		ingredientMap.mergeInt(normalizedStack, 1, Constants.INT_SUM);
		return false;
	}

	static <TYPE> void subtractCraftingRemainder(Object2IntMap<TYPE> ingredientMap, TYPE remainder, int count) {
		if (count <= 0) {
			throw new IllegalArgumentException("Crafting remainder count must be positive");
		}
		ingredientMap.mergeInt(remainder, Math.negateExact(count), Math::addExact);
	}

	@Nullable
	private Collection<Ingredient> getIngredientsChecked(RecipeHolder<?> recipeHolder) {
		try {
			return getIngredients(recipeHolder.value());
		} catch (Exception e) {
			ResourceLocation recipeID = recipeHolder.id();
			PECore.LOGGER.error(LogUtils.FATAL_MARKER, "Error mapping recipe {}. Failed to get ingredients. Please report this to {}.", recipeID, recipeID.getNamespace(), e);
		}
		return null;
	}

	//Allow overwriting the ingredients list because Smithing recipes don't override it themselves
	protected Collection<Ingredient> getIngredients(Recipe<?> recipe) {
		return recipe.getIngredients();
	}
}
