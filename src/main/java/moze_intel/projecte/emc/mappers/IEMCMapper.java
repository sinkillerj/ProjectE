package moze_intel.projecte.emc.mappers;

import moze_intel.projecte.emc.IMappingCollector;

public interface IEMCMapper<T> {
    public void addMappings(IMappingCollector<T> mapper);
}
