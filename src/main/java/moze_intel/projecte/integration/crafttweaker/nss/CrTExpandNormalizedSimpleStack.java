package moze_intel.projecte.integration.crafttweaker.nss;

import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker_annotations.annotations.Document;
import com.blamejared.crafttweaker_annotations.annotations.NativeTypeRegistration;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import org.openzen.zencode.java.ZenCodeType;

/**
 * Represents a "stack" to be used by the EMC mapper.
 */
@ZenRegister
@Document("mods/ProjectE/NormalizedSimpleStack")
@NativeTypeRegistration(value = NormalizedSimpleStack.class, zenCodeName = "mods.projecte.NormalizedSimpleStack")
public class CrTExpandNormalizedSimpleStack {

	private CrTExpandNormalizedSimpleStack() {
	}

	/**
	 * Allows casting a {@link moze_intel.projecte.api.nss.NormalizedSimpleStack} to a human-readable output.
	 */
	@ZenCodeType.Caster
	public static String asString(NormalizedSimpleStack internal) {
		return internal.toString();
	}
}