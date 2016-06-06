package moze_intel.projecte.emc.arithmetics;

/**
 * Defines the mathematical arithmetic to be used when calculating values.
 * For example, {@code <T>} could be {@link Double} or {@link Long}.
 * @param <T> The underlying type
 */
public interface IValueArithmetic<T extends Comparable<T>> {
	public boolean isZero(T value);
	public T getZero();
	public T add(T a, T b);
	public T mul(int a, T b);
	public T div(T a, int b);

	/**
	 * An item that has a "free" value contributes no additional cost to
	 * items which are derived from the free item
	 * @return The "free" value for this arithmetic.
     */
	public T getFree();
	public boolean isFree(T value);
}
