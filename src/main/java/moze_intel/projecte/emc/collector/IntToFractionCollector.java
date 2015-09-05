package moze_intel.projecte.emc.collector;

import org.apache.commons.lang3.math.Fraction;

import java.util.Map;

public class IntToFractionCollector<T> extends AbstractMappingCollector<T, Integer>
{
	MappingCollector<T, Fraction> inner;
	public IntToFractionCollector(MappingCollector<T, Fraction> inner) {
		this.inner = inner;
	}
	@Override
	public void setValueFromConversion(int outnumber, T something, Map<T, Integer> ingredientsWithAmount)
	{
		inner.setValueFromConversion(outnumber, something, ingredientsWithAmount);
	}

	@Override
	public void addConversion(int outnumber, T output, Map<T, Integer> ingredientsWithAmount)
	{
		inner.addConversion(outnumber, output, ingredientsWithAmount);
	}

	@Override
	public void setValueBefore(T something, Integer value)
	{
		inner.setValueBefore(something, Fraction.getFraction(value, 1));
	}

	@Override
	public void setValueAfter(T something, Integer value)
	{
		inner.setValueAfter(something, Fraction.getFraction(value, 1));
	}
}
