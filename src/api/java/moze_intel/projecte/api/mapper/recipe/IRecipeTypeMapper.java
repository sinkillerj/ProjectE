package moze_intel.projecte.api.mapper.recipe;

import moze_intel.projecte.api.mapper.collector.IMappingCollector;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;

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
	 * This method is used to determine the default for enabling/disabling this {@link IRecipeTypeMapper}. If this returns {@code false} {@link #canHandle(IRecipeType)}
	 * and {@link #handleRecipe(IMappingCollector, IRecipe)} will not be called.
	 *
	 * @return {@code true} if you want {@link #canHandle(IRecipeType)} and {@link #handleRecipe(IMappingCollector, IRecipe)} to be called, {@code false} otherwise.
	 */
	default boolean isAvailable() {
		return true;
	}

	/**
	 * Checks if this {@link IRecipeTypeMapper} can handle the given recipe type.
	 *
	 * @param recipeType The {@link IRecipeType} to check.
	 *
	 * @return {@code true} if this {@link IRecipeTypeMapper} can handle the given {@link IRecipeType}, {@code false} otherwise.
	 */
	boolean canHandle(IRecipeType<?> recipeType);

	/**
	 * Attempts to handle an {@link IRecipe} that is of a type restricted by {@link #canHandle(IRecipeType)}.
	 *
	 * @param mapper The mapper to add mapping data to.
	 * @param recipe The recipe to attempt to map.
	 *
	 * @return {@code true} if the {@link IRecipeTypeMapper} handled the given {@link IRecipe}, {@code false} otherwise
	 *
	 * @apiNote Make sure to call {@link #canHandle(IRecipeType)} before calling this method.
	 */
	boolean handleRecipe(IMappingCollector<NormalizedSimpleStack, Long> mapper, IRecipe<?> recipe);
}