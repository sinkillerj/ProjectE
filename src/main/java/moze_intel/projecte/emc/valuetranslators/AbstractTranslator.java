package moze_intel.projecte.emc.valuetranslators;

import moze_intel.projecte.emc.IValueGenerator;

import org.apache.commons.lang3.math.Fraction;

import java.util.Map;

public abstract class AbstractTranslator<T, IN extends Comparable<IN>, OUT extends Comparable<OUT>> implements IValueGenerator<T, OUT>
{

	protected IValueGenerator<T, IN> inner;
	public AbstractTranslator(IValueGenerator<T, IN> inner)
	{
		this.inner = inner;
	}

	public abstract IN translateValue(OUT v);

	@Override
	public void addConversionMultiple(int outnumber, T output, Map<T, Integer> ingredientsWithAmount)
	{
		inner.addConversionMultiple(outnumber, output, ingredientsWithAmount);
	}

	@Override
	public void addConversionMultiple(int outnumber, T output, Map<T, Integer> ingredientsWithAmount, OUT baseValueForConversion)
	{
		inner.addConversionMultiple(outnumber, output, ingredientsWithAmount, translateValue(baseValueForConversion));
	}

	@Override
	public void addConversion(int outnumber, T output, Iterable<T> ingredients)
	{
		inner.addConversion(outnumber, output, ingredients);
	}

	@Override
	public void addConversion(int outnumber, T output, Iterable<T> ingredients, OUT baseValueForConversion)
	{
		inner.addConversion(outnumber,output, ingredients, translateValue(baseValueForConversion));
	}

	@Override
	public void setValue(T something, OUT value, FixedValue type)
	{
		inner.setValue(something, translateValue(value), type);
	}
}
