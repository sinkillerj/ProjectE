package moze_intel.projecte.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;

/**
 * For config options that either the server or the client may care about but do not have to agree upon.
 */
public class CommonConfig {

	public final BooleanValue debugLogging;

	CommonConfig(ForgeConfigSpec.Builder builder) {
		builder.comment("Note: The majority of config options are in the server config file. If you do not see the server config file, try opening up a single player world. " +
				"ProjectE uses one \"server\" config file for all worlds, for convenience in going from one world to another, but makes it be a \"server\" config file so " +
				"that forge will automatically sync it when we connect to a multiplayer server.")
				.push("common");
		debugLogging = builder
				.comment("Enable more verbose debug logging")
				.define("debugLogging", false);
		builder.pop();
	}
}