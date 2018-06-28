package moze_intel.projecte.emc.arithmetics;

/**
 * Defines the mathematical arithmetic to be used when calculating values.
 * For example, {@code <T>} could be {@link Double} or {@link Long}.
 * @param <T> The underlying type
 */
public interface IValueArithmetic<T extends Comparable<T>> {
	boolean isZero(T value);
	T getZero();
	T add(T a, T b);
	T mul(long a, T b);
	T div(T a, long b);

	/**
	 * An item that has a "free" value contributes no additional cost to
	 * items which are derived from the free item
	 * @return The "free" value for this arithmetic.
     */
	T getFree();
	boolean isFree(T value);
}
