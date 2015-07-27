package mapeper.projecte.neirecipecollector.commands;

import codechicken.nei.recipe.IRecipeHandler;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import mapeper.projecte.neirecipecollector.ChatUtils;
import mapeper.projecte.neirecipecollector.LowerCasePrefixPredicate;
import mapeper.projecte.neirecipecollector.NEIRecipeCollector;
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
			ChatUtils.addChatError(sender, "Usage: '%s <IRecipeHandler class name> [options]'", previousCommands);
			ChatUtils.addChatError(sender, "Options: ");
			ChatUtils.addChatError(sender, "skip=<a>,<b>,... Skip specific slots");
			ChatUtils.addChatError(sender, "multiplier=<n>   Multiply the output stacksize");
			ChatUtils.addChatError(sender, "some             Show the first 3 recipes");
			ChatUtils.addChatError(sender, "log              Log all recipes");
			return;
		}

		String className = params.get(0);
		IRecipeHandler handler = NEIRecipeCollector.getInstance().getCraftingHandlersForLowerCaseName().get(className.toLowerCase());
		if (handler != null) {
			ChatUtils.addChatMessage(sender, "Found IRecipeHandler: %s", handler.getClass().getName());
			ChatUtils.addChatMessage(sender, "Recipe Name: %s", handler.getRecipeName());
			ChatUtils.addChatMessage(sender, "Recipe Count: %d", handler.numRecipes());
		} else {
			ChatUtils.addChatError(sender, "Could not load IRecipeHandler with Classname: %s", className);
			return;
		}

		boolean log = false;
		boolean some = false;
		List<Integer> skip = Lists.newArrayList();
		int multiplier = 1;
		for (String s: params.subList(1, params.size())) {
			if (s.equals("some")) {
				some = true;
			} else if (s.equals("log")) {
				log = true;
			} else if (s.startsWith("skip=")) {
				for (String n : Splitter.on(',').omitEmptyStrings().split(s.substring(5)))
				{
					try
					{
						int i = Integer.parseInt(n);
						if (i < 0) throw new NumberFormatException("Only >= 0 allowed");
						skip.add(i);
					} catch (NumberFormatException e) {
						ChatUtils.addChatError(sender, "Could not parse skip option '%s' as positive int", n);
						e.printStackTrace();
						return;
					}
				}
			} else if (s.startsWith("multiplier=")) {
				String m = s.substring("multiplier=".length());
				try {
					int i = Integer.parseInt(m);
					if (i <= 0) throw new NumberFormatException("Only > 0 allowed");
				} catch (NumberFormatException e) {
					ChatUtils.addChatError(sender, "Could not parse multiplier option '%s' as positive int", m);
					e.printStackTrace();
					return;
				}
			} else {
				ChatUtils.addChatError(sender, "Unknown option '%s'", s);
				return;
			}
		}
	}


}
