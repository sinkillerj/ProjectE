package moze_intel.projecte.emc.mappers.recipe;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.mapper.EMCMapper;
import moze_intel.projecte.api.mapper.IEMCMapper;
import moze_intel.projecte.api.mapper.collector.IMappingCollector;
import moze_intel.projecte.api.mapper.recipe.INSSFakeGroupManager;
import moze_intel.projecte.api.mapper.recipe.IRecipeTypeMapper;
import moze_intel.projecte.api.nss.NSSFake;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import moze_intel.projecte.config.PEConfigTranslations;
import moze_intel.projecte.utils.AnnotationHelper;
import moze_intel.projecte.utils.EMCHelper;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.common.ModConfigSpec;

@EMCMapper
public class CraftingMapper implements IEMCMapper<NormalizedSimpleStack, Long> {

	//Note: None of our defaults just directly support all recipe types, as mods may extend it for "random" things and have more input types required than just items
	// We also do this via annotations to allow for broader support for looping specific recipes and handling them
	private final Map<String, BooleanSupplier> enabledRecipeMappers = new HashMap<>();
	private final List<IRecipeTypeMapper> recipeMappers;

	public CraftingMapper() {
		//Load any recipe type mappers when instantiating the crafting mapper
		recipeMappers = AnnotationHelper.getRecipeTypeMappers();
	}

	private boolean isRecipeMapperEnabled(IRecipeTypeMapper mapper) {
		BooleanSupplier supplier = enabledRecipeMappers.get(mapper.getName());
		return supplier == null || supplier.getAsBoolean();
	}

	@Override
	public void addConfigOptions(ModConfigSpec.Builder configBuilder) {
		for (IRecipeTypeMapper recipeMapper : recipeMappers) {
			configBuilder.comment(recipeMapper.getDescription())
					.translation(recipeMapper.getTranslationKey())
					.push(recipeMapper.getConfigPath());
			enabledRecipeMappers.put(recipeMapper.getName(), PEConfigTranslations.MAPPING_RECIPE_TYPE_MAPPER_ENABLED.applyToBuilder(configBuilder)
					.define("enabled", recipeMapper.isAvailable()));
			recipeMapper.addConfigOptions(configBuilder);
			configBuilder.pop();
		}
	}

	@Override
	public void addMappings(IMappingCollector<NormalizedSimpleStack, Long> mapper, ReloadableServerResources serverResources,
			RegistryAccess registryAccess, ResourceManager resourceManager) {
		NSSFake.setCurrentNamespace("craftingMapper");
		try {
			addMappingsInternal(mapper, serverResources, registryAccess);
		} finally {
			NSSFake.resetNamespace();
		}
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	private void addMappingsInternal(IMappingCollector<NormalizedSimpleStack, Long> mapper, ReloadableServerResources serverResources,
			RegistryAccess registryAccess) {
		Reference2ObjectMap<ResourceKey<RecipeType<?>>, RecipeCountInfo> recipeCount = new Reference2ObjectOpenHashMap<>();
		Set<ResourceKey<RecipeType<?>>> canNotMap = new ReferenceOpenHashSet<>();
		RecipeManager recipeManager = serverResources.getRecipeManager();
		//Make a new fake group manager here instead of across the entire mapper so that we can reclaim the memory when we are done with this method
		NSSFakeGroupManager fakeGroupManager = new NSSFakeGroupManager(mapper);
		for (Map.Entry<ResourceKey<RecipeType<?>>, RecipeType<?>> entry : BuiltInRegistries.RECIPE_TYPE.entrySet()) {
			ResourceKey<RecipeType<?>> typeRegistryKey = entry.getKey();
			RecipeType<?> recipeType = entry.getValue();
			boolean wasHandled = false;
			List<RecipeHolder<?>> recipes = null;
			for (IRecipeTypeMapper recipeMapper : recipeMappers) {
				if (isRecipeMapperEnabled(recipeMapper)) {
					//If the sub mapper is enabled, use it
					if (recipeMapper.canHandle(recipeType)) {
						if (recipes == null) {
							//If we haven't already retrieved the recipes, do so
							//Note: The unchecked cast is needed as while the IDE doesn't have a warning without it,
							// it will not actually compile due to IRecipeType's generic only having to be of IRecipe<?>
							// so no information is stored about the type of inventory for the recipe
							recipes = recipeManager.getAllRecipesFor((RecipeType) recipeType);
						}
						RecipeHandlingResult<RecipeHolder<?>> handlingResult = handleRecipes(recipes, recipeHolder -> {
							try {
								return recipeMapper.handleRecipe(mapper, recipeHolder, registryAccess, fakeGroupManager);
							} catch (Exception e) {
								PECore.LOGGER.error(LogUtils.FATAL_MARKER, "A fatal error occurred while trying to map the recipe: {}", recipeHolder.id());
								throw e;
							}
						});
						int numHandled = handlingResult.handled();
						List<RecipeHolder<?>> unhandled = handlingResult.unhandled();
						if (numHandled > 0 || recipes.isEmpty()) {
							RecipeCountInfo recipeCountInfo = recipeCount.get(typeRegistryKey);
							if (recipeCountInfo != null) {
								recipeCountInfo.setUnhandled(unhandled);
							} else {
								recipeCount.put(typeRegistryKey, new RecipeCountInfo(recipeType, recipes.size(), unhandled));
							}
							wasHandled = true;
							if (unhandled.isEmpty()) {
								//If we have no more recipes that were unhandled break out of mapping this recipe type
								break;
							} else {
								//Otherwise we replace our collection of known recipes with the unhandled ones and reset the list of unhandled recipes
								recipes = unhandled;
							}
						}
						//If we didn't actually handle/map any recipes, continue looking
					}
				}
			}
			if (!wasHandled) {
				//Note: We cannot just look at not unhandled is empty as then if none of the mappers even support the type
				// it will not be true. We also don't have any issues due to how we modify the unhandled
				canNotMap.add(typeRegistryKey);
			}
		}
		PECore.debugLog("{} Statistics:", getName());

		for (Iterator<Reference2ObjectMap.Entry<ResourceKey<RecipeType<?>>, RecipeCountInfo>> iterator = Reference2ObjectMaps.fastIterator(recipeCount); iterator.hasNext(); ) {
			Reference2ObjectMap.Entry<ResourceKey<RecipeType<?>>, RecipeCountInfo> entry = iterator.next();
			ResourceLocation typeRegistryName = entry.getKey().location();
			RecipeCountInfo countInfo = entry.getValue();
			int total = countInfo.getTotalRecipes();
			List<RecipeHolder<?>> unhandled = countInfo.getUnhandled();
			RecipeDiagnosticResult<RecipeHolder<?>> diagnosticResult = classifyUnhandledRecipes(unhandled,
				recipeHolder -> getExpectedUnhandledReason(countInfo.getRecipeType(), recipeHolder, registryAccess));
			int handled = total - unhandled.size();
			PECore.debugLog("Recipe Mapping Summary for Type {}: {} handled, {} intentionally skipped, {} unexpectedly unhandled, {} total",
				typeRegistryName, handled, diagnosticResult.expected().size(), diagnosticResult.unexpected().size(), total);
			if (!diagnosticResult.expected().isEmpty()) {
				PECore.debugLog("Intentionally skipped dynamic or stateful Recipes of Type {}:", typeRegistryName);
				for (ExpectedUnhandledRecipe<RecipeHolder<?>> expected : diagnosticResult.expected()) {
					RecipeHolder<?> recipeHolder = expected.recipe();
					PECore.debugLog("Name: {}, Recipe class: {}, Reason: {}", recipeHolder.id(), recipeHolder.value().getClass().getName(), expected.reason());
				}
			}
			if (!diagnosticResult.unexpected().isEmpty()) {
				PECore.debugLog("Unexpectedly unhandled Recipes of Type {}:", typeRegistryName);
				for (RecipeHolder<?> recipeHolder : diagnosticResult.unexpected()) {
					PECore.debugLog("Name: {}, Recipe class: {}", recipeHolder.id(), recipeHolder.value().getClass().getName());
				}
			}
		}
		for (ResourceKey<RecipeType<?>> typeRegistryKey : canNotMap) {
			PECore.debugLog("Could not map any Recipes of Type: {}", typeRegistryKey.location());
		}
	}

	private String getExpectedUnhandledReason(RecipeType<?> recipeType, RecipeHolder<?> recipeHolder, RegistryAccess registryAccess) {
		for (IRecipeTypeMapper recipeMapper : recipeMappers) {
			if (isRecipeMapperEnabled(recipeMapper)) {
				try {
					if (recipeMapper.canHandle(recipeType)) {
						String reason = recipeMapper.getExpectedUnhandledReason(recipeHolder, registryAccess);
						if (reason != null && !reason.isBlank()) {
							return reason;
						}
					}
				} catch (RuntimeException e) {
					PECore.LOGGER.error("Recipe mapper {} failed while classifying unhandled recipe {}. Treating it as unexpectedly unhandled.",
						recipeMapper.getName(), recipeHolder.id(), e);
				}
			}
		}
		return null;
	}

	static <T> RecipeDiagnosticResult<T> classifyUnhandledRecipes(List<T> recipes, Function<T, String> expectedReasonProvider) {
		List<ExpectedUnhandledRecipe<T>> expected = new ArrayList<>();
		List<T> unexpected = new ArrayList<>();
		for (T recipe : recipes) {
			String reason = expectedReasonProvider.apply(recipe);
			if (reason == null || reason.isBlank()) {
				unexpected.add(recipe);
			} else {
				expected.add(new ExpectedUnhandledRecipe<>(recipe, reason));
			}
		}
		return new RecipeDiagnosticResult<>(expected, unexpected);
	}

	static <T> RecipeHandlingResult<T> handleRecipes(List<T> recipes, Predicate<T> handler) {
		//Each mapper pass owns its unhandled list so a mapper that handles nothing cannot contaminate a later pass.
		List<T> unhandled = null;
		int handled = 0;
		for (T recipe : recipes) {
			if (handler.test(recipe)) {
				handled++;
			} else {
				if (unhandled == null) {
					unhandled = new ArrayList<>(recipes.size() - handled);
				}
				unhandled.add(recipe);
			}
		}
		return new RecipeHandlingResult<>(handled, unhandled == null ? List.of() : unhandled);
	}

	record RecipeHandlingResult<T>(int handled, List<T> unhandled) {
	}

	record ExpectedUnhandledRecipe<T>(T recipe, String reason) {
	}

	record RecipeDiagnosticResult<T>(List<ExpectedUnhandledRecipe<T>> expected, List<T> unexpected) {
	}

	@Override
	public String getName() {
		return PEConfigTranslations.MAPPING_CRAFTING_MAPPER.title();
	}

	@Override
	public String getTranslationKey() {
		return PEConfigTranslations.MAPPING_CRAFTING_MAPPER.getTranslationKey();
	}

	@Override
	public String getDescription() {
		return PEConfigTranslations.MAPPING_CRAFTING_MAPPER.tooltip();
	}

	private static class RecipeCountInfo {

		private final RecipeType<?> recipeType;
		private final int totalRecipes;
		private List<RecipeHolder<?>> unhandled;

		private RecipeCountInfo(RecipeType<?> recipeType, int totalRecipes, List<RecipeHolder<?>> unhandled) {
			this.recipeType = recipeType;
			this.totalRecipes = totalRecipes;
			this.unhandled = unhandled;
		}

		public RecipeType<?> getRecipeType() {
			return recipeType;
		}

		public int getTotalRecipes() {
			return totalRecipes;
		}

		public void setUnhandled(List<RecipeHolder<?>> unhandled) {
			this.unhandled = unhandled;
		}

		public List<RecipeHolder<?>> getUnhandled() {
			return unhandled;
		}
	}

	private static class NSSFakeGroupManager implements INSSFakeGroupManager {

		private static final Function<Object2IntMap<NormalizedSimpleStack>, String> MAP_DESCRIPTOR = map -> map.object2IntEntrySet().stream()
				.map(entry -> entry.getKey() + ":" + entry.getIntValue()).collect(Collectors.joining(", "));
		private static final boolean DEBUG_GROUP_CONTENTS = false;

		private final Map<Object2IntMap<NormalizedSimpleStack>, FakeGroupData> ingredientGroupsWithCount = new HashMap<>();
		private final Map<Object2IntMap<NormalizedSimpleStack>, FakeGroupData> groupsWithCount = new HashMap<>();
		private final IMappingCollector<NormalizedSimpleStack, Long> mapper;
		private int fakeIndex;

		public NSSFakeGroupManager(IMappingCollector<NormalizedSimpleStack, Long> mapper) {
			this.mapper = mapper;
		}

		@Override
		public FakeGroupData getOrCreateFakeGroup(Object2IntMap<NormalizedSimpleStack> normalizedSimpleStacks, boolean representsIngredient, boolean skipConversions) {
			return getOrCreateFakeGroup(normalizedSimpleStacks, representsIngredient, skipConversions, Object2IntOpenHashMap::new);
		}

		@Override
		public FakeGroupData getOrCreateFakeGroupDirect(Object2IntMap<NormalizedSimpleStack> normalizedSimpleStacks, boolean representsIngredient, boolean skipConversions) {
			return getOrCreateFakeGroup(normalizedSimpleStacks, representsIngredient, skipConversions, UnaryOperator.identity());
		}

		private FakeGroupData getOrCreateFakeGroup(Object2IntMap<NormalizedSimpleStack> stacks, boolean representsIngredient, boolean skipConversions,
				UnaryOperator<Object2IntMap<NormalizedSimpleStack>> copyFunction) {
			Map<Object2IntMap<NormalizedSimpleStack>, FakeGroupData> groups = representsIngredient ? ingredientGroupsWithCount : groupsWithCount;
			FakeGroupData data = groups.get(stacks);
			if (data == null) {
				//Doesn't exist, create one with the next index add it as known and return
				// the group and the fact that we had to create a representation for it
				String description;
				if (DEBUG_GROUP_CONTENTS) {
					description = MAP_DESCRIPTOR.apply(stacks);
				} else {
					//Note: We use an incrementing index here as our crafting mapper sets a namespace
					// for NSSFake objects, so we can safely use integers as the description and not
					// have to worry about intersecting fake stacks. We also for good measure specify in
					// the IRecipeTypeMapper java docs that if fake stacks are needed by an implementer
					// they should make sure to make the name more complex than just a simple integer to
					// ensure that they do not collide with stacks created by this method.
					description = Integer.toString(fakeIndex++);
				}
				NormalizedSimpleStack dummy = NSSFake.create(description);
				//Note: We put that it wasn't created in the map, so when it is retrieved, we know this wasn't the first time
				groups.put(copyFunction.apply(stacks), new FakeGroupData(dummy, false));
				if (!skipConversions) {
					//Add conversions to the mapper
					if (representsIngredient) {
						//If it represents a combined ingredient, then we need to add a conversion to the dummy ingredient from each base stack
						for (Iterator<Object2IntMap.Entry<NormalizedSimpleStack>> iterator = Object2IntMaps.fastIterator(stacks); iterator.hasNext(); ) {
							Object2IntMap.Entry<NormalizedSimpleStack> entry = iterator.next();
							mapper.addConversion(1, dummy, EMCHelper.intMapOf(entry.getKey(), entry.getIntValue()));
						}
					} else {
						//If it represents a group of ingredients producing the dummy output, just add a conversion for it
						mapper.addConversion(1, dummy, stacks);
					}
				}
				return new FakeGroupData(dummy, true);
			}
			return data;
		}
	}
}
