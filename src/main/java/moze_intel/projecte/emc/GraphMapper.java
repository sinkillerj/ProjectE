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
        }
        fixedValueConversion.value = value;
        fixedValueConversion.type = type;
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
    }
}
