package moze_intel.projecte.emc;


import java.util.*;

public class GraphMapper<T extends Comparable<T>> {
    protected Map<T,List<Conversion<T>>> conversionsFor = new HashMap<T, List<Conversion<T>>>();
    protected Map<T,List<Conversion<T>>> usedIn = new HashMap<T,List<Conversion<T>>>();
    protected Map<T,Double> fixValueBeforeInherit = new HashMap<T, Double>();
    protected Map<T,Double> fixValueAfterInherit = new HashMap<T, Double>();
    protected Map<T,Integer> noDependencyConversionCount = new HashMap<T, Integer>();
    public static enum FixedValue {
        SuggestionAndInherit,FixAndInherit, FixAfterInherit, FixAndDoNotInherit
    }
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
        this.addConversionMultiple(outnumber, output, ingredientsWithAmount);
    }
    public void setValue(T something, double value, FixedValue type) {
        switch(type) {
            case FixAndInherit:
                fixValueBeforeInherit.put(something,value);
                fixValueAfterInherit.remove(something);
                break;
            case FixAndDoNotInherit:
                fixValueBeforeInherit.put(something,0.0);
                fixValueAfterInherit.put(something,value);
                break;
            case FixAfterInherit:
                fixValueBeforeInherit.remove(something);
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
                    }
                }
                lookAt.clear();
                if (solvableThings.isEmpty()) break;

                for (Map.Entry<T, Double> solvableThing : solvableThings.entrySet()) {
                    if (valueFor.containsKey(solvableThing.getKey())) continue;
                    valueFor.put(solvableThing.getKey(), solvableThing.getValue());
                    if (solvableThing.getValue() > 0) {
                        //Solvable Thing has a Value. Set it in all Conversions
                        for (Conversion<T> use : getUsesFor(solvableThing.getKey())) {
                            assert use.ingredientsWithAmount != null;
                            Integer amount = use.ingredientsWithAmount.get(solvableThing.getKey());
                            assert amount != null && amount != 0;
                            use.value += amount * solvableThing.getValue();
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
                solvableThings.clear();
            }
            //Remove all Conversions, that have ingredients left for things that have a noDepencencyConversion
            for (Map.Entry<T,List<Conversion<T>>> entry:conversionsFor.entrySet()) {
                if (getNoDependencyConversionCountFor(entry.getKey()) == 0) {
                    //Thing has no noDepencencyConversion => ignore this
                    continue;
                }
                //=> noDependencyConversionCount > 0
                Iterator<Conversion<T>> iterator = entry.getValue().iterator();
                while (iterator.hasNext()) {
                    Conversion<T> conversion = iterator.next();
                    if (conversion.ingredientsWithAmount != null && conversion.ingredientsWithAmount.size() > 0) {
                        //Conversion has ingredients left and there are other conversions without ingredients
                        iterator.remove();
                        lookAt.add(entry.getKey());
                    }
                }
            }
        }
        for (Map.Entry<T,Double> fixedValueAfterInherit: fixValueAfterInherit.entrySet()) {
            valueFor.put(fixedValueAfterInherit.getKey(),fixedValueAfterInherit.getValue());
        }
        return valueFor;
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
    }
}
