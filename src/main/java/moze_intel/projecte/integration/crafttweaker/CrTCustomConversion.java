package moze_intel.projecte.integration.crafttweaker;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker_annotations.annotations.Document;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import moze_intel.projecte.api.nss.NSSTag;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import moze_intel.projecte.integration.crafttweaker.actions.CustomConversionAction;
import org.openzen.zencode.java.ZenCodeType;

@ZenRegister
@Document("mods/ProjectE/CustomConversion")
@ZenCodeType.Name("mods.projecte.CustomConversion")
public class CrTCustomConversion {

	private CrTCustomConversion() {
	}

	/**
	 * Adds a conversion to be mapped from the given set of inputs into the given output, automatically propagating to elements for tags.
	 *
	 * @param stack       {@link NormalizedSimpleStack} representing the conversion's output.
	 * @param amount      Amount the conversion outputs.
	 * @param ingredients Map representing all inputs to the conversion.
	 */
	@ZenCodeType.Method
	public static void addConversion(NormalizedSimpleStack stack, int amount, Map<NormalizedSimpleStack, Integer> ingredients) {
		addConversion(stack, amount, stack instanceof NSSTag, ingredients);
	}

	/**
	 * Adds a conversion to be mapped from the given set of inputs into the given output
	 *
	 * @param stack         {@link NormalizedSimpleStack} representing the conversion's output.
	 * @param amount        Amount the conversion outputs.
	 * @param propagateTags Whether the conversion should be propagated to elements if the output is a tag (stack but be a tag).
	 * @param ingredients   Map representing all inputs to the conversion.
	 */
	@ZenCodeType.Method
	public static void addConversion(NormalizedSimpleStack stack, int amount, boolean propagateTags, Map<NormalizedSimpleStack, Integer> ingredients) {
		if (propagateTags && !(stack instanceof NSSTag)) {
			throw new IllegalArgumentException("Propagate Tags should always be false if the output is not a tag.");
		}
		CraftTweakerAPI.apply(new CustomConversionAction(stack, amount, propagateTags, false, ingredients));
	}

	/**
	 * Adds a conversion to be mapped from the given set of inputs into the given output, automatically propagating to elements for tags.
	 *
	 * @param stack       {@link NormalizedSimpleStack} representing the conversion's output.
	 * @param amount      Amount the conversion outputs.
	 * @param ingredients Map representing all inputs to the conversion.
	 */
	@ZenCodeType.Method
	public static void addConversion(NormalizedSimpleStack stack, int amount, NormalizedSimpleStack... ingredients) {
		addConversion(stack, amount, stack instanceof NSSTag, ingredients);
	}

	/**
	 * Adds a conversion to be mapped from the given set of inputs into the given output
	 *
	 * @param stack         {@link NormalizedSimpleStack} representing the conversion's output.
	 * @param amount        Amount the conversion outputs.
	 * @param propagateTags Whether the conversion should be propagated to elements if the output is a tag (stack but be a tag).
	 * @param ingredients   Map representing all inputs to the conversion.
	 */
	@ZenCodeType.Method
	public static void addConversion(NormalizedSimpleStack stack, int amount, boolean propagateTags, NormalizedSimpleStack... ingredients) {
		if (ingredients.length == 0) {
			throw new IllegalArgumentException("No ingredients specified for conversion.");
		}
		addConversion(stack, amount, propagateTags, Arrays.stream(ingredients).collect(Collectors.toMap(ingredient -> ingredient, ingredient -> 1, Integer::sum)));
	}

	/**
	 * Adds a conversion to be mapped from the given set of inputs into the given output setting the output EMC to the value of this conversion instead of taking other
	 * conversions into account, automatically propagating to elements for tags.
	 *
	 * @param stack       {@link NormalizedSimpleStack} representing the conversion's output.
	 * @param amount      Amount the conversion outputs.
	 * @param ingredients Map representing all inputs to the conversion.
	 */
	@ZenCodeType.Method
	public static void setConversion(NormalizedSimpleStack stack, int amount, Map<NormalizedSimpleStack, Integer> ingredients) {
		setConversion(stack, amount, stack instanceof NSSTag, ingredients);
	}

	/**
	 * Adds a conversion to be mapped from the given set of inputs into the given output setting the output EMC to the value of this conversion instead of taking other
	 * conversions into account.
	 *
	 * @param stack         {@link NormalizedSimpleStack} representing the conversion's output.
	 * @param amount        Amount the conversion outputs.
	 * @param propagateTags Whether the conversion should be propagated to elements if the output is a tag (stack but be a tag).
	 * @param ingredients   Map representing all inputs to the conversion.
	 */
	@ZenCodeType.Method
	public static void setConversion(NormalizedSimpleStack stack, int amount, boolean propagateTags, Map<NormalizedSimpleStack, Integer> ingredients) {
		if (propagateTags && !(stack instanceof NSSTag)) {
			throw new IllegalArgumentException("Propagate Tags should always be false if the output is not a tag.");
		}
		CraftTweakerAPI.apply(new CustomConversionAction(stack, amount, propagateTags, true, ingredients));
	}

	/**
	 * Adds a conversion to be mapped from the given set of inputs into the given output setting the output EMC to the value of this conversion instead of taking other
	 * conversions into account, automatically propagating to elements for tags.
	 *
	 * @param stack       {@link NormalizedSimpleStack} representing the conversion's output.
	 * @param amount      Amount the conversion outputs.
	 * @param ingredients Map representing all inputs to the conversion.
	 */
	@ZenCodeType.Method
	public static void setConversion(NormalizedSimpleStack stack, int amount, NormalizedSimpleStack... ingredients) {
		setConversion(stack, amount, stack instanceof NSSTag, ingredients);
	}

	/**
	 * Adds a conversion to be mapped from the given set of inputs into the given output setting the output EMC to the value of this conversion instead of taking other
	 * conversions into account.
	 *
	 * @param stack         {@link NormalizedSimpleStack} representing the conversion's output.
	 * @param amount        Amount the conversion outputs.
	 * @param propagateTags Whether the conversion should be propagated to elements if the output is a tag (stack but be a tag).
	 * @param ingredients   Map representing all inputs to the conversion.
	 */
	@ZenCodeType.Method
	public static void setConversion(NormalizedSimpleStack stack, int amount, boolean propagateTags, NormalizedSimpleStack... ingredients) {
		if (ingredients.length == 0) {
			throw new IllegalArgumentException("No ingredients specified for conversion.");
		}
		setConversion(stack, amount, propagateTags, Arrays.stream(ingredients).collect(Collectors.toMap(ingredient -> ingredient, ingredient -> 1, Integer::sum)));
	}
}