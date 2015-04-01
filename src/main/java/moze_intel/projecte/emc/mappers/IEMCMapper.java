package moze_intel.projecte.emc.mappers;

import moze_intel.projecte.emc.IMappingCollector;
import net.minecraftforge.common.config.Configuration;

public interface IEMCMapper<T, V extends Comparable<V>> {
	public String getName();

	public String getDescription();

	public boolean isAvailable();

	public void addMappings(IMappingCollector<T, V> mapper, Configuration config);
}
