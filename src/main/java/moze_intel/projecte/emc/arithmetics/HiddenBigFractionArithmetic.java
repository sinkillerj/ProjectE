package moze_intel.projecte.emc.arithmetics;

import org.apache.commons.math3.fraction.BigFraction;

public class HiddenBigFractionArithmetic extends FullBigFractionArithmetic
{
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
            BigFraction result = a.divide(b);
            if (BigFraction.ZERO.compareTo(result) <= 0 && result.compareTo(BigFraction.ONE) < 0)
            {
                return result;
            }
            return new BigFraction(result.longValue());
        } catch (ArithmeticException e) {
            // The documentation for Fraction.divideBy states that this Exception is only thrown if
            // * you try to divide by `null` (We are not doing this)
            // * the numerator or denumerator exceeds Integer.MAX_VALUE.
            // Because we only divide by values > 1 it means the denominator overflowed.
            // This means we reached (something > 1) /infinity, which is ~0.
            return BigFraction.ZERO;
        }
    }

}