package moze_intel.projecte.emc.collector;

import com.google.common.collect.ImmutableMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.mapper.arithmetic.IValueArithmetic;

public abstract class MappingCollector<T, V extends Comparable<V>, A extends IValueArithmetic<V>> extends AbstractMappingCollector<T, V, A> {

	private static final boolean DEBUG_GRAPHMAPPER = false;

	protected final A arithmetic;

	protected MappingCollector(A arithmetic) {
		super(arithmetic);
		this.arithmetic = arithmetic;
	}

	protected static void debugFormat(String format, Object... args) {
		if (DEBUG_GRAPHMAPPER) {
			PECore.debugLog(format, args);
		}
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
		for (T ingredient : conversion.ingredientsWithAmount.keySet()) {
			Set<Conversion> usesForIngredient = getUsesFor(ingredient);
			usesForIngredient.add(conversion);
		}
	}

	@Override
	public void addConversion(int outnumber, T output, Map<T, Integer> ingredientsWithAmount, A arithmeticForConversion) {
		if (output == null || ingredientsWithAmount.containsKey(null)) {
			PECore.debugLog("Ignoring Recipe because of invalid ingredient or output: {} -> {}x{}", ingredientsWithAmount, outnumber, output);
			return;
		}
		if (outnumber <= 0) {
			throw new IllegalArgumentException("outnumber has to be > 0!");
		}
		//Add the Conversions to the conversionsFor and usedIn Maps:
		Conversion conversion = new Conversion(output, outnumber, ingredientsWithAmount, arithmeticForConversion, arithmetic.getZero());
		if (!getConversionsFor(output).add(conversion)) {
			return;
		}
		addConversionToIngredientUsages(conversion);
	}

	@Override
	public void setValueBefore(T something, V value) {
		if (something == null) {
			return;
		}
		if (fixValueBeforeInherit.containsKey(something)) {
			PECore.debugLog("Overwriting fixValueBeforeInherit for {} from: {} to {}", something, fixValueBeforeInherit.get(something), value);
		}
		fixValueBeforeInherit.put(something, value);
		fixValueAfterInherit.remove(something);
	}

	@Override
	public void setValueAfter(T something, V value) {
		if (something == null) {
			return;
		}
		if (fixValueAfterInherit.containsKey(something)) {
			PECore.debugLog("Overwriting fixValueAfterInherit for {} from: {} to {}", something, fixValueAfterInherit.get(something), value);
		}
		fixValueAfterInherit.put(something, value);
	}

	@Override
	public void setValueFromConversion(int outnumber, T something, Map<T, Integer> ingredientsWithAmount) {
		if (something == null || ingredientsWithAmount.containsKey(null)) {
			PECore.debugLog("Ignoring setValueFromConversion because of invalid ingredient or output: {} -> {}x{}", ingredientsWithAmount, outnumber, something);
			return;
		}
		if (outnumber <= 0) {
			throw new IllegalArgumentException("outnumber has to be > 0!");
		}
		Conversion conversion = new Conversion(something, outnumber, ingredientsWithAmount, this.arithmetic);
		if (overwriteConversion.containsKey(something)) {
			Conversion oldConversion = overwriteConversion.get(something);
			PECore.debugLog("Overwriting setValueFromConversion {} with {}", overwriteConversion.get(something), conversion);
			for (T ingredient : ingredientsWithAmount.keySet()) {
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
			if (ingredientsWithAmount == null || ingredientsWithAmount.isEmpty()) {
				this.ingredientsWithAmount = Collections.emptyMap();
			} else {
				ImmutableMap.Builder<T, Integer> builder = ImmutableMap.builder();
				for (Map.Entry<T, Integer> ingredient : ingredientsWithAmount.entrySet()) {
					Integer amount = ingredient.getValue();
					if (amount == null) {
						throw new IllegalArgumentException("ingredient amount value has to be != null");
					} else if (amount != 0) {//Ingredients with an amount of 'zero' do not need to be handled.
						builder.put(ingredient.getKey(), amount);
					}
				}
				this.ingredientsWithAmount = builder.build();
			}
			this.arithmeticForConversion = arithmeticForConversion;
			this.value = value;
		}

		@Override
		public String toString() {
			return "" + value + " + " + ingredientsToString() + " => " + outnumber + "*" + output;
		}

		private String ingredientsToString() {
			if (ingredientsWithAmount.isEmpty()) {
				return "nothing";
			}
			return ingredientsWithAmount.entrySet().stream().map(e -> e.getValue() + "*" + e.getKey()).collect(Collectors.joining(" + "));
		}

		@Override
		public boolean equals(Object o) {
			return o instanceof MappingCollector.Conversion other && Objects.equals(output, other.output) && Objects.equals(value, other.value) &&
				   Objects.equals(ingredientsWithAmount, other.ingredientsWithAmount);
		}

		@Override
		public int hashCode() {
			return Objects.hash(output, value, ingredientsWithAmount);
		}
	}
}