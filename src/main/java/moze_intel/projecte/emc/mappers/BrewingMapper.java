package moze_intel.projecte.emc.mappers;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.mapper.IEMCMapper;
import moze_intel.projecte.api.mapper.collector.IMappingCollector;
import moze_intel.projecte.api.nss.NSSItem;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionBrewing;
import net.minecraft.potion.Potions;
import net.minecraft.resources.IResourceManager;
import net.minecraftforge.common.brewing.BrewingRecipe;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.common.brewing.IBrewingRecipe;
import net.minecraftforge.common.brewing.VanillaBrewingRecipe;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper.UnableToAccessFieldException;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper.UnableToFindFieldException;
import net.minecraftforge.registries.ForgeRegistries;

//@EMCMapper//TODO: Uncomment once we have support for NBT based normalized simple stacks
public class BrewingMapper implements IEMCMapper<NormalizedSimpleStack, Long> {

	//Note: We don't bother keeping track of the output field as we query the recipe to see if it is valid
	private static final Set<ItemInfo> allReagents = new HashSet<>();
	private static final Set<ItemInfo> allInputs = new HashSet<>();
	private static int totalConversions;
	private static int totalPotionItems;

	private static List<Object> itemConversions;
	private static List<Object> typeConversions;
	private static Class mixPredicateClass;

	//Note: We cache this value as while technically Mix Predicates can be added at any time, there is no way to
	private static boolean mapAllReagents() {
		if (itemConversions == null || typeConversions == null) {
			//Get references to the lists of Mixing Predicates if either of our references is null
			try {
				typeConversions = ObfuscationReflectionHelper.getPrivateValue(PotionBrewing.class, null, "field_185213_a");//POTION_TYPE_CONVERSIONS
				itemConversions = ObfuscationReflectionHelper.getPrivateValue(PotionBrewing.class, null, "field_185214_b");//POTION_ITEM_CONVERSIONS
			} catch (UnableToFindFieldException | UnableToAccessFieldException e) {
				PECore.LOGGER.error("Error getting conversion field: ", e);
				return false;
			}
		}
		int conversionCount = itemConversions.size() + typeConversions.size();
		//Use our best guess of if our cache is invalid because someone added to the list at runtime
		// Shouldn't happen but there is nothing by default making it so that the list because immutable
		// But if it does happen clear our cached values and then try to get everything again
		if (totalConversions == conversionCount) {
			return true;
		}
		allReagents.clear();

		if (mixPredicateClass == null) {
			//If we don't have a reference to the MixPredicate class try to get it
			try {
				mixPredicateClass = Class.forName("net.minecraft.potion.PotionBrewing$MixPredicate");
			} catch (ClassNotFoundException e) {
				PECore.LOGGER.error("Brewing mapper: could not find MixPredicate");
				return false;
			}
		}
		if (!addReagents(itemConversions) || !addReagents(typeConversions)) {
			return false;
		}
		totalConversions = conversionCount;
		return true;
	}

	private static boolean addReagents(List<Object> conversions) {
		for (Object conversion : conversions) {
			try {
				Ingredient reagent = (Ingredient) ObfuscationReflectionHelper.getPrivateValue(mixPredicateClass, conversion, "field_185199_b");
				for (ItemStack r : reagent.getMatchingStacks()) {
					allReagents.add(new ItemInfo(r));
				}
			} catch (Exception ex) {
				PECore.LOGGER.error("Brewing mapper: could not find field: {}", ex.getMessage());
				return false;
			}
		}
		return true;
	}

	private static void mapAllInputs() {
		//Use our best guess of if our cache is invalid because someone added to the list at runtime
		// Shouldn't happen but there is nothing by default making it so that the list because immutable
		// But if it does happen clear our cached values and then try to get everything again
		int count = PotionBrewing.POTION_ITEMS.size();
		if (totalPotionItems == count) {
			return;
		}
		allInputs.clear();
		Set<ItemInfo> inputs = new HashSet<>();
		for (Ingredient potionItem : PotionBrewing.POTION_ITEMS) {
			for (ItemStack input : potionItem.getMatchingStacks()) {
				inputs.add(new ItemInfo(input));
			}
		}
		for (Potion potion : ForgeRegistries.POTION_TYPES.getValues()) {
			for (ItemInfo input : inputs) {
				allInputs.add(input.makeWithPotion(potion));
			}
		}
		totalPotionItems = count;
	}

	@Override
	public void addMappings(IMappingCollector<NormalizedSimpleStack, Long> mapper, CommentedFileConfig config, IResourceManager resourceManager) {
		if (!mapAllReagents()) {
			//Something failed so stop the mapping we printed the error when it failed
			return;
		}
		mapAllInputs();

		Set<Class> canNotMap = new HashSet<>();
		int recipeCount = 0;
		List<IBrewingRecipe> recipes = BrewingRecipeRegistry.getRecipes();
		for (IBrewingRecipe recipe : recipes) {
			if (recipe instanceof BrewingRecipe) {
				BrewingRecipe brewingRecipe = (BrewingRecipe) recipe;
				Ingredient input = brewingRecipe.getInput();
				Ingredient reagent = brewingRecipe.getIngredient();
				ItemStack output = brewingRecipe.getOutput();
				ItemStack[] validInputs = input.getMatchingStacks();
				ItemStack[] validReagents = reagent.getMatchingStacks();

				NormalizedSimpleStack nssOut = NSSItem.createItem(output);
				for (ItemStack validInput : validInputs) {
					NormalizedSimpleStack nssInput = NSSItem.createItem(validInput);
					for (ItemStack validReagent : validReagents) {
						Map<NormalizedSimpleStack, Integer> ingredientsWithAmount = new HashMap<>();
						ingredientsWithAmount.put(nssInput, 3);
						ingredientsWithAmount.put(NSSItem.createItem(validReagent), validReagent.getCount());
						mapper.addConversion(output.getCount(), nssOut, ingredientsWithAmount);
						recipeCount++;
					}
				}
			} else if (recipe instanceof VanillaBrewingRecipe) {
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
							ingredientsWithAmount.put(NSSItem.createItem(validReagent), validReagent.getCount());
							mapper.addConversion(output.getCount(), NSSItem.createItem(output), ingredientsWithAmount);
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

	@Override
	public boolean isAvailable() {
		return true;
	}

	private static class ItemInfo {

		private final Item item;
		private final CompoundNBT nbt;

		private ItemInfo(ItemStack stack) {
			this(stack.getItem(), stack.getTag());
		}

		private ItemInfo(Item item, CompoundNBT nbt) {
			this.item = item;
			this.nbt = nbt;
		}

		public ItemStack createStack() {
			ItemStack stack = new ItemStack(item);
			stack.setTag(nbt);
			return stack;
		}

		@Override
		public int hashCode() {
			int code = item.hashCode();
			if (nbt != null) {
				code = 31 * code + nbt.hashCode();
			}
			return code;
		}

		@Override
		public boolean equals(Object o) {
			if (o == this) {
				return true;
			}
			if (o instanceof ItemInfo) {
				ItemInfo other = (ItemInfo) o;
				return item == other.item && Objects.equals(nbt, other.nbt);
			}
			return false;
		}

		//Version of PotionUtils.addPotionToItemStack except without boxing ItemInfo into an out of an ItemStack
		private ItemInfo makeWithPotion(Potion potion) {
			CompoundNBT nbt = this.nbt == null ? null : this.nbt.copy();
			if (potion == Potions.EMPTY) {
				if (nbt != null && nbt.contains("Potion")) {
					nbt.remove("Potion");
					if (nbt.isEmpty()) {
						nbt = null;
					}
				}
			} else {
				if (nbt == null) {
					nbt = new CompoundNBT();
				}
				nbt.putString("Potion", potion.getRegistryName().toString());
			}
			return new ItemInfo(item, nbt);
		}
	}
}