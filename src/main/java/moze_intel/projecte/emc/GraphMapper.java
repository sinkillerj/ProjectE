package moze_intel.projecte.emc;


import moze_intel.projecte.utils.PELogger;

import java.util.*;

public class GraphMapper<T> implements IMappingCollector<T> {
    private static final boolean DEBUG_GRAPHMAPPER = false;
    private static void debugFormat(String format, Object ... args){
        if (DEBUG_GRAPHMAPPER)
            System.out.format(format, args);
    }
    private static void debugPrintln(String s) {
        debugFormat("%s\n", s);
    }

    protected Map<T,List<Conversion<T>>> conversionsFor = new HashMap<T, List<Conversion<T>>>();
    protected Map<T,List<Conversion<T>>> usedIn = new HashMap<T,List<Conversion<T>>>();
    protected Map<T,Double> fixValueBeforeInherit = new HashMap<T, Double>();
    protected Map<T,Double> fixValueAfterInherit = new HashMap<T, Double>();
    protected Map<T,Integer> noDependencyConversionCount = new HashMap<T, Integer>();

    protected static <K,V> List<V> getOrCreateList(Map<K, List<V>> map, K key) {
        List<V> list;
        if (map.containsKey(key)) {
            list = map.get(key);
        } else {
            list = new LinkedList<V>();
            map.put(key,list);
        }
        return list;
    }

    protected List<Conversion<T>> getConversionsFor(T something) {
        return getOrCreateList(conversionsFor, something);
    }
    protected List<Conversion<T>> getUsesFor(T something) {
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
        addConversionMultiple(outnumber, output, ingredientsWithAmount, 0.0);
    }
    public void addConversionMultiple(int outnumber, T output, Map<T, Integer> ingredientsWithAmount, double baseValueForConversion) {
        ingredientsWithAmount = new HashMap<T, Integer>(ingredientsWithAmount);
        //Add the Conversions to the conversionsFor and usedIn Maps:
        Conversion<T> conversion = new Conversion<T>(output, outnumber,ingredientsWithAmount);
        conversion.value = baseValueForConversion;
        getConversionsFor(output).add(conversion);
        if (ingredientsWithAmount.size() == 0) increaseNoDependencyConversionCountFor(output);

        for (Map.Entry<T,Integer> ingredient:ingredientsWithAmount.entrySet()) {
            List<Conversion<T>> usesForIngredient = getUsesFor(ingredient.getKey());
            if (ingredient.getValue() == null) throw new IllegalArgumentException("ingredient amount value has to be != null");
            usesForIngredient.add(conversion);
        }
    }

    public void addConversion(int outnumber, T output, Iterable<T> ingredients) {
        addConversion(outnumber,output,ingredients,0.0);
    }
    public void addConversion(int outnumber, T output, Iterable<T> ingredients, double baseValueForConversion) {
        Map<T,Integer> ingredientsWithAmount = new HashMap<T, Integer>();
        for (T ingredient: ingredients) {
            if (ingredientsWithAmount.containsKey(ingredient)) {
                int amount = ingredientsWithAmount.get(ingredient);
                ingredientsWithAmount.put(ingredient, amount+1);
            } else {
                ingredientsWithAmount.put(ingredient, 1);
            }
        }
        this.addConversionMultiple(outnumber, output, ingredientsWithAmount, baseValueForConversion);
    }

    /**
     * Set a Value for something. value has to be >= 0 or Double.NaN, which indicates that 'something' can be used in
     * Conversions, but does not add anything to the value of the Conversion-result.
     * @param something
     * @param value  >= 0 or Double.NaN
     * @param type
     */
    public void setValue(T something, double value, FixedValue type) {
        switch(type) {
            case FixAndInherit:
                if (fixValueBeforeInherit.containsKey(something)) PELogger.logWarn("Overwriting fixValueBeforeInherit for " + something +":" + fixValueBeforeInherit.get(something) + " to " + value);
                fixValueBeforeInherit.put(something,value);
                if (fixValueAfterInherit.containsKey(something)) PELogger.logWarn("Removign fixValueAfterInherit for " + something +" before: " + fixValueAfterInherit.get(something));
                fixValueAfterInherit.remove(something);
                break;
            case FixAndDoNotInherit:
                if (fixValueBeforeInherit.containsKey(something)) PELogger.logWarn("Overwriting fixValueBeforeInherit for " + something +":" + fixValueBeforeInherit.get(something) + " to " + 0.0);
                fixValueBeforeInherit.put(something,0.0);
                if (fixValueAfterInherit.containsKey(something)) PELogger.logWarn("Overwriting fixValueAfterInherit for " + something +":" + fixValueAfterInherit.get(something) + " to " + value);
                fixValueAfterInherit.put(something,value);
                break;
            case FixAfterInherit:
                if (fixValueBeforeInherit.containsKey(something)) PELogger.logWarn("Removing fixValueBeforeInherit for " + something +" before: " + fixValueBeforeInherit.get(something));
                fixValueBeforeInherit.remove(something);
                if (fixValueAfterInherit.containsKey(something)) PELogger.logWarn("Overwriting fixValueAfterInherit for " + something +":" + fixValueAfterInherit.get(something) + " to " + value);
                fixValueAfterInherit.put(something,value);
                break;
            case SuggestionAndInherit:
                this.addConversionMultiple(1, something, new HashMap<T, Integer>(), value);
                break;
        }
    }


    public Map<T, Double> generateValues() {
        Map<T, Double> valueFor = new HashMap<T, Double>();
        Map<T,Double> solvableThings = new HashMap<T,Double>();

        //Everything, that only appears in 'uses' and has no conversion itself has a value of 0.
        for (T someThing: usedIn.keySet()) {
            if (!conversionsFor.containsKey(someThing) || conversionsFor.get(someThing).size() == 0) {
                solvableThings.put(someThing,0.0);
            }
        }

        solvableThings.putAll(fixValueBeforeInherit);


        Set<T> lookAt = new HashSet<T>(conversionsFor.keySet());
        while(!solvableThings.isEmpty() || !lookAt.isEmpty()) {
            while (true) {
                for (T something : lookAt) {
                    if (getConversionsFor(something).size() == 0) {
                        solvableThings.put(something, 0.0);
                        debugFormat("Set value for %s to %f because 0 conversions left\n", something.toString(), 0.0);
                    } else if (getNoDependencyConversionCountFor(something) == getConversionsFor(something).size()) {
                        //The output of this usage has only Conversions with a value left: Choose minimum value
                        double minValue = 0;
                        for (Conversion<T> conversion : getConversionsFor(something)) {
                            assert conversion.ingredientsWithAmount == null || conversion.ingredientsWithAmount.size() == 0;
                            double thisValue = conversion.value / conversion.outnumber;
                            assert thisValue >= 0;
                            if (minValue == 0 || (0 < thisValue && thisValue < minValue)) {
                                minValue = conversion.value / conversion.outnumber;
                            }
                        }
                        assert 0 <= minValue && minValue < Double.POSITIVE_INFINITY;
                        assert !solvableThings.containsKey(something);
                        solvableThings.put(something, minValue);
                        debugFormat("Set value for %s to %f because %d/%d Conversions solved\n", something.toString(), minValue, getNoDependencyConversionCountFor(something), getConversionsFor(something).size());
                    }
                }
                lookAt.clear();
                if (solvableThings.isEmpty()) break;

                for (Map.Entry<T, Double> solvableThing : solvableThings.entrySet()) {
                    if (valueFor.containsKey(solvableThing.getKey())) continue;
                    if (!solvableThing.getValue().isNaN()) {
                        valueFor.put(solvableThing.getKey(), solvableThing.getValue());
                    }
                    if (solvableThing.getValue() > 0 || solvableThing.getValue().isNaN()) {
                        //Solvable Thing has a Value. Set it in all Conversions
                        for (Conversion<T> use : getUsesFor(solvableThing.getKey())) {
                            assert use.ingredientsWithAmount != null;
                            Integer amount = use.ingredientsWithAmount.get(solvableThing.getKey());
                            if (amount == null)
                                throw new RuntimeException("F u!");
                            assert amount != null && amount != 0;
                            if (!solvableThing.getValue().isNaN()) {
                                use.value += amount * Math.floor(solvableThing.getValue());
                            }
                            use.ingredientsWithAmount.remove(solvableThing.getKey());
                            if (use.ingredientsWithAmount.size() == 0) {
                                increaseNoDependencyConversionCountFor(use.output);
                                lookAt.add(use.output);
                            }
                        }
                    } else {
                        //Solvable thing has no Value - All Conversions using this are invalid
                        for (Conversion<T> use : getUsesFor(solvableThing.getKey())) {
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
            List<Conversion<T>> toRemove = new LinkedList<Conversion<T>>();
            boolean foundMinSolve = false;
            for (Map.Entry<T,List<Conversion<T>>> entry:conversionsFor.entrySet()) {
                debugFormat("Looking at %s with %d/%d\n", entry, getNoDependencyConversionCountFor(entry.getKey()), entry.getValue().size());
                if (getNoDependencyConversionCountFor(entry.getKey()) == entry.getValue().size()) {
                    //Thing has no noDepencencyConversion => ignore this
                    continue;
                }
                //=> noDependencyConversionCount > 0
                double minValue = Double.POSITIVE_INFINITY;
                double minValueAll = Double.POSITIVE_INFINITY;
                for (Conversion<T> conversion: entry.getValue()) {
                    double conversionValue = conversion.value / conversion.outnumber;
                    if (conversion.ingredientsWithAmount == null || conversion.ingredientsWithAmount.size() == 0) {
                        if (0 < conversionValue && conversionValue < minValue) {
                            minValue = conversionValue;
                        }
                    }
                    if (conversionValue < minValueAll) {
                        minValueAll = conversionValue;
                    }
                }
                debugFormat("minValue for %s: %f ALL: %f\n", entry.getKey().toString(), minValue, minValueAll);
                if (minValue <= minValueAll) {
                    solvableThings.put(entry.getKey(), minValue);
                    foundMinSolve = true;
                    continue;
                }
                Iterator<Conversion<T>> iterator = entry.getValue().iterator();
                while (iterator.hasNext()) {
                    Conversion<T> conversion = iterator.next();
                    if (conversion.ingredientsWithAmount != null && conversion.ingredientsWithAmount.size() > 0) {
                        //Conversion has ingredients left and there are other conversions without ingredients
                        int count = findDeepIngredientCountForConversion(conversion, conversion.output, new HashSet<T>());
                        if (count >= conversion.outnumber) {
                            debugFormat("Removing %s. Count: %s: %d -> %d; %d/%d, %f < %f\n", conversion.toString(), conversion.output, count, conversion.outnumber, getNoDependencyConversionCountFor(conversion.output), getConversionsFor(conversion.output).size(), minValue, conversion.value / conversion.outnumber);
                            for (T ingredient: conversion.ingredientsWithAmount.keySet()) {
                                debugFormat("%s %d/%d\n", ingredient.toString(), getNoDependencyConversionCountFor(ingredient), getConversionsFor(ingredient).size());
                            }
                            toRemove.add(conversion);
                        } else {
                            debugFormat("NOT Removing %s. Count: %s: %d -> %d; %d/%d, %f < %f\n", conversion.toString(), conversion.output, count, conversion.outnumber, getNoDependencyConversionCountFor(conversion.output), getConversionsFor(conversion.output).size(), minValue, conversion.value / conversion.outnumber);
                        }
                    } else {
                        debugFormat("Skipping %s\n", conversion);
                    }
                }
            }
            if (!foundMinSolve) {
                for (Conversion<T> conversion : toRemove) {
                    getConversionsFor(conversion.output).remove(conversion);
                    for (T ingredient : conversion.ingredientsWithAmount.keySet()) {
                        getUsesFor(ingredient).remove(conversion);
                    }
                    lookAt.add(conversion.output);
                }
            }
        }
        for (Map.Entry<T,Double> fixedValueAfterInherit: fixValueAfterInherit.entrySet()) {
            valueFor.put(fixedValueAfterInherit.getKey(),fixedValueAfterInherit.getValue());
        }
        return valueFor;
    }

    protected int findDeepIngredientCountForConversion(Conversion<T> conversion, T something, Set<T> visited) {
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
                    for (Conversion<T> ingredientConversion: getConversionsFor(ingredient)) {
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

    protected static class Conversion<T> {
        T output;

        int outnumber = 1;
        double value = 0;
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
            this.value = 0;
        }

        public String toString() {
            return "" + value + " + " + this.ingredientsWithAmount + " => " + outnumber + "x" + output;
        }
    }

}
