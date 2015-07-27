package mapeper.projecte.neirecipecollector;

import codechicken.nei.recipe.IRecipeHandler;
import net.minecraft.command.ICommandSender;

import java.util.List;

public class CollectSubCommand implements ISubCommand
{
	@Override
	public String getCommandName()
	{
		return "collect";
	}

	@Override
	public List<String> addTabCompletionOptions(List<String> params)
	{
		if (params.size() == 1)
		{
			return LowerCasePrefixPredicate.autocompletionOptions(NEIRecipeCollector.getInstance().getCraftingHandlersForName().keySet(), params.get(0));
		}
		return null;
	}

	@Override
	public void processCommand(String previousCommands, ICommandSender sender, List<String> params)
	{
		if (params.size() == 0) {
			ChatUtils.addChatError(sender, "Usage: '%s [IRecipeHandler class name]'", previousCommands);
		}

		String className = params.get(0);
		IRecipeHandler handler = NEIRecipeCollector.getInstance().getCraftingHandlersForLowerCaseName().get(className.toLowerCase());
		if (handler != null) {
			ChatUtils.addChatMessage(sender, "Found IRecipeHandler: %s", handler.getClass().getName());
			ChatUtils.addChatMessage(sender, "Recipe Name: %s", handler.getRecipeName());
			ChatUtils.addChatMessage(sender, "Recipe Count: %d", handler.numRecipes());
		} else {
			ChatUtils.addChatError(sender, "Could not load IRecipeHandler with Classname: %s", className);
		}
	}


}
