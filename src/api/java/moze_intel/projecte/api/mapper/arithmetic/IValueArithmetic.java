package moze_intel.projecte.api.mapper.arithmetic;

/**
 * Defines the mathematical arithmetic to be used when calculating values. For example, {@code <T>} could be {@link Double} or {@link Long}.
 *
 * @param <T> The underlying type
 */
public interface IValueArithmetic<T extends Comparable<T>> {

	/**
	 * Checks if a given value is equal to {@link #getZero()}
	 *
	 * @param value The value to check.
	 *
	 * @return True if value is equal to {@link #getZero()}
	 */
	boolean isZero(T value);

	/**
	 * @return The value that represents "zero" in this {@link IValueArithmetic}
	 */
	T getZero();

	/**
	 * Adds two values together.
	 *
	 * @param a The first value to add.
	 * @param b The second value to add.
	 *
	 * @return The result of a + b
	 */
	T add(T a, T b);

	/**
	 * Multiplies two values together.
	 *
	 * @param a The first value to multiply.
	 * @param b The second value to multiply.
	 *
	 * @return The result of a * b
	 */
	T mul(long a, T b);

	/**
	 * Divides one value from another
	 *
	 * @param a The numerator.
	 * @param b The denominator.
	 *
	 * @return The result of a / b
	 */
	T div(T a, long b);

	/**
	 * An item that has a "free" value contributes no additional cost to items which are derived from the free item
	 *
	 * @return The "free" value for this arithmetic.
	 */
	T getFree();

	/**
	 * Checks if a given value is equal to {@link #getFree()}
	 *
	 * @param value The value to check.
	 *
	 * @return True if value is equal to {@link #getFree()}
	 */
	boolean isFree(T value);
}