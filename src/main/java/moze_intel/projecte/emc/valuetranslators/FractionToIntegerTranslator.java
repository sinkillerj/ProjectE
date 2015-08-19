package moze_intel.projecte.emc.valuetranslators;

import moze_intel.projecte.emc.IValueGenerator;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.math.Fraction;

import java.util.Map;

public class FractionToIntegerTranslator<T> extends AbstractTranslator<T, Fraction, Integer>
{
	public FractionToIntegerTranslator(IValueGenerator<T, Fraction> inner)
	{
		super(inner);
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


	@Override
	public Fraction translateValue(Integer v)
	{
		return Fraction.getFraction(v, 1);
	}
}
