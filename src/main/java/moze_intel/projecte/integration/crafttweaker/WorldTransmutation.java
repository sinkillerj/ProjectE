package moze_intel.projecte.integration.crafttweaker;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.blamejared.crafttweaker.impl.blocks.MCBlockState;
import org.openzen.zencode.java.ZenCodeType;

@ZenRegister
@ZenCodeType.Name("mods.projecte.WorldTransmutation")
public class WorldTransmutation
{
	@ZenCodeType.Method
	public static void add(MCBlockState input, MCBlockState output, @ZenCodeType.Optional MCBlockState sneakOutput)
	{
		if (checkNull(input, true) & checkNull(output, false))
		{
			CraftTweakerAPI.apply(new WorldTransmuteAction.Add(input, output, sneakOutput));
		}
	}

	@ZenCodeType.Method
	public static void remove(MCBlockState input, MCBlockState output, @ZenCodeType.Optional MCBlockState sneakOutput)
	{
		if (checkNull(input, true) & checkNull(output, false))
		{
			CraftTweakerAPI.apply(new WorldTransmuteAction.Remove(input, output, sneakOutput));
		}
	}

	@ZenCodeType.Method
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