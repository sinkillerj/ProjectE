package moze_intel.projecte.emc.generators;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.math.Fraction;

import java.util.HashMap;
import java.util.Map;

/**
 * Composes another IValueGenerator, and truncates all fractional values towards 0.
 * @param <T> The type we are generating values for
 */
public class DoubleToLongGenerator<T> implements IValueGenerator<T, Long>
{
	private final IValueGenerator<T, Double> inner;

	public DoubleToLongGenerator(IValueGenerator<T, Double> inner) {
		this.inner = inner;
	}

	@Override
	public Map<T, Long> generateValues()
	{
		Map<T, Double> innerResult = inner.generateValues();
		Map<T, Long> myResult = new HashMap<>();
		for (Map.Entry<T, Double> entry: innerResult.entrySet())
		{
			Double value = entry.getValue();
			if (value.longValue() > 0)
			{
				myResult.put(entry.getKey(), value.longValue());
			}
		}
		return myResult;
	}
}
