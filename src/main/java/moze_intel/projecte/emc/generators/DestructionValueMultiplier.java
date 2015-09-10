package moze_intel.projecte.emc.generators;

import org.apache.commons.lang3.math.Fraction;

import java.util.Map;

public class DestructionValueMultiplier<T> implements IMultiValueGenerator<T, Integer>
{
	private final Fraction multiplier;
	IValueGenerator<T, Integer> inner;
	public DestructionValueMultiplier(IValueGenerator<T, Integer> inner, Fraction multiplier) {
		this.inner = inner;
		this.multiplier = multiplier;
	}
	@Override
	public void generateValues(Map<T, Integer> valuesForCreation, Map<T, Integer> valuesForDestruction)
	{
		Map<T, Integer> innerValues = inner.generateValues();
		valuesForCreation.putAll(innerValues);
		for (Map.Entry<T, Integer> entry: innerValues.entrySet()) {
			valuesForDestruction.put(entry.getKey(), multiplier.multiplyBy(Fraction.getFraction(entry.getValue())).intValue());
		}
	}
}
