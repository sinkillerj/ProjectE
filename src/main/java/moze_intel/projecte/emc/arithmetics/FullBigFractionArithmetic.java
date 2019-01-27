package moze_intel.projecte.emc.arithmetics;

import org.apache.commons.math3.fraction.BigFraction;

import java.math.BigInteger;

public class FullBigFractionArithmetic implements IValueArithmetic<BigFraction>
{
    private final BigInteger MAX_LONG = BigInteger.valueOf(Long.MAX_VALUE);

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
        if (this.isFree(a)) return getFree();
        if (b == 0) return BigFraction.ZERO;
        BigFraction result = a.divide(b);
        if (result.getNumerator().compareTo(MAX_LONG) > 0 || result.getDenominator().compareTo(MAX_LONG) > 0) {
            //Overflowed a long as BigFraction can go past Long.MAX_VALUE
            return BigFraction.ZERO;
        }
        return result;
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