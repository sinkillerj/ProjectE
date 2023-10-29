package moze_intel.projecte.config;

import java.nio.file.Path;
import moze_intel.projecte.PECore;
import net.minecraftforge.fml.loading.FMLPaths;

public class ProjectEConfig {

	public static final Path CONFIG_DIR;
	public static final ServerConfig server = new ServerConfig();
	public static final CommonConfig common = new CommonConfig();
	public static final ClientConfig client = new ClientConfig();

	static {
		CONFIG_DIR = FMLPaths.getOrCreateGameRelativePath(FMLPaths.CONFIGDIR.get().resolve(PECore.MODNAME));
	}

	public static void register() {
		registerConfig(server);
		registerConfig(common);
		registerConfig(client);
	}

	/**
	 * Creates a mod config so that {@link net.minecraftforge.fml.config.ConfigTracker} will track it and sync server configs from server to client.
	 */
	public static void registerConfig(IPEConfig config) {
		PEModConfig peModConfig = new PEModConfig(PECore.MOD_CONTAINER, config);
		if (config.addToContainer()) {
			PECore.MOD_CONTAINER.addConfig(peModConfig);
		}
	}
}