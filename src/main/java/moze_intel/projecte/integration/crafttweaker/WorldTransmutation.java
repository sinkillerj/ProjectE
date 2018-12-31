package moze_intel.projecte.integration.crafttweaker;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.block.IBlockState;
import crafttweaker.api.item.IItemStack;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("mods.projecte.WorldTransmutation")
public class WorldTransmutation
{
	@ZenMethod
	public static void add(IItemStack output, IItemStack input, @Optional IItemStack sneakOutput)
	{
		if (checkNull(output, false) & checkNull(input, true))
		{
			CraftTweakerAPI.apply(new WorldTransmuteAction.Add(output, input, sneakOutput));
		}
	}

	@ZenMethod
	public static void add(IBlockState output, IBlockState input, @Optional IBlockState sneakOutput)
	{
		if (checkNull(output, false) & checkNull(input, true))
		{
			CraftTweakerAPI.apply(new WorldTransmuteAction.Add(output, input, sneakOutput));
		}
	}

	@ZenMethod
	public static void remove(IItemStack output, IItemStack input, @Optional IItemStack sneakOutput)
	{
		if (checkNull(output, false) & checkNull(input, true))
		{
			CraftTweakerAPI.apply(new WorldTransmuteAction.Remove(output, input, sneakOutput));
		}
	}

	@ZenMethod
	public static void remove(IBlockState output, IBlockState input, @Optional IBlockState sneakOutput)
	{
		if (checkNull(output, false) & checkNull(input, true))
		{
			CraftTweakerAPI.apply(new WorldTransmuteAction.Remove(output, input, sneakOutput));
		}
	}

	@ZenMethod
	public static void removeAll()
	{
		CraftTweakerAPI.apply(new WorldTransmuteAction.RemoveAll());
	}

	private static boolean checkNull(Object obj, boolean isInput) {
		if (obj == null)
		{
			CraftTweakerAPI.logError((isInput ? "Input" : "Output") + " cannot be null");
			return false;
		}
		return true;
	}
}