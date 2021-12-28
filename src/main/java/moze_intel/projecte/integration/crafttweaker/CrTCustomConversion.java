/*package moze_intel.projecte.integration.crafttweaker;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.blamejared.crafttweaker_annotations.annotations.Document;
import java.util.Map;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import moze_intel.projecte.integration.crafttweaker.actions.CustomConversionAction;
import org.openzen.zencode.java.ZenCodeType;

@ZenRegister
@Document("mods/ProjectE/CustomConversion")
@ZenCodeType.Name("mods.projecte.CustomConversion")
public class CrTCustomConversion {

	private CrTCustomConversion() {
	}

	/**
	 * Adds a conversion to be mapped from the given set of inputs into the given output
	 *
	 * @param stack       {@link NormalizedSimpleStack} representing the conversion's output.
	 * @param amount      Amount the conversion outputs.
	 * @param ingredients Map representing all inputs to the conversion.
	 *
	@ZenCodeType.Method
	public static void addConversion(NormalizedSimpleStack stack, int amount, Map<NormalizedSimpleStack, Integer> ingredients) {
		CraftTweakerAPI.apply(new CustomConversionAction(stack, amount, ingredients));
	}
}*/