package moze_intel.projecte.emc;

import moze_intel.projecte.PECore;
import moze_intel.projecte.utils.PELogger;

import java.util.*;

public class GraphMapper<T extends Comparable<T>> {
    protected Map<T,List<Conversion<T>>> conversionsFor = new HashMap<T, List<Conversion<T>>>();
    protected Map<T,List<Conversion<T>>> usedIn = new HashMap<T,List<Conversion<T>>>();
    protected Map<T,Conversion<T>> fixedValueFor = new HashMap<T, Conversion<T>>();
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

    public void addConversionMultiple(int outnumber, T output, Map<T, Integer> ingredientsWithAmount) {
        List<Conversion<T>> conversionsForOutput = getConversionsFor(output);
        Conversion<T> conversion = new Conversion<T>(output, outnumber,ingredientsWithAmount);
        conversionsForOutput.add(conversion);
        for (Map.Entry<T,Integer> ingredient:ingredientsWithAmount.entrySet()) {
            List<Conversion<T>> usesForIngredient = getUsesFor(ingredient.getKey());
            usesForIngredient.add(conversion);
        }
    }

    public void addConversion(int outnumber, T output, Iterable<T> ingredients) {
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
    }


    public Map<T, Double> generateValues() {
        Map<T, Double> valueFor = new HashMap<T, Double>();
        List<Conversion<T>> solvableConversions = new LinkedList<Conversion<T>>();

        for (Conversion<T> fixedValueConversion: fixedValueFor.values()) {
            if (fixedValueConversion.type == FixedValue.FixAndInherit) {
                solvableConversions.add(fixedValueConversion);
            } else if (fixedValueConversion.type == FixedValue.FixAndDoNotInherit) {
                //Thing has a fixed Value, that should not be inherited, so all Conversions using this are invalid
                for (Conversion<T> use:getUsesFor(fixedValueConversion.output)) {
                    use.markInvalid();
                    solvableConversions.add(use);
                }
                getConversionsFor(fixedValueConversion.output).clear();
                getConversionsFor(fixedValueConversion.output).add(fixedValueConversion);
                getUsesFor(fixedValueConversion.output).clear();
                solvableConversions.add(fixedValueConversion);
            }
        }


        List<Conversion<T>> nextSolvableConversions = new LinkedList<Conversion<T>>();
        while(!solvableConversions.isEmpty()) {
            for (Conversion<T> solvableConversion: solvableConversions) {
                //conversion has a value and no ingredient dependency
                assert solvableConversion.ingredientsWithAmount == null || solvableConversion.ingredientsWithAmount.size() == 0;
                T thisOutput = solvableConversion.output;
                if (solvableConversion.isValid()) {
                    //Is valid conversion
                    for (Conversion<T> use: getUsesFor(thisOutput)) {
                        //use.ingredientsWithAmount can not be null, because our output 'isUsedIn' the conversion.
                        assert use.ingredientsWithAmount != null;
                        Integer amount = use.ingredientsWithAmount.get(thisOutput);
                        assert amount != null && amount > 0;
                        use.value += amount * solvableConversion.value;
                        use.ingredientsWithAmount.remove(thisOutput);
                        if (use.ingredientsWithAmount.size() == 0) {
                            //FIXME there still might be other conversions for the output of 'use', so this is not solveable!
                            //Does not have any dependencys anymore: we can solve it in the next run.
                            nextSolvableConversions.add(use);
                        }
                    }
                    Collection<Conversion<T>> conversionsForThis = getConversionsFor(thisOutput);
                    double minValue = Double.POSITIVE_INFINITY;
                    for (Conversion<T> conversion: conversionsForThis) {
                        if (conversion.isValid()) {
                            if (conversion.value < minValue) {
                                minValue = conversion.value;
                            }
                        } else {
                            minValue = 0;
                            break;
                        }
                    }
                    if (0 < minValue && minValue < Double.POSITIVE_INFINITY) {
                        //FIXME this Value is not propagated properly!
                        //There are only valid Conversions for this Output => Choose Minimum
                        valueFor.put(thisOutput, minValue);
                        conversionsFor.remove(thisOutput);
                    }
                } else {
                    //Is invalid conversion
                    for (Conversion<T> use: getUsesFor(thisOutput)) {
                        use.markInvalid();
                        nextSolvableConversions.add(use);
                    }
                    Collection<Conversion<T>> conversionsForThis = getConversionsFor(thisOutput);
                    conversionsForThis.remove(solvableConversion);
                    if (conversionsForThis.size() == 0) {
                        //No valid Conversion left => No Value
                        valueFor.put(thisOutput, (double) 0);
                        conversionsFor.remove(thisOutput);
                    }
                }
            }

            {//Swap And Clear
                solvableConversions.clear();
                List<Conversion<T>> tmp = solvableConversions;
                solvableConversions = nextSolvableConversions;
                nextSolvableConversions = tmp;
            }
        }
        for (Conversion<T> fixedConversion: fixedValueFor.values()) {
            if (fixedConversion.type == FixedValue.FixAfterInherit) {
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
