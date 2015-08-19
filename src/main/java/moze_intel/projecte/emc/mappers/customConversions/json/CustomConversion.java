package moze_intel.projecte.emc.mappers.customConversions.json;

import moze_intel.projecte.emc.NormalizedSimpleStack;
import moze_intel.projecte.emc.collector.DumpToFileCollector;

import com.google.common.collect.Maps;

import java.util.Map;

public class CustomConversion
{
	public int count = 1;
	public String output;
	public Map<String, Integer> ingredients;
	public transient boolean evalOD = false;

	public static CustomConversion getFor(int count, NormalizedSimpleStack output, Map<NormalizedSimpleStack, Integer> ingredients) {
		CustomConversion conversion = new CustomConversion();
		conversion.count = count;
		conversion.output = output.json();
		conversion.ingredients = Maps.newHashMap();
		for (Map.Entry<NormalizedSimpleStack, Integer> entry: ingredients.entrySet()) {
			conversion.ingredients.put(entry.getKey().json(), entry.getValue());
		}
		return conversion;
	}
}
