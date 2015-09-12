package moze_intel.projecte.emc.generators;

import java.util.Map;

public class SameValueMultiGenerator<T, V extends Comparable<V>> implements IMultiValueGenerator<T, V>
{
	IValueGenerator<T, V> inner;
	public SameValueMultiGenerator(IValueGenerator<T, V> inner) {
		this.inner = inner;
	}
	@Override
	public void generateValues(Map<T, V> valuesForCreation, Map<T, V> valuesForDestruction)
	{
		Map<T, V> innerValues = inner.generateValues();
		valuesForCreation.putAll(innerValues);
		valuesForDestruction.putAll(innerValues);
	}
}
