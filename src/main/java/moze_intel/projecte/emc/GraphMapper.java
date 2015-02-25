package moze_intel.projecte.emc;


import moze_intel.projecte.utils.PELogger;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public abstract class GraphMapper<T, V extends Comparable<V>> implements IMappingCollector<T, V> {
	protected static final boolean DEBUG_GRAPHMAPPER = false;

	protected static void debugFormat(String format, Object... args) {
		if (DEBUG_GRAPHMAPPER)
			System.out.format(format, args);
	}

	protected static void debugPrintln(String s) {
		debugFormat("%s\n", s);
	}

	protected Map<T, List<Conversion>> conversionsFor = new HashMap<T, List<Conversion>>();
	protected Map<T, List<Conversion>> usedIn = new HashMap<T, List<Conversion>>();
	protected Map<T, V> fixValueBeforeInherit = new HashMap<T, V>();
	protected Map<T, V> fixValueAfterInherit = new HashMap<T, V>();
	protected Map<T, Integer> noDependencyConversionCount = new HashMap<T, Integer>();

	IValueArithmetic<V> arithmetic;
	public GraphMapper(IValueArithmetic<V> arithmetic) {
		this.arithmetic = arithmetic;
	}

	protected static <K, V> List<V> getOrCreateList(Map<K, List<V>> map, K key) {
		List<V> list;
		if (map.containsKey(key)) {
			list = map.get(key);
		} else {
			list = new LinkedList<V>();
			map.put(key, list);
		}
		return list;
	}

	protected List<Conversion> getConversionsFor(T something) {
		return getOrCreateList(conversionsFor, something);
	}

	protected List<Conversion> getUsesFor(T something) {
		return getOrCreateList(usedIn, something);
	}

	protected int getNoDependencyConversionCountFor(T something) {
		Integer count = noDependencyConversionCount.get(something);
		if (count == null) return 0;
		else return count;
	}

	protected void increaseNoDependencyConversionCountFor(T something) {
		noDependencyConversionCount.put(something, getNoDependencyConversionCountFor(something) + 1);
	}

	public void addConversionMultiple(int outnumber, T output, Map<T, Integer> ingredientsWithAmount) {
		addConversionMultiple(outnumber, output, ingredientsWithAmount, arithmetic.getZero());
	}

	public void addConversionMultiple(int outnumber, T output, Map<T, Integer> ingredientsWithAmount, V baseValueForConversion) {
		ingredientsWithAmount = new HashMap<T, Integer>(ingredientsWithAmount);
		if (output == null || ingredientsWithAmount.containsKey(null)) return;
		if (outnumber <= 0)
			throw new IllegalArgumentException("outnumber has to be > 0!");
		//Add the Conversions to the conversionsFor and usedIn Maps:
		Conversion conversion = new Conversion(output, outnumber, ingredientsWithAmount);
		conversion.value = baseValueForConversion;
		if (getConversionsFor(output).contains(conversion)) return;
		getConversionsFor(output).add(conversion);
		if (ingredientsWithAmount.size() == 0) increaseNoDependencyConversionCountFor(output);

		for (Map.Entry<T, Integer> ingredient : ingredientsWithAmount.entrySet()) {
			List<Conversion> usesForIngredient = getUsesFor(ingredient.getKey());
			if (ingredient.getValue() == null)
				throw new IllegalArgumentException("ingredient amount value has to be != null");
			usesForIngredient.add(conversion);
		}
	}

	public void addConversion(int outnumber, T output, Iterable<T> ingredients) {
		addConversion(outnumber, output, ingredients, arithmetic.getZero());
	}

	public void addConversion(int outnumber, T output, Iterable<T> ingredients, V baseValueForConversion) {
		Map<T, Integer> ingredientsWithAmount = new HashMap<T, Integer>();
		for (T ingredient : ingredients) {
			if (ingredientsWithAmount.containsKey(ingredient)) {
				int amount = ingredientsWithAmount.get(ingredient);
				ingredientsWithAmount.put(ingredient, amount + 1);
			} else {
				ingredientsWithAmount.put(ingredient, 1);
			}
		}
		this.addConversionMultiple(outnumber, output, ingredientsWithAmount, baseValueForConversion);
	}

	/**
	 * Set a Value for something. value has to be >= 0 or Free, which indicates that 'something' can be used in
	 * Conversions, but does not add anything to the value of the Conversion-result.
	 *
	 * @param something
	 * @param value
	 * @param type
	 */
	public void setValue(T something, V value, FixedValue type) {
		switch (type) {
			case FixAndInherit:
				if (fixValueBeforeInherit.containsKey(something))
					PELogger.logWarn("Overwriting fixValueBeforeInherit for " + something + ":" + fixValueBeforeInherit.get(something) + " to " + value);
				fixValueBeforeInherit.put(something, value);
				if (fixValueAfterInherit.containsKey(something))
					PELogger.logWarn("Removign fixValueAfterInherit for " + something + " before: " + fixValueAfterInherit.get(something));
				fixValueAfterInherit.remove(something);
				break;
			case FixAndDoNotInherit:
				if (fixValueBeforeInherit.containsKey(something))
					PELogger.logWarn("Overwriting fixValueBeforeInherit for " + something + ":" + fixValueBeforeInherit.get(something) + " to " + 0.0);
				fixValueBeforeInherit.put(something, arithmetic.getZero());
				if (fixValueAfterInherit.containsKey(something))
					PELogger.logWarn("Overwriting fixValueAfterInherit for " + something + ":" + fixValueAfterInherit.get(something) + " to " + value);
				fixValueAfterInherit.put(something, value);
				break;
			case FixAfterInherit:
				if (fixValueBeforeInherit.containsKey(something))
					PELogger.logWarn("Removing fixValueBeforeInherit for " + something + " before: " + fixValueBeforeInherit.get(something));
				fixValueBeforeInherit.remove(something);
				if (fixValueAfterInherit.containsKey(something))
					PELogger.logWarn("Overwriting fixValueAfterInherit for " + something + ":" + fixValueAfterInherit.get(something) + " to " + value);
				fixValueAfterInherit.put(something, value);
				break;
			case SuggestionAndInherit:
				this.addConversionMultiple(1, something, new HashMap<T, Integer>(), value);
				break;
		}
	}


	abstract public Map<T, V> generateValues();

	protected class Conversion {
		T output;

		int outnumber = 1;
		V value;
		Map<T, Integer> ingredientsWithAmount;

		protected Conversion(T output) {
			this.output = output;
		}

		protected Conversion(T output, int outnumber, Map<T, Integer> ingredientsWithAmount) {
			this(output);
			this.outnumber = outnumber;
			this.ingredientsWithAmount = ingredientsWithAmount;
		}

		public void markInvalid() {
			if (this.ingredientsWithAmount != null) {
				this.ingredientsWithAmount.clear();
				this.ingredientsWithAmount = null;
			}
			this.value = arithmetic.getZero();
		}

		public String toString() {
			return "" + value + " + " + this.ingredientsWithAmount + " => " + outnumber + "x" + output;
		}

		public boolean equals(Conversion other) {
			if (output.equals(other.output) && value.equals(other.value)) {
				if (ingredientsWithAmount == null && (other.ingredientsWithAmount == null || other.ingredientsWithAmount.size() == 0)) {
					return true;
				} else if (ingredientsWithAmount!=null) {
					if (this.ingredientsWithAmount.size() == 0) {
						return (other.ingredientsWithAmount == null || other.ingredientsWithAmount.size() == 0);
					} else {
						return ingredientsWithAmount.equals(other.ingredientsWithAmount);
					}
				}
			}
			return false;
		}
	}

}
