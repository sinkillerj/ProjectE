package moze_intel.projecte.emc;

import moze_intel.projecte.utils.PELogger;

import java.util.*;

public class GraphMapper<T extends Comparable<T>> {
    protected Map<T,List<Conversion<T>>> conversionsFor = new HashMap<T, List<Conversion<T>>>();
    protected Map<T,List<Conversion<T>>> usedIn = new HashMap<T,List<Conversion<T>>>();
    protected Map<T,Conversion<T>> fixedValueFor = new HashMap<T, Conversion<T>>();
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
        //Add the Conversions to the conversionsFor and usedIn Maps:
        Conversion<T> conversion = new Conversion<T>(output, outnumber,ingredientsWithAmount);
        conversion.value = baseValueForConversion;
        getConversionsFor(output).add(conversion);

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
        Conversion<T> fixedValueConversion;
        if (fixedValueFor.containsKey(something)) {
            fixedValueConversion = fixedValueFor.get(something);
            PELogger.logWarn("Fixed Value for " + something.toString() + " is overwritten!");
        } else {
            fixedValueConversion = new Conversion<T>(something);
            fixedValueFor.put(something,fixedValueConversion);
            getConversionsFor(something).add(fixedValueConversion);
        }
        fixedValueConversion.fixedValue = value;
        if (type == FixedValue.SuggestionAndInherit || type == FixedValue.FixAndInherit || type == FixedValue.FixAndDoNotInherit) {
            fixedValueConversion.value = value;
        }
        fixedValueConversion.type = type;
        //TODO do i need to increase the noDependencyConversionCount for this here?
    }


    public Map<T, Double> generateValues() {
        Map<T, Double> valueFor = new HashMap<T, Double>();
        Map<T,Double> solvableThings = new HashMap<T,Double>();

        for (Conversion<T> fixedValueConversion: fixedValueFor.values()) {
            if (fixedValueConversion.type == FixedValue.FixAndInherit) {
                solvableThings.put(fixedValueConversion.output,fixedValueConversion.fixedValue);
            } else if (fixedValueConversion.type == FixedValue.FixAndDoNotInherit) {
                //Thing has a fixed Value, that should not be inherited, so all Conversions using this are invalid
                for (Conversion<T> use:getUsesFor(fixedValueConversion.output)) {
                    use.markInvalid();//TODO Remove Conversion from the 'conversionsFor'-Map for the Conversion output?
                    //TODO use might be solvable when Conversion has been removed?
                }
                //TODO? Why am i doing this?
                getConversionsFor(fixedValueConversion.output).clear();
                getUsesFor(fixedValueConversion.output).clear();
                solvableThings.put(fixedValueConversion.output,fixedValueConversion.fixedValue);
            }
        }

        //Everything, that only appears in 'uses' and has no conversion itself has a value of 0.
        for (T someThing: usedIn.keySet()) {
            if (!conversionsFor.containsKey(someThing) || conversionsFor.get(someThing).size() == 0) {
                solvableThings.put(someThing,0.0);
            }
        }


        Map<T,Double> nextSolvableThings = new HashMap<T,Double>();
        while(!solvableThings.isEmpty()) {
            for (Map.Entry<T,Double> solvableThing: solvableThings.entrySet()) {
                assert !valueFor.containsKey(solvableThing.getKey());
                valueFor.put(solvableThing.getKey(),solvableThing.getValue());
                if (solvableThing.getValue() > 0) {
                    //Solvable Thing has a Value. Set it in all Conversions
                    for (Conversion<T> use: getUsesFor(solvableThing.getKey())) {
                        assert use.ingredientsWithAmount != null;
                        Integer amount = use.ingredientsWithAmount.get(solvableThing.getKey());
                        assert amount != null && amount > 0;
                        use.value += amount * solvableThing.getValue();
                        use.ingredientsWithAmount.remove(solvableThing.getKey());
                        if (use.ingredientsWithAmount.size() == 0) {
                            increaseNoDependencyConversionCountFor(use.output);
                            if (getNoDependencyConversionCountFor(use.output) == getConversionsFor(use.output).size()) {
                                //The output of this usage has only Conversions with a value left: Choose minimum value
                                double minValue = Double.POSITIVE_INFINITY;
                                for (Conversion<T> conversion: getConversionsFor(use.output)) {
                                    assert conversion.isValid();
                                    if (conversion.value / conversion.outnumber < minValue) {
                                        minValue = conversion.value / conversion.outnumber;
                                    }
                                }
                                assert 0 < minValue && minValue < Double.POSITIVE_INFINITY;
                                assert !nextSolvableThings.containsKey(use.output);
                                nextSolvableThings.put(use.output, minValue);
                            }
                        }
                    }
                } else {
                    //Solvable thing has no Value - All Conversions using this are invalid
                    for (Conversion<T> use: getUsesFor(solvableThing.getKey())) {
                        use.markInvalid();
                        getConversionsFor(use.output).remove(use);
                        //TODO need to Check if this is solvable
                    }
                }
            }

            {//Swap Ande Clear
                solvableThings.clear();
                Map<T,Double> tmp = solvableThings;
                solvableThings = nextSolvableThings;
                nextSolvableThings = tmp;
            }
        }
        for (Conversion<T> fixedConversion: fixedValueFor.values()) {
            if (fixedConversion.type == FixedValue.FixAfterInherit || fixedConversion.type == FixedValue.FixAndDoNotInherit) {
                valueFor.put(fixedConversion.output,fixedConversion.fixedValue);
            }
        }
        return valueFor;
    }

    protected static class Conversion<T> {
        T output;
        FixedValue type = FixedValue.SuggestionAndInherit;
        double fixedValue;

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
        public Conversion(T output, double setValue, FixedValue type) {
            this(output);
            if (setValue < 0) setValue = 0;
            this.type = type;
            this.fixedValue = setValue;
            if (type != FixedValue.FixAfterInherit) {
                this.value = setValue;
            }
        }

        public boolean isValid() {
            return this.value > 0 && (this.ingredientsWithAmount == null || this.ingredientsWithAmount.size() == 0);
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
