package moze_intel.projecte.emc.mappers.recipe;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

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
	public void addMappings(IMappingCollector<NormalizedSimpleStack, Long> mapper, final CommentedFileConfig config, IResourceManager resourceManager) {
		//TODO: Improve the debug logging for recipeCount, to include class types of failed recipes that fall in a specific class type
		Map<ResourceLocation, Pair<Integer, Integer>> recipeCount = new HashMap<>();
		Set<ResourceLocation> canNotMap = new HashSet<>();
		RecipeManager recipeManager = ServerLifecycleHooks.getCurrentServer().getRecipeManager();
		//TODO: If there ever ends up being a forge registry for recipe types, use that instead
		for (IRecipeType<?> recipeType : Registry.RECIPE_TYPE) {
			ResourceLocation typeRegistryName = Registry.RECIPE_TYPE.getKey(recipeType);
			boolean wasHandled = false;
			Collection<IRecipe<?>> recipes = null;
			for (IRecipeTypeMapper recipeMapper : recipeMappers) {
				String configKey = getName() + "." + recipeMapper.getName() + ".enabled";
				if (EMCMappingHandler.getOrSetDefault(config, configKey, recipeMapper.getDescription(), recipeMapper.isAvailable())) {
					//If the sub mapper is enabled, use it
					if (recipeMapper.canHandle(recipeType)) {
						if (recipes == null) {
							//If we haven't already retrieved the recipes, do so
							recipes = recipeManager.recipes.getOrDefault(recipeType, Collections.emptyMap()).values();
						}
						int numHandled = (int) recipes.stream().filter(recipe -> recipeMapper.handleRecipe(mapper, recipe)).count();
						if (numHandled > 0 || recipes.isEmpty()) {
							recipeCount.put(typeRegistryName, Pair.of(numHandled, recipes.size()));
							wasHandled = true;
							//On the first recipe mapper we run into that supports this recipe type, use it and move on to the next recipe type
							// or if there are not actually any recipes we mark it as handled
							//TODO: Eventually we may want to make it so that instead of just breaking we can attempt to handle
							// any missed recipes using an alternate mapper.
							break;
						}
						//If we didn't actually handle/map any recipes, continue looking
					}
				}
			}
			if (!wasHandled) {
				canNotMap.add(typeRegistryName);
			}
		}
		PECore.debugLog("CraftingMapper Statistics:");
		for (Map.Entry<ResourceLocation, Pair<Integer, Integer>> entry : recipeCount.entrySet()) {
			Pair<Integer, Integer> count = entry.getValue();
			PECore.debugLog("Found and handled {} of {} Recipes of Type {}", count.getFirst(), count.getSecond(), entry.getKey());
		}
		for (ResourceLocation typeRegistryName : canNotMap) {
			PECore.debugLog("Could not map Recipes with Type: {}", typeRegistryName);
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
}