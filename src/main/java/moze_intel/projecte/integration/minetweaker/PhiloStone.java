package moze_intel.projecte.integration.minetweaker;

import minetweaker.IUndoableAction;
import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IItemStack;
import minetweaker.api.minecraft.MineTweakerMC;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.customRecipes.RecipeShapelessHidden;
import moze_intel.projecte.utils.WorldTransmutations;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.Iterator;
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
	public static void removePhiloSmelting(IItemStack output)
	{
		MineTweakerAPI.apply(new RemoveRecipeAction(output));
	}

	@ZenMethod
	public static void addWorldTransmutation(IItemStack output, IItemStack input)
	{
		MineTweakerAPI.apply(new AddWorldTransmutationAction(output, input));
	}

	@ZenMethod
	public static void addWorldTransmutation(IItemStack output, IItemStack sneakOutput, IItemStack input)
	{
		MineTweakerAPI.apply(new AddWorldTransmutationAction(output, sneakOutput, input));
	}

	@ZenMethod
	public static void removeWorldTransmutation(IItemStack output, IItemStack input)
	{
		MineTweakerAPI.apply(new RemoveWorldTransmutationAction(output, input));
	}

	@ZenMethod
	public static void removeWorldTransmutation(IItemStack output, IItemStack sneakOutput, IItemStack input)
	{
		MineTweakerAPI.apply(new RemoveWorldTransmutationAction(output, sneakOutput, input));
	}


	// ###########################################################

	private static class AddRecipeAction implements IUndoableAction
	{
		private final ItemStack output;
		private final ItemStack input;
		private final ItemStack fuel;


		IRecipe recipe;


		//GameRegistry.addRecipe(new RecipeShapelessHidden(output, philosStone, input, input, input, input, input, input, input, new ItemStack(Items.COAL, 1, OreDictionary.WILDCARD_VALUE)));
		public AddRecipeAction(IItemStack output, IItemStack input)
		{
			this.output = MineTweakerMC.getItemStack(output);
			this.input = MineTweakerMC.getItemStack(input);
			this.fuel = new ItemStack(Items.COAL, 1, OreDictionary.WILDCARD_VALUE);
			this.recipe = new RecipeShapelessHidden(this.output, new ItemStack(ObjHandler.philosStone), this.input, this.input, this.input, this.input, this.input, this.input, this.input, this.fuel);
		}

		public AddRecipeAction(IItemStack output, IItemStack input, IItemStack fuel)
		{
			this.output = MineTweakerMC.getItemStack(output);
			this.input = MineTweakerMC.getItemStack(input);
			this.fuel = MineTweakerMC.getItemStack(fuel);
			this.recipe = new RecipeShapelessHidden(this.output, new ItemStack(ObjHandler.philosStone), this.input, this.input, this.input, this.input, this.input, this.input, this.input, this.fuel);
		}

		@Override
		public void apply()
		{
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
			return "Removing Philosopher's Stone Smelting Recipe for " + recipe.getRecipeOutput().getDisplayName();
		}

		@Override
		public String describeUndo()
		{
			return "Un-removing Philosopher's Stone Smelting Recipe for " + recipe.getRecipeOutput().getDisplayName();
		}

		@Override
		public Object getOverrideKey()
		{
			return null;
		}

	}

	private static class AddWorldTransmutationAction implements IUndoableAction
	{
		private final IBlockState output;
		private final IBlockState sneakOutput;
		private final IBlockState input;

		public AddWorldTransmutationAction(IItemStack output, IItemStack input)
		{
            this.output = MineTweakerMC.getBlock(output).getStateFromMeta(output.getDamage());
			this.sneakOutput = null;
			this.input = MineTweakerMC.getBlock(input).getStateFromMeta(input.getDamage());

		}

		public AddWorldTransmutationAction(IItemStack output, IItemStack sneakOutput, IItemStack input)
		{
            this.output = MineTweakerMC.getBlock(output).getStateFromMeta(output.getDamage());
            this.sneakOutput = MineTweakerMC.getBlock(sneakOutput).getStateFromMeta(sneakOutput.getDamage());
            this.input = MineTweakerMC.getBlock(input).getStateFromMeta(input.getDamage());
		}

		@Override
		public void apply()
		{
			WorldTransmutations.register(this.input, this.output, this.sneakOutput);
		}

		@Override
		public boolean canUndo()
		{
			return true;
		}

		@Override
		public void undo()
		{
			Iterator<WorldTransmutations.Entry> it = WorldTransmutations.getWorldTransmutations().iterator();

			while (it.hasNext())
			{
				WorldTransmutations.Entry entry = it.next();

				if (entry.input == this.input && entry.outputs.getLeft() == this.output)
				{
					if (entry.outputs.getRight() != null && entry.outputs.getRight() != this.sneakOutput)
					{
						continue;
					} else
					{
						it.remove();
					}
				}

			}
		}

		@Override
		public String describe()
		{
			return "Adding world transmutation recipe for " + output;
		}

		@Override
		public String describeUndo()
		{
			return "Removing world transmutation recipe for " + output;
		}

		@Override
		public Object getOverrideKey()
		{
			return null;
		}
	}

	private static class RemoveWorldTransmutationAction implements IUndoableAction
	{

		private final IBlockState output;
		private final IBlockState sneakOutput;
		private final IBlockState input;

		public RemoveWorldTransmutationAction(IItemStack output, IItemStack input)
		{
            this.output = MineTweakerMC.getBlock(output).getStateFromMeta(output.getDamage());
            this.sneakOutput = null;
            this.input = MineTweakerMC.getBlock(input).getStateFromMeta(input.getDamage());
		}


		public RemoveWorldTransmutationAction(IItemStack output, IItemStack sneakOutput, IItemStack input)
		{
            this.output = MineTweakerMC.getBlock(output).getStateFromMeta(output.getDamage());
            this.sneakOutput = MineTweakerMC.getBlock(sneakOutput).getStateFromMeta(sneakOutput.getDamage());
            this.input = MineTweakerMC.getBlock(input).getStateFromMeta(input.getDamage());
		}

		@Override
		public void apply()
		{

			Iterator<WorldTransmutations.Entry> it = WorldTransmutations.getWorldTransmutations().iterator();

			while (it.hasNext())
			{
				WorldTransmutations.Entry entry = it.next();

				if (entry.input == this.input && entry.outputs.getLeft() == this.output)
				{
					if (entry.outputs.getRight() != null && entry.outputs.getRight() != this.sneakOutput)
					{
						continue;
					} else
					{
						it.remove();
					}
				}

			}

		}

		@Override
		public boolean canUndo()
		{
			return true;
		}

		@Override
		public void undo()
		{
			WorldTransmutations.register(this.input, this.output, this.sneakOutput);
		}

		@Override
		public String describe()
		{
			return "Removing world transmutation recipe for " + output;
		}

		@Override
		public String describeUndo()
		{
			return "Un-removing world transmutation recipe for " + output;
		}

		@Override
		public Object getOverrideKey()
		{
			return null;
		}
	}
}
