package moze_intel.projecte.emc.arithmetics;

import org.apache.commons.lang3.math.Fraction;

public class FullFractionArithmetic implements IValueArithmetic<Fraction>
{
	@Override
	public boolean isZero(Fraction value)
	{
		return value.getNumerator() == 0;
	}

	@Override
	public Fraction getZero()
	{
		return Fraction.ZERO;
	}

	@Override
	public Fraction add(Fraction a, Fraction b)
	{
		if (isFree(a)) return b;
		if (isFree(b)) return a;

		return a.add(b);
	}

	@Override
	public Fraction mul(int a, Fraction b)
	{
		if (this.isFree(b)) return getFree();
		return b.multiplyBy(Fraction.getFraction(a, 1));
	}

	@Override
	public Fraction div(Fraction a, int b)
	{
		try
		{
			if (this.isFree(a)) return getFree();
			return a.divideBy(Fraction.getFraction(b, 1));
		} catch (ArithmeticException e) {
			// The documentation for Fraction.divideBy states that this Exception is only thrown if
			// * you try to divide by `null` (We are not doing this)
			// * the numerator or denumerator exceeds Integer.MAX_VALUE.
			// Because we only divide by values > 1 it means the denominator overflowed.
			// This means we reached (something > 1) /infinity, which is ~0.
			return Fraction.ZERO;
		}
	}

	@Override
	public Fraction getFree()
	{
		return Fraction.getFraction(Integer.MIN_VALUE, 1);
	}

	@Override
	public boolean isFree(Fraction value)
	{
		return value.getNumerator() == Integer.MIN_VALUE;
	}
}
