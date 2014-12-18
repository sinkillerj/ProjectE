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

    public void addConversionMultiple(int outnumber, T output, Iterable<Map.Entry<T, Integer>> ingredientsWithAmount) {
        List<Conversion<T>> conversionsForOutput = getConversionsFor(output);
        Conversion<T> conversion = new Conversion<T>(outnumber,ingredientsWithAmount);
        conversionsForOutput.add(conversion);
        for (Map.Entry<T,Integer> ingredient:ingredientsWithAmount) {
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
        this.addConversionMultiple(outnumber, output, ingredientsWithAmount.entrySet());
    }
    public void setValue(T something, double value, FixedValue type) {
        Conversion<T> fixedValueConversion;
        if (fixedValueFor.containsKey(something)) {
            fixedValueConversion = fixedValueFor.get(something);
            PELogger.logWarn("Fixed Value for " + something.toString() + " is overwritten!");
        } else {
            fixedValueConversion = new Conversion<T>();
            fixedValueFor.put(something,fixedValueConversion);
        }
        fixedValueConversion.value = value;
        fixedValueConversion.type = type;
    }

    protected static class Conversion<T> {

        FixedValue type = FixedValue.SuggestionAndInherit;
        double fixedValue;

        int outnumber = 1;
        double value = 0;
        Iterable<Map.Entry<T, Integer>> ingredientsWithAmount;
        protected Conversion() {}
        protected Conversion(int outnumber, Iterable<Map.Entry<T,Integer>> ingredientsWithAmount) {
            this.outnumber = outnumber;
            this.ingredientsWithAmount = ingredientsWithAmount;
        }
        public Conversion(double setValue, FixedValue type) {
            if (setValue < 0) setValue = 0;
            this.type = type;
            this.fixedValue = setValue;
            if (type != FixedValue.FixAfterInherit) {
                this.value = setValue;
            }

        }
    }
}
