package moze_intel.projecte.api.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import mcp.MethodsReturnNonnullByDefault;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import net.minecraft.util.ResourceLocation;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CustomConversionBuilder implements CustomConversionBuilderNSSHelper {

	private static final long FREE_ARITHMETIC_VALUE = Long.MIN_VALUE;
	private final Map<String, ConversionGroupBuilder> groups = new LinkedHashMap<>();
	private final Map<NormalizedSimpleStack, Long> fixedValueBefore = new LinkedHashMap<>();
	private final Map<NormalizedSimpleStack, Long> fixedValueAfter = new LinkedHashMap<>();
	private final List<FixedValueConversionBuilder> fixedValueConversions = new ArrayList<>();
	private final ResourceLocation id;
	private boolean replace;
	@Nullable
	private String comment;

	CustomConversionBuilder(ResourceLocation id) {
		this.id = id;
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
		if (groups.containsKey(groupName)) {
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
		return fixedValue(stack, FREE_ARITHMETIC_VALUE, fixedValueBefore, "before");
	}

	@Override
	public CustomConversionBuilder after(NormalizedSimpleStack stack, long emc) {
		return fixedValue(stack, emc, fixedValueAfter, "after");
	}

	@Override
	public CustomConversionBuilder after(NormalizedSimpleStack stack) {
		return fixedValue(stack, FREE_ARITHMETIC_VALUE, fixedValueAfter, "after");
	}

	/**
	 * Adds a fixed value to the proper map after validating it as valid.
	 */
	private CustomConversionBuilder fixedValue(NormalizedSimpleStack stack, long emc, Map<NormalizedSimpleStack, Long> fixedValues, String type) {
		Objects.requireNonNull(stack, "Normalized Simple Stack cannot be null.");
		if (emc < 1) {
			throw new IllegalArgumentException("EMC value must be at least one.");
		} else if (fixedValues.containsKey(stack)) {
			throw new RuntimeException("Fixed value " + type + " already set for '" + stack + "'.");
		}
		fixedValues.put(stack, emc);
		return this;
	}

	@Override
	public FixedValueConversionBuilder conversion(NormalizedSimpleStack output, int amount) {
		if (amount < 1) {
			throw new IllegalArgumentException("Output amount for fixed value conversions must be at least one.");
		}
		FixedValueConversionBuilder builder = new FixedValueConversionBuilder(output, amount);
		fixedValueConversions.add(builder);
		return builder;
	}

	/**
	 * Serializes this custom conversion file into the json object representing its contents.
	 */
	JsonObject serialize() {
		JsonObject json = new JsonObject();
		if (comment != null) {
			json.addProperty("comment", comment);
		}
		if (replace) {
			json.addProperty("replace", true);
		}
		if (!groups.isEmpty()) {
			JsonObject jsonGroups = new JsonObject();
			for (Map.Entry<String, ConversionGroupBuilder> entry : groups.entrySet()) {
				String groupName = entry.getKey();
				ConversionGroupBuilder group = entry.getValue();
				JsonObject groupJson = group.serialize();
				validateNonEmpty(groupJson, group.hasComment(), "Group", groupName);
				jsonGroups.add(groupName, groupJson);
			}
			json.add("groups", jsonGroups);
		}
		if (!fixedValueBefore.isEmpty() || !fixedValueAfter.isEmpty() || !fixedValueConversions.isEmpty()) {
			JsonObject fixedValues = new JsonObject();
			if (!fixedValueBefore.isEmpty()) {
				fixedValues.add("before", serializeFixedValues(fixedValueBefore));
			}
			if (!fixedValueAfter.isEmpty()) {
				fixedValues.add("after", serializeFixedValues(fixedValueAfter));
			}
			if (!fixedValueConversions.isEmpty()) {
				fixedValues.add("conversion", serializeConversions(fixedValueConversions));
			}
			json.add("values", fixedValues);
		}
		validateNonEmpty(json, comment != null, "Custom conversion", id.toString());
		return json;
	}

	/**
	 * Validates that a json object is not empty, or consists only of a comment.
	 */
	private static void validateNonEmpty(JsonObject json, boolean hasComment, String type, String name) {
		int elements = json.size();
		if (elements == 0) {
			throw new RuntimeException(type + " '" + name + "' is empty and should be removed.");
		} else if (elements == 1 && hasComment) {
			throw new RuntimeException(type + " '" + name + "' consists only of a comment and should be removed.");
		}
	}

	/**
	 * Serializes a map of fixed values into a json object.
	 */
	private static JsonObject serializeFixedValues(Map<NormalizedSimpleStack, Long> fixedValues) {
		JsonObject json = new JsonObject();
		for (Map.Entry<NormalizedSimpleStack, Long> entry : fixedValues.entrySet()) {
			String key = entry.getKey().json();
			long emc = entry.getValue();
			if (emc == FREE_ARITHMETIC_VALUE) {
				json.addProperty(key, "free");
			} else {
				json.addProperty(key, emc);
			}
		}
		return json;
	}

	/**
	 * Serializing a list of conversions into a json array.
	 */
	static JsonArray serializeConversions(List<? extends ConversionBuilder<?>> conversions) {
		Set<JsonObject> addedConversions = new HashSet<>();
		JsonArray jsonConversions = new JsonArray();
		for (ConversionBuilder<?> conversion : conversions) {
			JsonObject jsonConversion = conversion.serialize();
			//Very simple check to protect against copy paste duplicates
			// This won't find things where the inputs are in different orders or where an entry isn't
			// actually needed, but should help against copy paste issues.
			if (addedConversions.add(jsonConversion)) {
				jsonConversions.add(jsonConversion);
			} else {
				throw new RuntimeException("Duplicate conversion: " + conversion + ". This is likely a copy paste error and should be removed.");
			}
		}
		return jsonConversions;
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

	public class FixedValueConversionBuilder extends ConversionBuilder<FixedValueConversionBuilder> {

		private FixedValueConversionBuilder(NormalizedSimpleStack output, int count) {
			super(output, count);
		}

		/**
		 * Ends this fixed value conversion builder and returns to the {@link CustomConversionBuilder}.
		 *
		 * @apiNote While it is not required to call this method if it is the last line of your builder calls. It is recommended to do so to get better line number errors
		 * if you accidentally forgot to include any ingredients.
		 */
		public CustomConversionBuilder end() {
			validateIngredients();
			return CustomConversionBuilder.this;
		}
	}
}