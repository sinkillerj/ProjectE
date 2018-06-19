package moze_intel.projecte.emc.arithmetics;

import org.apache.commons.math3.fraction.BigFraction;

public class FullBigFractionArithmetic implements IValueArithmetic<BigFraction>
{
    @Override
    public boolean isZero(BigFraction value)
    {
        return BigFraction.ZERO.equals(value);
    }

    @Override
    public BigFraction getZero()
    {
        return BigFraction.ZERO;
    }

    @Override
    public BigFraction add(BigFraction a, BigFraction b)
    {
        if (isFree(a)) return b;
        if (isFree(b)) return a;

        return a.add(b);
    }

    @Override
    public BigFraction mul(long a, BigFraction b)
    {
        if (this.isFree(b)) return getFree();
        return b.multiply(a);
    }

    @Override
    public BigFraction div(BigFraction a, long b)
    {
        try
        {
            if (this.isFree(a)) return getFree();
            if (a.getDenominatorAsLong() <= 0) {
                //Overflowed a long as BigFraction can go past Long.MAX_VALUE
                return BigFraction.ZERO;
            }
            return a.divide(b);
        } catch (ArithmeticException e) {
            // The documentation for Fraction.divideBy states that this Exception is only thrown if
            // * you try to divide by `null` (We are not doing this)
            // * the numerator or denumerator exceeds Integer.MAX_VALUE.
            // Because we only divide by values > 1 it means the denominator overflowed.
            // This means we reached (something > 1) /infinity, which is ~0.
            return BigFraction.ZERO;
        }
    }

    @Override
    public BigFraction getFree()
    {
        return new BigFraction(Long.MIN_VALUE);
    }

    @Override
    public boolean isFree(BigFraction value)
    {
        return value.getNumeratorAsLong() == Long.MIN_VALUE;
    }
}