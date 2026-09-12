package moze_intel.projecte.api.mapper.collector;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import moze_intel.projecte.api.mapper.arithmetic.IValueArithmetic;
import net.minecraft.core.HolderLookup;

/**
 * A Class that is used to collect Contributions to the EMC Mapping.
 *
 * @param <T> The type, that is used to uniquely identify Items/Blocks/Everything
 * @param <V> The type for the EMC Value
 */
public interface IMappingCollector<T, V extends Comparable<V>> {

	/**
	 * Add a Conversion that produced {@code outnumber} items of {@code output} by consuming the ingredients, that are specified in the {@code ingredientsWithAmount}
	 * Map.
	 * <p>
	 * <br/>
	 *
	 * The Map contains the ingredients and the amount of how many are consumed. It can contain negative amounts, if you get multiple items back. Entries with an
	 * amount of 0 are ignored and do not contribute value or create a dependency for the conversion.
	 *
	 * @param outnumber             How many items are produced
	 * @param output                What is produced
	 * @param ingredientsWithAmount What is consumed and how many of it
	 */
	void addConversion(int outnumber, T output, Object2IntMap<T> ingredientsWithAmount);

	/**
	 * Add a Conversion that produced {@code outnumber} items of {@code output} by consuming the {@code ingredients}.
	 * <p>
	 * <br/>
	 *
	 * Each entry in {@code ingredients} is 1 item. May contain the same items multiple times.
	 *
	 * @param outnumber   How many items are produced
	 * @param output      What is produced
	 * @param ingredients What is consumed
	 *
	 * @implNote This method internally combines the iterable into an {@link Object2IntMap}, merging any duplicates. If you are creating a list just to pass to this
	 * method and run into any performance issues, it is recommended to just directly pass in an {@link Object2IntMap} via
	 * {@link #addConversion(int, Object, Object2IntMap)} instead.
	 */
	void addConversion(int outnumber, T output, Iterable<T> ingredients);

	/**
	 * Set a fixed {@code value} for {@code something} before the calculation happens.
	 * <p>
	 * <br/>
	 *
	 * {@code value} has to be >= 0 or {@link IValueArithmetic#isFree(Comparable) Free}, which indicates that {@code something} can be used in Conversions, but does not
	 * add anything to the value of the Conversion-result.
	 * <p>
	 * <br/>
	 *
	 * ALL {@code setValueAfter} WILL BE CLEARED!
	 *
	 * @param something The thing that should get the Value.
	 * @param value     The value. >= 0 or {@link IValueArithmetic#isFree(Comparable)}{@code == true}
	 */
	void setValueBefore(T something, V value);

	/**
	 * Set a fixed {@code value} for {@code something} after the calculation was done. This value will not be propagated.
	 * <p>
	 * <br/>
	 *
	 * {@code value} has to be >= 0.
	 * <p>
	 * <br/>
	 *
	 * THIS WILL BE OVERWRITTEN, IF {@code setValueBefore} IS CALLED!
	 *
	 * @param something The thing that should get the Value.
	 * @param value     The value. >= 0
	 */
	void setValueAfter(T something, V value);

	/**
	 * Set a fixed {@code value} for {@code outnumber} items of {@code something} produced by consuming the {@code ingredients}.
	 *
	 * @param outnumber   How many items are produced
	 * @param something   The thing that should get the Value.
	 * @param ingredients What is consumed
	 *
	 * @implNote This method internally combines the iterable into an {@link Object2IntMap}, merging any duplicates. If you are creating a list just to pass to this
	 * method and run into any performance issues, it is recommended to just directly pass in an {@link Object2IntMap} via
	 * {@link #setValueFromConversion(int, Object, Object2IntMap)} instead.
	 */
	void setValueFromConversion(int outnumber, T something, Iterable<T> ingredients);

	/**
	 * Set a fixed {@code value} for {@code outnumber} items of {@code something} produced by consuming the ingredients, that are specified in the
	 * {@code ingredientsWithAmount} Map.
	 *
	 * @param outnumber             How many items are produced
	 * @param something             The thing that should get the Value.
	 * @param ingredientsWithAmount What is consumed and how many of it
	 */
	void setValueFromConversion(int outnumber, T something, Object2IntMap<T> ingredientsWithAmount);

	/**
	 * Called when this {@link IMappingCollector} is done collecting information.
	 */
	void finishCollection(HolderLookup.Provider registries);
}
