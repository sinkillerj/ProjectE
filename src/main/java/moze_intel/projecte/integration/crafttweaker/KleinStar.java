package moze_intel.projecte.integration.crafttweaker;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.customRecipes.RecipeShapelessKleinStar;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("mods.projecte.KleinStar")
public class KleinStar
{
	@ZenMethod
	public static void addKleinRecipe(String name, IItemStack output, IItemStack[] inputs)
	{
		ItemStack outputStack = checkOutput(output);
		if (outputStack != null)
		{
			if (inputs == null || inputs.length == 0)
			{
				CraftTweakerAPI.logError("Inputs cannot be null");
				return;
			}
			NonNullList<Ingredient> ingredients = NonNullList.create();
			for (IItemStack stack : inputs)
			{
				if (stack == null)
				{
					CraftTweakerAPI.logError("Inputs cannot contain any null entries");
					return;
				}
				ingredients.add(CraftTweakerMC.getIngredient(stack));
			}
			CraftTweakerAPI.apply(new AddKleinRecipe(name, outputStack, ingredients));
		}
	}

	private static ItemStack checkOutput(IItemStack output) {
		if (output != null)
		{
			ItemStack outputStack = CraftTweakerMC.getItemStack(output);
			if (outputStack.getItem() == ObjHandler.kleinStars)
			{
				return outputStack;
			}
		}
		CraftTweakerAPI.logError("Output must be a Klein Star");
		return null;
	}

	public static class AddKleinRecipe implements IAction
	{
		private final NonNullList<Ingredient> ingredients;
		private final ItemStack output;
		private final String name;

		public AddKleinRecipe(String name, ItemStack output, NonNullList<Ingredient> ingredients)
		{
			this.name = name;
			this.output = output;
			this.ingredients = ingredients;
		}

		@Override
		public void apply()
		{
			ForgeRegistries.RECIPES.register(new RecipeShapelessKleinStar("", output, ingredients).setRegistryName(name));
		}

		@Override
		public String describe()
		{
			return "Adding recipe for " + output;
		}
	}
}