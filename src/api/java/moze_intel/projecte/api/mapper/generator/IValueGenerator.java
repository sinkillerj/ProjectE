package moze_intel.projecte.api.mapper.generator;

import java.util.Map;

/**
 * Defines something that can simply yield a mapping of values.
 *
 * @param <T> The key type
 * @param <V> The value type
 */
public interface IValueGenerator<T, V extends Comparable<V>> {

	/**
	 * Generate values for a mapper.
	 */
	Map<T, V> generateValues();
}