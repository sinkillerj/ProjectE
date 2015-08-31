package moze_intel.projecte.emc.valuetranslators;

import moze_intel.projecte.emc.IValueArithmetic;
import moze_intel.projecte.emc.IValueGenerator;

import java.util.Map;

public abstract class AbstractTranslator<T, IN extends Comparable<IN>, OUT extends Comparable<OUT>, A extends IValueArithmetic> implements IValueGenerator<T, OUT, A>
{
	protected IValueGenerator<T, IN, A> inner;
	public AbstractTranslator(IValueGenerator<T, IN, A> inner)
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

	@Override
	public void addConversion(int outnumber, T output, Map<T, Integer> ingredientsWithAmount, A arithmeticForConversion)
	{
		inner.addConversion(outnumber, output, ingredientsWithAmount, arithmeticForConversion);
	}

	@Override
	public void addConversion(int outnumber, T output, Iterable<T> ingredients, A arithmeticForConversion)
	{
		inner.addConversion(outnumber, output, ingredients, arithmeticForConversion);
	}

	@Override
	public A getArithmetic() {
		return inner.getArithmetic();
	}
}
