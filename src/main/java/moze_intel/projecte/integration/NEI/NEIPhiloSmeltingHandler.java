package moze_intel.projecte.integration.NEI;

import codechicken.nei.NEIServerUtils;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.ShapedRecipeHandler;
import codechicken.nei.recipe.TemplateRecipeHandler;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.customRecipes.RecipeShapelessHidden;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.StatCollector;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


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
			ingredients = new ArrayList<>();
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
			List<IRecipe> allrecipes = CraftingManager.getInstance().getRecipeList();
			for (IRecipe irecipe : allrecipes)
			{
				if (irecipe instanceof RecipeShapelessHidden && irecipe.getRecipeOutput().getItem() != ObjHandler.kleinStars)
				{
					arecipes.add(new CachedPhiloSmelting(((RecipeShapelessHidden) irecipe).getInput(), irecipe.getRecipeOutput()));
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
		List<IRecipe> allrecipes = CraftingManager.getInstance().getRecipeList();
		for (IRecipe irecipe : allrecipes)
		{
			if (NEIServerUtils.areStacksSameTypeCrafting(irecipe.getRecipeOutput(), result))
			{
				if (irecipe instanceof RecipeShapelessHidden && irecipe.getRecipeOutput().getItem() != ObjHandler.kleinStars)
				{
					arecipes.add(new CachedPhiloSmelting(((RecipeShapelessHidden) irecipe).getInput(), irecipe.getRecipeOutput()));
				}
			}
		}
	}


	@Override
	public void loadUsageRecipes(ItemStack ingredient)
	{
		List<IRecipe> allrecipes = CraftingManager.getInstance().getRecipeList();
		for (IRecipe irecipe : allrecipes)
		{
			if (irecipe instanceof RecipeShapelessHidden && irecipe.getRecipeOutput().getItem() != ObjHandler.kleinStars)
			{
				for (Object is : ((RecipeShapelessHidden) irecipe).getInput())
				{
					if (NEIServerUtils.areStacksSameTypeCrafting((ItemStack) is, ingredient))
					{
						arecipes.add(new CachedPhiloSmelting(((RecipeShapelessHidden) irecipe).getInput(), irecipe.getRecipeOutput()));
						break;
					}
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