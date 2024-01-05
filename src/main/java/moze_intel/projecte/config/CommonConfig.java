package moze_intel.projecte.config;

import moze_intel.projecte.config.value.CachedBooleanValue;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * For config options that either the server or the client may care about but do not have to agree upon.
 */
public class CommonConfig extends BasePEConfig {

	private final ModConfigSpec configSpec;

	public final CachedBooleanValue debugLogging;
	public final CachedBooleanValue craftableTome;
	public final CachedBooleanValue fullKleinStars;

	CommonConfig() {
		ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
		builder.comment("Note: The majority of config options are in the server config file. If you do not see the server config file, try opening up a single player world. " +
						"ProjectE uses one \"server\" config file for all worlds, for convenience in going from one world to another, but makes it be a \"server\" config file so " +
						"that forge will automatically sync it when we connect to a multiplayer server.")
				.push("common");
		debugLogging = CachedBooleanValue.wrap(this, builder
				.comment("Enable more verbose debug logging")
				.define("debugLogging", false));
		craftableTome = CachedBooleanValue.wrap(this, builder
				.comment("The Tome of Knowledge can be crafted.")
				.define("craftableTome", false));
		fullKleinStars = CachedBooleanValue.wrap(this, builder
				.comment("Require full omega klein stars in the tome of knowledge and gem armor recipes. This is the same behavior that EE2 had.")
				.define("fullKleinStars", false));
		builder.pop();
		configSpec = builder.build();
	}

	@Override
	public String getFileName() {
		return "common";
	}

	@Override
	public ModConfigSpec getConfigSpec() {
		return configSpec;
	}

	@Override
	public ModConfig.Type getConfigType() {
		return ModConfig.Type.COMMON;
	}
}