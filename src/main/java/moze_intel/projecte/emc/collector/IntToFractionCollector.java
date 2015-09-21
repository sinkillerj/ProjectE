package moze_intel.projecte.emc.collector;

import moze_intel.projecte.emc.arithmetics.IValueArithmetic;

import org.apache.commons.lang3.math.Fraction;

import java.util.Map;

public class IntToFractionCollector<T, A extends IValueArithmetic> extends AbstractMappingCollector<T, Integer, A>
{
	IExtendedMappingCollector<T, Fraction, A> inner;
	public IntToFractionCollector(IExtendedMappingCollector<T, Fraction, A> inner) {
		super(inner.getArithmetic());
		this.inner = inner;
	}
	@Override
	public void setValueFromConversion(int outnumber, T something, Map<T, Integer> ingredientsWithAmount)
	{
		inner.setValueFromConversion(outnumber, something, ingredientsWithAmount);
	}

	@Override
	public void addConversion(int outnumber, T output, Map<T, Integer> ingredientsWithAmount, A arithmeticForConversion)
	{
		inner.addConversion(outnumber, output, ingredientsWithAmount, arithmeticForConversion);
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
