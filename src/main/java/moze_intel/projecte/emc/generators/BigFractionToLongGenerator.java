package moze_intel.projecte.emc.generators;

import org.apache.commons.math3.fraction.BigFraction;

import java.util.HashMap;
import java.util.Map;

/**
 * Composes another IValueGenerator, and truncates all fractional values towards 0.
 * @param <T> The type we are generating values for
 */
public class BigFractionToLongGenerator<T> implements IValueGenerator<T, Long>
{
    private final IValueGenerator<T, BigFraction> inner;

    public BigFractionToLongGenerator(IValueGenerator<T, BigFraction> inner) {
        this.inner = inner;
    }

    @Override
    public Map<T, Long> generateValues()
    {
        Map<T, BigFraction> innerResult = inner.generateValues();
        Map<T, Long> myResult = new HashMap<>();
        for (Map.Entry<T, BigFraction> entry: innerResult.entrySet())
        {
            BigFraction value = entry.getValue();
            if (value.longValue() > 0)
            {
                myResult.put(entry.getKey(), value.longValue());
            }
        }
        return myResult;
    }
}