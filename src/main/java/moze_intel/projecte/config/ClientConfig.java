package moze_intel.projecte.config;

import moze_intel.projecte.config.value.CachedBooleanValue;
import moze_intel.projecte.config.value.CachedIntValue;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

/**
 * For config options that only the client cares about
 */
public class ClientConfig extends BasePEConfig {

	private final ForgeConfigSpec configSpec;

	public final CachedBooleanValue tagToolTips;
	public final CachedBooleanValue emcToolTips;
	public final CachedBooleanValue shiftEmcToolTips;
	public final CachedBooleanValue statToolTips;
	public final CachedBooleanValue pedestalToolTips;
	public final CachedBooleanValue pulsatingOverlay;
	public final CachedIntValue condenserEmcDisplayMode;

	ClientConfig() {
		ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
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
		statToolTips = CachedBooleanValue.wrap(this, builder
				.comment("Show stats as tooltips for various ProjectE blocks")
				.define("statToolTips", true));
		pedestalToolTips = CachedBooleanValue.wrap(this, builder
				.comment("Show DM pedestal functions in item tooltips")
				.define("pedestalToolTips", true));
		pulsatingOverlay = CachedBooleanValue.wrap(this, builder
				.comment("The Philosopher's Stone overlay softly pulsates")
				.define("pulsatingOverlay", false));
		condenserEmcDisplayMode = CachedIntValue.wrap(this, builder
				.comment("Sets how EMC is displayed inside Energy Condensers.\n0: Shows how much EMC is stored or how much is required to make the item if there is Excess stored EMC. (EE2 Style)\n1: Will show the stored amount of EMC and render green if the value exceeds required amount.\n2: If the stored EMC value is greater then required it will display the total EMC stored seperately in green.\nNote: If the value displayed would overrun the edge of the GUI it will instead state 'Overflow'.")
				.define("showTotalEMCInCondenser", 0));
		builder.pop();
		configSpec = builder.build();
	}

	@Override
	public String getFileName() {
		return "client";
	}

	@Override
	public ForgeConfigSpec getConfigSpec() {
		return configSpec;
	}

	@Override
	public ModConfig.Type getConfigType() {
		return ModConfig.Type.CLIENT;
	}
}