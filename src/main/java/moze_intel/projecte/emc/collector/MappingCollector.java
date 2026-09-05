package moze_intel.projecte.emc.collector;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.mapper.arithmetic.IValueArithmetic;
import org.jetbrains.annotations.NotNull;

public abstract class MappingCollector<T, V extends Comparable<V>, A extends IValueArithmetic<V>> extends AbstractMappingCollector<T, V, A> {

	private static final boolean DEBUG_GRAPHMAPPER = false;

	private final Function<T, Set<Conversion>> CREATE_CONVERSIONS = t -> new LinkedHashSet<>();
	protected final A arithmetic;

	protected MappingCollector(A arithmetic) {
		super(arithmetic);
		this.arithmetic = arithmetic;
	}

	protected static boolean isDebugGraphmapper() {
		return DEBUG_GRAPHMAPPER;
	}

	protected static void debugFormat(String format, Object... args) {
		if (isDebugGraphmapper()) {
			PECore.debugLog(format, args);
		}
	}

	protected static void debugPrintln(String s) {
		debugFormat(s);
	}

	protected final Map<@NotNull T, @NotNull Conversion> overwriteConversion = new Object2ObjectOpenHashMap<>();
	protected final Map<@NotNull T, @NotNull Set<Conversion>> conversionsFor = new Object2ObjectOpenHashMap<>();
	protected final Map<@NotNull T, @NotNull Set<Conversion>> usedIn = new Object2ObjectOpenHashMap<>();
	protected final Map<@NotNull T, @NotNull V> fixValueBeforeInherit = new Object2ObjectOpenHashMap<>();
	protected final Map<@NotNull T, @NotNull V> fixValueAfterInherit = new Object2ObjectOpenHashMap<>();

	private Set<Conversion> getConversionsFor(@NotNull T something) {
		return conversionsFor.computeIfAbsent(something, CREATE_CONVERSIONS);
	}

	protected void removeUseFor(@NotNull T something, @NotNull Conversion conversion) {
		Set<Conversion> conversions = usedIn.get(something);
		if (conversions != null) {
			conversions.remove(conversion);
		}
	}

	protected Set<Conversion> getUsesFor(@NotNull T something) {
		return usedIn.computeIfAbsent(something, CREATE_CONVERSIONS);
	}

	private void addConversionToIngredientUsages(Conversion conversion) {
		for (T ingredient : conversion.ingredientsWithAmount.keySet()) {
			getUsesFor(ingredient).add(conversion);
		}
	}

	@Override
	public void addConversion(int outnumber, T output, Object2IntMap<T> ingredientsWithAmount, A arithmeticForConversion) {
		if (output == null || ingredientsWithAmount.containsKey(null)) {
			PECore.debugLog("Ignoring Recipe because of invalid ingredient or output: {} -> {}x{}", ingredientsWithAmount, outnumber, output);
			return;
		} else if (outnumber <= 0) {
			throw new IllegalArgumentException("outnumber has to be > 0!");
		}
		//Add the Conversions to the conversionsFor and usedIn Maps:
		Conversion conversion = new Conversion(output, outnumber, ingredientsWithAmount, arithmeticForConversion, arithmetic.getZero());
		if (getConversionsFor(output).add(conversion)) {
			addConversionToIngredientUsages(conversion);
		}
	}

	@Override
	public void setValueBefore(T something, V value) {
		if (something == null || value == null) {
			return;
		}
		V valueBeforeInherit = fixValueBeforeInherit.get(something);
		if (valueBeforeInherit != null) {
			PECore.debugLog("Overwriting fixValueBeforeInherit for {} from: {} to {}", something, valueBeforeInherit, value);
		}
		fixValueBeforeInherit.put(something, value);
		fixValueAfterInherit.remove(something);
	}

	@Override
	public void setValueAfter(T something, V value) {
		if (something == null || value == null) {
			return;
		}
		V valueAfterInherit = fixValueAfterInherit.get(something);
		if (valueAfterInherit != null) {
			PECore.debugLog("Overwriting fixValueAfterInherit for {} from: {} to {}", something, valueAfterInherit, value);
		}
		fixValueAfterInherit.put(something, value);
	}

	@Override
	public void setValueFromConversion(int outnumber, T something, Object2IntMap<T> ingredientsWithAmount) {
		if (something == null || ingredientsWithAmount.containsKey(null)) {
			PECore.debugLog("Ignoring setValueFromConversion because of invalid ingredient or output: {} -> {}x{}", ingredientsWithAmount, outnumber, something);
			return;
		}
		if (outnumber <= 0) {
			throw new IllegalArgumentException("outnumber has to be > 0!");
		}
		Conversion conversion = new Conversion(something, outnumber, ingredientsWithAmount, this.arithmetic);
		Conversion oldConversion = overwriteConversion.get(something);
		if (oldConversion != null) {
			PECore.debugLog("Overwriting setValueFromConversion {} with {}", oldConversion, conversion);
			for (T ingredient : oldConversion.ingredientsWithAmount.keySet()) {
				removeUseFor(ingredient, oldConversion);
			}
		}
		addConversionToIngredientUsages(conversion);
		overwriteConversion.put(something, conversion);
	}

	protected class Conversion {

		public final T output;

		public final int outnumber;
		public final V value;
		public final Object2IntMap<T> ingredientsWithAmount;
		public final A arithmeticForConversion;
		private final int hash;

		Conversion(T output, int outnumber, Object2IntMap<T> ingredientsWithAmount, A arithmeticForConversion) {
			this(output, outnumber, ingredientsWithAmount, arithmeticForConversion, arithmetic.getZero());
		}

		Conversion(T output, int outnumber, Object2IntMap<T> ingredientsWithAmount, A arithmeticForConversion, V value) {
			this.output = output;
			this.outnumber = outnumber;
			if (ingredientsWithAmount == null || ingredientsWithAmount.isEmpty()) {
				this.ingredientsWithAmount = Object2IntMaps.emptyMap();
			} else {
				Object2IntMap<T> filteredMap = new Object2IntOpenHashMap<>(ingredientsWithAmount.size());
				for (Iterator<Object2IntMap.Entry<T>> iterator = Object2IntMaps.fastIterator(ingredientsWithAmount); iterator.hasNext(); ) {
					Object2IntMap.Entry<T> ingredient = iterator.next();
					int amount = ingredient.getIntValue();
					if (amount != 0) {//Ingredients with an amount of 'zero' do not need to be handled.
						filteredMap.put(ingredient.getKey(), amount);
					}
				}
				this.ingredientsWithAmount = filteredMap;
			}
			this.arithmeticForConversion = arithmeticForConversion;
			this.value = value;
			this.hash = Objects.hash(this.output, this.value, this.ingredientsWithAmount);
		}

		@Override
		public String toString() {
			return value + " + " + ingredientsToString() + " => " + outnumber + "*" + output;
		}

		private String ingredientsToString() {
			if (ingredientsWithAmount.isEmpty()) {
				return "nothing";
			}
			StringBuilder ingredients = new StringBuilder();
			boolean addSeparator = false;
			for (Iterator<Object2IntMap.Entry<T>> iterator = Object2IntMaps.fastIterator(ingredientsWithAmount); iterator.hasNext(); ) {
				Object2IntMap.Entry<T> ingredient = iterator.next();
				if (addSeparator) {
					ingredients.append(" + ");
				} else {
					addSeparator = true;
				}
				ingredients.append(ingredient.getIntValue())
						.append('*')
						.append(ingredient.getKey());
			}
			return ingredients.toString();
		}

		@Override
		public boolean equals(Object o) {
			return o instanceof MappingCollector<?, ?, ?>.Conversion other && Objects.equals(output, other.output) && Objects.equals(value, other.value) &&
				   Objects.equals(ingredientsWithAmount, other.ingredientsWithAmount);
		}

		@Override
		public int hashCode() {
			return this.hash;
		}
	}
}