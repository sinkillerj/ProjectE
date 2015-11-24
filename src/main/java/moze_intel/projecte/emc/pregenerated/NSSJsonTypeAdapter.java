package moze_intel.projecte.emc.pregenerated;

import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import moze_intel.projecte.emc.NormalizedSimpleStack;
import moze_intel.projecte.utils.PELogger;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;

public class NSSJsonTypeAdapter extends TypeAdapter<NormalizedSimpleStack>
{

	@Override
	public void write(JsonWriter out, NormalizedSimpleStack stack) throws IOException
	{
		if (stack instanceof NormalizedSimpleStack.NSSItem)
		{
			NormalizedSimpleStack.NSSItem item = (NormalizedSimpleStack.NSSItem) stack;
			Item itemObject = Item.itemRegistry.getObjectById(item.id);
			if (itemObject != null)
			{
				ResourceLocation itemName = Item.itemRegistry.getNameForObject(itemObject);
				if (itemName != null)
				{
					out.value(String.format("%s|%d", itemName.toString(), item.damage));
					return;
				}
			}
			throw new JsonParseException(String.format("Could not write %s to JSON", item));
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
