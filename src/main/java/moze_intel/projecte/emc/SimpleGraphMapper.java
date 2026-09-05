package moze_intel.projecte.emc;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.mapper.arithmetic.IValueArithmetic;
import moze_intel.projecte.api.mapper.generator.IValueGenerator;
import moze_intel.projecte.emc.collector.MappingCollector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SimpleGraphMapper<T, V extends Comparable<V>, A extends IValueArithmetic<V>> extends MappingCollector<T, V, A> implements IValueGenerator<T, V> {

	private static final boolean OVERWRITE_FIXED_VALUES = false;

	private final V ZERO;

	private static boolean logFoundExploits = true;

	public SimpleGraphMapper(A arithmetic) {
		super(arithmetic);
		ZERO = arithmetic.getZero();
	}

	static void setLogFoundExploits(boolean log) {
		logFoundExploits = log;
	}

	private void addReason(@Nullable Map<T, Object> reasonForChange, T key, Object reason) {
		if (reasonForChange != null) {//Only track the reasons if we have a map to track them with
			reasonForChange.put(key, reason);
		}
	}

	private boolean updateMapWithMinimum(Map<T, V> m, T key, V value) {
		V stored = m.get(key);
		if (stored == null || stored.compareTo(value) > 0) {
			//No Value or a value that is smaller than this
			m.put(key, value);
			return true;
		}
		return false;
	}

	private boolean canOverride(T something, V value) {
		if (OVERWRITE_FIXED_VALUES) {
			return true;
		}
		V valueBeforeInherit = fixValueBeforeInherit.get(something);
		return valueBeforeInherit == null || valueBeforeInherit.compareTo(value) == 0;
	}

	private boolean canOverrideZero(T something) {
		if (OVERWRITE_FIXED_VALUES) {
			return true;
		}
		V valueBeforeInherit = fixValueBeforeInherit.get(something);
		return valueBeforeInherit == null || arithmetic.isZero(valueBeforeInherit);
	}

	@Override
	public Map<T, V> generateValues() {
		Map<@NotNull T, @NotNull V> values = new Object2ObjectOpenHashMap<>();

		// All values that changed in previous iteration, so everything depending on it needs to be updated
		@Nullable
		Map<@NotNull T, @NotNull V> changedValues = new Object2ObjectOpenHashMap<>(fixValueBeforeInherit);
		@Nullable
		Map<@NotNull T, @NotNull Object> reasonForChange = null;
		if (isDebugGraphmapper()) {
			reasonForChange = new Object2ObjectOpenHashMap<>(changedValues.size());
			for (Map.Entry<T, V> entry : fixValueBeforeInherit.entrySet()) {
				reasonForChange.put(entry.getKey(), "fixValueBefore");
			}
		}

		while (changedValues != null && !changedValues.isEmpty()) {
			while (changedValues != null && !changedValues.isEmpty()) {
				// Changes that happened when processing current changes
				@Nullable
				Map<@NotNull T, @NotNull V> nextChangedValues = null;

				debugPrintln("Loop");
				for (Map.Entry<T, V> entry : changedValues.entrySet()) {
					T key = entry.getKey();
					V value = entry.getValue();
					if (canOverride(key, value) && updateMapWithMinimum(values, key, value)) {
						//The new Value is now set in 'values'
						if (reasonForChange != null) {
							//Note: We include a manual check we are tracking the reasons, so that we can skip looking the reason up when it won't actually be used
							debugFormat("Set Value for {} to {} because {}", key, value, reasonForChange.get(key));
						}
						//We have a new value for 'entry.getKey()' now we need to update everything that uses it as an ingredient.
						Set<Conversion> usesFor = usedIn.get(key);
						if (usesFor == null) {
							continue;
						}
						for (Conversion conversion : usesFor) {
							Conversion oldConversion = overwriteConversion.get(conversion.output);
							if (oldConversion != null && oldConversion != conversion) {
								//There is a "SetValue-Conversion" for this item, and it's not this one, so we skip it.
								continue;
							}
							//Calculate how much the conversion-output costs with the new Value for entry.getKey
							V ingredientValue = valueForConversion(values, conversion);
							V resultValueConversion = conversion.arithmeticForConversion.div(ingredientValue, conversion.outnumber);
							if (arithmetic.isGreaterThanZero(resultValueConversion) || conversion.arithmeticForConversion.isFree(resultValueConversion)) {
								//We could calculate a valid value for the conversion
								V storedValue = values.get(conversion.output);
								if (storedValue == null || storedValue.compareTo(resultValueConversion) > 0) {
									//And there is no smaller value for that conversion output yet
									if (nextChangedValues == null) {//Lazily init nextChangedValues so if there aren't any we don't have to initialize it
										nextChangedValues = new Object2ObjectOpenHashMap<>();
									}
									if (updateMapWithMinimum(nextChangedValues, conversion.output, resultValueConversion)) {
										//So we mark that new value to set it in the next iteration.
										addReason(reasonForChange, conversion.output, key);
									}
								}
							}
						}
					}
				}

				changedValues = nextChangedValues;
			}
			//Iterate over all Conversions for a single conversion output
			for (Map.Entry<T, Set<Conversion>> entry : conversionsFor.entrySet()) {
				T key = entry.getKey();
				@Nullable
				V minConversionValue = null;
				//What is the actual emc value for the conversion output
				@Nullable
				V resultValueActual = values.get(key);
				if (resultValueActual != null && arithmetic.isZero(resultValueActual)) {
					//Note: If the result is actually zero, then we pretend it is null, so that we can optimize our comparison check
					// when comparing the value from the conversion
					resultValueActual = null;
				}
				//For all Conversions. All these have the same output.
				for (Conversion conversion : entry.getValue()) {
					//entry.getKey() == conversion.output
					//How much do the ingredients cost:
					V ingredientValue = valueForConversion(values, conversion);
					//What would the output cost be, if that conversion would be used
					V resultValueConversion = conversion.arithmeticForConversion.div(ingredientValue, conversion.outnumber);
					//Find the smallest EMC value for the conversion.output
					if (arithmetic.isGreaterThanZero(resultValueConversion) || conversion.arithmeticForConversion.isFree(resultValueConversion)) {
						if (minConversionValue == null || minConversionValue.compareTo(resultValueConversion) > 0) {
							minConversionValue = resultValueConversion;
						}
					}
					//the cost for the ingredients is greater zero, but smaller than the value that the output has.
					//This is a Loophole. We remove it by setting the value to 0.
					if (arithmetic.isGreaterThanZero(ingredientValue) && isLessThan(resultValueConversion, resultValueActual)) {
						Conversion oldConversion = overwriteConversion.get(conversion.output);
						if (oldConversion != null && oldConversion != conversion) {
							if (logFoundExploits) {
								PECore.LOGGER.warn("EMC Exploit: \"{}\" ingredient cost: {} value of result: {} setValueFromConversion: {}", conversion,
										ingredientValue, valueOrZero(resultValueActual), oldConversion);
							}
						} else if (canOverrideZero(key)) {
							if (isDebugGraphmapper()) {
								debugFormat("Setting {} to 0 because result ({}) > cost ({}): {}", key, valueOrZero(resultValueActual), ingredientValue, conversion);
								addReason(reasonForChange, conversion.output, "exploit recipe");
							}
							if (changedValues == null) {//Lazily init changedValues so if there aren't any we don't have to initialize it
								changedValues = new Object2ObjectOpenHashMap<>();
							}
							changedValues.put(conversion.output, ZERO);
						} else if (logFoundExploits) {
							PECore.LOGGER.warn("EMC Exploit: ingredients ({}) cost {} but output value is {}", conversion, ingredientValue, valueOrZero(resultValueActual));
						}
					}
				}
				if (minConversionValue == null) {//|| arithmetic.isZero(minConversionValue)
					//we could not find any valid conversion
					// Note: We know that minConversionValue is not zero, as the only cases we update it are:
					// - to a value that we have checked is greater than zero
					// - to a value that represents it is free
					// While it does have a negative value when free, this is still not directly equal to zero, so we can skip the check
					//Note: We can use resultValueActual even though we might have made it null if it was zero.
					// That is fine, as even if it was zero instead of being null, it would fail for the check ensuring it is greater than zero
					if (resultValueActual != null && arithmetic.isGreaterThanZero(resultValueActual) && canOverrideZero(key)) {
						//but the value for the conversion output is > 0, so we set it to 0.
						debugFormat("Removing Value for {} because it does not have any nonzero-conversions anymore.", key);
						if (changedValues == null) {//Lazily init changedValues so if there aren't any we don't have to initialize it
							changedValues = new Object2ObjectOpenHashMap<>();
						}
						changedValues.put(key, ZERO);
						addReason(reasonForChange, key, "all conversions dead");
					}
				}
			}
		}
		debugPrintln("");
		values.putAll(fixValueAfterInherit);
		//Remove all 'free' items from the output-values
		values.entrySet().removeIf(something -> arithmetic.isFree(something.getValue()));
		return values;
	}

	private V valueOrZero(@Nullable V value) {
		return value == null ? ZERO : value;
	}

	private boolean isLessThan(V resultValueConversion, @Nullable V resultValueActual) {
		//If we don't have an actual value, then compare the value directly against zero using the optimized method
		// otherwise use the comparator to see which is smaller
		return resultValueActual == null ? arithmetic.isLessThanZero(resultValueConversion) : resultValueConversion.compareTo(resultValueActual) < 0;
	}

	/**
	 * Calculate the combined Cost for the ingredients in the Conversion.
	 *
	 * @param values     The values for the ingredients to use in the calculation
	 * @param conversion The Conversion for which to calculate the combined ingredient cost.
	 *
	 * @return The combined ingredient value, ZERO or arithmetic.getFree()
	 */
	private V valueForConversion(Map<T, V> values, Conversion conversion) {
		try {
			return valueForConversionUnsafe(values, conversion);
		} catch (ArithmeticException e) {
			PECore.LOGGER.warn("Could not calculate value for {}: {}", conversion.toString(), e.toString());
			return ZERO;
		} catch (Exception e) {
			PECore.LOGGER.warn("Could not calculate value for {}: {}", conversion.toString(), e, e);
			return ZERO;
		}
	}

	private V valueForConversionUnsafe(Map<T, V> values, Conversion conversion) {
		V value = conversion.value;
		boolean allIngredientsAreFree = true;
		boolean hasPositiveIngredientValues = false;
		for (Iterator<Object2IntMap.Entry<T>> iterator = Object2IntMaps.fastIterator(conversion.ingredientsWithAmount); iterator.hasNext(); ) {
			Object2IntMap.Entry<T> entry = iterator.next();
			V storedValue = values.get(entry.getKey());
			if (storedValue == null) {
				//There is an ingredient that does not have a value => we cannot calculate the combined ingredient cost.
				return ZERO;
			}
			int amount = entry.getIntValue();
			//The ingredient has a value and
			//value = value + amount * ingredientCost
			V ingredientValue = conversion.arithmeticForConversion.mul(amount, storedValue);
			if (arithmetic.isZero(ingredientValue)) {
				//There is an ingredient with value = 0 => we cannot calculate the combined ingredient cost.
				return ZERO;
			} else {
				V newValue = conversion.arithmeticForConversion.add(value, ingredientValue);
				//Identity compare to see if the value changed. The two cases where the same object will be returned are:
				// 1. If the other value was zero
				// 2. If the other value was free
				// We care specifically about handling the case where the ingredient value is not free,
				// so we check if it changed OR if it was zero and that is why it didn't change,
				// as checking against it being zero is cheaper than checking if it is free
				// Note: In theory isZero(ingredientValue) should always return false (due to the above check),
				// except if someone decides to add a conversion with a custom arithmetic
				if (value != newValue || conversion.arithmeticForConversion.isZero(ingredientValue)) {
					value = newValue;
					if (arithmetic.isGreaterThanZero(ingredientValue) && amount > 0) {
						hasPositiveIngredientValues = true;
					}
					allIngredientsAreFree = false;
				}
			}
		}
		//When all the ingredients for are 'free' or ingredients with negative amount made the Conversion have a value <= 0 this item should be free
		if (allIngredientsAreFree || (hasPositiveIngredientValues && arithmetic.isLessThanEqualZero(value))) {
			return conversion.arithmeticForConversion.getFree();
		}
		return value;
	}
}