package moze_intel.projecte.emc.mappers.recipe;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.mapper.EMCMapper;
import moze_intel.projecte.api.mapper.IEMCMapper;
import moze_intel.projecte.api.mapper.collector.IMappingCollector;
import moze_intel.projecte.api.mapper.recipe.IRecipeTypeMapper;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import moze_intel.projecte.emc.EMCMappingHandler;
import moze_intel.projecte.utils.AnnotationHelper;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.resources.DataPackRegistries;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

@EMCMapper
public class CraftingMapper implements IEMCMapper<NormalizedSimpleStack, Long> {

	//Note: None of our defaults just directly support all recipe types, as mods may extend it for "random" things and have more input types required than just items
	// We also do this via annotations to allow for broader support for looping specific recipes and handling them
	private static final List<IRecipeTypeMapper> recipeMappers = new ArrayList<>();

	public static void loadMappers() {
		if (recipeMappers.isEmpty()) {
			recipeMappers.addAll(AnnotationHelper.getRecipeTypeMappers());
		}
	}

	@Override
	@SuppressWarnings({"unchecked", "rawtypes"})
	public void addMappings(IMappingCollector<NormalizedSimpleStack, Long> mapper, final CommentedFileConfig config, DataPackRegistries dataPackRegistries,
			IResourceManager resourceManager) {
		Map<ResourceLocation, RecipeCountInfo> recipeCount = new HashMap<>();
		Set<ResourceLocation> canNotMap = new HashSet<>();
		RecipeManager recipeManager = dataPackRegistries.getRecipeManager();
		for (IRecipeType<?> recipeType : Registry.RECIPE_TYPE) {
			ResourceLocation typeRegistryName = Registry.RECIPE_TYPE.getKey(recipeType);
			boolean wasHandled = false;
			List<? extends IRecipe<?>> recipes = null;
			List<IRecipe<?>> unhandled = new ArrayList<>();
			for (IRecipeTypeMapper recipeMapper : recipeMappers) {
				String configKey = getName() + "." + recipeMapper.getName() + ".enabled";
				if (EMCMappingHandler.getOrSetDefault(config, configKey, recipeMapper.getDescription(), recipeMapper.isAvailable())) {
					//If the sub mapper is enabled, use it
					if (recipeMapper.canHandle(recipeType)) {
						if (recipes == null) {
							//If we haven't already retrieved the recipes, do so
							//Note: The unchecked cast is needed as while the IDE doesn't have a warning without it
							// it will not actually compile due to IRecipeType's generic only having to be of IRecipe<?>
							// so no information is stored about the type of inventory for the recipe
							recipes = recipeManager.getRecipesForType((IRecipeType) recipeType);
						}
						int numHandled = 0;
						for (IRecipe<?> recipe : recipes) {
							try {
								if (recipeMapper.handleRecipe(mapper, recipe)) {
									numHandled++;
								} else {
									unhandled.add(recipe);
								}
							} catch (Exception e) {
								PECore.LOGGER.fatal("A fatal error occurred while trying to map the recipe: {}", recipe.getId());
								throw e;
							}
						}
						if (numHandled > 0 || recipes.isEmpty()) {
							if (recipeCount.containsKey(typeRegistryName)) {
								recipeCount.get(typeRegistryName).setUnhandled(unhandled);
							} else {
								recipeCount.put(typeRegistryName, new RecipeCountInfo(recipes.size(), unhandled));
							}
							wasHandled = true;
							if (unhandled.isEmpty()) {
								//If we have no more recipes that were unhandled break out of mapping this recipe type
								break;
							} else {
								//Otherwise we replace our collection of known recipes with the unhandled ones and reset the list of unhandled recipes
								recipes = unhandled;
								unhandled = new ArrayList<>();
							}
						}
						//If we didn't actually handle/map any recipes, continue looking
					}
				}
			}
			if (!wasHandled) {
				//Note: We cannot just look at not unhandled is empty as then if none of the mappers even support the type
				// it will not be true. We also don't have any issues due to how we modify the unhandled
				canNotMap.add(typeRegistryName);
			}
		}
		PECore.debugLog("CraftingMapper Statistics:");
		for (Map.Entry<ResourceLocation, RecipeCountInfo> entry : recipeCount.entrySet()) {
			ResourceLocation typeRegistryName = entry.getKey();
			RecipeCountInfo countInfo = entry.getValue();
			int total = countInfo.getTotalRecipes();
			List<IRecipe<?>> unhandled = countInfo.getUnhandled();
			PECore.debugLog("Found and handled {} of {} Recipes of Type {}", total - unhandled.size(), total, typeRegistryName);
			if (!unhandled.isEmpty()) {
				PECore.debugLog("Unhandled Recipes of Type {}:", typeRegistryName);
				for (IRecipe<?> recipe : unhandled) {
					PECore.debugLog("Name: {}, Recipe class: {}", recipe.getId(), recipe.getClass().getName());
				}
			}
		}
		for (ResourceLocation typeRegistryName : canNotMap) {
			PECore.debugLog("Could not map any Recipes of Type: {}", typeRegistryName);
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

	private static class RecipeCountInfo {

		private final int totalRecipes;
		private List<IRecipe<?>> unhandled;

		private RecipeCountInfo(int totalRecipes, List<IRecipe<?>> unhandled) {
			this.totalRecipes = totalRecipes;
			this.unhandled = unhandled;
		}

		public int getTotalRecipes() {
			return totalRecipes;
		}

		public void setUnhandled(List<IRecipe<?>> unhandled) {
			this.unhandled = unhandled;
		}

		public List<IRecipe<?>> getUnhandled() {
			return unhandled;
		}
	}
}