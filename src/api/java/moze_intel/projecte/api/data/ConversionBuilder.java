package moze_intel.projecte.api.data;

import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.ParametersAreNonnullByDefault;
import moze_intel.projecte.api.conversion.CustomConversion;
import moze_intel.projecte.api.nss.NSSTag;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import net.minecraft.MethodsReturnNonnullByDefault;

/**
 * Builder class to help create conversions.
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ConversionBuilder<PARENT> implements ConversionBuilderNSSHelper<PARENT> {

	private final PARENT parent;

	private final Map<NormalizedSimpleStack, Integer> ingredients = new LinkedHashMap<>();
	private final NormalizedSimpleStack output;
	private final int outputAmount;
	private boolean propagateTags;

	ConversionBuilder(PARENT parent, NormalizedSimpleStack output, int outputAmount) {
		this.parent = parent;
		this.output = output;
		this.outputAmount = outputAmount;
	}

	CustomConversion build() {
		return new CustomConversion(outputAmount, output, ingredients, propagateTags);
	}

	@Override
	public String toString() {
		return output + " " + outputAmount;
	}

	/**
	 * Ends this group conversion builder and returns to the parent.
	 *
	 * @apiNote While it is not required to call this method if it is the last line of your builder calls. It is recommended to do so to get better line number
	 * errors if you accidentally forgot to include any ingredients.
	 */
	public PARENT end() {
		if (ingredients.isEmpty()) {
			throw new RuntimeException("Conversion does not contain any ingredients.");
		}
		return parent;
	}

	/**
	 * Enables propagating tags if the output is a tag. This makes it so that the conversion will be applied to all elements in the tag as well, and not just to the tag.
	 */
	public ConversionBuilder<PARENT> propagateTags() {
		if (propagateTags) {
			throw new RuntimeException("Propagate tags has already been set, remove unnecessary call.");
		} else if (output instanceof NSSTag nssTag && !nssTag.representsTag()) {
			throw new RuntimeException("Propagate tags can only be enabled for conversion outputs that are tags.");
		}
		propagateTags = true;
		return this;
	}

	@Override
	public ConversionBuilder<PARENT> ingredient(NormalizedSimpleStack input, int amount) {
		if (ingredients.containsKey(input)) {
			throw new RuntimeException("Conversion already contains ingredient '" + input + "', merge identical ingredients.");
		} else if (amount == 0) {
			//Allow negatives, but not zero
			throw new RuntimeException("Conversion for empty ingredient '" + input + "' should be removed.");
		}
		ingredients.put(input, amount);
		return this;
	}
}