package moze_intel.projecte.emc.mappers;

import moze_intel.projecte.emc.collector.IMappingCollector;
import net.minecraftforge.common.config.Configuration;

/**
 * Interface for Classes that want to make Contributions to the EMC Mapping.
 * @param <T> The type, that is used to uniquely identify Items/Blocks/Everything
 * @param <V> The type for the EMC Value
 */
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
	 * The Configuration Object will be a {@link moze_intel.projecte.utils.PrefixConfiguration},
	 * so you can use {@code ""} (Empty String) as a Category to write into the root-Category that is created for your IEMCMapper.
	 * @param mapper
	 * @param config
	 */
	void addMappings(IMappingCollector<T, V> mapper, Configuration config);
}
