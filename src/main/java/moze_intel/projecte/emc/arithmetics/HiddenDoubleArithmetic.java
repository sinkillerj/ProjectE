package moze_intel.projecte.emc.arithmetics;

public class HiddenDoubleArithmetic extends DoubleArithmetic {
    @Override
    public Double div(Double a, long b) {
        if (this.isFree(a)) return getFree();
        double result = a / b;
        if (result >= 0 && result < 1)
        {
            return result;
        }
        return (double) (long) result;
    }
}