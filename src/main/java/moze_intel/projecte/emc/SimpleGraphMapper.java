package moze_intel.projecte.emc;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.SequencedSet;
import java.util.Set;
import java.util.function.Predicate;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.mapper.arithmetic.IValueArithmetic;
import moze_intel.projecte.api.mapper.generator.IValueGenerator;
import moze_intel.projecte.emc.collector.MappingCollector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SimpleGraphMapper<T, V extends Comparable<V>, A extends IValueArithmetic<V>> extends MappingCollector<T, V, A> implements IValueGenerator<T, V> {

	private static final boolean OVERWRITE_FIXED_VALUES = false;

	private final V ZERO;
	@Nullable
	private final V minimumPublishableValue;
	private final Predicate<T> publishableItem;

	private static boolean logFoundExploits = true;

	enum ConversionValueRelation {
		LOSS,
		EXACT,
		PROFITABLE
	}

	record ConversionValueComparison<V>(ConversionValueRelation relation, V ingredientCost, V outputValue, int outputCount, V totalOutputValue) {
	}

	/**
	 * Compares the complete ingredient cost against the complete output value. Diagnostics must use the output count rather than comparing the
	 * ingredient total to a single output item's value.
	 */
	static <V extends Comparable<V>> ConversionValueComparison<V> compareConversionValues(IValueArithmetic<V> arithmetic, V ingredientCost,
			V outputValue, int outputCount) {
		if (outputCount <= 0) {
			throw new IllegalArgumentException("outputCount has to be > 0");
		}
		V totalOutputValue = arithmetic.mul(outputCount, outputValue);
		int comparison = totalOutputValue.compareTo(ingredientCost);
		ConversionValueRelation relation = comparison < 0 ? ConversionValueRelation.LOSS
				: comparison > 0 ? ConversionValueRelation.PROFITABLE : ConversionValueRelation.EXACT;
		return new ConversionValueComparison<>(relation, ingredientCost, outputValue, outputCount, totalOutputValue);
	}

	public SimpleGraphMapper(A arithmetic) {
		this(arithmetic, null, key -> false);
	}

	/** Opt-in modpack recovery policy; the one-argument constructor retains normal exploit cleanup. */
	public SimpleGraphMapper(A arithmetic, @Nullable V minimumPublishableValue, Predicate<T> publishableItem) {
		super(arithmetic);
		ZERO = arithmetic.getZero();
		this.minimumPublishableValue = minimumPublishableValue;
		this.publishableItem = publishableItem;
	}

	private boolean belowMinimum(T key, V value) {
		return minimumPublishableValue != null && publishableItem.test(key) && arithmetic.isGreaterThanZero(value)
				&& value.compareTo(minimumPublishableValue) < 0;
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
		if (stored == null || stored.compareTo(value) > 0
				|| (minimumPublishableValue != null && arithmetic.isZero(stored) && arithmetic.isGreaterThanZero(value))) {
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
		Map<@NotNull T, @NotNull V> values = new HashMap<>();
		Map<T, Set<T>> dependencies = new HashMap<>();
		Map<T, Set<T>> changedDependencies = new HashMap<>();
		Set<T> fallbackCandidates = new HashSet<>();
		Set<T> activatedFallbacks = new HashSet<>();

		// All values that changed in previous iteration, so everything depending on it needs to be updated
		@Nullable
		Map<@NotNull T, @NotNull V> changedValues = new HashMap<>(fixValueBeforeInherit);
		@Nullable
		Map<@NotNull T, @NotNull Object> reasonForChange = null;
		if (isDebugGraphmapper()) {
			reasonForChange = new HashMap<>(changedValues.size());
			for (Map.Entry<T, V> entry : fixValueBeforeInherit.entrySet()) {
				reasonForChange.put(entry.getKey(), "fixValueBefore");
			}
		}

		Map<@NotNull T, @NotNull V> invalidValues = new HashMap<>();
		while (changedValues != null && !changedValues.isEmpty()) {
			while (changedValues != null && !changedValues.isEmpty()) {
				// Changes that happened when processing current changes
				@Nullable
				Map<@NotNull T, @NotNull V> nextChangedValues = null;
				Map<T, Set<T>> nextDependencies = new HashMap<>();

				debugPrintln("Loop");
				for (Map.Entry<T, V> entry : changedValues.entrySet()) {
					T key = entry.getKey();
					V value = entry.getValue();
					if (canOverride(key, value) && updateMapWithMinimum(values, key, value)) {
						dependencies.put(key, changedDependencies.getOrDefault(key, Set.of()));
						//The new Value is now set in 'values'
						if (reasonForChange != null) {
							//Note: We include a manual check we are tracking the reasons, so that we can skip looking the reason up when it won't actually be used
							debugFormat("Set Value for {} to {} because {}", key, value, reasonForChange.get(key));
						}
						//We have a new value for 'entry.getKey()' now we need to update everything that uses it as an ingredient.
						SequencedSet<Conversion> usesFor = usedIn.get(key);
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
							Set<T> resultDependencies = dependenciesFor(dependencies, conversion);
							if (resultDependencies.contains(conversion.output)) {
								continue;
							}
							V ingredientValue = valueForConversion(values, conversion);
							V resultValueConversion = conversion.arithmeticForConversion.div(ingredientValue, conversion.outnumber);
							if (belowMinimum(conversion.output, resultValueConversion)) {
								if (!activatedFallbacks.contains(conversion.output)) {
									fallbackCandidates.add(conversion.output);
								}
								continue;
							}
							if (arithmetic.isGreaterThanZero(resultValueConversion) || conversion.arithmeticForConversion.isFree(resultValueConversion)) {
								//We could calculate a valid value for the conversion
								V storedValue = values.get(conversion.output);
								if (storedValue == null || storedValue.compareTo(resultValueConversion) > 0) {
									//And there is no smaller value for that conversion output yet
									if (nextChangedValues == null) {//Lazily init nextChangedValues so if there aren't any we don't have to initialize it
										nextChangedValues = new HashMap<>();
									}
									if (updateMapWithMinimum(nextChangedValues, conversion.output, resultValueConversion)) {
										nextDependencies.put(conversion.output, resultDependencies);
										//So we mark that new value to set it in the next iteration.
										addReason(reasonForChange, conversion.output, key);
									}
								}
							}
						}
					}
				}

				changedValues = nextChangedValues;
				changedDependencies = nextDependencies;
			}
			invalidValues.clear();
			collectInvalidValues(values, dependencies, activatedFallbacks, reasonForChange, invalidValues);
			changedValues = invalidValues.isEmpty() ? null : invalidValues;
			changedDependencies = new HashMap<>();
			if (changedValues == null && minimumPublishableValue != null) {
				changedValues = new HashMap<>();
				for (T key : fallbackCandidates) {
					V stored = values.get(key);
					if (!fixValueBeforeInherit.containsKey(key) && !fixValueAfterInherit.containsKey(key)
							&& (stored == null || arithmetic.isZero(stored))) {
						changedValues.put(key, minimumPublishableValue);
						activatedFallbacks.add(key);
					}
				}
				fallbackCandidates.clear();
			}
		}
		restoreMissingItems(values, dependencies);
		debugPrintln("");
		values.putAll(fixValueAfterInherit);
		//Remove all 'free' items from the output-values
		values.entrySet().removeIf(something -> arithmetic.isFree(something.getValue()));
		return values;
	}

	private Set<T> dependenciesFor(Map<T, Set<T>> dependencies, Conversion conversion) {
		if (minimumPublishableValue == null) {
			return Set.of();
		}
		Set<T> result = new HashSet<>();
		for (T ingredient : conversion.ingredientsWithAmount.keySet()) {
			result.add(ingredient);
			result.addAll(dependencies.getOrDefault(ingredient, Set.of()));
		}
		return result;
	}

	// Run once validation settles. Only fill gaps; existing free and explicitly fixed values remain authoritative.
	private void restoreMissingItems(Map<T, V> values, Map<T, Set<T>> dependencies) {
		if (minimumPublishableValue == null) {
			return;
		}
		boolean changed;
		do {
			changed = false;
			Set<T> outputs = new HashSet<>(conversionsFor.keySet());
			outputs.addAll(overwriteConversion.keySet());
			for (T key : outputs) {
				V current = values.get(key);
				if (!publishableItem.test(key) || fixValueBeforeInherit.containsKey(key) || fixValueAfterInherit.containsKey(key)
						|| (current != null && !arithmetic.isZero(current))) {
					continue;
				}
				Conversion overwrite = overwriteConversion.get(key);
				Set<Conversion> candidates = overwrite == null ? conversionsFor.get(key) : Set.of(overwrite);
				V best = null;
				Set<T> bestDependencies = Set.of();
				for (Conversion conversion : candidates) {
					Set<T> candidateDependencies = dependenciesFor(dependencies, conversion);
					if (candidateDependencies.contains(key)) {
						continue;
					}
					V candidate = conversion.arithmeticForConversion.div(valueForConversion(values, conversion), conversion.outnumber);
					if (arithmetic.isGreaterThanZero(candidate) && !belowMinimum(key, candidate)
							&& (best == null || candidate.compareTo(best) < 0)) {
						best = candidate;
						bestDependencies = candidateDependencies;
					}
				}
				if (best != null) {
					values.put(key, best);
					dependencies.put(key, bestDependencies);
					changed = true;
				}
			}
		} while (changed);
	}

	private void collectInvalidValues(Map<T, V> values, Map<T, Set<T>> dependencies, Set<T> activatedFallbacks,
			@Nullable Map<T, Object> reasonForChange,
			Map<@NotNull T, @NotNull V> invalidValues) {
		for (Map.Entry<T, SequencedSet<Conversion>> entry : conversionsFor.entrySet()) {
			T key = entry.getKey();
			Conversion overwrite = overwriteConversion.get(key);
			validateConversionOutput(values, dependencies, activatedFallbacks, invalidValues, reasonForChange, key, entry.getValue(), overwrite);
		}
		for (Map.Entry<T, Conversion> entry : overwriteConversion.entrySet()) {
			if (!conversionsFor.containsKey(entry.getKey())) {
				validateConversionOutput(values, dependencies, activatedFallbacks, invalidValues, reasonForChange, entry.getKey(), Set.of(), entry.getValue());
			}
		}
	}

	@Nullable
	private V validateActiveConversion(Map<T, V> values, Map<T, Set<T>> dependencies, Map<T, V> invalidValues,
			@Nullable Map<T, Object> reasonForChange, T key,
			@Nullable V resultValueActual, @Nullable V minConversionValue, Conversion conversion) {
		if (dependenciesFor(dependencies, conversion).contains(conversion.output)) {
			return minConversionValue;
		}
		V ingredientValue = valueForConversion(values, conversion);
		V resultValueConversion = conversion.arithmeticForConversion.div(ingredientValue, conversion.outnumber);
		if (belowMinimum(conversion.output, resultValueConversion)) {
			return minConversionValue;
		}
		if (arithmetic.isGreaterThanZero(resultValueConversion) || conversion.arithmeticForConversion.isFree(resultValueConversion)) {
			if (minConversionValue == null || minConversionValue.compareTo(resultValueConversion) > 0) {
				minConversionValue = resultValueConversion;
			}
		}
		if (arithmetic.isGreaterThanZero(ingredientValue) && isLessThan(resultValueConversion, resultValueActual)) {
			if (canOverrideZero(key)) {
				if (isDebugGraphmapper()) {
					debugFormat("Setting {} to 0 because result ({}) > cost ({}): {}", key, valueOrZero(resultValueActual), ingredientValue, conversion);
					addReason(reasonForChange, conversion.output, "exploit recipe");
				}
				invalidValues.put(conversion.output, ZERO);
			} else if (logFoundExploits) {
				logProfitableConversionMismatch(conversion, ingredientValue, resultValueActual, null);
			}
		}
		return minConversionValue;
	}

	private void validateConversionOutput(Map<T, V> values, Map<T, Set<T>> dependencies, Set<T> activatedFallbacks,
			Map<T, V> invalidValues, @Nullable Map<T, Object> reasonForChange, T key,
			Set<Conversion> regularConversions, @Nullable Conversion overwrite) {
		@Nullable
		V minConversionValue = null;
		//What is the actual EMC value for the conversion output
		@Nullable
		V resultValueActual = values.get(key);
		if (resultValueActual != null && arithmetic.isZero(resultValueActual)) {
			//If the result is actually zero, pretend it is null to optimize comparisons against conversion values.
			resultValueActual = null;
		}

		if (overwrite == null) {
			for (Conversion conversion : regularConversions) {
				minConversionValue = validateActiveConversion(values, dependencies, invalidValues, reasonForChange, key, resultValueActual, minConversionValue, conversion);
			}
		} else {
			minConversionValue = validateActiveConversion(values, dependencies, invalidValues, reasonForChange, key, resultValueActual, null, overwrite);
		}

		if (overwrite != null) {
			for (Conversion conversion : regularConversions) {
				V ingredientValue = valueForConversion(values, conversion);
				V resultValueConversion = conversion.arithmeticForConversion.div(ingredientValue, conversion.outnumber);
				if (arithmetic.isGreaterThanZero(ingredientValue) && isLessThan(resultValueConversion, resultValueActual) && logFoundExploits) {
					logProfitableConversionMismatch(conversion, ingredientValue, resultValueActual, overwrite);
				}
			}
		}

		if (minConversionValue == null && resultValueActual != null && arithmetic.isGreaterThanZero(resultValueActual) && canOverrideZero(key)
				&& !activatedFallbacks.contains(key)) {
			debugFormat("Removing Value for {} because it does not have any nonzero-conversions anymore.", key);
			invalidValues.put(key, ZERO);
			addReason(reasonForChange, key, "all conversions dead");
		}
	}

	private void logProfitableConversionMismatch(Conversion conversion, V ingredientValue, @Nullable V resultValueActual,
			@Nullable Conversion forcedConversion) {
		V outputValue = valueOrZero(resultValueActual);
		try {
			ConversionValueComparison<V> comparison = compareConversionValues(conversion.arithmeticForConversion, ingredientValue, outputValue,
					conversion.outnumber);
			if (comparison.relation() != ConversionValueRelation.PROFITABLE) {
				//A custom arithmetic implementation may not preserve the usual multiply/divide relationship. Avoid emitting a false exploit warning.
				PECore.debugLog("Skipped profitable EMC mismatch warning for {} because total output relation was {} (cost {}, total output {}).",
						conversion, comparison.relation(), comparison.ingredientCost(), comparison.totalOutputValue());
				return;
			}
			if (forcedConversion == null) {
				PECore.LOGGER.warn("Profitable EMC conversion mismatch: recipe ({}) costs {} total but produces {} x {} = {} total. "
						+ "The fixed/custom output value was retained.", conversion, comparison.ingredientCost(), comparison.outputCount(),
						comparison.outputValue(), comparison.totalOutputValue());
			} else {
				PECore.LOGGER.warn("Profitable EMC conversion mismatch against forced mapping: recipe ({}) costs {} total but produces {} x {} = {} total. "
						+ "Forced mapping: {}", conversion, comparison.ingredientCost(), comparison.outputCount(), comparison.outputValue(),
						comparison.totalOutputValue(), forcedConversion);
			}
		} catch (RuntimeException e) {
			//Diagnostics must never interfere with mapping. Fall back to the values already known to be safe to render.
			if (forcedConversion == null) {
				PECore.LOGGER.warn("Profitable EMC conversion mismatch: recipe ({}) costs {} total but its output value is {} each. "
						+ "The fixed/custom output value was retained; total output value could not be calculated for diagnostics.", conversion,
						ingredientValue, outputValue);
			} else {
				PECore.LOGGER.warn("Profitable EMC conversion mismatch against forced mapping: recipe ({}) costs {} total but its output value is {} each. "
						+ "Forced mapping: {}; total output value could not be calculated for diagnostics.", conversion, ingredientValue, outputValue,
						forcedConversion);
			}
		}
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
