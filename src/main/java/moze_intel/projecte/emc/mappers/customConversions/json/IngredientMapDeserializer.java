package moze_intel.projecte.emc.mappers.customConversions.json;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Map;

public class IngredientMapDeserializer implements JsonDeserializer<Map<String, Integer>>
{
	@Override
	public Map<String, Integer> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
	{
		if (json.isJsonArray()) {
			Map<String, Integer> out = Maps.newHashMap();
			JsonArray array = json.getAsJsonArray();
			for (JsonElement e: array) {
				String v = e.getAsString();
				int count = 0;
				if (out.containsKey(v)) {
					count = out.get(v);
				}
				count += 1;
				out.put(v, count);
			}
			return out;
		} else if (json.isJsonObject()) {
			return new Gson().fromJson(json, new TypeToken<Map<String, Integer>>(){}.getType());
		}
		throw new JsonParseException("Expected Array or Object");
	}
}
