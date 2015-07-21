package moze_intel.projecte.emc.mappers;

import moze_intel.projecte.emc.IMappingCollector;
import moze_intel.projecte.emc.IngredientMap;
import moze_intel.projecte.emc.NormalizedSimpleStack;

import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.IRecipeHandler;
import codechicken.nei.recipe.TemplateRecipeHandler;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class NEIMapper implements IEMCMapper<NormalizedSimpleStack, Integer>
{
	@Override
	public String getName()
	{
		return "NEIMapper";
	}

	@Override
	public String getDescription()
	{
		return "Reads Recipes from the GUIs that are displayed by NEI. THIS MAPPER DOES NOT WORK DURING STARTUP OR ON SERVERS!\n" +
				"YOU HAVE TO USE /projecte_reloadEMC TO PREGENERATE VALUES IF YOU WANT TO USE THIS!\n" +
				"This will also reveal additional configuration options in this file!";

	}

	@Override
	public boolean isAvailable()
	{
		return getCraftingHandlersFromNEI() != null;
	}

	protected Collection getCraftingHandlersFromNEI() {
		try
		{
			Class clazz = Class.forName("codechicken.nei.recipe.GuiCraftingRecipe");
			Field f = clazz.getDeclaredField("craftinghandlers");
			Object craftinghandlers = f.get(null);
			if (craftinghandlers instanceof Collection) {
				return (Collection)craftinghandlers;
			} else {
				return null;
			}
		} catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void addMappings(IMappingCollector<NormalizedSimpleStack, Integer> mapper, Configuration config)
	{
		try {
			Collection craftinghandlers = getCraftingHandlersFromNEI();
			if (craftinghandlers == null) return;
			System.out.println("NEI has " + craftinghandlers.size() + " CraftingHandlers");
			for (Object o: craftinghandlers) {
				if (o != null && o instanceof IRecipeHandler) {
					IRecipeHandler recipeHandler = (IRecipeHandler)o;
					System.out.println(recipeHandler);
					if (!(recipeHandler instanceof TemplateRecipeHandler)) {
						System.out.println("Not TemplateRecipeHandler - ignoring");
						continue;
					}
					TemplateRecipeHandler trh = (TemplateRecipeHandler)recipeHandler;
					try
					{
						trh.loadCraftingRecipes(trh.getOverlayIdentifier());
					} catch (Exception e) {
						e.printStackTrace();
					}
					RecipeHandlerConfig  recipeHandlerConfig = RecipeHandlerConfig.fromCategory(config, trh);
					if (recipeHandlerConfig.enabled)
					{
						doTemplateRecipeHandler(mapper, recipeHandlerConfig, trh);
					}
				}
			}
		} catch (Exception e) {
			System.out.println("Could not load Recipes from NEI");
			e.printStackTrace();
		}
	}

	protected static String getCategoryForRecipeHandler(TemplateRecipeHandler trh)
	{
		return "recipeHandlers." + trh.getClass().getName().replace('.', '_');
	}

	protected void doTemplateRecipeHandler(IMappingCollector<NormalizedSimpleStack, Integer> mapper, RecipeHandlerConfig config, TemplateRecipeHandler trh)
	{
		try {
			System.out.println(trh.numRecipes());
			for (int recipeNumber = 0; recipeNumber < trh.numRecipes(); recipeNumber++) {
				List<PositionedStack> ingredients = trh.getIngredientStacks(recipeNumber);
				ItemStack outStack = trh.getResultStack(recipeNumber).item;
				IngredientMap<NormalizedSimpleStack> ingredientsNSSMap = new IngredientMap<NormalizedSimpleStack>();
				if (config.log) {
					StringBuilder sb = new StringBuilder();
					for (int slotNum = 0; slotNum < ingredients.size(); slotNum++)
					{
						if (slotNum != 0) sb.append(" + ");
						if (config.skipSlots.contains(slotNum + 1)) sb.append("(");
						if (ingredients.get(slotNum).items.length > 1) sb.append("[");
						sb.append(ingredients.get(slotNum).item);
						if (ingredients.get(slotNum).items.length > 1) sb.append(", ..]");
						if (config.skipSlots.contains(slotNum + 1)) sb.append(")");
					}
					sb.append(" = ").append(outStack);
					System.out.println(sb.toString());
				}
				for (int slotNum = 0; slotNum < ingredients.size(); slotNum++)
				{
					if (config.skipSlots.contains(slotNum + 1)) continue;
					PositionedStack ingredient = ingredients.get(slotNum);
					NormalizedSimpleStack ingredientNSS;
					if (ingredient.items.length == 1) {
						ingredientNSS = NormalizedSimpleStack.getNormalizedSimpleStackFor(ingredient.items[0]);
						ingredientsNSSMap.addIngredient(ingredientNSS, Math.max(ingredient.items[0].stackSize, 1));
					} else {
						ingredientNSS = NormalizedSimpleStack.createGroup(Arrays.asList(ingredient.items));
						Map<NormalizedSimpleStack, Integer> groupCountMap = Maps.newHashMap();
						for (ItemStack itemStack: ingredient.items) {
							groupCountMap.put(NormalizedSimpleStack.getNormalizedSimpleStackFor(itemStack), Math.max(itemStack.stackSize, 1));
							mapper.addConversionMultiple(1, ingredientNSS, groupCountMap);
							groupCountMap.clear();
						}
						ingredientsNSSMap.addIngredient(ingredientNSS, 1);
					}

				}
				if (config.log)
				{
					ingredientsNSSMap.toString();
				}
				mapper.addConversionMultiple(outStack.stackSize*config.outputMultiplier, NormalizedSimpleStack.getNormalizedSimpleStackFor(outStack), ingredientsNSSMap.getMap());
			}
		} catch (Exception e) {
			System.out.println("Could not get Recipes from IRecipeHandler" + trh.toString());
		}
	}


	Map<String, RecipeHandlerConfig> config = Maps.newHashMap();
	protected static class RecipeHandlerConfig
	{
		private RecipeHandlerConfig() {}
		public boolean enabled;
		public Set<Integer> skipSlots;
		public int outputMultiplier;
		public boolean log;
		public static RecipeHandlerConfig fromCategory(Configuration config, TemplateRecipeHandler trh)
		{
			String category = getCategoryForRecipeHandler(trh);
			RecipeHandlerConfig c = new RecipeHandlerConfig();
			c.enabled = config.getBoolean("enabled", category, false,
					String.format("Has %s recipes for \"%s\" with identifier \"%s\"", trh.numRecipes(), trh.getRecipeName(), trh.getOverlayIdentifier())
			);
			c.outputMultiplier = config.getInt("outputMultiplier", category, 1, 1, 128, "Increase the amount gained.");
			c.log = config.getBoolean("logging", category, false, "Enable extensive logging for this RecipeHandler");
			int[] skipSlots = config.get(category, "skipSlots", new int[]{}, "").getIntList();
			c.skipSlots = Sets.newHashSet();
			for (int i = 0; i < skipSlots.length; i++)
			{
				c.skipSlots.add(skipSlots[i]);
			}
			return c;
		}
	}
}
