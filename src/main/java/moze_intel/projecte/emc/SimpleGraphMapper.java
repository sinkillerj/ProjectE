package moze_intel.projecte.emc;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SimpleGraphMapper<T, V extends Comparable<V>> extends GraphMapper<T, V> {
	static boolean OVERWRITE_FIXED_VALUES = false;
	public SimpleGraphMapper(IValueArithmetic arithmetic) {
		super(arithmetic);
	}

	protected static<K,V extends Comparable<V>> boolean hasSmaller(Map<K,V> m, K key, V value) {
		return (m.containsKey(key) && value.compareTo(m.get(key)) >= 0);
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
		Map<T, V> values = new HashMap<T, V>();
		Map<T, V> newValueFor = new HashMap<T, V>();
		Map<T, V> nextValueFor = new HashMap<T, V>();


		newValueFor.putAll(fixValueBeforeInherit);
		while (!newValueFor.isEmpty()) {
			while (!newValueFor.isEmpty()) {
				debugPrintln("Loop");
				for (Map.Entry<T, V> entry : newValueFor.entrySet()) {
					if (canOverride(entry.getKey(),entry.getValue()) && updateMapWithMinimum(values, entry.getKey(), entry.getValue())) {
						debugFormat("Set Value for %s to %s\n", entry.getKey(), entry.getValue());
						for (Conversion conversion : getUsesFor(entry.getKey())) {
							V conversionValue = arithmetic.div(valueForConversion(values, conversion), conversion.outnumber);
							if (conversionValue.compareTo(arithmetic.getZero()) > 0 || arithmetic.isFree(conversionValue)) {
								if (!hasSmaller(values, conversion.output, conversionValue)) {
									updateMapWithMinimum(nextValueFor, conversion.output, conversionValue);
								}
							}
						}
					}
				}

				{
					newValueFor.clear();
					Map<T, V> tmp = nextValueFor;
					nextValueFor = newValueFor;
					newValueFor = tmp;
				}
			}
			for (Map.Entry<T, List<Conversion>> entry : conversionsFor.entrySet()) {
				for (Conversion conversion : entry.getValue()) {
					//entry.getKey() == conversion.output
					V conversionValue = valueForConversion(values, conversion);
					V resultValue = values.containsKey(entry.getKey()) ? arithmetic.mul(conversion.outnumber, values.get(entry.getKey())) : arithmetic.getZero();
					if (canOverride(entry.getKey(),arithmetic.getZero()) && arithmetic.getZero().compareTo(conversionValue) < 0 && conversionValue.compareTo(resultValue) < 0) {
						debugFormat("Setting %s to 0 because %s > %s = %s\n", entry.getKey(), resultValue, conversionValue, conversion);
						newValueFor.put(conversion.output, arithmetic.getZero());
					}
				}
			}
		}
		debugPrintln("");
		for (Map.Entry<T, V> fixedValueAfterInherit : fixValueAfterInherit.entrySet()) {
			values.put(fixedValueAfterInherit.getKey(), fixedValueAfterInherit.getValue());
		}
		for (Iterator<T> iter = values.keySet().iterator(); iter.hasNext();) {
			T something = iter.next();
			if (arithmetic.isFree(values.get(something))) {
				iter.remove();
			}
		}
		return values;
	}

	protected V valueForConversion(Map<T, V> values, Conversion conversion) {
		V value = conversion.value;
		boolean allIngredientsAreFree = true;
		boolean hasPositiveIngredientValues = false;
		for (Map.Entry<T, Integer> entry:conversion.ingredientsWithAmount.entrySet()) {
			if (values.containsKey(entry.getKey())) {
				//value = value + amount * ingredientcost
				V ingredientValue = values.get(entry.getKey());
				if (ingredientValue.compareTo(arithmetic.getZero()) != 0) {
					if (!arithmetic.isFree(ingredientValue)) {
						value = arithmetic.add(value, arithmetic.mul(entry.getValue(), ingredientValue));
						if (ingredientValue.compareTo(arithmetic.getZero()) > 0 && entry.getValue() > 0) hasPositiveIngredientValues = true;
						allIngredientsAreFree = false;
					}
				} else {
					return arithmetic.getZero();
				}
			} else {
				return arithmetic.getZero();
			}
		}
		if (allIngredientsAreFree || (hasPositiveIngredientValues && value.compareTo(arithmetic.getZero()) <= 0)) return arithmetic.getFree();
		return value;
	}
}
