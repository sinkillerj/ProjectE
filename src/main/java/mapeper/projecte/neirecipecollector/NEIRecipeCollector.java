package mapeper.projecte.neirecipecollector;

import codechicken.nei.recipe.IRecipeHandler;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

public class NEIRecipeCollector
{
	private static NEIRecipeCollector instance;
	public static NEIRecipeCollector getInstance() {
		if (instance != null) return instance;
		return (instance = new NEIRecipeCollector());
	}
	private NEIRecipeCollector() {}

	Collection<IRecipeHandler> craftingHandlers;
	public Collection<IRecipeHandler> getCraftingHandlers() {
		if (craftingHandlers != null) return craftingHandlers;
		return (craftingHandlers = getCraftingHandlersFromNEI());
	}

	Map<String, IRecipeHandler> craftingHandlersForName;
	public Map<String, IRecipeHandler> getCraftingHandlersForName() {
		if (craftingHandlersForName != null) return craftingHandlersForName;
		return craftingHandlersForName = Maps.uniqueIndex(getCraftingHandlers(), new Function<IRecipeHandler, String>()
		{
			@Nullable
			@Override
			public String apply(@Nullable IRecipeHandler input)
			{
				return input.getClass().getName();
			}
		});
	}
	Map<String, IRecipeHandler> craftingHandlersForLowerCaseName;
	public Map<String, IRecipeHandler> getCraftingHandlersForLowerCaseName() {
		if (craftingHandlersForLowerCaseName != null) return craftingHandlersForLowerCaseName;
		return craftingHandlersForLowerCaseName = Maps.uniqueIndex(getCraftingHandlers(), new Function<IRecipeHandler, String>()
		{
			@Nullable
			@Override
			public String apply(@Nullable IRecipeHandler input)
			{
				return input.getClass().getName().toLowerCase();
			}
		});
	}

	private Collection<IRecipeHandler> getCraftingHandlersFromNEI() {
		try
		{
			Class clazz = Class.forName("codechicken.nei.recipe.GuiCraftingRecipe");
			Field f = clazz.getDeclaredField("craftinghandlers");
			Object craftingHandlers = f.get(null);
			if (craftingHandlers instanceof Collection) {
				return (Collection<IRecipeHandler>)Lists.newArrayList(Iterables.filter((Collection) craftingHandlers, new Predicate()
				{
					@Override
					public boolean apply(@Nullable Object input)
					{
						return input instanceof IRecipeHandler;
					}
				}));
			}
			throw new IllegalStateException("Could not get crafting handlers from NEI!");
		} catch (Exception e)
		{
			throw new IllegalStateException("Could not get crafting handlers from NEI!", e);
		}
	}
}
