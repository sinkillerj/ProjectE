package moze_intel.projecte.api.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.ParametersAreNonnullByDefault;
import moze_intel.projecte.api.nss.NSSTag;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import net.minecraft.MethodsReturnNonnullByDefault;

/**
 * Builder class to help create conversions.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ConversionBuilder<BUILDER extends ConversionBuilder<BUILDER>> implements ConversionBuilderNSSHelper<BUILDER> {

	private final Map<NormalizedSimpleStack, Integer> ingredients = new LinkedHashMap<>();
	private final NormalizedSimpleStack output;
	private final int outputAmount;
	private boolean propagateTags;

	ConversionBuilder(NormalizedSimpleStack output, int outputAmount) {
		this.output = output;
		this.outputAmount = outputAmount;
	}

	@Override
	public String toString() {
		return output + " " + outputAmount;
	}

	@SuppressWarnings("unchecked")
	private BUILDER getThis() {
		return (BUILDER) this;
	}

	/**
	 * Enables propagating tags if the output is a tag. This makes it so that the conversion will be applied to all elements in the tag as well, and not just to the tag.
	 */
	public BUILDER propagateTags() {
		if (propagateTags) {
			throw new RuntimeException("Propagate tags has already been set, remove unnecessary call.");
		} else if (output instanceof NSSTag && !((NSSTag) output).representsTag()) {
			throw new RuntimeException("Propagate tags can only be enabled for conversion outputs that are tags.");
		}
		propagateTags = true;
		return getThis();
	}

	@Override
	public BUILDER ingredient(NormalizedSimpleStack input, int amount) {
		if (ingredients.containsKey(input)) {
			throw new RuntimeException("Conversion already contains ingredient '" + input + "', merge identical ingredients.");
		} else if (amount == 0) {
			//Allow negatives, but not zero
			throw new RuntimeException("Conversion for empty ingredient '" + input + "' should be removed.");
		}
		ingredients.put(input, amount);
		return getThis();
	}

	/**
	 * Validates there are ingredients, and otherwise throws an exception.
	 */
	protected void validateIngredients() {
		if (ingredients.isEmpty()) {
			throw new RuntimeException("Conversion does not contain any ingredients.");
		}
	}

	/**
	 * Serializes this conversion into a json object.
	 */
	JsonObject serialize() {
		//Validate the ingredients again in case end never was called
		validateIngredients();
		JsonObject json = new JsonObject();
		if (propagateTags) {
			json.addProperty("propagateTags", true);
		}
		json.addProperty("output", output.json());
		if (outputAmount != 1) {
			json.addProperty("count", outputAmount);
		}
		if (ingredients.values().stream().allMatch(value -> value == 1)) {
			//If all the ingredients are size one, use the simpler array format
			JsonArray jsonIngredients = new JsonArray();
			for (NormalizedSimpleStack stack : ingredients.keySet()) {
				jsonIngredients.add(stack.json());
			}
			json.add("ingredients", jsonIngredients);
		} else {
			//Otherwise, use the more extensive format that specifies each ingredients' count
			JsonObject jsonIngredients = new JsonObject();
			for (Map.Entry<NormalizedSimpleStack, Integer> entry : ingredients.entrySet()) {
				jsonIngredients.addProperty(entry.getKey().json(), entry.getValue());
			}
			json.add("ingredients", jsonIngredients);
		}
		return json;
	}
}