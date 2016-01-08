package moze_intel.projecte.emc;

import com.google.common.collect.Maps;

import moze_intel.projecte.emc.arithmetics.IValueArithmetic;
import moze_intel.projecte.emc.collector.MappingCollector;
import moze_intel.projecte.emc.generators.IValueGenerator;
import moze_intel.projecte.utils.PELogger;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SimpleGraphMapper<T, V extends Comparable<V>, A extends IValueArithmetic<V>> extends MappingCollector<T, V, A> implements IValueGenerator<T, V>
{
	static boolean OVERWRITE_FIXED_VALUES = false;
	protected V ZERO;

	private static boolean logFoundExploits = true;
	public SimpleGraphMapper(A arithmetic) {
		super(arithmetic);
		ZERO = arithmetic.getZero();
	}

	protected static <K, V extends Comparable<V>> boolean hasSmallerOrEqual(Map<K, V> m, K key, V value) {
		return (m.containsKey(key) && m.get(key).compareTo(value) <= 0);
	}

	protected static<K,V extends Comparable<V>> boolean hasSmaller(Map<K,V> m, K key, V value) {
		return (m.containsKey(key) && m.get(key).compareTo(value) < 0);
	}

	public static void setLogFoundExploits(boolean log) {
		logFoundExploits = log;
	}

	protected static<K, V extends Comparable<V>> boolean updateMapWithMinimum(Map<K,V> m, K key, V value) {
		if (!hasSmaller(m,key,value)) {
			//No Value or a value that is smaller than this
			m.put(key, value);
			return true;
		}
		return false;
	}

	protected boolean canOverride(T something, V value) {
		if (OVERWRITE_FIXED_VALUES) return  true;
		if (fixValueBeforeInherit.containsKey(something)) {
			return fixValueBeforeInherit.get(something).compareTo(value) == 0;
		}
		return true;
	}

	@Override
	public Map<T, V> generateValues() {
		Map<T, V> values = Maps.newHashMap();
		Map<T, V> newValueFor = Maps.newHashMap();
		Map<T, V> nextValueFor = Maps.newHashMap();
		Map<T,Object> reasonForChange = Maps.newHashMap();


		for (Map.Entry<T,V> entry: fixValueBeforeInherit.entrySet()) {
			newValueFor.put(entry.getKey(),entry.getValue());
			reasonForChange.put(entry.getKey(), "fixValueBefore");
		}
		while (!newValueFor.isEmpty()) {
			while (!newValueFor.isEmpty()) {
				debugPrintln("Loop");
				for (Map.Entry<T, V> entry : newValueFor.entrySet()) {
					if (canOverride(entry.getKey(),entry.getValue()) && updateMapWithMinimum(values, entry.getKey(), entry.getValue())) {
						//The new Value is now set in 'values'
						debugFormat("Set Value for %s to %s because %s", entry.getKey(), entry.getValue(), reasonForChange.get(entry.getKey()));
						//We have a new value for 'entry.getKey()' now we need to update everything that uses it as an ingredient.
						for (Conversion conversion : getUsesFor(entry.getKey())) {
							if (overwriteConversion.containsKey(conversion.output) && overwriteConversion.get(conversion.output) != conversion) {
								//There is a "SetValue-Conversion" for this item and its not this one, so we skip it.
								continue;
							}
							//Calculate how much the conversion-output costs with the new Value for entry.getKey
							V conversionValue = conversion.arithmeticForConversion.div(valueForConversion(values, conversion), conversion.outnumber);
							if (conversionValue.compareTo(ZERO) > 0 || conversion.arithmeticForConversion.isFree(conversionValue)) {
								//We could calculate a valid value for the conversion
								if (!hasSmallerOrEqual(values, conversion.output, conversionValue)) {
									//And there is no smaller value for that conversion output yet
									if (updateMapWithMinimum(nextValueFor, conversion.output, conversionValue)) {
										//So we mark that new value to set it in the next iteration.
										reasonForChange.put(conversion.output, entry.getKey());
									}
								}
							}
						}
					}
				}

				//Swap nextValueFor into newValueFor and clear newValueFor
				{
					newValueFor.clear();
					Map<T, V> tmp = nextValueFor;
					nextValueFor = newValueFor;
					newValueFor = tmp;
				}
			}
			//Iterate over all Conversions for a single conversion output
			for (Map.Entry<T, List<Conversion>> entry : conversionsFor.entrySet()) {
				V minConversionValue = null;
				//For all Conversions. All these have the same output.
				for (Conversion conversion : entry.getValue()) {
					//entry.getKey() == conversion.output
					//How much do the ingredients cost:
					V conversionValue = valueForConversion(values, conversion);
					//What would the output cost be, if that conversion would be used
					V conversionValueSingle = conversion.arithmeticForConversion.div(conversionValue, conversion.outnumber);
					//What is the actual emc value for the conversion output
					V resultValueSingle = values.containsKey(entry.getKey()) ? values.get(entry.getKey()) : ZERO;

					//Find the smallest EMC value for the conversion.output
					if (conversionValueSingle.compareTo(ZERO) > 0 || conversion.arithmeticForConversion.isFree(conversionValueSingle)) {
						if (minConversionValue == null || minConversionValue.compareTo(conversionValueSingle) > 0) {
							minConversionValue = conversionValueSingle;
						}
					}
					//the cost for the ingredients is greater zero, but smaller than the value that the output has.
					//This is a Loophole. We remove it by setting the value to 0.
					if (ZERO.compareTo(conversionValue) < 0 && conversionValueSingle.compareTo(resultValueSingle) < 0) {
						if (overwriteConversion.containsKey(conversion.output) && overwriteConversion.get(conversion.output) != conversion) {
							PELogger.logWarn(String.format("EMC Exploit: \"%s\" ingredient cost: %s value of result: %s setValueFromConversion: %s", conversion, conversionValue, resultValueSingle, overwriteConversion.get(conversion.output)));
						} else if (canOverride(entry.getKey(), ZERO)) {
							debugFormat("Setting %s to 0 because result (%s) > cost (%s): %s", entry.getKey(), resultValueSingle, conversionValue, conversion);
							newValueFor.put(conversion.output, ZERO);
							reasonForChange.put(conversion.output, "exploit recipe");
						} else if (logFoundExploits) {
							PELogger.logWarn(String.format("EMC Exploit: \"%s\" ingredient cost: %s fixed value of result: %s", conversion, conversionValue, resultValueSingle));
						}
					}
				}
				if (minConversionValue == null || minConversionValue.equals(ZERO)) {
					//we could not find any valid conversion
					if (values.containsKey(entry.getKey()) && !values.get(entry.getKey()).equals(ZERO) && canOverride(entry.getKey(), ZERO) && !hasSmaller(values, entry.getKey(), ZERO)) {
						//but the value for the conversion output is > 0, so we set it to 0.
						debugFormat("Removing Value for %s because it does not have any nonzero-conversions anymore.", entry.getKey());
						newValueFor.put(entry.getKey(), ZERO);
						reasonForChange.put(entry.getKey(), "all conversions dead");
					}
				}
			}
		}
		debugPrintln("");
		for (Map.Entry<T, V> fixedValueAfterInherit : fixValueAfterInherit.entrySet()) {
			values.put(fixedValueAfterInherit.getKey(), fixedValueAfterInherit.getValue());
		}
		//Remove all 'free' items from the output-values
		for (Iterator<T> iter = values.keySet().iterator(); iter.hasNext();) {
			T something = iter.next();
			if (arithmetic.isFree(values.get(something))) {
				iter.remove();
			}
		}
		return values;
	}

	/**
	 * Calculate the combined Cost for the ingredients in the Conversion.
	 * @param values The values for the ingredients to use in the calculation
	 * @param conversion The Conversion for which to calculate the combined ingredient cost.
	 * @return The combined ingredient value, ZERO or arithmetic.getFree()
	 */
	protected V valueForConversion(Map<T, V> values, Conversion conversion)
	{
		try {
			return valueForConversionUnsafe(values, conversion);
		} catch (ArithmeticException e) {
			PELogger.logWarn(String.format("Could not calculate value for %s: %s", conversion.toString(), e.toString()));
			return ZERO;
		} catch (Exception e) {
			PELogger.logWarn(String.format("Could not calculate value for %s: %s", conversion.toString(), e.toString()));
			e.printStackTrace();
			return ZERO;
		}
	}

	protected V valueForConversionUnsafe(Map<T, V> values, Conversion conversion)
	{
		V value = conversion.value;
		boolean allIngredientsAreFree = true;
		boolean hasPositiveIngredientValues = false;
		for (Map.Entry<T, Integer> entry:conversion.ingredientsWithAmount.entrySet()) {
			if (values.containsKey(entry.getKey())) {
				//The ingredient has a value
				if (entry.getValue() == 0)
				{
					//Ingredients with an amount of 'zero' do not need to be handled.
					continue;
				}
				//value = value + amount * ingredientcost
				V ingredientValue = conversion.arithmeticForConversion.mul(entry.getValue(),values.get(entry.getKey()));
				if (ingredientValue.compareTo(ZERO) != 0) {
					if (!conversion.arithmeticForConversion.isFree(ingredientValue)) {
						value = conversion.arithmeticForConversion.add(value, ingredientValue);
						if (ingredientValue.compareTo(ZERO) > 0 && entry.getValue() > 0) hasPositiveIngredientValues = true;
						allIngredientsAreFree = false;
					}
				} else {
					//There is an ingredient with value = 0 => we cannot calculate the combined ingredient cost.
					return ZERO;
				}
			} else {
				//There is an ingredient that does not have a value => we cannot calculate the combined ingredient cost.
				return ZERO;
			}
		}
		//When all the ingredients for are 'free' or ingredients with negative amount made the Conversion have a value <= 0 this item should be free
		if (allIngredientsAreFree || (hasPositiveIngredientValues && value.compareTo(ZERO) <= 0)) return conversion.arithmeticForConversion.getFree();
		return value;
	}
}
