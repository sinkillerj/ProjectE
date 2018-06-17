package moze_intel.projecte.emc.arithmetics;

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
        return 0D;
    }

    @Override
    public Double add(Double a, Double b) {
        if (isFree(a)) return b;
        if (isFree(b)) return a;

        if (a > 0 && b > 0 && a.longValue() + b.longValue() < 0) {
            return 0D;
        }
        return a + b;
    }

    @Override
    public Double mul(long a, Double b) {
        if (this.isFree(b)) return getFree();

        return b * a;
    }

    @Override
    public Double div(Double a, long b) {
        if (this.isFree(a)) return getFree();
        return a / b;
    }

    @Override
    public Double getFree() {
        return -Double.MAX_VALUE;
    }

    @Override
    public boolean isFree(Double value) {
        return value == -Double.MAX_VALUE;
    }
}
