package mapeper.projecte.neirecipecollector;

import moze_intel.projecte.emc.mappers.customConversions.json.ConversionGroup;
import moze_intel.projecte.emc.mappers.customConversions.json.CustomConversionFile;

import codechicken.nei.recipe.TemplateRecipeHandler;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class NEIRecipeCollector
{
	private static NEIRecipeCollector instance;
	public static NEIRecipeCollector getInstance() {
		if (instance != null) return instance;
		return (instance = new NEIRecipeCollector());
	}
	private NEIRecipeCollector() {}

	Collection<TemplateRecipeHandler> craftingHandlers;
	public Collection<TemplateRecipeHandler> getCraftingHandlers() {
		if (craftingHandlers != null) return craftingHandlers;
		return (craftingHandlers = getCraftingHandlersFromNEI());
	}

	Map<String, TemplateRecipeHandler> craftingHandlersForName;
	public Map<String, TemplateRecipeHandler> getCraftingHandlersForName() {
		if (craftingHandlersForName != null) return craftingHandlersForName;
		return craftingHandlersForName = Maps.uniqueIndex(getCraftingHandlers(), new Function<TemplateRecipeHandler, String>()
		{
			@Nullable
			@Override
			public String apply(@Nullable TemplateRecipeHandler input)
			{
				return input.getClass().getName();
			}
		});
	}
	Map<String, TemplateRecipeHandler> craftingHandlersForLowerCaseName;
	public Map<String, TemplateRecipeHandler> getCraftingHandlersForLowerCaseName() {
		if (craftingHandlersForLowerCaseName != null) return craftingHandlersForLowerCaseName;
		return craftingHandlersForLowerCaseName = Maps.uniqueIndex(getCraftingHandlers(), new Function<TemplateRecipeHandler, String>()
		{
			@Nullable
			@Override
			public String apply(@Nullable TemplateRecipeHandler input)
			{
				return input.getClass().getName().toLowerCase();
			}
		});
	}

	private Collection<TemplateRecipeHandler> getCraftingHandlersFromNEI() {
		try
		{
			Class clazz = Class.forName("codechicken.nei.recipe.GuiCraftingRecipe");
			Field f = clazz.getDeclaredField("craftinghandlers");
			Object craftingHandlers = f.get(null);
			if (craftingHandlers instanceof Collection) {
				return (Collection<TemplateRecipeHandler>)Lists.newArrayList(Iterables.filter((Collection) craftingHandlers, new Predicate()
				{
					@Override
					public boolean apply(@Nullable Object input)
					{
						return input instanceof TemplateRecipeHandler;
					}
				}));
			}
			throw new IllegalStateException("Could not get crafting handlers from NEI!");
		} catch (Exception e)
		{
			throw new IllegalStateException("Could not get crafting handlers from NEI!", e);
		}
	}


	private CustomConversionFile bufferFile = new CustomConversionFile();
	public void addConversionGroupToBuffer(String groupname, ConversionGroup group) {
		bufferFile.groups.put(groupname, group);
	}
	public void clearBuffer() {
		bufferFile = new CustomConversionFile();
		comments.clear();
	}
	public CustomConversionFile getBufferFile() {
		bufferFile.comment = Joiner.on("\n").join(comments);
		return bufferFile;
	}
	List<String> comments = Lists.newArrayList();
	public void addCommentLine(String comment) {
		comments.add(comment);
	}
}
