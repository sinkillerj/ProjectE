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
            if (a.getNumerator().compareTo(MAX_LONG) > 0 || a.getDenominator().compareTo(MAX_LONG) > 0) {
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
            return BigFraction.ZERO;
        }
    }

}