package moze_intel.projecte.emc.arithmetics;

public interface IValueArithmetic<T extends Comparable<T>> {
	public boolean isZero(T value);
	public T getZero();
	public T add(T a, T b);
	public T mul(int a, T b);
	public T div(T a, int b);
	public T getFree();
	public boolean isFree(T value);
}
