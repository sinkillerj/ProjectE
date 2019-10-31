package moze_intel.projecte.integration.crafttweaker;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.blamejared.crafttweaker.impl.blocks.MCBlockState;
import moze_intel.projecte.integration.crafttweaker.actions.WorldTransmuteAction;
import org.openzen.zencode.java.ZenCodeType;

@ZenRegister
@ZenCodeType.Name("mods.projecte.WorldTransmutation")
public class WorldTransmutation {

	@ZenCodeType.Method
	public static void add(MCBlockState input, MCBlockState output, @ZenCodeType.Optional MCBlockState sneakOutput) {
		if (validate(input, output)) {
			CraftTweakerAPI.apply(new WorldTransmuteAction.Add(input, output, sneakOutput));
		}
	}

	@ZenCodeType.Method
	public static void remove(MCBlockState input, MCBlockState output, @ZenCodeType.Optional MCBlockState sneakOutput) {
		if (validate(input, output)) {
			CraftTweakerAPI.apply(new WorldTransmuteAction.Remove(input, output, sneakOutput));
		}
	}

	@ZenCodeType.Method
	public static void removeAll() {
		CraftTweakerAPI.apply(new WorldTransmuteAction.RemoveAll());
	}

	private static boolean validate(MCBlockState input, MCBlockState output) {
		return CraftTweakerHelper.checkNonNull(input, "Input cannot be null") & CraftTweakerHelper.checkNonNull(output, "Output cannot be null");
	}
}