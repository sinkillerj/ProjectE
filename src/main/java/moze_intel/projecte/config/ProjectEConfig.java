package moze_intel.projecte.config;

import java.nio.file.Path;
import moze_intel.projecte.PECore;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.loading.FMLPaths;

public class ProjectEConfig {

	public static final Path CONFIG_DIR;
	public static final ServerConfig server = new ServerConfig();
	public static final CommonConfig common = new CommonConfig();
	public static final ClientConfig client = new ClientConfig();

	static {
		CONFIG_DIR = FMLPaths.getOrCreateGameRelativePath(FMLPaths.CONFIGDIR.get().resolve(PECore.MODNAME));
	}

	public static void register(ModContainer modContainer) {
		registerConfig(modContainer, server);
		registerConfig(modContainer, common);
		registerConfig(modContainer, client);
	}

	/**
	 * Creates a mod config so that {@link net.neoforged.fml.config.ConfigTracker} will track it and sync server configs from server to client.
	 */
	public static void registerConfig(ModContainer modContainer,IPEConfig config) {
		PEModConfig peModConfig = new PEModConfig(modContainer, config);
		if (config.addToContainer()) {
			modContainer.addConfig(peModConfig);
		}
	}
}