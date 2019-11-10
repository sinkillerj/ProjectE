package moze_intel.projecte.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;

/**
 * For config options that only the client cares about
 */
public class ClientConfig {

	public final BooleanValue tagToolTips;
	public final BooleanValue emcToolTips;
	public final BooleanValue shiftEmcToolTips;
	public final BooleanValue statToolTips;
	public final BooleanValue pedestalToolTips;
	public final BooleanValue pulsatingOverlay;

	ClientConfig(ForgeConfigSpec.Builder builder) {
		//We push as client in case we ever want to add an overarching comment to the client config
		builder.push("client");
		tagToolTips = builder
				.comment("Show item tags in tooltips (useful for custom EMC registration)")
				.define("tagToolTips", false);
		emcToolTips = builder
				.comment("Show the EMC value as a tooltip on items and blocks")
				.define("emcToolTips", true);
		shiftEmcToolTips = builder
				.comment("Requires holding shift to display the EMC value as a tooltip on items and blocks. Note: this does nothing if emcToolTips is disabled.")
				.define("shiftEmcToolTips", false);
		statToolTips = builder
				.comment("Show stats as tooltips for various ProjectE blocks")
				.define("statToolTips", true);
		pedestalToolTips = builder
				.comment("Show DM pedestal functions in item tooltips")
				.define("pedestalToolTips", true);
		pulsatingOverlay = builder
				.comment("The Philosopher's Stone overlay softly pulsates")
				.define("pulsatingOverlay", false);
		builder.pop();
	}
}