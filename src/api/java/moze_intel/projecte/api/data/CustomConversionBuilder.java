package moze_intel.projecte.api.data;

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
import net.minecraft.MethodsReturnNonnullByDefault;
import org.jetbrains.annotations.Nullable;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CustomConversionBuilder implements CustomConversionBuilderNSSHelper {

	private final Map<String, ConversionGroupBuilder> groups = new LinkedHashMap<>();
	private final Map<NormalizedSimpleStack, Long> fixedValueBefore = new LinkedHashMap<>();
	private final Map<NormalizedSimpleStack, Long> fixedValueAfter = new LinkedHashMap<>();
	private final List<ConversionBuilder<?>> fixedValueConversions = new ArrayList<>();
	private boolean replace;
	@Nullable
	private String comment;

	CustomConversionBuilder() {
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
	 * Optionally adds a given comment to the custom conversion file. Useful for describing what the file is used for to people looking at the json file.
	 *
	 * @param comment Comment to add.
	 */
	public CustomConversionBuilder comment(String comment) {
		validateComment(this.comment, comment, "Custom Conversion");
		this.comment = comment;
		return this;
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
	private CustomConversionBuilder fixedValue(NormalizedSimpleStack stack, long emc, Map<NormalizedSimpleStack, Long> fixedValues, String type) {
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

	/**
	 * Validates only one comment per section is set and that the comment being set is not null.
	 */
	static void validateComment(@Nullable String currentComment, String comment, String location) {
		Objects.requireNonNull(comment, "Comment defaults to null, remove unnecessary call.");
		if (currentComment != null) {
			throw new RuntimeException(location + " Builder already has a comment declared.");
		}
	}
}