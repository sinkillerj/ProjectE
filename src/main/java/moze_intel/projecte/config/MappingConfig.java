package moze_intel.projecte.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.components.IDataComponentProcessor;
import moze_intel.projecte.api.mapper.IEMCMapper;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import moze_intel.projecte.api.proxy.IEMCProxy;
import moze_intel.projecte.config.value.CachedBooleanValue;
import net.neoforged.fml.config.ModConfig.Type;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * For config options having to do with EMC Mapping. Syncs from server to client.
 */
public class MappingConfig extends BasePEConfig {

	private static MappingConfig INSTANCE;

	/**
	 * If the config has not already been initialized setup a config the with given list of {@link IEMCMapper}s and creates a dummy "server" config so that it will be
	 * synced by the {@link net.neoforged.fml.config.ConfigTracker} from server to client.
	 *
	 * @implNote We register the dummy config as being owned by our mod container, but we don't tell the mod container about the dummy config so that it does not
	 * overwrite our main server config.
	 */
	public static void setup(@NotNull List<IEMCMapper<NormalizedSimpleStack, Long>> mappers, @NotNull List<IDataComponentProcessor> processors) {
		if (INSTANCE == null) {
			ProjectEConfig.registerConfig(PECore.MOD_CONTAINER, INSTANCE = new MappingConfig(mappers, processors));
		}
	}

	public final CachedBooleanValue dumpToFile;
	public final CachedBooleanValue usePregenerated;
	public final CachedBooleanValue logExploits;
	public final CachedBooleanValue recoverMissingItemEmc;

	private final ModConfigSpec configSpec;
	private final Map<String, BooleanSupplier> mappersEnabledConfig;
	private final Map<String, ProcessorConfig> processorConfigs;

	private MappingConfig(@NotNull List<IEMCMapper<NormalizedSimpleStack, Long>> mappers, @NotNull List<IDataComponentProcessor> processors) {
		ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

		dumpToFile = CachedBooleanValue.wrap(this, PEConfigTranslations.MAPPING_DUMP_TO_FILE.applyToBuilder(builder).define("dumpToFile", false));
		usePregenerated = CachedBooleanValue.wrap(this, PEConfigTranslations.MAPPING_PREGENERATED.applyToBuilder(builder).define("usePregenerated", false));
		logExploits = CachedBooleanValue.wrap(this, PEConfigTranslations.MAPPING_LOG_EXPLOITS.applyToBuilder(builder).define("logFoundExploits", true));
		recoverMissingItemEmc = CachedBooleanValue.wrap(this, PEConfigTranslations.MAPPING_RECOVER_MISSING_ITEMS.applyToBuilder(builder)
				.define("recoverMissingItemEmc", false));

		PEConfigTranslations.MAPPING_MAPPERS.applyToBuilder(builder).push("mappers");
		mappersEnabledConfig = new HashMap<>(mappers.size());
		for (IEMCMapper<NormalizedSimpleStack, Long> mapper : mappers) {
			builder.comment(mapper.getDescription())
					.translation(mapper.getTranslationKey())
					.push(mapper.getConfigPath());
			mappersEnabledConfig.put(mapper.getName(),
					CachedBooleanValue.wrap(this, PEConfigTranslations.MAPPING_MAPPER_ENABLED.applyToBuilder(builder).define("enabled", mapper.isAvailable()))
			);
			mapper.addConfigOptions(builder);
			builder.pop();
		}
		builder.pop();

		PEConfigTranslations.MAPPING_PROCESSORS.applyToBuilder(builder).push("processors");
		processorConfigs = new HashMap<>(processors.size());
		for (IDataComponentProcessor processor : processors) {
			processorConfigs.put(processor.getName(), new ProcessorConfig(this, builder, processor));
		}
		builder.pop();


		configSpec = builder.build();
	}

	public static boolean dumpToFile() {
		return INSTANCE != null && INSTANCE.dumpToFile.get();
	}

	public static boolean usePregenerated() {
		return INSTANCE != null && INSTANCE.usePregenerated.get();
	}

	public static boolean logExploits() {
		return INSTANCE == null || INSTANCE.logExploits.get();
	}

	public static boolean recoverMissingItemEmc() {
		return INSTANCE != null && INSTANCE.recoverMissingItemEmc.get();
	}

	/**
	 * @return True if the given {@link IEMCMapper} is enabled.
	 */
	public static boolean isEnabled(IEMCMapper<NormalizedSimpleStack, Long> mapper) {
		if (INSTANCE == null) {
			return mapper.isAvailable();
		}
		String name = mapper.getName();
		BooleanSupplier isEnabled = INSTANCE.mappersEnabledConfig.get(name);
		if (isEnabled == null) {
			PECore.LOGGER.warn("Mapper Config: '{}' is missing from the config.", name);
			return mapper.isAvailable();
		}
		return isEnabled.getAsBoolean();
	}

	/**
	 * @return True if the given {@link IDataComponentProcessor} is enabled.
	 */
	public static boolean isEnabled(IDataComponentProcessor processor) {
		if (INSTANCE == null) {
			return processor.isAvailable();
		}
		String name = processor.getName();
		ProcessorConfig processorConfig = INSTANCE.processorConfigs.get(name);
		if (processorConfig == null) {
			PECore.LOGGER.warn("Processor Config: '{}' is missing from the config.", name);
			return processor.isAvailable();
		}
		return processorConfig.enabled.get();
	}

	/**
	 * @return True if the given {@link IDataComponentProcessor} should contribute to the persistent data.
	 */
	public static boolean hasPersistent(IDataComponentProcessor processor) {
		if (INSTANCE == null) {
			return processor.hasPersistentComponents() && processor.usePersistentComponents();
		}
		String name = processor.getName();
		ProcessorConfig processorConfig = INSTANCE.processorConfigs.get(name);
		if (processorConfig == null) {
			PECore.LOGGER.warn("Persistent processor Config: '{}' is missing from the config.", name);
			return processor.hasPersistentComponents() && processor.usePersistentComponents();
		} else if (processorConfig.persistent == null) {
			if (processor.hasPersistentComponents()) {
				PECore.LOGGER.warn("Processor Config: '{}' has persistent Data Components but is missing the config option.", name);
				return processor.usePersistentComponents();
			}
			return false;
		}
		return processorConfig.persistent.get();
	}

	@Override
	public String getFileName() {
		return "mapping";
	}

	@Override
	public String getTranslation() {
		return "EMC Mapper Config";
	}

	@Override
	public ModConfigSpec getConfigSpec() {
		return configSpec;
	}

	@Override
	public Type getConfigType() {
		return Type.SERVER;
	}

	private static class ProcessorConfig {

		private final CachedBooleanValue enabled;
		@Nullable
		private final CachedBooleanValue persistent;

		private ProcessorConfig(IPEConfig config, ModConfigSpec.Builder builder, IDataComponentProcessor processor) {
			builder.comment(processor.getDescription())
					.translation(processor.getTranslationKey())
					.push(processor.getConfigPath());
			enabled = CachedBooleanValue.wrap(config, PEConfigTranslations.DCP_ENABLED.applyToBuilder(builder).define("enabled", processor.isAvailable()));
			//If it is enabled and was previously disabled, update the processor data for it,
			// otherwise if it was previously enabled and now is disabled we clear the cache for it
			enabled.addInvalidationListener(() -> processor.updateCachedValues(enabled.get() ? IEMCProxy.INSTANCE : null));
			if (processor.hasPersistentComponents()) {
				persistent = CachedBooleanValue.wrap(config, PEConfigTranslations.DCP_PERSISTENT.applyToBuilder(builder).define("persistent", processor.usePersistentComponents()));
			} else {
				persistent = null;
			}
			processor.addConfigOptions(builder);
			builder.pop();
		}
	}
}
