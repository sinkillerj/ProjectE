package moze_intel.projecte.emc.generators;

import org.apache.commons.lang3.math.Fraction;

import java.util.Map;

public class DestructionValueMultiplier<T> implements IMultiValueGenerator<T, Fraction>
{
	private final Fraction multiplier;
	IValueGenerator<T, Fraction> inner;
	public DestructionValueMultiplier(IValueGenerator<T, Fraction> inner, Fraction multiplier) {
		this.inner = inner;
		this.multiplier = multiplier;
	}
	@Override
	public void generateValues(Map valuesForCreation, Map valuesForDestruction)
	{
		Map<T, Fraction> innerValues = inner.generateValues();
		valuesForCreation.putAll(innerValues);
		for (Map.Entry<T, Fraction> entry: innerValues.entrySet()) {
			valuesForDestruction.put(entry.getKey(), multiplier.multiplyBy(entry.getValue()));
		}
	}
}
