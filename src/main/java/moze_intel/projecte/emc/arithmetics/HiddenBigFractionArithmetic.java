package moze_intel.projecte.emc.arithmetics;

import org.apache.commons.math3.fraction.BigFraction;

public class HiddenBigFractionArithmetic extends FullBigFractionArithmetic
{
    @Override
    public BigFraction div(BigFraction a, long b)
    {
        BigFraction result = super.div(a, b);
        if (BigFraction.ZERO.compareTo(result) <= 0 && result.compareTo(BigFraction.ONE) < 0)
        {
            return result;
        }
        return new BigFraction(result.longValue());
    }

}