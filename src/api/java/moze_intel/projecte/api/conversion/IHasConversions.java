package moze_intel.projecte.api.conversion;

import java.util.List;

/**
 * Helper interface for objects that track a list of conversions.
 */
public interface IHasConversions {

	/**
	 * {@return list of conversions}
	 */
	List<CustomConversion> conversions();

	/**
	 * Adds a custom conversion to this object's list of conversions.
	 *
	 * @param conversion Conversion to add.
	 */
	default void addConversion(CustomConversion conversion) {
		conversions().add(conversion);
	}
}