package moze_intel.projecte.emc.mappers.customConversions.json;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class CustomConversion
{
	public int count = 1;
	public String output;
	@SerializedName("ingr")
	public Map<String, Integer> ingredients;
}
