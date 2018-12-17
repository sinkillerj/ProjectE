package moze_intel.projecte.integration.crafttweaker;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import moze_intel.projecte.integration.crafttweaker.actions.SmeltingRecipeAction;
import moze_intel.projecte.integration.crafttweaker.actions.WorldTransmuteAction;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("mods.projecte.PhiloStone")
public class PhiloStone
{
	@ZenMethod
	public static void addPhiloSmelting(String name, IItemStack output, IIngredient input, @Optional IIngredient fuel)
	{
		if (checkOutput(output) & checkInput(input))
		{
			CraftTweakerAPI.apply(new SmeltingRecipeAction.Add(name, output, input, fuel));
		}
	}

	@ZenMethod
	public static void removePhiloSmelting(IItemStack output)
	{
		if (checkOutput(output))
		{
			CraftTweakerAPI.apply(new SmeltingRecipeAction.Remove(output));
		}
	}

	@ZenMethod
	public static void removeAllPhiloSmelting()
	{
		CraftTweakerAPI.apply(new SmeltingRecipeAction.RemoveAll());
	}


	@ZenMethod
	public static void addWorldTransmutation(IItemStack output, IItemStack input, @Optional IItemStack sneakOutput)
	{
		if (checkOutput(output) & checkInput(input))
		{
			CraftTweakerAPI.apply(new WorldTransmuteAction.Add(output, sneakOutput, input));
		}
	}

	@ZenMethod
	public static void removeWorldTransmutation(IItemStack output, IItemStack input, @Optional IItemStack sneakOutput)
	{
		if (checkOutput(output) & checkInput(input))
		{
			CraftTweakerAPI.apply(new WorldTransmuteAction.Remove(output, sneakOutput, input));
		}
	}

	@ZenMethod
	public static void removeAllWorldTransmutation()
	{
		CraftTweakerAPI.apply(new WorldTransmuteAction.RemoveAll());
	}

	private static boolean checkOutput(IItemStack output) {
		if (output == null)
		{
			CraftTweakerAPI.logError("Output cannot be null");
			return false;
		}
		return true;
	}

	private static boolean checkInput(IIngredient input) {
		if (input == null)
		{
			CraftTweakerAPI.logError("Input cannot be null");
			return false;
		}
		return true;
	}
}