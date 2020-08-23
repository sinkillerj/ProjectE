package moze_intel.projecte.api.mapper.collector;

import java.util.Map;
import moze_intel.projecte.api.mapper.arithmetic.IValueArithmetic;

/**
 * A Class that is used to collect Contributions to the EMC Mapping.
 *
 * @param <T> The type, that is used to uniquely identify Items/Blocks/Everything
 * @param <V> The type for the EMC Value
 * @param <A> The type for the arithmetic used in calculating the values.
 */
public interface IExtendedMappingCollector<T, V extends Comparable<V>, A extends IValueArithmetic<?>> extends IMappingCollector<T, V> {

	/**
	 * Add a Conversion that produced {@code outnumber} items of {@code output} by consuming the ingredients, that are specified in the {@code ingredientsWithAmount} Map,
	 * using the given {@code arithmeticForConversion}
	 *
	 * <br/>
	 *
	 * The Map contains the ingredients and the amount of how many are consumed. It can contain negative amounts, if you get multiple items back. An amount of 0 will
	 * indicate, that the {@code output} should only get an EMC value assigned, if the zero-amount-ingredient also has an EMC value.
	 *
	 * @param outnumber               How many items are produced
	 * @param output                  What is produced
	 * @param ingredientsWithAmount   What is consumed and how many of it
	 * @param arithmeticForConversion The {@link IValueArithmetic} to use for calculating the conversion.
	 */
	void addConversion(int outnumber, T output, Map<T, Integer> ingredientsWithAmount, A arithmeticForConversion);

	/**
	 * Add a Conversion that produced {@code outnumber} items of {@code output} by consuming the {@code ingredients}, using the given {@code arithmeticForConversion}
	 *
	 * <br/>
	 *
	 * Each entry in {@code ingredients} is 1 item. May contain the same items multiple times.
	 *
	 * @param outnumber               How many items are produced
	 * @param output                  What is produced
	 * @param ingredients             What is consumed
	 * @param arithmeticForConversion The {@link IValueArithmetic} to use for calculating the conversion.
	 */
	void addConversion(int outnumber, T output, Iterable<T> ingredients, A arithmeticForConversion);

	/**
	 * @return The {@link IValueArithmetic} this {@link IExtendedMappingCollector} uses.
	 */
	A getArithmetic();
}