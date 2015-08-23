package moze_intel.projecte.integration.MineTweaker;

import minetweaker.IUndoableAction;
import minetweaker.MineTweakerAPI;
import moze_intel.projecte.gameObjs.customRecipes.RecipeShapelessHidden;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.List;

@ZenClass("mods.projecte.HiddenShapeless")
public class HiddenShapeless
{
	@ZenMethod
	public static void removeRecipe(ItemStack output)
	{
		MineTweakerAPI.apply(new RemoveAction(output));
	}

	private static class RemoveAction implements IUndoableAction
	{
		IRecipe recipe = null;
		ItemStack remove;

		public RemoveAction(ItemStack rem){
			remove = rem;
		}

		@Override
		public void apply(){

			List<IRecipe> recipes = CraftingManager.getInstance().getRecipeList();

			for (IRecipe irecipe : recipes)
			{
				if (irecipe instanceof RecipeShapelessHidden)
				{
					if (irecipe.getRecipeOutput().isItemEqual(remove))
					{
						CraftingManager.getInstance().getRecipeList().remove(irecipe);
					}
				}
			}
		}

		@Override
		public boolean canUndo(){
			return recipe != null;
		}

		@Override
		public void undo(){
			CraftingManager.getInstance().getRecipeList().add(recipe);
		}

		@Override
		public String describe(){
			return "Removing Hidden Crafting Recipe for " + recipe.getRecipeOutput().getDisplayName();
		}

		@Override
		public String describeUndo(){
			return "Un-removing Hidden Crafting Recipe for " + recipe.getRecipeOutput().getDisplayName();
		}

		@Override
		public Object getOverrideKey() {
			return null;
		}

	}




}
