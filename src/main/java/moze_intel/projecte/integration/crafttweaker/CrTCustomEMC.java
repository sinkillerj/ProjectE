package moze_intel.projecte.integration.crafttweaker;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker_annotations.annotations.Document;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import moze_intel.projecte.integration.crafttweaker.actions.CustomEMCAction;
import org.openzen.zencode.java.ZenCodeType;

@ZenRegister
@Document("mods/ProjectE/CustomEMC")
@ZenCodeType.Name("mods.projecte.CustomEMC")
public class CrTCustomEMC {

	private CrTCustomEMC() {
	}

	/**
	 * Set the EMC value for the given {@link NormalizedSimpleStack} to the given amount.
	 *
	 * @param stack {@link NormalizedSimpleStack} to set the EMC of.
	 * @param emc   EMC value, must not be negative.
	 */
	@ZenCodeType.Method
	public static void setEMCValue(NormalizedSimpleStack stack, long emc) {
		if (emc < 0) {
			throw new IllegalArgumentException("EMC cannot be set to a negative number. Was set to: " + emc);
		}
		CraftTweakerAPI.apply(new CustomEMCAction(stack, emc));
	}

	/**
	 * Removes the associated EMC value for the given {@link NormalizedSimpleStack}.
	 *
	 * @param stack {@link NormalizedSimpleStack} to remove EMC from.
	 *
	 * @implNote This is a wrapper that basically acts as if {@link #setEMCValue(NormalizedSimpleStack, long)} was passed zero for the EMC value.
	 */
	@ZenCodeType.Method
	public static void removeEMCValue(NormalizedSimpleStack stack) {
		CraftTweakerAPI.apply(new CustomEMCAction(stack, 0));
	}
}