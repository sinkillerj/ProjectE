package moze_intel.projecte.emc;


import java.util.*;

public class ComplexGraphMapper<T, V extends Comparable<V>> extends GraphMapper<T, V> {
	public ComplexGraphMapper(IValueArithmetic arithmetic) {
		super(arithmetic);
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
						debugFormat("Set value for %s to %s because 0 conversions left\n", something.toString(), 0);
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
						debugFormat("Set value for %s to %d because %d/%d Conversions solved\n", something.toString(), minValue, getNoDependencyConversionCountFor(something), getConversionsFor(something).size());
					}
				}
				System.out.format("lookat: %d solvable: %d\n", lookAt.size(), solvableThings.size());
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
				if (getNoDependencyConversionCountFor(entry.getKey()) == entry.getValue().size() || valueFor.containsKey(entry.getKey()))  {
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
				debugFormat("minValue for %s: %d ALL: %s\n", entry.getKey().toString(), minValue, minValueAll);
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
						System.out.println("Starting recursion...");
						int count = findDeepIngredientCountForConversion(conversion, conversion.output, new HashSet<T>());
						System.out.println(count);
						if (count >= conversion.outnumber) {
							debugFormat("Removing %s. Count: %s: %d -> %d; %d/%d, min: %s this: %s\n", conversion.toString(), conversion.output, count, conversion.outnumber, getNoDependencyConversionCountFor(conversion.output), getConversionsFor(conversion.output).size(), minValue, arithmetic.div(conversion.value, conversion.outnumber));
							for (T ingredient : conversion.ingredientsWithAmount.keySet()) {
								debugFormat("%s %d/%d\n", ingredient.toString(), getNoDependencyConversionCountFor(ingredient), getConversionsFor(ingredient).size());
							}
							toRemove.add(conversion);
						} else if (count == 0) {
							if (!hasSolvedDependency(conversion)) {
								debugFormat("Removing %s because it has no other solved dependencys\n", conversion);
								toRemove.add(conversion);
							}
						} else  {
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
	protected boolean hasSolvedDependency(Conversion conversion) {
		if (conversion.ingredientsWithAmount != null && conversion.ingredientsWithAmount.size() > 0) {
			for (T ingredient: conversion.ingredientsWithAmount.keySet()) {
				if (hasSolvedConversionDependency(ingredient, new HashSet<T>(Arrays.asList(conversion.output)))) {
					return true;
				}
			}
			return false;
		}
		return true;
	}

	protected boolean hasSolvedConversionDependency(T something, Set<T> visited) {
		if (visited.contains(something)) return false;
		visited.add(something);
		for (Conversion conversion: getConversionsFor(something)) {
			if (conversion.ingredientsWithAmount == null || conversion.ingredientsWithAmount.size() == 0) return true;
			for (T ingredient: conversion.ingredientsWithAmount.keySet()) {
				if (hasSolvedConversionDependency(ingredient, visited)) return true;
			}
		}
		return false;
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
					//visited.remove(ingredient);
				}
			}
		}
		return count;
	}
}
