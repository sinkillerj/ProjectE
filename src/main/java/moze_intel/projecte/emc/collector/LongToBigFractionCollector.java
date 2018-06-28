package moze_intel.projecte.emc.collector;

import moze_intel.projecte.emc.arithmetics.IValueArithmetic;
import org.apache.commons.math3.fraction.BigFraction;

import java.util.Map;

public class LongToBigFractionCollector<T, A extends IValueArithmetic> extends AbstractMappingCollector<T, Long, A>
{
    private final IExtendedMappingCollector<T, BigFraction, A> inner;

    public LongToBigFractionCollector(IExtendedMappingCollector<T, BigFraction, A> inner) {
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
        inner.setValueBefore(something, new BigFraction(value));
    }

    @Override
    public void setValueAfter(T something, Long value)
    {
        inner.setValueAfter(something, new BigFraction(value));
    }
}