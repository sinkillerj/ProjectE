package moze_intel.projecte.integration.crafttweaker.nss;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker.api.data.IData;
import com.blamejared.crafttweaker.api.data.MapData;
import com.blamejared.crafttweaker.api.data.op.IDataOps;
import com.blamejared.crafttweaker_annotations.annotations.Document;
import com.blamejared.crafttweaker_annotations.annotations.NativeTypeRegistration;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.codec.IPECodecHelper;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import org.apache.logging.log4j.Logger;
import org.openzen.zencode.java.ZenCodeType;

/**
 * Represents a "stack" to be used by the EMC mapper.
 */
@ZenRegister
@Document("mods/ProjectE/NormalizedSimpleStack")
@NativeTypeRegistration(value = NormalizedSimpleStack.class, zenCodeName = "mods.projecte.NormalizedSimpleStack")
public class CrTExpandNormalizedSimpleStack {

	private static final Logger CRT_LOGGER = CraftTweakerAPI.getLogger(PECore.MODNAME);

	private CrTExpandNormalizedSimpleStack() {
	}

	/**
	 * Allows casting a {@link NormalizedSimpleStack} to a human-readable output.
	 */
	@ZenCodeType.Caster
	public static String asString(NormalizedSimpleStack internal) {
		return internal.toString();
	}

	/**
	 * Allows casting a {@link NormalizedSimpleStack} to a human-readable output.
	 */
	@ZenCodeType.Caster
	public static IData asData(NormalizedSimpleStack internal) {
		return IPECodecHelper.INSTANCE.explicitNSSCodec()
				.encode(internal, IDataOps.INSTANCE, new MapData())
				.getOrThrow(false, str -> CRT_LOGGER.error("Failed to convert NormalizedSimpleStack to IData: {}", str));
	}
}