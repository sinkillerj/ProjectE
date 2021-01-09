package moze_intel.projecte.emc.mappers.customConversions.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import moze_intel.projecte.emc.mappers.customConversions.CustomConversionMapper;

public class CustomConversionDeserializer implements JsonDeserializer<CustomConversion> {

	@Override
	public CustomConversion deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		CustomConversion out = new CustomConversion();
		JsonObject o = json.getAsJsonObject();
		boolean foundOutput = false, foundIngredients = false;
		for (Map.Entry<String, JsonElement> entry : o.entrySet()) {
			JsonElement element = entry.getValue();
			if ("count".equalsIgnoreCase(entry.getKey())) {
				out.count = element.getAsInt();
			} else if ("output".equals(entry.getKey())) {
				if (foundOutput) {
					throw new JsonParseException("Multiple values for output field");
				}
				foundOutput = true;
				out.output = context.deserialize(new JsonPrimitive(element.getAsString()), NormalizedSimpleStack.class);
			} else if ("ingredients".equals(entry.getKey())) {
				if (foundIngredients) {
					throw new JsonParseException("Multiple values for ingredient field");
				}
				foundIngredients = true;
				if (element.isJsonArray()) {
					Map<NormalizedSimpleStack, Integer> outMap = new HashMap<>();
					for (JsonElement e : element.getAsJsonArray()) {
						NormalizedSimpleStack v = context.deserialize(new JsonPrimitive(e.getAsString()), NormalizedSimpleStack.class);
						outMap.merge(v, 1, Integer::sum);
					}
					out.ingredients = outMap;
				} else if (element.isJsonObject()) {
					out.ingredients = CustomConversionMapper.GSON.fromJson(element, new TypeToken<Map<NormalizedSimpleStack, Integer>>() {}.getType());
				} else {
					throw new JsonParseException("Could not parse ingredients!");
				}
			} else if (entry.getKey().equalsIgnoreCase("propagateTags")) {
				out.propagateTags = element.getAsBoolean();
			} else {
				throw new JsonParseException(String.format("Unknown Key: %s in Conversion with value %s", entry.getKey(), element));
			}
		}
		if (!foundOutput) {
			throw new JsonParseException("No output declared");
		} else if (!foundIngredients) {
			throw new JsonParseException("No ingredients declared");
		}
		//TODO - 1.16: Validate that the count is at least 1?
		return out;
	}
}