package moze_intel.projecte.emc.mappers.customConversions;

import moze_intel.projecte.emc.IMappingCollector;
import moze_intel.projecte.emc.NormalizedSimpleStack;
import moze_intel.projecte.emc.mappers.IEMCMapper;
import moze_intel.projecte.emc.mappers.customConversions.json.IngredientMapDeserializer;
import moze_intel.projecte.emc.mappers.customConversions.json.CustomConversionFile;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraftforge.common.config.Configuration;

import java.util.Map;

public class CustomConversionMapper implements IEMCMapper<NormalizedSimpleStack, Integer>
{
	@Override
	public String getName()
	{
		return "CustomConversionMapper";
	}

	@Override
	public String getDescription()
	{
		return "";
	}

	@Override
	public boolean isAvailable()
	{
		return true;
	}

	@Override
	public void addMappings(IMappingCollector<NormalizedSimpleStack, Integer> mapper, Configuration config)
	{

	}

	public static CustomConversionFile parseJson(String json) {
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(new TypeToken<Map<String, Integer>>(){}.getType(), new IngredientMapDeserializer());
		Gson gson = builder.create();
		return gson.fromJson(json, CustomConversionFile.class);
	}
}
