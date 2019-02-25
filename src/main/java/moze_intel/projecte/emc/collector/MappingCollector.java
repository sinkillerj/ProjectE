package moze_intel.projecte.emc.collector;


import com.google.common.collect.Maps;
import moze_intel.projecte.PECore;
import moze_intel.projecte.emc.arithmetics.IValueArithmetic;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class MappingCollector<T, V extends Comparable<V>,  A extends IValueArithmetic<V>> extends AbstractMappingCollector<T, V, A>  {
	private static final boolean DEBUG_GRAPHMAPPER = false;

	protected final A arithmetic;
	protected MappingCollector(A arithmetic) {
		super(arithmetic);
		this.arithmetic = arithmetic;
	}

	protected static void debugFormat(String format, Object... args) {
		if (DEBUG_GRAPHMAPPER)
			PECore.debugLog(format, args);
	}

	protected static void debugPrintln(String s) {
		debugFormat(s);
	}

	protected final Map<T, Conversion> overwriteConversion = new HashMap<>();
	protected final Map<T, Set<Conversion>> conversionsFor = new HashMap<>();
	private final Map<T, Set<Conversion>> usedIn = new HashMap<>();
	protected final Map<T, V> fixValueBeforeInherit = new HashMap<>();
	protected final Map<T, V> fixValueAfterInherit = new HashMap<>();

	private Set<Conversion> getConversionsFor(T something) {
		return conversionsFor.computeIfAbsent(something, t -> new LinkedHashSet<>());
	}

	protected Set<Conversion> getUsesFor(T something) {
		return usedIn.computeIfAbsent(something, t -> new LinkedHashSet<>());
	}

	private void addConversionToIngredientUsages(Conversion conversion) {
		for (Map.Entry<T, Integer> ingredient : conversion.ingredientsWithAmount.entrySet()) {
			Set<Conversion> usesForIngredient = getUsesFor(ingredient.getKey());
			if (ingredient.getValue() == null)
				throw new IllegalArgumentException("ingredient amount value has to be != null");
			usesForIngredient.add(conversion);
		}
	}

	public void addConversion(int outnumber, T output, Map<T, Integer> ingredientsWithAmount, A arithmeticForConversion) {
		ingredientsWithAmount = Maps.newHashMap(ingredientsWithAmount);
		if (output == null || ingredientsWithAmount.containsKey(null)) {
			PECore.debugLog("Ignoring Recipe because of invalid ingredient or output: {} -> {}x{}", ingredientsWithAmount, outnumber, output);
			return;
		}
		if (outnumber <= 0)
			throw new IllegalArgumentException("outnumber has to be > 0!");
		//Add the Conversions to the conversionsFor and usedIn Maps:
		Conversion conversion = new Conversion(output, outnumber, ingredientsWithAmount, arithmeticForConversion, arithmetic.getZero());
		if (!getConversionsFor(output).add(conversion)) {
			return;
		}
		addConversionToIngredientUsages(conversion);
	}

	@Override
	public void setValueBefore(T something, V value) {
		if (something == null) return;
		if (fixValueBeforeInherit.containsKey(something))
			PECore.debugLog("Overwriting fixValueBeforeInherit for {}:{} to {}", something, fixValueBeforeInherit.get(something), value);
		fixValueBeforeInherit.put(something, value);
		fixValueAfterInherit.remove(something);
	}

	@Override
	public void setValueAfter(T something, V value) {
		if (something == null) return;
		if (fixValueAfterInherit.containsKey(something))
			PECore.debugLog("Overwriting fixValueAfterInherit for {}:{} to {}", something, fixValueAfterInherit.get(something), value);
		fixValueAfterInherit.put(something, value);
	}



	@Override
	public void setValueFromConversion(int outnumber, T something, Map<T, Integer> ingredientsWithAmount)
	{
		if (something == null || ingredientsWithAmount.containsKey(null)) {
			PECore.debugLog("Ignoring setValueFromConversion because of invalid ingredient or output: {} -> {}x{}", ingredientsWithAmount, outnumber, something);
			return;
		}
		if (outnumber <= 0)
			throw new IllegalArgumentException("outnumber has to be > 0!");
		Conversion conversion = new Conversion(something, outnumber, ingredientsWithAmount, this.arithmetic);
		if (overwriteConversion.containsKey(something)) {
			Conversion oldConversion = overwriteConversion.get(something);
			PECore.debugLog("Overwriting setValueFromConversion {} with {}", overwriteConversion.get(something), conversion);
			for (T ingredient: ingredientsWithAmount.keySet()) {
				getUsesFor(ingredient).remove(oldConversion);
			}
		}
		addConversionToIngredientUsages(conversion);
		overwriteConversion.put(something, conversion);
	}

	protected class Conversion {
		public final T output;

		public final int outnumber;
		public final V value;
		public final Map<T, Integer> ingredientsWithAmount;
		public final A arithmeticForConversion;

		Conversion(T output, int outnumber, Map<T, Integer> ingredientsWithAmount, A arithmeticForConversion) {
			this(output, outnumber, ingredientsWithAmount, arithmeticForConversion, arithmetic.getZero());
		}

		Conversion(T output, int outnumber, Map<T, Integer> ingredientsWithAmount, A arithmeticForConversion, V value) {
			this.output = output;
			this.outnumber = outnumber;
			this.ingredientsWithAmount = ingredientsWithAmount == null ? Collections.emptyMap() : ingredientsWithAmount;
			this.arithmeticForConversion = arithmeticForConversion;
			this.value = value;
		}

		@Override
		public String toString() {
			return "" + value + " + " + ingredientsToString() + " => " + outnumber + "*" + output;
		}

		private String ingredientsToString() {
			if (ingredientsWithAmount == null || ingredientsWithAmount.size() == 0) return "nothing";
			return ingredientsWithAmount.entrySet().stream()
					.map(e -> e.getValue() + "*" + e.getKey())
					.collect(Collectors.joining(" + "));
		}

		@Override
		public boolean equals(Object o) {
			if (!(o instanceof MappingCollector.Conversion))
				return false;
			Conversion other = (Conversion) o;

			return Objects.equals(output, other.output)
					&& Objects.equals(value, other.value)
					&& Objects.equals(ingredientsWithAmount, other.ingredientsWithAmount);
		}

		@Override
		public int hashCode() {
			return Objects.hash(output, value, ingredientsWithAmount);
		}
	}

}
