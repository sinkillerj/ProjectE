package moze_intel.projecte.emc.mappers.customConversions.json;

import com.google.common.collect.Maps;
import moze_intel.projecte.emc.json.NormalizedSimpleStack;

import java.util.HashMap;
import java.util.Map;

public class CustomConversion
{
	public int count = 1;
	public NormalizedSimpleStack output;
	public Map<NormalizedSimpleStack, Integer> ingredients;
	public transient boolean evalOD = false;

	public static CustomConversion getFor(int count, NormalizedSimpleStack output, Map<NormalizedSimpleStack, Integer> ingredients) {
		CustomConversion conversion = new CustomConversion();
		conversion.count = count;
		conversion.output = output;
		conversion.ingredients = new HashMap<>();
		for (Map.Entry<NormalizedSimpleStack, Integer> entry: ingredients.entrySet()) {
			conversion.ingredients.put(entry.getKey(), entry.getValue());
		}
		return conversion;
	}

	@Override
	public String toString() {
		return "{" + count + " * " + output + " = " + ingredients.toString() + "}";
	}
}
