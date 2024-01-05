package moze_intel.projecte.config;

import moze_intel.projecte.PECore;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;

/**
 * Custom {@link ModConfig} implementation that makes invalidating custom caches easier.
 */
public class PEModConfig extends ModConfig {

	private final IPEConfig peConfig;

	public PEModConfig(ModContainer container, IPEConfig config) {
		super(config.getConfigType(), config.getConfigSpec(), container, PECore.MODNAME + "/" + config.getFileName() + ".toml");
		this.peConfig = config;
	}

	public void clearCache(ModConfigEvent event) {
		peConfig.clearCache(event instanceof ModConfigEvent.Unloading);
	}
}