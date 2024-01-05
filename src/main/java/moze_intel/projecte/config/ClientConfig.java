package moze_intel.projecte.config;

import moze_intel.projecte.config.value.CachedBooleanValue;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * For config options that only the client cares about
 */
public class ClientConfig extends BasePEConfig {

	private final ModConfigSpec configSpec;

	public final CachedBooleanValue tagToolTips;
	public final CachedBooleanValue emcToolTips;
	public final CachedBooleanValue shiftEmcToolTips;
	public final CachedBooleanValue shiftLearnedToolTips;
	public final CachedBooleanValue statToolTips;
	public final CachedBooleanValue pedestalToolTips;
	public final CachedBooleanValue pulsatingOverlay;

	ClientConfig() {
		ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
		//We push as client in case we ever want to add an overarching comment to the client config
		builder.push("client");
		tagToolTips = CachedBooleanValue.wrap(this, builder
				.comment("Show item tags in tooltips (useful for custom EMC registration)")
				.define("tagToolTips", false));
		emcToolTips = CachedBooleanValue.wrap(this, builder
				.comment("Show the EMC value as a tooltip on items and blocks")
				.define("emcToolTips", true));
		shiftEmcToolTips = CachedBooleanValue.wrap(this, builder
				.comment("Requires holding shift to display the EMC value as a tooltip on items and blocks. Note: this does nothing if emcToolTips is disabled.")
				.define("shiftEmcToolTips", false));
		shiftLearnedToolTips = CachedBooleanValue.wrap(this, builder
				.comment("Requires holding shift to display the learned/unlearned text as a tooltip on items and blocks. Note: this does nothing if emcToolTips is disabled.")
				.define("shiftLearnedToolTips", true));
		statToolTips = CachedBooleanValue.wrap(this, builder
				.comment("Show stats as tooltips for various ProjectE blocks")
				.define("statToolTips", true));
		pedestalToolTips = CachedBooleanValue.wrap(this, builder
				.comment("Show DM pedestal functions in item tooltips")
				.define("pedestalToolTips", true));
		pulsatingOverlay = CachedBooleanValue.wrap(this, builder
				.comment("The Philosopher's Stone overlay softly pulsates")
				.define("pulsatingOverlay", false));
		builder.pop();
		configSpec = builder.build();
	}

	@Override
	public String getFileName() {
		return "client";
	}

	@Override
	public ModConfigSpec getConfigSpec() {
		return configSpec;
	}

	@Override
	public ModConfig.Type getConfigType() {
		return ModConfig.Type.CLIENT;
	}
}