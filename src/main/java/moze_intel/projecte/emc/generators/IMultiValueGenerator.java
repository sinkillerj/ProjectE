package moze_intel.projecte.emc.generators;

import java.util.Map;

public interface IMultiValueGenerator<T, V extends Comparable<V>>
{
	public void generateValues(Map<T, V> valuesForCreation, Map<T, V> valuesForDestruction);
}
