package moze_intel.projecte.emc.collector;


import com.google.common.collect.Maps;
import moze_intel.projecte.emc.arithmetics.IValueArithmetic;
import moze_intel.projecte.utils.PELogger;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public abstract class MappingCollector<T, V extends Comparable<V>,  A extends IValueArithmetic<V>> extends AbstractMappingCollector<T, V, A>  {
	private static final boolean DEBUG_GRAPHMAPPER = false;

	protected final A arithmetic;
	protected MappingCollector(A arithmetic) {
		super(arithmetic);
		this.arithmetic = arithmetic;
	}

	protected static void debugFormat(String format, Object... args) {
		if (DEBUG_GRAPHMAPPER)
			PELogger.logInfo(String.format(format, args));
	}

	protected static void debugPrintln(String s) {
		debugFormat("%s", s);
	}

	protected final Map<T, Conversion> overwriteConversion = Maps.newHashMap();
	protected final Map<T, List<Conversion>> conversionsFor = Maps.newHashMap();
	private final Map<T, List<Conversion>> usedIn = Maps.newHashMap();
	protected final Map<T, V> fixValueBeforeInherit = Maps.newHashMap();
	protected final Map<T, V> fixValueAfterInherit = Maps.newHashMap();
	private final Map<T, Integer> noDependencyConversionCount = Maps.newHashMap();

	public static <K, V> List<V> getOrCreateList(Map<K, List<V>> map, K key) {
		List<V> list;
		if (map.containsKey(key)) {
			list = map.get(key);
		} else {
			list = new LinkedList<>();
			map.put(key, list);
		}
		return list;
	}

	private List<Conversion> getConversionsFor(T something) {
		return getOrCreateList(conversionsFor, something);
	}

	protected List<Conversion> getUsesFor(T something) {
		return getOrCreateList(usedIn, something);
	}

	private int getNoDependencyConversionCountFor(T something) {
		Integer count = noDependencyConversionCount.get(something);
		if (count == null) return 0;
		else return count;
	}

	private void increaseNoDependencyConversionCountFor(T something) {
		noDependencyConversionCount.put(something, getNoDependencyConversionCountFor(something) + 1);
	}

	private void addConversionToIngredientUsages(Conversion conversion) {
		for (Map.Entry<T, Integer> ingredient : conversion.ingredientsWithAmount.entrySet()) {
			List<Conversion> usesForIngredient = getUsesFor(ingredient.getKey());
			if (ingredient.getValue() == null)
				throw new IllegalArgumentException("ingredient amount value has to be != null");
			usesForIngredient.add(conversion);
		}
	}

	public void addConversion(int outnumber, T output, Map<T, Integer> ingredientsWithAmount, A arithmeticForConversion) {
		ingredientsWithAmount = Maps.newHashMap(ingredientsWithAmount);
		if (output == null || ingredientsWithAmount.containsKey(null)) {
			PELogger.logWarn(String.format("Ignoring Recipe because of invalid ingredient or output: %s -> %dx%s", ingredientsWithAmount, outnumber, output));
			return;
		}
		if (outnumber <= 0)
			throw new IllegalArgumentException("outnumber has to be > 0!");
		//Add the Conversions to the conversionsFor and usedIn Maps:
		Conversion conversion = new Conversion(output, outnumber, ingredientsWithAmount);
		conversion.value = arithmetic.getZero();
		conversion.arithmeticForConversion = arithmeticForConversion;
		if (getConversionsFor(output).contains(conversion)) return;
		getConversionsFor(output).add(conversion);
		if (ingredientsWithAmount.size() == 0) increaseNoDependencyConversionCountFor(output);
		addConversionToIngredientUsages(conversion);
	}

	@Override
	public void setValueBefore(T something, V value) {
		if (something == null) return;
		if (fixValueBeforeInherit.containsKey(something))
			PELogger.logWarn("Overwriting fixValueBeforeInherit for " + something + ":" + fixValueBeforeInherit.get(something) + " to " + value);
		fixValueBeforeInherit.put(something, value);
		fixValueAfterInherit.remove(something);
	}

	@Override
	public void setValueAfter(T something, V value) {
		if (something == null) return;
		if (fixValueAfterInherit.containsKey(something))
			PELogger.logWarn("Overwriting fixValueAfterInherit for " + something + ":" + fixValueAfterInherit.get(something) + " to " + value);
		fixValueAfterInherit.put(something, value);
	}



	@Override
	public void setValueFromConversion(int outnumber, T something, Map<T, Integer> ingredientsWithAmount)
	{
		if (something == null || ingredientsWithAmount.containsKey(null)) {
			PELogger.logWarn(String.format("Ignoring setValueFromConversion because of invalid ingredient or output: %s -> %dx%s", ingredientsWithAmount, outnumber, something));
			return;
		}
		if (outnumber <= 0)
			throw new IllegalArgumentException("outnumber has to be > 0!");
		Conversion conversion = new Conversion(something, outnumber, ingredientsWithAmount);
		conversion.arithmeticForConversion = this.arithmetic;
		if (overwriteConversion.containsKey(something)) {
			Conversion oldConversion = overwriteConversion.get(something);
			PELogger.logWarn("Overwriting setValueFromConversion " + overwriteConversion.get(something) + " with " + conversion);
			for (T ingredient: ingredientsWithAmount.keySet()) {
				getUsesFor(ingredient).remove(oldConversion);
			}
		}
		addConversionToIngredientUsages(conversion);
		overwriteConversion.put(something, conversion);
	}


	abstract public Map<T, V> generateValues();

	protected class Conversion {
		public final T output;

		public int outnumber = 1;
		public V value = arithmetic.getZero();
		public Map<T, Integer> ingredientsWithAmount;
		public A arithmeticForConversion;

		Conversion(T output) {
			this.output = output;
		}

		Conversion(T output, int outnumber, Map<T, Integer> ingredientsWithAmount) {
			this(output);
			this.outnumber = outnumber;
			this.ingredientsWithAmount = ingredientsWithAmount;
		}

		public String toString() {
			return "" + value + " + " + ingredientsToString() + " => " + outnumber + "*" + output;
		}

		public String ingredientsToString() {
			if (ingredientsWithAmount == null || ingredientsWithAmount.size() == 0) return "nothing";
			StringBuilder sb = new StringBuilder();
			Iterator<Map.Entry<T,Integer>> iter = ingredientsWithAmount.entrySet().iterator();
			if (iter.hasNext()) {
				Map.Entry<T, Integer> entry = iter.next();
				sb.append(entry.getValue()).append("*").append(entry.getKey().toString());
				while(iter.hasNext()) {
					entry = iter.next();
					sb.append(" + ").append(entry.getValue()).append("*").append(entry.getKey().toString());
				}
			}


			return sb.toString();
		}

		public boolean equals(Conversion other) {
			if (output.equals(other.output) && value.equals(other.value)) {
				if (ingredientsWithAmount == null || ingredientsWithAmount.size() == 0) {
					return other.ingredientsWithAmount == null || other.ingredientsWithAmount.size() == 0;
				} else {
					return ingredientsWithAmount.equals(other.ingredientsWithAmount);
				}
			}
			return false;
		}
	}

}
