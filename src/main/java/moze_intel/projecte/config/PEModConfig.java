package moze_intel.projecte.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import java.nio.file.Path;
import java.util.function.Function;
import moze_intel.projecte.PECore;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.config.ConfigFileTypeHandler;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.loading.FMLPaths;

/**
 * Custom {@link ModConfig} implementation that allows for rerouting the server config from being in the worlds folder to being in the normal config folder. This allows
 * for us to use the built in sync support, without the extra hassle of having to explain to people where the config file is, or require people in single player to edit
 * the config each time they make a new world.
 */
public class PEModConfig extends ModConfig {

	private static final PEConfigFileTypeHandler PE_TOML = new PEConfigFileTypeHandler();

	private final IPEConfig peConfig;

	public PEModConfig(ModContainer container, IPEConfig config) {
		super(config.getConfigType(), config.getConfigSpec(), container, PECore.MODNAME + "/" + config.getFileName() + ".toml");
		this.peConfig = config;
	}

	@Override
	public ConfigFileTypeHandler getHandler() {
		return PE_TOML;
	}

	public void clearCache(ModConfigEvent event) {
		//TODO - 1.20: Add support for the unloading event subtype
		peConfig.clearCache(false);//event instanceof ModConfigEvent.Unloading);
	}

	private static class PEConfigFileTypeHandler extends ConfigFileTypeHandler {

		private static Path getPath(Path configBasePath) {
			//Intercept server config path reading for ProjectE configs and reroute it to the normal config directory
			if (configBasePath.endsWith("serverconfig")) {
				return FMLPaths.CONFIGDIR.get();
			}
			return configBasePath;
		}

		@Override
		public Function<ModConfig, CommentedFileConfig> reader(Path configBasePath) {
			return super.reader(getPath(configBasePath));
		}

		@Override
		public void unload(Path configBasePath, ModConfig config) {
			super.unload(getPath(configBasePath), config);
		}
	}
}