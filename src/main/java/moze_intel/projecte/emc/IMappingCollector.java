package moze_intel.projecte.emc;

import java.util.Map;

public interface IMappingCollector<T> {
    public static enum FixedValue {
        SuggestionAndInherit,FixAndInherit, FixAfterInherit, FixAndDoNotInherit
    }
    public void addConversionMultiple(int outnumber, T output, Map<T, Integer> ingredientsWithAmount);
    public void addConversionMultiple(int outnumber, T output, Map<T, Integer> ingredientsWithAmount, double baseValueForConversion);
    public void addConversion(int outnumber, T output, Iterable<T> ingredients);
    public void addConversion(int outnumber, T output, Iterable<T> ingredients, double baseValueForConversion);
    public void setValue(T something, double value, FixedValue type);

}
