package moze_intel.projecte.emc;

import java.util.Map;

public interface IValueGenerator<T, V extends Comparable<V>> extends IMappingCollector<T, V>
{
	public Map<T, V> generateValues();
}
