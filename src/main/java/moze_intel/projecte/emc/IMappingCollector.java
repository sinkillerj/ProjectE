package moze_intel.projecte.emc;

import java.util.Map;

/**
 * A Class that is used to collect Contributions to the EMC Mapping.
 *
 * @param <T> The type, that is used to uniquely identify Items/Blocks/Everything
 * @param <V> The type for the EMC Value
 */
public interface IMappingCollector<T, V extends Comparable<V>> {
	/**
	 * There are different Ways to set a fixed emc value for something.
	 */
	public static enum FixedValue {
		/**
		 * The most safe way to assign an EMC value.<br/>
		 * The Mapper is free to overwrite the suggested EMC value, if it finds a way to create the item with fewer EMC.
		 */
		SuggestionAndInherit,
		/**
		 * The default way to assign an EMC value. <br/>
		 * The value is set and can be used to calculate EMC values for things that use the item as an ingredient.
		 */
		FixAndInherit,
		/**
		 * The mapper can use its default mapping to calculate a value for the item and inherit this value to other items.
		 * This item will get the specified value after the inheritance has taken place
		 */
		FixAfterInherit,
		/**
		 * The mapper will assign a value of 0 to the item before doing the inheritance, so it can not be used as an ingredient.
		 * <br/>
		 * When the inheritance is finished it will overwrite that artificial value of 0 with the specified EMC value
		 */
		FixAndDoNotInherit
	}

	/**
	 * Add a Conversion that produced {@code outnumber} items of {@code output} by consuming the ingredients, that are specified in the {@code ingredientsWithAmount} Map.<br/>
	 * The Map contains the ingredients and the amount of how many are consumed. It can contain negative amounts, if you get multiple items back.
	 * An amount of 0 will indicate, that the {@code output} should only get an EMC value assigned, if the zero-amount-ingredient also has an EMC value.
	 * @param outnumber How many items are produced
	 * @param output What is produced
	 * @param ingredientsWithAmount What is consumed and how many of it
	 */
	public void addConversion(int outnumber, T output, Map<T, Integer> ingredientsWithAmount);

	/**
	 * The same as {@link #addConversion(int, Object, java.util.Map)}, but adds an extra cost to the Conversion.
	 * @param outnumber How many items are produced
	 * @param output What is produced
	 * @param ingredientsWithAmount What is consumed
	 * @param baseValueForConversion A base cost for performing the Conversion
	 */
	public void addConversion(int outnumber, T output, Map<T, Integer> ingredientsWithAmount, V baseValueForConversion);

	/**
	 * Add a Conversion that produced {@code outnumber} items of {@code output} by consuming the {@code ingredients}. <br/>
	 * Each entry in {@code ingredients} is 1 item. May contain the same items multiple times.
	 * @param outnumber How many items are produced
	 * @param output What is produced
	 * @param ingredients What is consumed
	 */
	public void addConversion(int outnumber, T output, Iterable<T> ingredients);

	/**
	 * The same as {@link #addConversion(int, Object, Iterable)}, but adds an extra cost to the Conversion.
	 * @param outnumber How many items are produced
	 * @param output What is produced
	 * @param ingredients What is consumed
	 * @param baseValueForConversion A base cost for performing the Conversion
	 */
	public void addConversion(int outnumber, T output, Iterable<T> ingredients, V baseValueForConversion);

	/**
	 * Set a fixed {@code value} for {@code something}.<br/>
	 * {@code type} controls how the Value should be handled when calculating the values for other items.<br/>
	 * {@code value} has to be >= 0 or {@link moze_intel.projecte.emc.IValueArithmetic#isFree(Comparable) Free}, which indicates that {@code something} can be used in
	 * Conversions, but does not add anything to the value of the Conversion-result.
	 * @param something The thing that should get the Value.
	 * @param value The value. >= 0 or {@link moze_intel.projecte.emc.IValueArithmetic#isFree(Comparable)}{@code == true}
	 * @param type How the value should be assigned. See {@link moze_intel.projecte.emc.IMappingCollector.FixedValue}
	 */
	public void setValue(T something, V value, FixedValue type);

}
