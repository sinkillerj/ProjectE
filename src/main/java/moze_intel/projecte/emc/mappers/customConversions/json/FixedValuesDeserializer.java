package moze_intel.projecte.emc.mappers.customConversions.json;

import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;
import moze_intel.projecte.emc.json.NormalizedSimpleStack;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FixedValuesDeserializer implements JsonDeserializer<FixedValues>
{
	@Override
	public FixedValues deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
	{
		FixedValues fixed = new FixedValues();
		JsonObject o = json.getAsJsonObject();
		for(Map.Entry<String, JsonElement> entry: o.entrySet()) {
			if (entry.getKey().equals("before")) {
				fixed.setValueBefore = parseSetValueMap(entry.getValue().getAsJsonObject(), context);
			} else if (entry.getKey().equals("after")) {
				fixed.setValueAfter = parseSetValueMap(entry.getValue().getAsJsonObject(), context);
			} else if (entry.getKey().equals("conversion")) {
				fixed.conversion = context.deserialize(entry.getValue().getAsJsonArray(), new TypeToken<List<CustomConversion>>(){}.getType());
			} else {
				throw new JsonParseException(String.format("Can not parse \"%s\":%s in fixedValues", entry.getKey(), entry.getValue()));
			}
		}
		return fixed;
	}

	private Map<NormalizedSimpleStack, Long> parseSetValueMap(JsonObject o, JsonDeserializationContext context) {
		Map<NormalizedSimpleStack, Long> out = new HashMap<>();
		for (Map.Entry<String, JsonElement> entry: o.entrySet()) {
			JsonPrimitive primitive = entry.getValue().getAsJsonPrimitive();
			if (primitive.isNumber()) {
				out.put(context.deserialize(new JsonPrimitive(entry.getKey()), NormalizedSimpleStack.class),  primitive.getAsLong());
				continue;
			} else if (primitive.isString()) {
				if (primitive.getAsString().toLowerCase().equals("free")) {
					out.put(context.deserialize(new JsonPrimitive(entry.getKey()), NormalizedSimpleStack.class), Long.MIN_VALUE); //TODO Get Value for 'free' from arithmetic?
					continue;
				}
			}
			throw new JsonParseException("Could not parse " + o + " into 'free' or integer.");
		}
		return out;
	}
}
