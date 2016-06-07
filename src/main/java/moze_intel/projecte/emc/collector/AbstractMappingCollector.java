package moze_intel.projecte.emc.collector;

import moze_intel.projecte.emc.arithmetics.IValueArithmetic;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractMappingCollector<T, V extends Comparable<V>, A extends IValueArithmetic> implements IExtendedMappingCollector<T, V, A>
{

	private final A defaultArithmetic;

	AbstractMappingCollector(A defaultArithmetic) {
		this.defaultArithmetic = defaultArithmetic;
	}

	public void addConversion(int outnumber, T output, Iterable<T> ingredients) {
		addConversion(outnumber, output, listToMapOfCounts(ingredients));
	}

	public void addConversion(int outnumber, T output, Iterable<T> ingredients, A arithmeticForConversion) {
		addConversion(outnumber, output, listToMapOfCounts(ingredients), arithmeticForConversion);
	}

	private Map<T, Integer> listToMapOfCounts(Iterable<T> iterable) {
		Map<T, Integer> map = new HashMap<>();
		for (T ingredient : iterable) {
			if (map.containsKey(ingredient)) {
				int amount = map.get(ingredient);
				map.put(ingredient, amount + 1);
			} else {
				map.put(ingredient, 1);
			}
		}
		return map;
	}

	@Override
	public void setValueFromConversion(int outnumber, T something, Iterable<T> ingredients)
	{
		this.setValueFromConversion(outnumber, something, listToMapOfCounts(ingredients));
	}

	public abstract void setValueFromConversion(int outnumber, T something, Map<T, Integer> ingredientsWithAmount);

	public void addConversion(int outnumber, T output, Map<T, Integer> ingredientsWithAmount) {
		this.addConversion(outnumber, output, ingredientsWithAmount, this.defaultArithmetic);
	}

	public abstract void addConversion(int outnumber, T output, Map<T, Integer> ingredientsWithAmount, A arithmeticForConversion);

	public A getArithmetic()
	{
		return this.defaultArithmetic;
	}

	@Override
	public void finishCollection() {

	}
}
