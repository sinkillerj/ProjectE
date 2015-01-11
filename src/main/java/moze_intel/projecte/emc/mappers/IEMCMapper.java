package moze_intel.projecte.emc.mappers;

import moze_intel.projecte.emc.IMappingCollector;

public interface IEMCMapper<T, V extends Comparable<V>> {
	public void addMappings(IMappingCollector<T, V> mapper);
}
