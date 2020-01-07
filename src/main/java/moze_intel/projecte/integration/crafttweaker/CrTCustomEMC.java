package moze_intel.projecte.integration.crafttweaker;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import moze_intel.projecte.integration.crafttweaker.actions.CustomEMCAction;
import moze_intel.projecte.integration.crafttweaker.nss.NSSCrT;
import org.openzen.zencode.java.ZenCodeType;

@ZenRegister
@ZenCodeType.Name("mods.projecte.CustomEMC")
public class CrTCustomEMC {

	@ZenCodeType.Method
	public static void setEMCValue(NSSCrT stack, long emc) {
		if (CraftTweakerHelper.checkNonNull(stack, "The NSS to set an EMC value for cannot be null.") & CraftTweakerHelper.validateEMC(emc)) {
			CraftTweakerAPI.apply(new CustomEMCAction(stack.getInternal(), emc));
		}
	}

	/**
	 * Wrapper that basically acts as if {@link #setEMCValue(NSSCrT, long)} was passed zero for the emc value.
	 */
	@ZenCodeType.Method
	public static void removeEMCValue(NSSCrT stack) {
		if (CraftTweakerHelper.checkNonNull(stack, "The NSS to remove an EMC value from cannot be null.")) {
			CraftTweakerAPI.apply(new CustomEMCAction(stack.getInternal(), 0));
		}
	}
}