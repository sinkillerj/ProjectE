package moze_intel.projecte.emc.mappers.customConversions.json;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.annotations.SerializedName;
import moze_intel.projecte.emc.json.NormalizedSimpleStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FixedValues
{
	@SerializedName("before")
	public Map<NormalizedSimpleStack, Long> setValueBefore = new HashMap<>();
	@SerializedName("after")
	public Map<NormalizedSimpleStack, Long> setValueAfter = new HashMap<>();
	public List<CustomConversion> conversion = new ArrayList<>();
}
