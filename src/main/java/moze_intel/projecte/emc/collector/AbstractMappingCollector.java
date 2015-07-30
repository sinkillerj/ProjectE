package moze_intel.projecte.emc.collector;

import moze_intel.projecte.emc.IValueArithmetic;
import moze_intel.projecte.emc.collector.IMappingCollector;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractMappingCollector<T, V extends Comparable<V>> implements IMappingCollector<T, V>
{
	protected IValueArithmetic<V> arithmetic;
	public AbstractMappingCollector(IValueArithmetic<V> arithmetic) {
		this.arithmetic = arithmetic;
	}

	public void addConversion(int outnumber, T output, Map<T, Integer> ingredientsWithAmount) {
		addConversion(outnumber, output, ingredientsWithAmount, arithmetic.getZero());
	}

	public void addConversion(int outnumber, T output, Iterable<T> ingredients) {
		addConversion(outnumber, output, ingredients, arithmetic.getZero());
	}

	protected Map<T, Integer> listToMapOfCounts(Iterable<T> iterable) {
		Map<T, Integer> map = new HashMap<T, Integer>();
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

	public void addConversion(int outnumber, T output, Iterable<T> ingredients, V baseValueForConversion) {
		this.addConversion(outnumber, output, listToMapOfCounts(ingredients), baseValueForConversion);
	}

	public abstract void addConversion(int outnumber, T output, Map<T, Integer> ingredientsWithAmount, V baseValueForConversion);
}
