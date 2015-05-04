package moze_intel.projecte.emc.pregenerated;

import moze_intel.projecte.emc.NormalizedSimpleStack;
import moze_intel.projecte.utils.PELogger;

import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.minecraft.item.Item;

import java.io.IOException;

public class NSSJsonTypeAdapter extends TypeAdapter<NormalizedSimpleStack>
{

	@Override
	public void write(JsonWriter out, NormalizedSimpleStack stack) throws IOException
	{
		if (stack instanceof NormalizedSimpleStack.NSSItem)
		{
			NormalizedSimpleStack.NSSItem item = (NormalizedSimpleStack.NSSItem) stack;
			Object itemObject = Item.itemRegistry.getObjectById(item.id);
			if (itemObject != null)
			{
				String itemName = Item.itemRegistry.getNameForObject(itemObject);
				if (itemName != null)
				{
					out.value(String.format("%s|%d", itemName, item.damage));
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
		int pipeIndex = serializedItem.lastIndexOf('|');
		if (pipeIndex < 0)
		{
			return null;
		}
		String itemName = serializedItem.substring(0, pipeIndex);
		String itemDamageString = serializedItem.substring(pipeIndex + 1);
		int itemDamage;
		try {
			itemDamage = Integer.parseInt(itemDamageString);
		} catch (NumberFormatException e) {
			throw new JsonParseException(String.format("Could not parse '%s' to metadata-integer", itemDamageString), e);
		}

		Object itemObject = Item.itemRegistry.getObject(itemName);
		if (itemObject != null)
		{
			int id = Item.itemRegistry.getIDForObject(itemObject);
			return NormalizedSimpleStack.getNormalizedSimpleStackFor(id, itemDamage);
		}
		PELogger.logWarn(String.format("Could not get Item-Object for Item with name: '%s'", itemName));
		return null;

	}
}
