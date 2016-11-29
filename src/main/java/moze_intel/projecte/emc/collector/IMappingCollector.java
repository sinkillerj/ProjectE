package moze_intel.projecte.emc.collector;

import moze_intel.projecte.emc.arithmetics.IValueArithmetic;

import java.util.Map;

/**
 * A Class that is used to collect Contributions to the EMC Mapping.
 *
 * @param <T> The type, that is used to uniquely identify Items/Blocks/Everything
 * @param <V> The type for the EMC Value
 */
public interface IMappingCollector<T, V extends Comparable<V>> {
	/**
	 * Add a Conversion that produced {@code outnumber} items of {@code output} by consuming the ingredients, that are specified in the {@code ingredientsWithAmount} Map.<br/>
	 * The Map contains the ingredients and the amount of how many are consumed. It can contain negative amounts, if you get multiple items back.
	 * An amount of 0 will indicate, that the {@code output} should only get an EMC value assigned, if the zero-amount-ingredient also has an EMC value.
	 * @param outnumber How many items are produced
	 * @param output What is produced
	 * @param ingredientsWithAmount What is consumed and how many of it
	 */
	void addConversion(int outnumber, T output, Map<T, Integer> ingredientsWithAmount);

	/**
	 * Add a Conversion that produced {@code outnumber} items of {@code output} by consuming the {@code ingredients}. <br/>
	 * Each entry in {@code ingredients} is 1 item. May contain the same items multiple times.
	 * @param outnumber How many items are produced
	 * @param output What is produced
	 * @param ingredients What is consumed
	 */
	void addConversion(int outnumber, T output, Iterable<T> ingredients);

	/**
	 * Set a fixed {@code value} for {@code something} before the calculation happens.<br/>
	 * {@code value} has to be >= 0 or {@link IValueArithmetic#isFree(Comparable) Free}, which indicates that {@code something} can be used in
	 * Conversions, but does not add anything to the value of the Conversion-result.<br/>
	 * ALL {@code setValueAfter} WILL BE CLEARED!
	 * @param something The thing that should get the Value.
	 * @param value The value. >= 0 or {@link IValueArithmetic#isFree(Comparable)}{@code == true}
	 */
	void setValueBefore(T something, V value);

	/**
	 * Set a fixed {@code value} for {@code something} after the calculation was done. This value will not be propagated.<br/>
	 * {@code value} has to be >= 0. <br/>
	 * THIS WILL BE OVERWRITTEN, IF {@code setValueBefore} IS CALLED!
	 * @param something The thing that should get the Value.
	 * @param value The value. >= 0
	 */
	void setValueAfter(T something, V value);

	void setValueFromConversion(int outnumber, T something, Iterable<T> ingredients);

	void setValueFromConversion(int outnumber, T something, Map<T, Integer> ingredientsWithAmount);

	void finishCollection();
}
