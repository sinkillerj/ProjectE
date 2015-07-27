package moze_intel.projecte.emc.mappers.customConversions.json;

import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;

import java.lang.reflect.Type;
import java.util.Map;

public class FixedValuesDeserializer implements JsonDeserializer<FixedValues>
{
	@Override
	public FixedValues deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
	{
		FixedValues fixed = new FixedValues();
		JsonObject o = json.getAsJsonObject();

		fixed.setValueBefore = parseSetValueMapFromObject(o, "before");
		fixed.setValueAfter = parseSetValueMapFromObject(o, "after");
		fixed.conversion = context.deserialize(o.getAsJsonArray("conversion"), CustomConversion[].class);
		return fixed;
	}

	Map<String, Integer> parseSetValueMapFromObject(JsonObject o, String key) {
		if (o.has(key)) {
			return parseSetValueMap(o.getAsJsonObject(key));
		}
		return Maps.newHashMap();
	}

	Map<String, Integer> parseSetValueMap(JsonObject o) {
		Map<String, Integer> out = Maps.newHashMap();
		for (Map.Entry<String, JsonElement> entry: o.entrySet()) {
			JsonPrimitive primitive = entry.getValue().getAsJsonPrimitive();
			if (primitive.isNumber()) {
				out.put(entry.getKey(),  primitive.getAsInt());
				continue;
			} else if (primitive.isString()) {
				if (primitive.getAsString().toLowerCase().equals("free")) {
					out.put(entry.getKey(), Integer.MIN_VALUE); //TODO Get Value for 'free' from arithmetic?
					continue;
				}
			}
			throw new JsonParseException("Could not parse " + o + " into 'free' or integer.");
		}
		return out;
	}
}
