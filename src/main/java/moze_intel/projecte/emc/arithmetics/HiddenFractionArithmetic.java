package moze_intel.projecte.emc.arithmetics;

import org.apache.commons.lang3.math.Fraction;

public class HiddenFractionArithmetic extends FullFractionArithmetic
{
	@Override
	public Fraction div(Fraction a, int b)
	{
		try
		{
			if (this.isFree(a)) return getFree();
			Fraction result = a.divideBy(Fraction.getFraction(b, 1));
			if (Fraction.ZERO.compareTo(result) <= 0 && result.compareTo(Fraction.ONE) < 0)
			{
				return result;
			}
			return Fraction.getFraction(result.intValue(), 1);
		} catch (ArithmeticException e) {
			// The documentation for Fraction.divideBy states that this Exception is only thrown if
			// * you try to divide by `null` (We are not doing this)
			// * the numerator or denumerator exceeds Integer.MAX_VALUE.
			// Because we only divide by values > 1 it means the denominator overflowed.
			// This means we reached (something > 1) /infinity, which is ~0.
			return Fraction.ZERO;
		}
	}

}
