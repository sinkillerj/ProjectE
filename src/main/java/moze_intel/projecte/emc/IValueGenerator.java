package moze_intel.projecte.emc;

import moze_intel.projecte.emc.collector.IExtendedMappingCollector;
import moze_intel.projecte.emc.collector.IMappingCollector;

import java.util.Map;

public interface IValueGenerator<T, V extends Comparable<V>, A extends IValueArithmetic> extends IExtendedMappingCollector<T, V, A>
{
	public Map<T, V> generateValues();
}
