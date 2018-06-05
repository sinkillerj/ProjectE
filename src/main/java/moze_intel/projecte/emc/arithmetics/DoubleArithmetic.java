package moze_intel.projecte.emc.arithmetics;

import org.apache.commons.lang3.math.Fraction;

/**
 * Created by Voyager on 04.05.2018.
 */
public class DoubleArithmetic implements IValueArithmetic<Double> {
    @Override
    public boolean isZero(Double value) {
        return value == 0;
    }

    @Override
    public Double getZero() {
        return 0d;
    }

    @Override
    public Double add(Double a, Double b) {
        if (isFree(a)) return b;
        if (isFree(b)) return a;

        return a + b;
    }

    @Override
    public Double mul(int a, Double b) {
        if (this.isFree(b)) return getFree();

        return b * a;
    }

    @Override
    public Double div(Double a, int b) {
        if (this.isFree(a)) return getFree();
        double result = a / b;
        if (result >= 0 && result < 1)
        {
            return result;
        }
        return (double) ((long) result);
    }

    @Override
    public Double getFree() {
        return Double.MIN_VALUE;
    }

    @Override
    public boolean isFree(Double value) {
        return value == getFree();
    }
}
