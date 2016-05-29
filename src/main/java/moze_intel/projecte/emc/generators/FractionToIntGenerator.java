package moze_intel.projecte.emc.generators;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.math.Fraction;

import java.util.Map;

public class FractionToIntGenerator<T> implements IValueGenerator<T, Integer>
{
	private final IValueGenerator<T, Fraction> inner;

	public FractionToIntGenerator(IValueGenerator<T, Fraction> inner) {
		this.inner = inner;
	}

	@Override
	public Map<T, Integer> generateValues()
	{
		Map<T, Fraction> innerReslt = inner.generateValues();
		Map<T, Integer> myResult = Maps.newHashMap();
		for (Map.Entry<T, Fraction> entry: innerReslt.entrySet())
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
