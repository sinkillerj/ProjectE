package moze_intel.projecte.integration.crafttweaker;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import java.util.Map;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import moze_intel.projecte.integration.crafttweaker.actions.CustomConversionAction;
import org.openzen.zencode.java.ZenCodeType;

@ZenRegister
@ZenCodeType.Name("mods.projecte.CustomConversion")
public class CrTCustomConversion {

	@ZenCodeType.Method
	public static void addConversion(NormalizedSimpleStack stack, int amount, Map<NormalizedSimpleStack, Integer> ingredients) {
		CraftTweakerAPI.apply(new CustomConversionAction(stack, amount, ingredients));
	}
}