package moze_intel.projecte.api.mapper;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import moze_intel.projecte.api.mapper.collector.IMappingCollector;
import net.minecraft.resources.IResourceManager;

/**
 * Interface for Classes that want to make Contributions to the EMC Mapping.
 * @param <T> The type, that is used to uniquely identify Items/Blocks/Everything
 * @param <V> The type for the EMC Value
 */
//TODO: Make it so that it is possible/easier to register custom IEMCMappers.
// Maybe we want to do it in a similar way to having an annotation to find them for registering? (Similar to how JEI does their plugins)
public interface IEMCMapper<T, V extends Comparable<V>> {
	/**
	 * A unique Name for the IEMCMapper. This is used to identify the IEMCMapper in the Configuration.
	 * @return A unique Name
	 */
	String getName();

	/**
	 * A Description, that will be included as a Comment in the Configuration File
	 * @return A <b>short</b> description
	 */
	String getDescription();

	/**
	 * This method is used to determine if this EMCMapper can work in the current environment.
	 * If this returns {@code false} {@link #addMappings} will not be called.<br/>
	 * This method will also be used to determine the default for enabling/disabling this IEMCMapper
	 * @return {@code true} if you want {@link #addMappings} to be called, {@code false} otherwise.
	 */
	boolean isAvailable();

	/**
	 * The method that allows the IEMCMapper to contribute to the EMC Mapping. Use the methods provided by the {@link IMappingCollector}.
	 * <br/>
	 * Use the config object to generate a useful Configuration for your IEMCMapper.
	 * <br/>
	 * The Configuration Object will be a {@link com.electronwill.nightconfig.core.file.CommentedFileConfig} representing the top-level mapping.cfg file.
	 * Please use properly prefixed config keys and do not clobber those not belonging to your mapper
	 * @param mapper
	 * @param config
	 * @param resourceManager
	 */
	void addMappings(IMappingCollector<T, V> mapper, CommentedFileConfig config, IResourceManager resourceManager);
}
