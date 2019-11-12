package moze_intel.projecte.config;

import java.util.Arrays;
import java.util.List;
import javax.annotation.Nonnull;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.nbt.INBTProcessor;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.fml.config.ModConfig.Type;

/**
 * For config options having to do with NBT Processors. Syncs from server to client.
 */
public class NBTProcessorConfig {

	private static NBTProcessorConfig INSTANCE;
	private static ForgeConfigSpec spec;
	private static PEModConfig dummyConfig;

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
	public static void setup(@Nonnull List<INBTProcessor> processors) {
		if (INSTANCE == null) {
			ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
			INSTANCE = new NBTProcessorConfig(builder, processors);
			spec = builder.build();
			dummyConfig = new PEModConfig(Type.SERVER, spec, PECore.MOD_CONTAINER, "processing");
		}
	}

	private NBTProcessorConfig(@Nonnull ForgeConfigSpec.Builder builder, @Nonnull List<INBTProcessor> processors) {
		builder.comment("This config is used to control which NBT Processors get used, and which ones actually contribute to the persistent NBT data that gets " +
						"saved to knowledge/copied in a condenser.",
				"To disable an NBT Processor set the '" + ENABLED + "' option for it to false.",
				"To disable an NBT Processor from contributing to the persistent data set the '" + PERSISTENT + "' option for it to false. Note: that if there is no " +
				PERSISTENT + "' config option, the NBT Processor never has any persistent data.",
				"The config options in this file are synced from server to client, as the processors get used dynamically to calculate/preview EMC values for items " +
				"and are not included in the synced EMC mappings.")
				.push(MAIN_KEY);
		for (INBTProcessor processor : processors) {
			builder.comment(processor.getDescription()).push(processor.getName());
			builder.define(ENABLED, processor.isAvailable());
			if (processor.hasPersistentNBT()) {
				builder.define(PERSISTENT, processor.usePersistentNBT());
			}
			builder.pop();
		}
		builder.pop();
	}

	/**
	 * @return True if the given {@link INBTProcessor} is enabled.
	 */
	public static boolean isEnabled(INBTProcessor processor) {
		return getValue(processor, ENABLED);
	}

	/**
	 * @return True if the given {@link INBTProcessor} should contribute to the persistent data.
	 */
	public static boolean hasPersistent(INBTProcessor processor) {
		return getValue(processor, PERSISTENT);
	}

	/**
	 * Gets a boolean value from the config
	 */
	private static boolean getValue(INBTProcessor processor, String key) {
		return ((BooleanValue) spec.getValues().get(Arrays.asList(MAIN_KEY, processor.getName(), key))).get();
	}
}