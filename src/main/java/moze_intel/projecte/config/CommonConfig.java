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
		debugLogging = CachedBooleanValue.wrap(this, PEConfigTranslations.COMMON_DEBUG_LOGGING.applyToBuilder(builder).define("debugLogging", false));

		PEConfigTranslations.COMMON_CRAFTING.applyToBuilder(builder).push("crafting");
		craftableTome = CachedBooleanValue.wrap(this, PEConfigTranslations.COMMON_CRAFTING_TOME.applyToBuilder(builder).define("craftableTome", false));
		fullKleinStars = CachedBooleanValue.wrap(this, PEConfigTranslations.COMMON_CRAFTING_FULL_KLEIN.applyToBuilder(builder).define("fullKleinStars", false));
		builder.pop();
		configSpec = builder.build();
	}

	@Override
	public String getFileName() {
		return "common";
	}

	@Override
	public String getTranslation() {
		return "Common Config";
	}

	@Override
	public ModConfigSpec getConfigSpec() {
		return configSpec;
	}

	@Override
	public ModConfig.Type getConfigType() {
		return ModConfig.Type.LOCAL;
	}
}
