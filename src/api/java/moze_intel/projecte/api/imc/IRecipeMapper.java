package moze_intel.projecte.api.imc;

import java.util.function.Predicate;
import net.minecraft.item.crafting.IRecipe;

public class IRecipeMapper {

	private final String name;
	private final String description;
	private final Predicate<IRecipe> canHandle;

	/**
	 * @param name        The name this IRecipeMapper will have as a sub type of the CraftingMapper
	 * @param description The description this IRecipeMapper will have as a sub type of the CraftingMapper
	 * @param canHandle   A predicate that checks if this IRecipeMapper can handle a specific IRecipe type.
	 */
	public IRecipeMapper(String name, String description, Predicate<IRecipe> canHandle) {
		this.name = name;
		this.description = description;
		this.canHandle = canHandle;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public boolean canHandle(IRecipe recipe) {
		return canHandle.test(recipe);
	}
}