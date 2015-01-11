package moze_intel.projecte.emc;

import java.util.Map;

public interface IMappingCollector<T, V extends Comparable<V>> {
	public static enum FixedValue {
		SuggestionAndInherit, FixAndInherit, FixAfterInherit, FixAndDoNotInherit
	}

	public void addConversionMultiple(int outnumber, T output, Map<T, Integer> ingredientsWithAmount);

	public void addConversionMultiple(int outnumber, T output, Map<T, Integer> ingredientsWithAmount, V baseValueForConversion);

	public void addConversion(int outnumber, T output, Iterable<T> ingredients);

	public void addConversion(int outnumber, T output, Iterable<T> ingredients, V baseValueForConversion);

	public void setValue(T something, V value, FixedValue type);

}
