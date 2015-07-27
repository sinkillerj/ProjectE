package moze_intel.projecte.emc.valuetranslators;

import moze_intel.projecte.emc.IValueGenerator;

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
	public void addConversion(int outnumber, T output, Map<T, Integer> ingredientsWithAmount)
	{
		inner.addConversion(outnumber, output, ingredientsWithAmount);
	}

	@Override
	public void addConversion(int outnumber, T output, Map<T, Integer> ingredientsWithAmount, OUT baseValueForConversion)
	{
		inner.addConversion(outnumber, output, ingredientsWithAmount, translateValue(baseValueForConversion));
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

	@Override
	public void setValueFromConversion(int outnumber, T something, Iterable<T> ingredients)
	{
		inner.setValueFromConversion(outnumber, something, ingredients);
	}

	@Override
	public void setValueFromConversion(int outnumber, T something, Map<T, Integer> ingredientsWithAmount)
	{
		inner.setValueFromConversion(outnumber, something, ingredientsWithAmount);
	}
}
