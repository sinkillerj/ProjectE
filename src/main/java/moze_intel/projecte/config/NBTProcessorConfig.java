package moze_intel.projecte.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.nbt.INBTProcessor;
import moze_intel.projecte.config.value.CachedBooleanValue;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * For config options having to do with NBT Processors. Syncs from server to client.
 */
public class NBTProcessorConfig extends BasePEConfig {

	private static NBTProcessorConfig INSTANCE;
	private static final String ENABLED = "enabled";
	private static final String PERSISTENT = "persistent";
	private static final String MAIN_KEY = "processors";

	/**
	 * If the config has not already been initialized setup a config the with given list of {@link INBTProcessor}s and creates a dummy "server" config so that it will be
	 * synced by the {@link net.minecraftforge.fml.config.ConfigTracker} from server to client.
	 *
	 * @implNote We register the dummy config as being owned by our mod container, but we don't tell the mod container about the dummy config so that it does not
	 * overwrite our main server config.
	 */
	public static void setup(@NotNull List<INBTProcessor> processors) {
		if (INSTANCE == null) {
			ProjectEConfig.registerConfig(INSTANCE = new NBTProcessorConfig(processors));
		}
	}

	private final ForgeConfigSpec configSpec;
	private final Map<String, ProcessorConfig> processorConfigs = new HashMap<>();

	private NBTProcessorConfig(@NotNull List<INBTProcessor> processors) {
		ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
		builder.comment("This config is used to control which NBT Processors get used, and which ones actually contribute to the persistent NBT data that gets " +
						"saved to knowledge/copied in a condenser.",
				"To disable an NBT Processor set the '" + ENABLED + "' option for it to false.",
				"To disable an NBT Processor from contributing to the persistent data set the '" + PERSISTENT + "' option for it to false. Note: that if there is no " +
				PERSISTENT + "' config option, the NBT Processor never has any persistent data.",
				"The config options in this file are synced from server to client, as the processors get used dynamically to calculate/preview EMC values for items " +
				"and are not included in the synced EMC mappings.")
				.push(MAIN_KEY);
		for (INBTProcessor processor : processors) {
			processorConfigs.put(processor.getName(), new ProcessorConfig(this, builder, processor));
		}
		builder.pop();
		configSpec = builder.build();
	}

	/**
	 * @return True if the given {@link INBTProcessor} is enabled.
	 */
	public static boolean isEnabled(INBTProcessor processor) {
		if (INSTANCE == null) {
			return true;
		}
		String name = processor.getName();
		ProcessorConfig processorConfig = INSTANCE.processorConfigs.get(name);
		if (processorConfig == null) {
			PECore.LOGGER.warn("Processor Config: '{}' is missing from the config.", name);
			return false;
		}
		return processorConfig.enabled.get();
	}

	/**
	 * @return True if the given {@link INBTProcessor} should contribute to the persistent data.
	 */
	public static boolean hasPersistent(INBTProcessor processor) {
		if (INSTANCE == null) {
			return false;
		}
		String name = processor.getName();
		ProcessorConfig processorConfig = INSTANCE.processorConfigs.get(name);
		if (processorConfig == null) {
			PECore.LOGGER.warn("Processor Config: '{}' is missing from the config.", name);
			return false;
		} else if (processorConfig.persistent == null) {
			if (processor.hasPersistentNBT()) {
				PECore.LOGGER.warn("Processor Config: '{}' has persistent NBT but is missing the config option.", name);
			}
			return false;
		}
		return processorConfig.persistent.get();
	}

	@Override
	public String getFileName() {
		return "processing";
	}

	@Override
	public ForgeConfigSpec getConfigSpec() {
		return configSpec;
	}

	@Override
	public Type getConfigType() {
		return Type.SERVER;
	}

	@Override
	public boolean addToContainer() {
		return false;
	}

	private static class ProcessorConfig {

		public final CachedBooleanValue enabled;
		@Nullable
		public final CachedBooleanValue persistent;

		private ProcessorConfig(IPEConfig config, ForgeConfigSpec.Builder builder, INBTProcessor processor) {
			builder.comment(processor.getDescription()).push(processor.getName());
			enabled = CachedBooleanValue.wrap(config, builder.define(ENABLED, processor.isAvailable()));
			if (processor.hasPersistentNBT()) {
				persistent = CachedBooleanValue.wrap(config, builder.define(PERSISTENT, processor.usePersistentNBT()));
			} else {
				persistent = null;
			}
			builder.pop();
		}
	}
}