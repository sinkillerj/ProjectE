package moze_intel.projecte.emc.mappers.customConversions.json;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Map;

public class CustomConversionDeserializer implements JsonDeserializer<CustomConversion>
{
	@Override
	public CustomConversion deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
	{
		CustomConversion out = new CustomConversion();
		JsonObject o = json.getAsJsonObject();
		JsonElement element = getElementWithMultipleNames(o, "count", "c");
		if (element != null) {
			out.count = element.getAsInt();
		}

		element = getElementWithMultipleNames(o, "output", "out", "o");
		if (element == null) {
			throw new JsonParseException("Missing mandatory key: 'output', 'out' or 'o'");
		}
		out.output = element.getAsString();

		element = getElementWithMultipleNames(o, "ingredients", "ingr", "i");
		if (element == null) {
			throw new JsonParseException("Missing mandatory key: 'ingredients', 'ingr' or 'i'");
		} else {
			if (element.isJsonArray()) {
				Map<String, Integer> outMap = Maps.newHashMap();
				JsonArray array = element.getAsJsonArray();
				for (JsonElement e: array) {
					String v = e.getAsString();
					int count = 0;
					if (outMap.containsKey(v)) {
						count = outMap.get(v);
					}
					count += 1;
					outMap.put(v, count);
				}
				out.ingredients = outMap;
			} else if (element.isJsonObject()) {
				out.ingredients = new Gson().fromJson(element, new TypeToken<Map<String, Integer>>(){}.getType());
			} else {
				throw new JsonParseException("Could not parse ingredients!");
			}
		}

		return out;
	}

	private static JsonElement getElementWithMultipleNames(JsonObject o, String ... names) throws JsonParseException {
		String found = null;
		JsonElement out = null;
		for (String n: names) {
			if (o.has(n)) {
				if (found != null) throw new JsonParseException("Field '" + found + "' would be overwritten by '" + n + "'");
				found = n;
				out = o.get(found);
			}
		}
		return out;
	}
}
