package moze_intel.projecte.integration.crafttweaker.nss;

import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.blamejared.crafttweaker_annotations.annotations.NativeTypeRegistration;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import org.openzen.zencode.java.ZenCodeType;

@ZenRegister
@NativeTypeRegistration(value = NormalizedSimpleStack.class, zenCodeName = "mods.projecte.NormalizedSimpleStack")
public class CrTExpandNormalizedSimpleStack {

	@ZenCodeType.Caster
	public static String asString(NormalizedSimpleStack internal) {
		return internal.toString();
	}
}