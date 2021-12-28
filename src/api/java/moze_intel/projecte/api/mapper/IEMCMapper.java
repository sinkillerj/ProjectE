package moze_intel.projecte.api.mapper;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import moze_intel.projecte.api.mapper.collector.IMappingCollector;
import net.minecraft.server.ServerResources;
import net.minecraft.server.packs.resources.ResourceManager;

/**
 * Interface for Classes that want to make Contributions to the EMC Mapping.
 *
 * @param <T> The type, that is used to uniquely identify Items/Blocks/Everything
 * @param <V> The type for the EMC Value
 */
public interface IEMCMapper<T, V extends Comparable<V>> {

	/**
	 * A unique Name for the {@link IEMCMapper}. This is used to identify the {@link IEMCMapper} in the Configuration.
	 *
	 * @return A unique Name
	 */
	String getName();

	/**
	 * A Description, that will be included as a Comment in the Configuration File
	 *
	 * @return A <b>short</b> description
	 */
	String getDescription();

	/**
	 * This method is used to determine the default for enabling/disabling this {@link IEMCMapper}. If this returns {@code false} {@link #addMappings} will not be
	 * called.
	 *
	 * @return {@code true} if you want {@link #addMappings} to be called, {@code false} otherwise.
	 */
	default boolean isAvailable() {
		return true;
	}

	/**
	 * The method that allows the {@link IEMCMapper} to contribute to the EMC Mapping. Use the methods provided by the {@link IMappingCollector}. <br/> Use the config
	 * object to generate a useful Configuration for your {@link IEMCMapper}. <br/> The Configuration Object will be a {@link
	 * com.electronwill.nightconfig.core.file.CommentedFileConfig} representing the top-level mapping.cfg file. Please use properly prefixed config keys and do not
	 * clobber those not belonging to your mapper
	 */
	void addMappings(IMappingCollector<T, V> mapper, CommentedFileConfig config, ServerResources dataPackRegistries, ResourceManager resourceManager);
}