package moze_intel.projecte.integration.NEI;

import codechicken.nei.NEIServerUtils;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.ShapedRecipeHandler;
import codechicken.nei.recipe.TemplateRecipeHandler;
import moze_intel.projecte.gameObjs.ObjHandler;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.oredict.OreDictionary;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


public class NEIPhiloSmeltingHandler extends ShapedRecipeHandler
{
	private static String name = StatCollector.translateToLocal("pe.nei.philo");

	private static String id = "philoSmelting";

	public int[][] stackorder = new int[][]{
			{0, 0},
			{1, 0},
			{0, 1},
			{1, 1},
			{0, 2},
			{1, 2},
			{2, 0},
			{2, 1},
			{2, 2}};

	public class CachedPhiloSmelting extends CachedRecipe
	{
		public CachedPhiloSmelting()
		{
			ingredients = new ArrayList<PositionedStack>();
		}

		public CachedPhiloSmelting(ItemStack output)
		{
			this();
			setResult(output);
		}

		public CachedPhiloSmelting(Object[] input, ItemStack output)
		{
			this(Arrays.asList(input), output);
		}

		public CachedPhiloSmelting(List<?> input, ItemStack output)
		{
			this(output);
			setIngredients(input);
		}

		public void setIngredients(List<?> items)
		{
			ingredients.clear();
			for (int ingred = 0; ingred < items.size(); ingred++)
			{
				PositionedStack stack = new PositionedStack(items.get(ingred), 25 + stackorder[ingred][0] * 18, 6 + stackorder[ingred][1] * 18);
				stack.setMaxSize(1);
				ingredients.add(stack);
			}
		}

		public void setResult(ItemStack output)
		{
			result = new PositionedStack(output, 119, 24);
		}

		@Override
		public List<PositionedStack> getIngredients()
		{
			return getCycledIngredients(cycleticks / 20, ingredients);
		}

		@Override
		public PositionedStack getResult()
		{
			return result;
		}

		public ArrayList<PositionedStack> ingredients;
		public PositionedStack result;
	}

	public String getRecipeName()
	{
		return name;
	}

	@Override
	public void loadCraftingRecipes(String outputId, Object... results)
	{
		if (outputId.equals(id) && getClass() == NEIPhiloSmeltingHandler.class)
		{
			for (Map.Entry<ItemStack[], ItemStack> entry : ObjHandler.MAP.entrySet())
			{
				if (entry != null)
				{
					List<ItemStack> ingList = new ArrayList<ItemStack>();

					ingList.add(new ItemStack(ObjHandler.philosStone));
					for (int i = 0; i < 7; i++)
					{
						ingList.add(entry.getKey()[0]);
					}
					ingList.add(entry.getKey()[1]);

					arecipes.add(new CachedPhiloSmelting(ingList, entry.getValue()));
				}
			}
		} else
		{
			super.loadCraftingRecipes(outputId, results);
		}
	}

	@Override
	public void loadCraftingRecipes(ItemStack result)
	{
		for (Map.Entry<ItemStack[], ItemStack> entry : ObjHandler.MAP.entrySet())
		{
			if (NEIServerUtils.areStacksSameTypeCrafting(entry.getValue(), result))
			{
				if (entry != null && entry.getValue() != null)
				{
					List<ItemStack> ingList = new ArrayList<ItemStack>();

					ingList.add(new ItemStack(ObjHandler.philosStone));
					for (int i = 0; i < 7; i++)
					{
						ingList.add(entry.getKey()[0]);
					}
					ingList.add(entry.getKey()[1]);

					arecipes.add(new CachedPhiloSmelting(ingList, entry.getValue()));
				}
			}
		}
	}


	@Override
	public void loadUsageRecipes(ItemStack ingredient)
	{
		for (Map.Entry<ItemStack[], ItemStack> entry : ObjHandler.MAP.entrySet())
		{
			if (NEIServerUtils.areStacksSameTypeCrafting(entry.getKey()[0], ingredient) || (NEIServerUtils.areStacksSameTypeCrafting(new ItemStack(ObjHandler.philosStone), ingredient)))
			{
				if (entry != null)
				{
					List<ItemStack> ingList = new ArrayList<ItemStack>();

					ingList.add(new ItemStack(ObjHandler.philosStone));
					for (int i = 0; i < 7; i++)
					{
						ingList.add(entry.getKey()[0]);
					}
					ingList.add(entry.getKey()[1]);

					arecipes.add(new CachedPhiloSmelting(ingList, entry.getValue()));
				}
			}
		}
	}

	@Override
	public boolean isRecipe2x2(int recipe)
	{
		return getIngredientStacks(recipe).size() <= 4;
	}

	@Override
	public void loadTransferRects()
	{
		this.transferRects.add(new TemplateRecipeHandler.RecipeTransferRect(new Rectangle(83, 23, 25, 10), id));
	}
}