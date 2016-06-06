package moze_intel.projecte.emc.generators;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.math.Fraction;

import java.util.Map;

/**
 * Composes another IValueGenerator, and truncates all fractional values towards 0.
 * @param <T> The type we are generating values for
 */
public class FractionToIntGenerator<T> implements IValueGenerator<T, Integer>
{
	private final IValueGenerator<T, Fraction> inner;

	public FractionToIntGenerator(IValueGenerator<T, Fraction> inner) {
		this.inner = inner;
	}

	@Override
	public Map<T, Integer> generateValues()
	{
		Map<T, Fraction> innerResult = inner.generateValues();
		Map<T, Integer> myResult = Maps.newHashMap();
		for (Map.Entry<T, Fraction> entry: innerResult.entrySet())
		{
			Fraction value = entry.getValue();
			if (value.intValue() > 0)
			{
				myResult.put(entry.getKey(), value.intValue());
			}
		}
		return myResult;
	}
}
