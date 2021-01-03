package moze_intel.projecte.integration.crafttweaker;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import moze_intel.projecte.integration.crafttweaker.actions.CustomEMCAction;
import org.openzen.zencode.java.ZenCodeType;

@ZenRegister
@ZenCodeType.Name("mods.projecte.CustomEMC")
public class CrTCustomEMC {

	@ZenCodeType.Method
	public static void setEMCValue(NormalizedSimpleStack stack, long emc) {
		if (emc < 0) {
			throw new IllegalArgumentException("EMC cannot be set to a negative number. Was set to: " + emc);
		}
		CraftTweakerAPI.apply(new CustomEMCAction(stack, emc));
	}

	/**
	 * Wrapper that basically acts as if {@link #setEMCValue(NormalizedSimpleStack, long)} was passed zero for the emc value.
	 */
	@ZenCodeType.Method
	public static void removeEMCValue(NormalizedSimpleStack stack) {
		CraftTweakerAPI.apply(new CustomEMCAction(stack, 0));
	}
}