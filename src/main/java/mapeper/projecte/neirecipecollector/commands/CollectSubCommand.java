package mapeper.projecte.neirecipecollector.commands;

import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;
import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import cpw.mods.fml.common.registry.GameRegistry;
import mapeper.projecte.neirecipecollector.ChatUtils;
import mapeper.projecte.neirecipecollector.LowerCasePrefixPredicate;
import mapeper.projecte.neirecipecollector.NEIRecipeCollector;
import mapeper.projecte.neirecipecollector.ProjectENEIRecipeCollector;
import net.minecraft.command.ICommandSender;
import net.minecraft.item.ItemStack;
import org.apache.logging.log4j.Logger;
import scala.actors.threadpool.Arrays;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
			ChatUtils.addChatError(sender, "s<slotnum or o>=<n>   overwrite stacksize of slot in recipe (number from 0 to n or 'o' for output)");
			ChatUtils.addChatError(sender, "s<slotnum or o>*<n>   multiply stacksize of slot in recipe (number from 0 to n or 'o' for output)");
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

		Pattern overwriteOrMultiplyPattern = Pattern.compile("^s(\\d+|o)(\\=|\\*)(\\d+)$");

		int multiplier = 1;
		for (String s: params.subList(1, params.size())) {
			Matcher stacksizeOperandMatcher = overwriteOrMultiplyPattern.matcher(s);

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
			} else if (stacksizeOperandMatcher.matches()) {
				String slot = stacksizeOperandMatcher.group(1);
				int slotNum = 0;
				String operand = stacksizeOperandMatcher.group(2);
				String number = stacksizeOperandMatcher.group(3);
				if (slot.equals("o")) {
					slotNum = -1;
				} else {
					slotNum = Integer.parseInt(slot);
				}
				int n = Integer.parseInt(number);
				Map<Integer, Integer> map;
				if (operand.equals("=")) map = setStacksizeForSlot;
				else if (operand.equals("*")) map = multiplyStacksizeForSlot;
				else {
					ChatUtils.addChatError(sender, "Cannot parse Operand in %s", s);
					return;
				}
				map.put(slotNum, n);
			} else {
				ChatUtils.addChatError(sender, "Unknown option '%s'", s);
				return;
			}
			//TODO Options: Searching OreDict instead of Fake Items (Enable to use OD for groups and single-items separate)
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
		setStacksizeForSlot.clear();
		multiplyStacksizeForSlot.clear();
	}

	Map<Integer, Integer> setStacksizeForSlot = Maps.newHashMap();
	Map<Integer, Integer> multiplyStacksizeForSlot = Maps.newHashMap();
	boolean unlocal = false;
	boolean fulllists = false;

	private String itemToStringWithStacksize(ItemStack itemStack, int slotNum) {
		boolean changedStackSize = false;
		int stacksize = itemStack.stackSize;
		if (setStacksizeForSlot.containsKey(slotNum)) {
			changedStackSize = true;
			stacksize = setStacksizeForSlot.get(slotNum);
		}
		if (multiplyStacksizeForSlot.containsKey(slotNum)) {
			changedStackSize = true;
			stacksize *= setStacksizeForSlot.get(slotNum);
		}
		if (changedStackSize) {
			return String.format("(%d*)%d*%s", itemStack.stackSize, stacksize, itemToString(itemStack));
		} else {
			return String.format("%d*%s", itemStack.stackSize, itemToString(itemStack));
		}
	}

	private String itemToString(ItemStack itemStack) {
		if (unlocal) {
			return String.format("%s|%s", GameRegistry.findUniqueIdentifierFor(itemStack.getItem()).toString(), itemStack.getItemDamage());
		}
		return String.format("'%s'", itemStack.getDisplayName());
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
				sb.append(itemToStringWithStacksize(iterator.next(), slotNum));
				if (fulllists)
				{
					while (iterator.hasNext())
					{
						sb.append(", ").append(itemToStringWithStacksize(iterator.next(), slotNum));
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
		sb.append(" = ").append(itemToStringWithStacksize(outStack.item, -1));
		return sb.toString();
	}

}
