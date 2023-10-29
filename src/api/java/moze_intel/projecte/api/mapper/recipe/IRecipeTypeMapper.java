package moze_intel.projecte.api.mapper.recipe;

import moze_intel.projecte.api.mapper.collector.IMappingCollector;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;

/**
 * Interface for Classes that want to make Contributions to the EMC Mapping via the CraftingMapper.
 */
public interface IRecipeTypeMapper {

	/**
	 * A unique Name for the {@link IRecipeTypeMapper}. This is used to identify the {@link IRecipeTypeMapper} in the Configuration.
	 *
	 * @return A unique Name
	 */
	String getName();

	/**
	 * A Description, that will be included as a Comment in the Configuration File
	 *
	 * @return A <b>short</b> description
	 */
	String getDescription();

	/**
	 * This method is used to determine the default for enabling/disabling this {@link IRecipeTypeMapper}. If this returns {@code false} {@link #canHandle(RecipeType)}
	 * and {@link #handleRecipe(IMappingCollector, Recipe, RegistryAccess, INSSFakeGroupManager)} will not be called.
	 *
	 * @return {@code true} if you want {@link #canHandle(RecipeType)} and {@link #handleRecipe(IMappingCollector, Recipe, RegistryAccess, INSSFakeGroupManager)} to be
	 * called, {@code false} otherwise.
	 */
	default boolean isAvailable() {
		return true;
	}

	/**
	 * Checks if this {@link IRecipeTypeMapper} can handle the given recipe type.
	 *
	 * @param recipeType The {@link RecipeType} to check.
	 *
	 * @return {@code true} if this {@link IRecipeTypeMapper} can handle the given {@link RecipeType}, {@code false} otherwise.
	 */
	boolean canHandle(RecipeType<?> recipeType);

	/**
	 * Attempts to handle a {@link Recipe} that is of a type restricted by {@link #canHandle(RecipeType)}.
	 *
	 * @param mapper           The mapper to add mapping data to.
	 * @param recipe           The recipe to attempt to map.
	 * @param registryAccess   Registry access for use in getting recipe outputs and the like
	 * @param fakeGroupManager The manager for helping create and manage "groupings" of valid ingredients.
	 *
	 * @return {@code true} if the {@link IRecipeTypeMapper} handled the given {@link Recipe}, {@code false} otherwise
	 *
	 * @apiNote Make sure to call {@link #canHandle(RecipeType)} before calling this method.
	 * @implNote Due to how the fakeGroupManager works, {@link moze_intel.projecte.api.nss.NSSFake} implementations should only be created in this method with
	 * descriptions that are more complex than a single integer, as otherwise they may intersect with {@link NormalizedSimpleStack}s created by the fakeGroupManager.
	 */
	boolean handleRecipe(IMappingCollector<NormalizedSimpleStack, Long> mapper, Recipe<?> recipe, RegistryAccess registryAccess, INSSFakeGroupManager fakeGroupManager);
}