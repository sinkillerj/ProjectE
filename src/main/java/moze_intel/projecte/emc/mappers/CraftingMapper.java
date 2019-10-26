package moze_intel.projecte.emc.mappers;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import moze_intel.projecte.PECore;
import moze_intel.projecte.emc.EMCMapper;
import moze_intel.projecte.emc.IngredientMap;
import moze_intel.projecte.emc.collector.IMappingCollector;
import moze_intel.projecte.api.nss.NSSFake;
import moze_intel.projecte.api.nss.NSSItem;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.AbstractCookingRecipe;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.SingleItemRecipe;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CraftingMapper implements IEMCMapper<NormalizedSimpleStack, Long> {
	private final List<IRecipeMapper> recipeMappers = Arrays.asList(new VanillaRecipeMapper(), new RecipeStagesRecipeMapper());

	@Override
	public void addMappings(IMappingCollector<NormalizedSimpleStack, Long> mapper, final CommentedFileConfig config, IResourceManager resourceManager) {
		Map<ResourceLocation, Integer> recipeCount = new HashMap<>();
		Set<Class> canNotMap = new HashSet<>();

		for (IRecipe recipe : ServerLifecycleHooks.getCurrentServer().getRecipeManager().getRecipes()) {
			boolean handled = false;
			ItemStack recipeOutput = recipe.getRecipeOutput();
			if (recipeOutput.isEmpty()) continue;
			NormalizedSimpleStack recipeOutputNorm = NSSItem.createItem(recipeOutput);
			for (IRecipeMapper recipeMapper : recipeMappers) {
				String configKey = getName() + "." + recipeMapper.getName() + ".enabled";
				if (!EMCMapper.getOrSetDefault(config, configKey, recipeMapper.getDescription(), true))
					continue;
				if (recipeMapper.canHandle(recipe)) {
					handled = true;
					for (CraftingIngredients variation : recipeMapper.getIngredientsFor(recipe)) {
						IngredientMap<NormalizedSimpleStack> ingredientMap = new IngredientMap<>();
						for (ItemStack stack : variation.fixedIngredients) {
							if (stack.isEmpty()) continue;
							if (stack.getItem().hasContainerItem(stack)) {
								ingredientMap.addIngredient(NSSItem.createItem(stack.getItem().getContainerItem(stack)), -1);
							}
							ingredientMap.addIngredient(NSSItem.createItem(stack), 1);
						}
						for (Iterable<ItemStack> multiIngredient : variation.multiIngredients) {
							NormalizedSimpleStack dummy = NSSFake.create(multiIngredient.toString());
							ingredientMap.addIngredient(dummy, 1);
							for (ItemStack stack : multiIngredient) {
								if (stack.isEmpty()) continue;
								IngredientMap<NormalizedSimpleStack> groupIngredientMap = new IngredientMap<>();
								if (stack.getItem().hasContainerItem(stack)) {
									groupIngredientMap.addIngredient(NSSItem.createItem(stack.getItem().getContainerItem(stack)), -1);
								}
								groupIngredientMap.addIngredient(NSSItem.createItem(stack), 1);
								mapper.addConversion(1, dummy, groupIngredientMap.getMap());
							}
						}
						mapper.addConversion(recipeOutput.getCount(), recipeOutputNorm, ingredientMap.getMap());
					}
					break;
				}
			}
			if (!handled) {
				canNotMap.add(recipe.getClass());
			} else {
				recipeCount.merge(recipe.getSerializer().getRegistryName(), 1, Integer::sum);
			}
		}

		PECore.debugLog("CraftingMapper Statistics:");
		for (Map.Entry<ResourceLocation, Integer> entry: recipeCount.entrySet()) {
			PECore.debugLog("Found {} Recipes of Type {}", entry.getValue(), entry.getKey());
		}
		for (Class<?> c : canNotMap) {
			PECore.debugLog("Could not map Crafting Recipes with Type: {}", c.getName());
		}
	}

	@Override
	public String getName() {
		return "CraftingMapper";
	}

	@Override
	public String getDescription() {
		return "Add Conversions for Crafting Recipes gathered from net.minecraft.item.crafting.RecipeManager";
	}

	@Override
	public boolean isAvailable() {
		return true;
	}

	public interface IRecipeMapper {
		String getName();
		String getDescription();
		boolean canHandle(IRecipe recipe);

		default Iterable<CraftingIngredients> getIngredientsFor(IRecipe recipe) {
			List<Iterable<ItemStack>> variableInputs = new ArrayList<>();
			List<ItemStack> fixedInputs = new ArrayList<>();
			for (Object i : recipe.getIngredients()) {
				Ingredient recipeItem = (Ingredient) i;
				ItemStack[] matches = recipeItem.getMatchingStacks();
				if (matches.length == 1) {
					fixedInputs.add(matches[0].copy());
				} else if (matches.length > 0) {
					List<ItemStack> recipeItemOptions = new LinkedList<>();
					for (ItemStack option : matches) {
						recipeItemOptions.add(option.copy());
					}
					variableInputs.add(recipeItemOptions);
				}
			}
			return Collections.singletonList(new CraftingIngredients(fixedInputs, variableInputs));
		}
	}

	private static class CraftingIngredients {
		public final Iterable<ItemStack> fixedIngredients;
		public final Iterable<Iterable<ItemStack>> multiIngredients;
		public CraftingIngredients(Iterable<ItemStack> fixedIngredients, Iterable<Iterable<ItemStack>> multiIngredients) {
			this.fixedIngredients = fixedIngredients;
			this.multiIngredients = multiIngredients;
		}
	}

	private static class VanillaRecipeMapper implements IRecipeMapper {

		@Override
		public String getName() {
			return "VanillaRecipeMapper";
		}

		@Override
		public String getDescription() {
			return "Maps crafting table, cooking, and stone cutter recipes";
		}

		@Override
		public boolean canHandle(IRecipe recipe) {
			//Note: Does not just directly support IRecipe, as mods may extend it for "random" things and have more input types
			// required than just items
			return recipe instanceof ICraftingRecipe || recipe instanceof AbstractCookingRecipe || recipe instanceof SingleItemRecipe;
		}
	}
}
