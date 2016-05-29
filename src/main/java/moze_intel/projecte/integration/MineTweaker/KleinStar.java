package moze_intel.projecte.integration.MineTweaker;

import cpw.mods.fml.common.registry.GameRegistry;
import minetweaker.IUndoableAction;
import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IItemStack;
import minetweaker.api.minecraft.MineTweakerMC;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.customRecipes.RecipeShapedKleinStar;
import moze_intel.projecte.gameObjs.customRecipes.RecipeShapelessHidden;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.ArrayList;
import java.util.List;


@ZenClass("mods.projecte.KleinStar")
public class KleinStar
{
	@ZenMethod
	public static void addShaped(IItemStack output, IItemStack[][] inputs)
	{
		addRecipe(output, inputs, true);
	}

	@ZenMethod
	public static void addShapeless(IItemStack output, IItemStack[] inputs)
	{
		addRecipe(output, new IItemStack[][]{inputs}, false);
	}


	private static void addRecipe(IItemStack output, IItemStack[][] inputs, boolean shaped)
	{

		ItemStack outputStack = MineTweakerMC.getItemStack(output);
		if (outputStack.getItem() == ObjHandler.kleinStars)
		{
			if (shaped && inputs.length != 3)
			{
				MineTweakerAPI.logError("Unable to add recipe with input rows other than 3");
				return;
			}

			List<ItemStack> isInputs = new ArrayList<>();
			for (IItemStack[] input : inputs)
			{
				for (IItemStack stack : input)
				{
					isInputs.add(MineTweakerMC.getItemStack(stack));
				}
			}

			MineTweakerAPI.apply(new AddRecipeAction(outputStack, isInputs.toArray(new ItemStack[]{}), shaped));
		}
	}

	@ZenMethod
	public static void removeRecipe(IItemStack output)
	{
		MineTweakerAPI.apply(new RemoveRecipeAction(output));
	}


	// ###########################################################

	private static class AddRecipeAction implements IUndoableAction
	{
		private final ItemStack output;
		private final ItemStack[] inputs;
		private final boolean shaped;

		IRecipe recipe;

		public AddRecipeAction(ItemStack outputStack, ItemStack[] inputs, boolean shaped)
		{
			this.output = outputStack;
			this.inputs = inputs;
			this.shaped = shaped;
		}

		@Override
		public void apply()
		{
			recipe = shaped ? new RecipeShapedKleinStar(3, 3, inputs, output) : new RecipeShapelessHidden(output, inputs);
			GameRegistry.addRecipe(recipe);
		}

		@Override
		public boolean canUndo()
		{
			return true;
		}

		@Override
		public void undo()
		{
			CraftingManager.getInstance().getRecipeList().remove(recipe);
		}

		@Override
		public String describe()
		{
			return "Adding recipe for " + output;
		}

		@Override
		public String describeUndo()
		{
			return "Removing recipe for " + output;
		}

		@Override
		public Object getOverrideKey()
		{
			return null;
		}
	}


	private static class RemoveRecipeAction implements IUndoableAction
	{
		IRecipe recipe = null;
		ItemStack remove;

		public RemoveRecipeAction(IItemStack rem)
		{
			remove = MineTweakerMC.getItemStack(rem);

			List<IRecipe> allrecipes = CraftingManager.getInstance().getRecipeList();
			for (IRecipe irecipe : allrecipes)
			{
				if (irecipe instanceof RecipeShapelessHidden)
				{
					if (remove.isItemEqual(irecipe.getRecipeOutput()))
					{
						recipe = irecipe;
					}
				}
			}
		}

		@Override
		public void apply()
		{
			CraftingManager.getInstance().getRecipeList().remove(recipe);
		}

		@Override
		public boolean canUndo()
		{
			return recipe != null;
		}

		@Override
		public void undo()
		{
			CraftingManager.getInstance().getRecipeList().add(recipe);
		}

		@Override
		public String describe()
		{
			return "Removing recipe for " + recipe.getRecipeOutput().getDisplayName() + " in a " + recipe.getClass();
		}

		@Override
		public String describeUndo()
		{
			return "Un-removing recipe for " + recipe.getRecipeOutput().getDisplayName() + " in a " + recipe.getClass();
		}

		@Override
		public Object getOverrideKey()
		{
			return null;
		}

	}

}
