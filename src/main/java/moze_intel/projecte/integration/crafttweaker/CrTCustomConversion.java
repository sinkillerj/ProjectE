/*package moze_intel.projecte.integration.crafttweaker;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import moze_intel.projecte.integration.crafttweaker.actions.CustomConversionAction;
import moze_intel.projecte.integration.crafttweaker.nss.NSSCrT;
import org.openzen.zencode.java.ZenCodeType;

@ZenRegister
@ZenCodeType.Name("mods.projecte.CustomConversion")
public class CrTCustomConversion {

	@ZenCodeType.Method
	public static void addConversion(NSSCrT stack, int amount, Map<NSSCrT, Integer> crtIngredients) {
		if (CraftTweakerHelper.checkNonNull(stack, "The output NSS for conversion mappings cannot be null.")) {
			Map<NormalizedSimpleStack, Integer> ingredients = new HashMap<>();
			for (Entry<NSSCrT, Integer> entry : crtIngredients.entrySet()) {
				NSSCrT ingredientStack = entry.getKey();
				Integer value = entry.getValue();
				if (CraftTweakerHelper.checkNonNull(ingredientStack, "The NSS value for ingredients cannot be null.") &
					CraftTweakerHelper.checkNonNull(value, "The amount for the given ingredient cannot be null.")) {
					ingredients.put(ingredientStack.getInternal(), value);
				} else {
					return;
				}
			}
			CraftTweakerAPI.apply(new CustomConversionAction(stack.getInternal(), amount, ingredients));
		}
	}
}*/