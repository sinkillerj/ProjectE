package moze_intel.projecte.api.data;

import it.unimi.dsi.fastutil.objects.Object2LongLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongSortedMap;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.ParametersAreNonnullByDefault;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.conversion.CustomConversionFile;
import moze_intel.projecte.api.conversion.FixedValues;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;

@ParametersAreNonnullByDefault
public class CustomConversionBuilder extends BaseFileBuilder<CustomConversionBuilder> implements CustomConversionBuilderNSSHelper {

	private final Map<String, ConversionGroupBuilder> groups = new LinkedHashMap<>();
	private final Object2LongSortedMap<NormalizedSimpleStack> fixedValueBefore = new Object2LongLinkedOpenHashMap<>();
	private final Object2LongSortedMap<NormalizedSimpleStack> fixedValueAfter = new Object2LongLinkedOpenHashMap<>();
	private final List<ConversionBuilder<?>> fixedValueConversions = new ArrayList<>();
	private boolean replace;

	CustomConversionBuilder() {
		super("Custom Conversion");
	}

	CustomConversionFile build() {
		return new CustomConversionFile(replace, comment,
				groups.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().build(), (a, b) -> {
					throw new IllegalStateException("No duplicate keys");
				}, LinkedHashMap::new)),
				new FixedValues(fixedValueBefore, fixedValueAfter, fixedValueConversions.stream().map(ConversionBuilder::build).toList())
		);
	}

	/**
	 * Enables replace mode to make this custom conversion file overwrite other files in the same place instead of merge with them.
	 */
	public CustomConversionBuilder replace() {
		if (replace) {
			throw new RuntimeException("Replace has already been set, remove unnecessary call.");
		}
		replace = true;
		return this;
	}

	/**
	 * Creates a {@link ConversionGroupBuilder} with the given group name.
	 *
	 * @param groupName Name of the group.
	 */
	public ConversionGroupBuilder group(String groupName) {
		Objects.requireNonNull(groupName, "Group name cannot be null.");
		if (groupName.isEmpty()) {
			throw new RuntimeException("Group with name cannot be empty.");
		} else if (groups.containsKey(groupName)) {
			throw new RuntimeException("Group with name '" + groupName + "' already exists.");
		}
		ConversionGroupBuilder builder = new ConversionGroupBuilder(this);
		groups.put(groupName, builder);
		return builder;
	}

	@Override
	public CustomConversionBuilder before(NormalizedSimpleStack stack, long emc) {
		return fixedValue(stack, emc, fixedValueBefore, "before");
	}

	@Override
	public CustomConversionBuilder before(NormalizedSimpleStack stack) {
		return fixedValue(stack, ProjectEAPI.FREE_ARITHMETIC_VALUE, fixedValueBefore, "before");
	}

	@Override
	public CustomConversionBuilder after(NormalizedSimpleStack stack, long emc) {
		return fixedValue(stack, emc, fixedValueAfter, "after");
	}

	@Override
	public CustomConversionBuilder after(NormalizedSimpleStack stack) {
		return fixedValue(stack, ProjectEAPI.FREE_ARITHMETIC_VALUE, fixedValueAfter, "after");
	}

	/**
	 * Adds a fixed value to the proper map after validating it as valid.
	 */
	private CustomConversionBuilder fixedValue(NormalizedSimpleStack stack, long emc, Object2LongMap<NormalizedSimpleStack> fixedValues, String type) {
		Objects.requireNonNull(stack, "Normalized Simple Stack cannot be null.");
		if (emc < 1 && emc != ProjectEAPI.FREE_ARITHMETIC_VALUE) {
			throw new IllegalArgumentException("EMC value must be at least one.");
		} else if (fixedValues.containsKey(stack)) {
			throw new RuntimeException("Fixed value " + type + " already set for '" + stack + "'.");
		}
		fixedValues.put(stack, emc);
		return this;
	}

	@Override
	public ConversionBuilder<CustomConversionBuilder> conversion(NormalizedSimpleStack output, int amount) {
		if (amount < 1) {
			throw new IllegalArgumentException("Output amount for fixed value conversions must be at least one.");
		}
		ConversionBuilder<CustomConversionBuilder> builder = new ConversionBuilder<>(this, output, amount);
		fixedValueConversions.add(builder);
		return builder;
	}
}
