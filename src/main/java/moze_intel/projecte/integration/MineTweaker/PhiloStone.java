package moze_intel.projecte.integration.MineTweaker;

import cpw.mods.fml.common.registry.GameRegistry;
import minetweaker.IUndoableAction;
import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IItemStack;
import minetweaker.api.minecraft.MineTweakerMC;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.customRecipes.RecipeShapelessHidden;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.OreDictionary;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.List;

@ZenClass("mods.projecte.PhiloStone")
public class PhiloStone
{
	@ZenMethod
	public static void addPhiloSmelting(IItemStack output, IItemStack input)
	{
		MineTweakerAPI.apply(new AddRecipeAction(output, input));
	}

	@ZenMethod
	public static void addPhiloSmelting(IItemStack output, IItemStack input, IItemStack fuel)
	{
		MineTweakerAPI.apply(new AddRecipeAction(output, input, fuel));
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
		private final ItemStack input;
		private final ItemStack fuel;
		private final ItemStack[] inputs = new ItemStack[2];
		

		IRecipe irecipe;


		//GameRegistry.addRecipe(new RecipeShapelessHidden(output, philosStone, input, input, input, input, input, input, input, new ItemStack(Items.coal, 1, OreDictionary.WILDCARD_VALUE)));
		public AddRecipeAction(IItemStack output, IItemStack input)
		{
			this.output = MineTweakerMC.getItemStack(output);
			this.input = MineTweakerMC.getItemStack(input);
			this.fuel = new ItemStack(Items.coal, 1, OreDictionary.WILDCARD_VALUE);
			this.irecipe = new RecipeShapelessHidden(this.output, new ItemStack(ObjHandler.philosStone), this.input, this.input, this.input, this.input, this.input, this.input, this.input, this.fuel);

			this.inputs[0] = this.input;
			this.inputs[1] = this.fuel;
		}

		public AddRecipeAction(IItemStack output, IItemStack input, IItemStack fuel)
		{
			this.output = MineTweakerMC.getItemStack(output);
			this.input = MineTweakerMC.getItemStack(input);
			this.fuel = MineTweakerMC.getItemStack(fuel);
			this.irecipe = new RecipeShapelessHidden(this.output, new ItemStack(ObjHandler.philosStone), this.input, this.input, this.input, this.input, this.input, this.input, this.input, this.fuel);

			this.inputs[0] = this.input;
			this.inputs[1] = this.fuel;
		}

		@Override
		public void apply()
		{
			ObjHandler.MAP.put(inputs, output);
			GameRegistry.addRecipe(irecipe);
		}

		@Override
		public boolean canUndo()
		{
			return true;
		}

		@Override
		public void undo()
		{
			CraftingManager.getInstance().getRecipeList().remove(irecipe);
		}

		@Override
		public String describe()
		{
			return "Adding Philosopher's Stone Smelting recipe for " + output;
		}

		@Override
		public String describeUndo()
		{
			return "Removing Philosopher's Stone Smelting recipe for " + output;
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
		}

		@Override
		public void apply()
		{

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
			return "Removing Hidden Crafting Recipe for " + recipe.getRecipeOutput().getDisplayName();
		}

		@Override
		public String describeUndo()
		{
			return "Un-removing Hidden Crafting Recipe for " + recipe.getRecipeOutput().getDisplayName();
		}

		@Override
		public Object getOverrideKey()
		{
			return null;
		}

	}

}
