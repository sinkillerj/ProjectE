package moze_intel.projecte.config;

import java.nio.file.Path;
import moze_intel.projecte.PECore;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.commons.lang3.tuple.Pair;

public class ProjectEConfig {

	public static final Path CONFIG_DIR;
	public static final ServerConfig server;
	public static final CommonConfig common;
	public static final ClientConfig client;

	private static final ForgeConfigSpec serverSpec;
	private static final ForgeConfigSpec commonSpec;
	private static final ForgeConfigSpec clientSpec;

	static {
		CONFIG_DIR = FMLPaths.getOrCreateGameRelativePath(FMLPaths.CONFIGDIR.get().resolve(PECore.MODNAME), PECore.MODNAME);
		Pair<ServerConfig, ForgeConfigSpec> serverConfiguration = new ForgeConfigSpec.Builder().configure(ServerConfig::new);
		server = serverConfiguration.getLeft();
		serverSpec = serverConfiguration.getRight();
		Pair<CommonConfig, ForgeConfigSpec> commonConfiguration = new ForgeConfigSpec.Builder().configure(CommonConfig::new);
		common = commonConfiguration.getLeft();
		commonSpec = commonConfiguration.getRight();
		Pair<ClientConfig, ForgeConfigSpec> clientConfiguration = new ForgeConfigSpec.Builder().configure(ClientConfig::new);
		client = clientConfiguration.getLeft();
		clientSpec = clientConfiguration.getRight();
	}

	public static void register() {
		registerConfig(Type.SERVER, serverSpec, "server");
		registerConfig(Type.COMMON, commonSpec, "common");
		registerConfig(Type.CLIENT, clientSpec, "client");
	}

	private static void registerConfig(ModConfig.Type type, ForgeConfigSpec spec, String fileName) {
		PECore.MOD_CONTAINER.addConfig(new PEModConfig(type, spec, PECore.MOD_CONTAINER, fileName));
	}
}