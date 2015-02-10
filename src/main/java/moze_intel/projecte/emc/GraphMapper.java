package moze_intel.projecte.emc;


import moze_intel.projecte.utils.PELogger;

import java.util.*;

public class GraphMapper<T, V extends Comparable<V>> implements IMappingCollector<T, V> {
	private static final boolean DEBUG_GRAPHMAPPER = false;

	private static void debugFormat(String format, Object... args) {
		if (DEBUG_GRAPHMAPPER)
			System.out.format(format, args);
	}

	private static void debugPrintln(String s) {
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


	public Map<T, V> generateValues() {
		Map<T, V> valueFor = new HashMap<T, V>();
		Map<T, V> solvableThings = new HashMap<T, V>();

		//Everything, that only appears in 'uses' and has no conversion itself has a value of 0.
		for (T someThing : usedIn.keySet()) {
			if (!conversionsFor.containsKey(someThing) || conversionsFor.get(someThing).size() == 0) {
				solvableThings.put(someThing, arithmetic.getZero());
			}
		}

		solvableThings.putAll(fixValueBeforeInherit);


		Set<T> lookAt = new HashSet<T>(conversionsFor.keySet());
		while (!solvableThings.isEmpty() || !lookAt.isEmpty()) {
			while (true) {
				for (T something : lookAt) {
					if (getConversionsFor(something).size() == 0) {
						solvableThings.put(something, arithmetic.getZero());
						debugFormat("Set value for %s to %f because 0 conversions left\n", something.toString(), 0.0);
					} else if (getNoDependencyConversionCountFor(something) == getConversionsFor(something).size()) {
						//The output of this usage has only Conversions with a value left: Choose minimum value
						V minValue = arithmetic.getZero();
						for (Conversion conversion : getConversionsFor(something)) {
							assert conversion.ingredientsWithAmount == null || conversion.ingredientsWithAmount.size() == 0;
							V thisValue = arithmetic.div(conversion.value,  conversion.outnumber);
							assert thisValue.compareTo(arithmetic.getZero()) >= 0;
							if (arithmetic.isZero(minValue) || (thisValue.compareTo(arithmetic.getZero()) > 0 && thisValue.compareTo(minValue) < 0)) {
								minValue = thisValue;
							}
						}
						assert minValue.compareTo(arithmetic.getZero()) >= 0;
						assert !solvableThings.containsKey(something);
						solvableThings.put(something, minValue);
						debugFormat("Set value for %s to %f because %d/%d Conversions solved\n", something.toString(), minValue, getNoDependencyConversionCountFor(something), getConversionsFor(something).size());
					}
				}
				lookAt.clear();
				if (solvableThings.isEmpty()) break;

				for (Map.Entry<T, V> solvableThing : solvableThings.entrySet()) {
					if (valueFor.containsKey(solvableThing.getKey())) continue;
					if (!arithmetic.isFree(solvableThing.getValue())) {
						valueFor.put(solvableThing.getKey(), solvableThing.getValue());
					}
					if (solvableThing.getValue().compareTo(arithmetic.getZero()) > 0 || arithmetic.isFree(solvableThing.getValue())) {
						//Solvable Thing has a Value. Set it in all Conversions
						for (Conversion use : getUsesFor(solvableThing.getKey())) {
							assert use.ingredientsWithAmount != null;
							Integer amount = use.ingredientsWithAmount.get(solvableThing.getKey());
							if (amount == null)
								throw new RuntimeException("F u!");
							assert amount != null && amount != 0;
							if (!arithmetic.isFree(solvableThing.getValue())) {
								use.value = arithmetic.add(arithmetic.mul(amount, solvableThing.getValue()), use.value);
							}
							use.ingredientsWithAmount.remove(solvableThing.getKey());
							if (use.ingredientsWithAmount.size() == 0) {
								increaseNoDependencyConversionCountFor(use.output);
								lookAt.add(use.output);
							}
						}
					} else {
						//Solvable thing has no Value - All Conversions using this are invalid
						for (Conversion use : getUsesFor(solvableThing.getKey())) {
							for (T ingredient : use.ingredientsWithAmount.keySet()) {
								if (!ingredient.equals(solvableThing.getKey())) {
									getUsesFor(ingredient).remove(use);
								}
							}
							use.markInvalid();
							getConversionsFor(use.output).remove(use);
							lookAt.add(use.output);
						}
					}
				}
				debugPrintln("Finished solvableThings...");
				solvableThings.clear();
			}
			debugPrintln("No Solvables left... Trying to remove Conversions");
			//Remove all Conversions, that have ingredients left for things that have a noDepencencyConversion
			List<Conversion> toRemove = new LinkedList<Conversion>();
			boolean foundMinSolve = false;
			for (Map.Entry<T, List<Conversion>> entry : conversionsFor.entrySet()) {
				debugFormat("Looking at %s with %d/%d\n", entry, getNoDependencyConversionCountFor(entry.getKey()), entry.getValue().size());
				if (getNoDependencyConversionCountFor(entry.getKey()) == entry.getValue().size()) {
					//Thing has no noDepencencyConversion => ignore this
					continue;
				}
				//=> noDependencyConversionCount > 0
				V minValue = null;
				V minValueAll = null;
				for (Conversion conversion : entry.getValue()) {
					V conversionValue = arithmetic.div(conversion.value, conversion.outnumber);
					if (conversion.ingredientsWithAmount == null || conversion.ingredientsWithAmount.size() == 0) {
						if (conversionValue.compareTo(arithmetic.getZero()) > 0 && (minValue == null || conversionValue.compareTo(minValue) < 0)) {
							minValue = conversionValue;
						}
					}
					if (minValueAll == null || conversionValue.compareTo(minValueAll) < 0) {
						minValueAll = conversionValue;
					}
				}
				debugFormat("minValue for %s: %f ALL: %f\n", entry.getKey().toString(), minValue, minValueAll);
				if (minValue != null && minValue.compareTo(minValueAll) <= 0) {
					//There is a valid Conversion, that has the smallest value => we can set this value right away
					solvableThings.put(entry.getKey(), minValue);
					foundMinSolve = true;
					continue;
				}
				if (foundMinSolve)
					continue; //We already have a solution by selecting minimum => Don't need to think about removing conversions.
				Iterator<Conversion> iterator = entry.getValue().iterator();
				while (iterator.hasNext()) {
					Conversion conversion = iterator.next();
					if (conversion.ingredientsWithAmount != null && conversion.ingredientsWithAmount.size() > 0) {
						//Conversion has ingredients left and there are other conversions without ingredients
						int count = findDeepIngredientCountForConversion(conversion, conversion.output, new HashSet<T>());
						if (count >= conversion.outnumber) {
							debugFormat("Removing %s. Count: %s: %d -> %d; %d/%d, min: %s this: %s\n", conversion.toString(), conversion.output, count, conversion.outnumber, getNoDependencyConversionCountFor(conversion.output), getConversionsFor(conversion.output).size(), minValue, arithmetic.div(conversion.value, conversion.outnumber));
							for (T ingredient : conversion.ingredientsWithAmount.keySet()) {
								debugFormat("%s %d/%d\n", ingredient.toString(), getNoDependencyConversionCountFor(ingredient), getConversionsFor(ingredient).size());
							}
							toRemove.add(conversion);
						} else {
							debugFormat("NOT Removing %s. Count: %s: %d -> %d; %d/%d, min: %s this: %s\n", conversion.toString(), conversion.output, count, conversion.outnumber, getNoDependencyConversionCountFor(conversion.output), getConversionsFor(conversion.output).size(), minValue, arithmetic.div(conversion.value, conversion.outnumber));
						}
					} else {
						debugFormat("Skipping %s\n", conversion);
					}
				}
			}
			if (!foundMinSolve) {
				for (Conversion conversion : toRemove) {
					getConversionsFor(conversion.output).remove(conversion);
					for (T ingredient : conversion.ingredientsWithAmount.keySet()) {
						getUsesFor(ingredient).remove(conversion);
					}
					lookAt.add(conversion.output);
				}
			}
		}
		for (Map.Entry<T, V> fixedValueAfterInherit : fixValueAfterInherit.entrySet()) {
			valueFor.put(fixedValueAfterInherit.getKey(), fixedValueAfterInherit.getValue());
		}
		return valueFor;
	}

	protected int findDeepIngredientCountForConversion(Conversion conversion, T something, Set<T> visited) {
		int count = 0;
		if (conversion.ingredientsWithAmount != null) {
			for (T ingredient : conversion.ingredientsWithAmount.keySet()) {
				if (something.equals(ingredient)) {
					Integer i = conversion.ingredientsWithAmount.get(ingredient);
					if (i != null && i > 0)
						count += i;
				} else if (visited.contains(ingredient)) {
					return 0;
				} else {
					int minCount = Integer.MAX_VALUE;
					visited.add(ingredient);
					for (Conversion ingredientConversion : getConversionsFor(ingredient)) {
						int ingredientConversionCount = findDeepIngredientCountForConversion(ingredientConversion, something, visited);
						if (ingredientConversionCount < minCount) minCount = ingredientConversionCount;
					}
					count += minCount;
					visited.remove(ingredient);
				}
			}
		}
		return count;
	}

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
