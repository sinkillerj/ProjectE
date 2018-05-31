package moze_intel.projecte.emc.mappers.customConversions.json;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;
import moze_intel.projecte.emc.json.NormalizedSimpleStack;
import moze_intel.projecte.emc.mappers.customConversions.CustomConversionMapper;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class CustomConversionDeserializer implements JsonDeserializer<CustomConversion>
{
	@Override
	public CustomConversion deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
	{
		CustomConversion out = new CustomConversion();
		JsonObject o = json.getAsJsonObject();
		boolean foundOutput = false, foundIngredients = false;
		for (Map.Entry<String, JsonElement> entry:o.entrySet()) {
			JsonElement element = entry.getValue();
			if (isInList(entry.getKey(), "count", "c")) {
				out.count = element.getAsInt();
			}
			else if (isInList(entry.getKey(),  "output", "out", "o")) {
				if (foundOutput) {
					throw new JsonParseException("Multiple values for output field");
				}
				foundOutput = true;
				out.output = context.deserialize(new JsonPrimitive(element.getAsString()), NormalizedSimpleStack.class);
			} else if (isInList(entry.getKey(), "ingredients", "ingr", "i")) {
				if (foundIngredients) {
					throw new JsonParseException("Multiple values for ingredient field");
				}
				foundIngredients = true;
				if (element.isJsonArray()) {
					Map<NormalizedSimpleStack, Integer> outMap = new HashMap<>();
					JsonArray array = element.getAsJsonArray();
					for (JsonElement e: array) {
						NormalizedSimpleStack v = context.deserialize(new JsonPrimitive(e.getAsString()), NormalizedSimpleStack.class);
						int count = 0;
						if (outMap.containsKey(v)) {
							count = outMap.get(v);
						}
						count += 1;
						outMap.put(v, count);
					}
					out.ingredients = outMap;
				} else if (element.isJsonObject()) {
					out.ingredients = CustomConversionMapper.GSON.fromJson(element, new TypeToken<Map<NormalizedSimpleStack, Integer>>(){}.getType());
				} else {
					throw new JsonParseException("Could not parse ingredients!");
				}
			} else if (entry.getKey().equalsIgnoreCase("evalOD")) {
				out.evalOD = element.getAsBoolean();
			} else {
				throw new JsonParseException(String.format("Unknown Key: %s in Conversion with value %s", entry.getKey(), element));
			}
		}
		return out;
	}

	private static boolean isInList(String s, String ... names) {
		for (String n: names) {
			if (n.equalsIgnoreCase(s)) return true;
		}
		return false;
	}
}
