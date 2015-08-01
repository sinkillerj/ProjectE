package mapeper.projecte.neirecipecollector.commands;

import moze_intel.projecte.emc.NormalizedSimpleStack;
import moze_intel.projecte.emc.mappers.customConversions.json.ConversionGroup;
import moze_intel.projecte.emc.mappers.customConversions.json.CustomConversion;
import moze_intel.projecte.utils.PELogger;

import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import cpw.mods.fml.common.registry.GameRegistry;
import mapeper.projecte.neirecipecollector.ChatUtils;
import moze_intel.projecte.utils.LowerCasePrefixPredicate;
import mapeper.projecte.neirecipecollector.NEIRecipeCollector;
import mapeper.projecte.neirecipecollector.OreDictSearcher;
import net.minecraft.command.ICommandSender;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import org.apache.logging.log4j.Logger;
import scala.actors.threadpool.Arrays;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CollectSubCommand implements ISubCommand
{
	private Logger LOGGER = PELogger.logger;
	@Override
	public String getCommandName()
	{
		return "collect";
	}
	Pattern overwriteOrMultiplyPattern = Pattern.compile("^s(\\d+|o)(\\=|\\*)(\\d+)$");
	Pattern overwriteOrMultiplyPatternWithoutOperator = Pattern.compile("^s(\\d+|o)$");

	List<String> otherOptions = Lists.newArrayList("skip=", "oredict", "some", "log", "unlocal", "fulllist", "so", "s0", "s1", "s2", "s3", "s4", "s5", "s6", "s7", "s8", "s9");
	@Override
	public List<String> addTabCompletionOptions(List<String> params)
	{
		if (params.size() == 1)
		{
			return LowerCasePrefixPredicate.autocompletionOptions(NEIRecipeCollector.getInstance().getCraftingHandlersForName().keySet(), params.get(0));
		} else if (params.size() > 1) {
			String lastParam = params.get(params.size() - 1);
			if (overwriteOrMultiplyPatternWithoutOperator.matcher(lastParam).matches()) {
				return Lists.newArrayList(lastParam + "*", lastParam + "=");
			}
			return LowerCasePrefixPredicate.autocompletionOptions(otherOptions, lastParam);
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
			ChatUtils.addChatError(sender, "oredict          try to find oredictionary replacements for all the ingredients");
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



		for (String s: params.subList(1, params.size())) {
			Matcher stacksizeOperandMatcher = overwriteOrMultiplyPattern.matcher(s);

			if (s.equals("some")) {
				some = true;
			} else if (s.equals("log")) {
				log = true;
			} else if (s.equals("fulllist")) {
				fulllists = true;
			} else if (s.equals("unlocal"))
			{
				unlocal = true;
			} else if (s.equals("oredict")) {
				tryToFindOreDict = true;
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
		}


		ConversionGroup group = new ConversionGroup();
		String thisCommand = previousCommands + " " + Joiner.on(" ").join(params);
		group.comment = "Generated with " + thisCommand;
		NEIRecipeCollector.getInstance().addCommentLine(thisCommand);
		for (int i = 0; i < numRecipes; i++) {
			List<PositionedStack> ingredients = handler.getIngredientStacks(i);
			PositionedStack outStack = handler.getResultStack(i);
			if (some && i < 3) {
				ChatUtils.addChatMessage(sender, listOfPositionedStacksToStringForChat(ingredients, outStack, skip));
			}
			if (log) {
				LOGGER.info(listOfPositionedStacksToStringForLog(ingredients, outStack, skip));
			}
			Collection<CustomConversion> conversions = createCustomConversionsFromNEI(ingredients, outStack);
			group.conversions.addAll(conversions);
		}
		NEIRecipeCollector.getInstance().addConversionGroupToBuffer(className, group);
		ChatUtils.addChatMessage(sender, "Wrote Conversions for %s to Buffer file. Save buffer with '/%s store'", className, NEIRecipeCollectorCommand.COMMANDNAME);
 	}

	private Collection<CustomConversion> createCustomConversionsFromNEI(List<PositionedStack> ingredients, PositionedStack outStack)
	{
		List<CustomConversion> conversions = Lists.newArrayList();
		CustomConversion conversion = new CustomConversion();
		conversions.add(conversion);
		conversion.output = NormalizedSimpleStack.getFor(outStack.item).json();
		conversion.count  = outStack.item.stackSize;
		conversion.ingredients = Maps.newHashMap();
		for (int slotNum = 0; slotNum < ingredients.size(); slotNum++) {
			String oreDict = null;
			PositionedStack positionedStack = ingredients.get(slotNum);
			NormalizedSimpleStack stack;
			if (tryToFindOreDict) {
				oreDict = OreDictSearcher.tryToFindOreDict(positionedStack.items);
			}
			if (oreDict != null) {
				stack = NormalizedSimpleStack.forOreDictionary(oreDict);
			} else {
				if (positionedStack.items.length > 1) {
					stack = NormalizedSimpleStack.createFake(positionedStack.items.toString());
					for (ItemStack itemStack: positionedStack.items) {
						conversions.add(CustomConversion.getFor(stack, NormalizedSimpleStack.getFor(itemStack)));
					}
				} else {
					stack = NormalizedSimpleStack.getFor(positionedStack.item);
				}
			}

			int stacksize = positionedStack.item.stackSize;
			if (setStacksizeForSlot.containsKey(slotNum)) {
				stacksize = setStacksizeForSlot.get(slotNum);
			}
			if (multiplyStacksizeForSlot.containsKey(slotNum)) {
				stacksize *= setStacksizeForSlot.get(slotNum);
			}

			conversion.ingredients.put(stack.json(), stacksize);

		}
		return conversions;
	}

	void resetSettings() {
		fulllists = false;
		unlocal = false;
		tryToFindOreDict = false;
		setStacksizeForSlot.clear();
		multiplyStacksizeForSlot.clear();
	}

	Map<Integer, Integer> setStacksizeForSlot = Maps.newHashMap();
	Map<Integer, Integer> multiplyStacksizeForSlot = Maps.newHashMap();
	boolean unlocal = false;
	boolean fulllists = false;
	boolean tryToFindOreDict = false;

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

	private String listOfPositionedStacksToStringForChat(List<PositionedStack> stacks, PositionedStack outStack, Set<Integer> skipSlots) {
		return listOfPositionedStacksToString(stacks, outStack, skipSlots, EnumChatFormatting.STRIKETHROUGH.toString(), EnumChatFormatting.RESET.toString());
	}

	private String listOfPositionedStacksToStringForLog(List<PositionedStack> stacks, PositionedStack outStack, Set<Integer> skipSlots) {
		return listOfPositionedStacksToString(stacks, outStack, skipSlots, "(", ")");
	}

	private String listOfPositionedStacksToString(List<PositionedStack> stacks, PositionedStack outStack, Set<Integer> skipSlots, String skipStart, String skipEnd) {
		StringBuilder sb = new StringBuilder();
		for (int slotNum = 0; slotNum < stacks.size(); slotNum++)
		{
			if (slotNum != 0) sb.append(" + ");
			if (skipSlots.contains(slotNum)) sb.append(skipStart);
			if (stacks.get(slotNum).items.length > 1) sb.append("[");
			Iterator<ItemStack> iterator = Arrays.asList(stacks.get(slotNum).items).iterator();
			if (iterator.hasNext())
			{
				String oreDict = null;
				if (tryToFindOreDict) {
					oreDict = OreDictSearcher.tryToFindOreDict(stacks.get(slotNum).items);
				}
				if (oreDict != null) {
					sb.append("OD:").append(oreDict);
				} else
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
				}
			} else {
				sb.append('-');
			}
			if (stacks.get(slotNum).items.length > 1) sb.append(']');
			if (skipSlots.contains(slotNum)) sb.append(skipEnd);
		}
		sb.append(" = ").append(itemToStringWithStacksize(outStack.item, -1));
		return sb.toString();
	}

}
