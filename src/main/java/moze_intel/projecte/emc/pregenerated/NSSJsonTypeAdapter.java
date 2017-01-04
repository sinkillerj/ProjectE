package moze_intel.projecte.emc.pregenerated;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import moze_intel.projecte.emc.json.NSSItem;
import moze_intel.projecte.emc.json.NormalizedSimpleStack;

import java.io.IOException;

class NSSJsonTypeAdapter extends TypeAdapter<NormalizedSimpleStack>
{

	@Override
	public void write(JsonWriter out, NormalizedSimpleStack stack) throws IOException
	{
		if (stack instanceof NSSItem)
		{
			NSSItem item = (NSSItem) stack;
			out.value(String.format("%s|%d", item.itemName, item.damage));
		}
		else
		{
			out.nullValue();
			//throw new JsonParseException("Can only write NSSItems to JSON for now");
		}
	}

	@Override
	public NormalizedSimpleStack read(JsonReader in) throws IOException
	{

		String serializedItem = in.nextString();
		try {
			return NormalizedSimpleStack.fromSerializedItem(serializedItem);
		} catch (Exception e) {
			throw new IOException(e);
		}
	}
}
