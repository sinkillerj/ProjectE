package moze_intel.projecte.emc.mappers.customConversions.json;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

public class FixedValues
{
	@SerializedName("before")
	public Map<String, Integer> setValueBefore = Maps.newHashMap();
	@SerializedName("after")
	public Map<String, Integer> setValueAfter = Maps.newHashMap();
	public List<CustomConversion> conversion = Lists.newArrayList();
}
