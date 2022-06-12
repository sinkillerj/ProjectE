package moze_intel.projecte.emc.mappers;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.ItemInfo;
import moze_intel.projecte.api.mapper.EMCMapper;
import moze_intel.projecte.api.mapper.IEMCMapper;
import moze_intel.projecte.api.mapper.collector.IMappingCollector;
import moze_intel.projecte.api.nss.NSSFluid;
import moze_intel.projecte.api.nss.NSSItem;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import moze_intel.projecte.utils.ItemInfoHelper;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.brewing.BrewingRecipe;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.common.brewing.IBrewingRecipe;
import net.minecraftforge.common.brewing.VanillaBrewingRecipe;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

@EMCMapper
public class BrewingMapper implements IEMCMapper<NormalizedSimpleStack, Long> {

	//Note: We don't bother keeping track of the output field as we query the recipe to see if it is valid
	private static final Set<ItemInfo> allReagents = new HashSet<>();
	private static final Set<ItemInfo> allInputs = new HashSet<>();
	private static int totalConversions;
	private static int totalPotionItems;

	private static boolean mapAllReagents() {
		//Use our best guess of if our cache is invalid because someone added to the list at runtime
		// Shouldn't happen but there is nothing by default making it so that the list because immutable
		// But if it does happen clear our cached values and then try to get everything again
		int conversionCount = PotionBrewing.CONTAINER_MIXES.size() + PotionBrewing.POTION_MIXES.size();
		if (totalConversions == conversionCount) {
			return true;
		}
		allReagents.clear();
		addReagents(PotionBrewing.CONTAINER_MIXES);
		addReagents(PotionBrewing.POTION_MIXES);
		totalConversions = conversionCount;
		return true;
	}

	private static <T> void addReagents(List<PotionBrewing.Mix<T>> conversions) {
		for (PotionBrewing.Mix<T> conversion : conversions) {
			for (ItemStack r : conversion.ingredient.getItems()) {
				allReagents.add(ItemInfo.fromStack(r));
			}
		}
	}

	private static void mapAllInputs() {
		//Use our best guess of if our cache is invalid because someone added to the list at runtime
		// Shouldn't happen but there is nothing by default making it so that the list because immutable
		// But if it does happen clear our cached values and then try to get everything again
		int count = PotionBrewing.ALLOWED_CONTAINERS.size();
		if (totalPotionItems == count) {
			return;
		}
		allInputs.clear();
		Set<ItemInfo> inputs = new HashSet<>();
		for (Ingredient potionItem : PotionBrewing.ALLOWED_CONTAINERS) {
			ItemStack[] matchingStacks = getMatchingStacks(potionItem);
			if (matchingStacks != null) {
				//Silently ignore any invalid potion items (ingredients that may be tags) this should never be the case
				// unless someone ATs the map and inserts a custom ingredient into it, but just in case, don't crash
				for (ItemStack input : matchingStacks) {
					inputs.add(ItemInfo.fromStack(input));
				}
			}
		}
		for (Potion potion : ForgeRegistries.POTIONS.getValues()) {
			for (ItemInfo input : inputs) {
				allInputs.add(ItemInfoHelper.makeWithPotion(input, potion));
			}
		}
		totalPotionItems = count;
	}

	@Override
	public void addMappings(IMappingCollector<NormalizedSimpleStack, Long> mapper, CommentedFileConfig config, ReloadableServerResources serverResources,
			ResourceManager resourceManager) {
		boolean vanillaRetrieved = mapAllReagents();
		if (vanillaRetrieved) {
			mapAllInputs();
		}

		//Add conversion for empty bottle + water to water bottle
		Map<NormalizedSimpleStack, Integer> waterIngredients = new HashMap<>();
		waterIngredients.put(NSSItem.createItem(Items.GLASS_BOTTLE), 1);
		waterIngredients.put(NSSFluid.createTag(FluidTags.WATER), FluidAttributes.BUCKET_VOLUME / 3);
		mapper.addConversion(1, NSSItem.createItem(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER)), waterIngredients);

		Set<Class<?>> canNotMap = new HashSet<>();
		int recipeCount = 0;
		List<IBrewingRecipe> recipes = BrewingRecipeRegistry.getRecipes();
		for (IBrewingRecipe recipe : recipes) {
			if (recipe instanceof BrewingRecipe brewingRecipe) {
				ItemStack[] validInputs = getMatchingStacks(brewingRecipe.getInput());
				ItemStack[] validReagents = getMatchingStacks(brewingRecipe.getIngredient());
				if (validInputs == null || validReagents == null) {
					//Skip brewing recipes that we are not able to process such as ones using tags
					// as ingredients, as tags don't exist when the brewing recipe is being defined
					continue;
				}
				ItemStack output = brewingRecipe.getOutput();
				NormalizedSimpleStack nssOut = NSSItem.createItem(output);
				for (ItemStack validInput : validInputs) {
					NormalizedSimpleStack nssInput = NSSItem.createItem(validInput);
					for (ItemStack validReagent : validReagents) {
						Map<NormalizedSimpleStack, Integer> ingredientsWithAmount = new HashMap<>();
						ingredientsWithAmount.put(nssInput, 3);
						ingredientsWithAmount.put(NSSItem.createItem(validReagent), validReagent.getCount());
						//Add the conversion, 3 input + x reagent = 3 y output as strictly speaking the only one of the three parts
						// in the recipe that are required to be one in stack size is the input
						mapper.addConversion(3 * output.getCount(), nssOut, ingredientsWithAmount);
						recipeCount++;
					}
				}
			} else if (recipe instanceof VanillaBrewingRecipe) {
				if (!vanillaRetrieved) {
					//Skip doing vanilla recipes because we failed to get them properly
					canNotMap.add(recipe.getClass());
					continue;
				}
				//Check all known valid inputs and reagents to see which ones create valid inputs
				// Note: Getting the list of outputs while getting reagents will cause things to be missed
				// As the PotionBrewing class does not contain all valid mappings (For example: Potion of luck + gunpowder -> splash potion of luck)
				for (ItemInfo inputInfo : allInputs) {
					ItemStack validInput = inputInfo.createStack();
					NormalizedSimpleStack nssInput = NSSItem.createItem(validInput);
					for (ItemInfo reagentInfo : allReagents) {
						ItemStack validReagent = reagentInfo.createStack();
						ItemStack output = recipe.getOutput(validInput, validReagent);
						if (!output.isEmpty()) {
							Map<NormalizedSimpleStack, Integer> ingredientsWithAmount = new HashMap<>();
							ingredientsWithAmount.put(nssInput, 3);
							ingredientsWithAmount.put(NSSItem.createItem(validReagent), 1);
							//Add the conversion, 3 input + reagent = 3 y output as the output technically could be stacked
							mapper.addConversion(3 * output.getCount(), NSSItem.createItem(output), ingredientsWithAmount);
							recipeCount++;
						}
					}
				}
			} else {
				canNotMap.add(recipe.getClass());
			}
		}

		PECore.debugLog("BrewingMapper Statistics:");
		PECore.debugLog("Found {} Brewing Recipes", recipeCount);
		for (Class<?> c : canNotMap) {
			PECore.debugLog("Could not map Brewing Recipes with Type: {}", c.getName());
		}
	}

	@Override
	public String getName() {
		return "BrewingMapper";
	}

	@Override
	public String getDescription() {
		return "Add Conversions for Brewing Recipes";
	}

	@Nullable
	private static ItemStack[] getMatchingStacks(Ingredient ingredient) {
		try {
			return ingredient.getItems();
		} catch (Exception e) {
			return null;
		}
	}
}