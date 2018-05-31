package moze_intel.projecte.emc.mappers.customConversions.json;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import moze_intel.projecte.emc.json.NormalizedSimpleStack;
import moze_intel.projecte.emc.mappers.customConversions.CustomConversionMapper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Holds deserialized custom conversions.
 * Full grammar specification: https://gist.github.com/williewillus/9ebb0d04329526e31564
 */
public class CustomConversionFile
{
	public String comment;
	public final Map<String, ConversionGroup> groups = new HashMap<>();
	public final FixedValues values = new FixedValues();

	public void write(File file) throws IOException
	{
		try (FileWriter fileWriter = new FileWriter(file))
		{
			CustomConversionMapper.GSON.toJson(this, fileWriter);
		}
	}
}
