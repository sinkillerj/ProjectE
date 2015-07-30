package moze_intel.projecte.emc.valuetranslators;

import moze_intel.projecte.emc.IValueArithmetic;
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
	public void addConversion(int outnumber, T output, Iterable<T> ingredients)
	{
		inner.addConversion(outnumber, output, ingredients);
	}

	@Override
	public void setValueBefore(T something, OUT value)
	{
		inner.setValueBefore(something, translateValue(value));
	}

	@Override
	public void setValueAfter(T something, OUT value)
	{
		inner.setValueAfter(something, translateValue(value));
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
