package mapeper.projecte.neirecipecollector.commands;

import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.IRecipeHandler;
import codechicken.nei.recipe.TemplateRecipeHandler;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import cpw.mods.fml.common.registry.GameRegistry;
import mapeper.projecte.neirecipecollector.ChatUtils;
import mapeper.projecte.neirecipecollector.LowerCasePrefixPredicate;
import mapeper.projecte.neirecipecollector.NEIRecipeCollector;
import mapeper.projecte.neirecipecollector.ProjectENEIRecipeCollector;
import net.minecraft.command.ICommandSender;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import org.apache.logging.log4j.Logger;
import scala.actors.threadpool.Arrays;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class CollectSubCommand implements ISubCommand
{
	private Logger LOGGER = ProjectENEIRecipeCollector.logger;
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
		resetSettings();
		if (params.size() == 0) {
			ChatUtils.addChatError(sender, "Usage: '%s <IRecipeHandler class name> [options]'", previousCommands);
			ChatUtils.addChatError(sender, "Options: ");
			ChatUtils.addChatError(sender, "skip=<a>,<b>,... Skip specific slots");
			ChatUtils.addChatError(sender, "multiplier=<n>   Multiply the output stacksize");
			ChatUtils.addChatError(sender, "some             Show the first 3 recipes");
			ChatUtils.addChatError(sender, "log              Log all recipes");
			ChatUtils.addChatError(sender, "unlocal          Log unique item names instead display name");
			ChatUtils.addChatError(sender, "fulllist         Don't abbreviate ingredient options");
			return;
		}

		String className = params.get(0);
		TemplateRecipeHandler handler = NEIRecipeCollector.getInstance().getCraftingHandlersForLowerCaseName().get(className.toLowerCase());
		if (handler == null) {
			ChatUtils.addChatError(sender, "Could not load IRecipeHandler with Classname: %s", className);
			return;
		}

		if (handler.numRecipes() == 0)
		{
			try
			{
				handler.loadCraftingRecipes(handler.getOverlayIdentifier());
			} catch (Exception e)
			{
				e.printStackTrace();
				ChatUtils.addChatError(sender, "Error on loadCraftingRecipes: %s", e);
				return;
			}
		} else {
			LOGGER.info("Not loading Crafting Recipes for Handler because it already has %d recipes loaded", handler.numRecipes());
		}
		int numRecipes = handler.numRecipes();
		ChatUtils.addChatMessage(sender, "Found IRecipeHandler: %s", handler.getClass().getName());
		ChatUtils.addChatMessage(sender, "Recipe Name: %s", handler.getRecipeName());
		ChatUtils.addChatMessage(sender, "Recipe Count: %d", numRecipes);

		boolean log = false;
		boolean some = false;

		Set<Integer> skip = Sets.newHashSet();
		int multiplier = 1;
		for (String s: params.subList(1, params.size())) {
			if (s.equals("some")) {
				some = true;
			} else if (s.equals("log")) {
				log = true;
			} else if (s.equals("fulllist")) {
				fulllists = true;
			} else if (s.equals("unlocal")) {
				unlocal = true;
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
			//TODO Options: Searching OreDict instead of Fake Items (Enable to use OD for groups and single-items separate)
			//TODO Option to set ingredient and or output stacksize to a specific value (For Potions to turn 1+1=1 into 1+3=3
		}

		for (int i = 0; i < numRecipes; i++) {
			List<PositionedStack> ingredients = handler.getIngredientStacks(i);
			PositionedStack outStack = handler.getResultStack(i);
			if (some && i < 3) {
				ChatUtils.addChatMessage(sender, listOfPositionedStacksToString(ingredients, outStack, skip));
			}
			if (log) {
				LOGGER.info(listOfPositionedStacksToString(ingredients, outStack, skip));
			}
		}
 	}

	void resetSettings() {
		fulllists = false;
		unlocal = false;

	}

	boolean unlocal = false;
	boolean fulllists = false;

	private String itemToString(ItemStack itemStack) {
		if (unlocal) {
			return String.format("%d*%s|%s", itemStack.stackSize, GameRegistry.findUniqueIdentifierFor(itemStack.getItem()).toString(), itemStack.getItemDamage());
		}
		return String.format("%d*'%s'", itemStack.stackSize, itemStack.getDisplayName());
	}

	private String listOfPositionedStacksToString(List<PositionedStack> stacks, PositionedStack outStack, Set<Integer> skipSlots) {
		StringBuilder sb = new StringBuilder();
		for (int slotNum = 0; slotNum < stacks.size(); slotNum++)
		{
			if (slotNum != 0) sb.append(" + ");
			if (skipSlots.contains(slotNum)) sb.append("(");
			if (stacks.get(slotNum).items.length > 1) sb.append("[");
			Iterator<ItemStack> iterator = Arrays.asList(stacks.get(slotNum).items).iterator();
			if (iterator.hasNext())
			{
				sb.append(itemToString(iterator.next()));
				if (fulllists)
				{
					while (iterator.hasNext())
					{
						sb.append(", ").append(itemToString(iterator.next()));
					}
				}
				else if (iterator.hasNext())
				{
					sb.append(", ..");
				}
			} else {
				sb.append('-');
			}
			if (stacks.get(slotNum).items.length > 1) sb.append(']');
			if (skipSlots.contains(slotNum)) sb.append(")");
		}
		sb.append(" = ").append(itemToString(outStack.item));
		return sb.toString();
	}

}
