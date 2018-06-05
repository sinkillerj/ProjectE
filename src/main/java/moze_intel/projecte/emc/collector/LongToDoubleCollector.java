package moze_intel.projecte.emc.collector;

import moze_intel.projecte.emc.arithmetics.IValueArithmetic;
import org.apache.commons.lang3.math.Fraction;

import java.util.Map;

public class LongToDoubleCollector<T, A extends IValueArithmetic> extends AbstractMappingCollector<T, Long, A>
{
	private final IExtendedMappingCollector<T, Double, A> inner;

	public LongToDoubleCollector(IExtendedMappingCollector<T, Double, A> inner) {
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
	public void setValueBefore(T something, Long value)
	{
		inner.setValueBefore(something, (double) value);
	}

	@Override
	public void setValueAfter(T something, Long value)
	{
		inner.setValueAfter(something, (double) value);
	}
}
