package moze_intel.projecte.emc.mappers.customConversions.json;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

public class FixedValues
{
	public Map<String, Integer> setValueBefore = Maps.newHashMap();
	public Map<String, Integer> setValueAfter = Maps.newHashMap();
	public List<CustomConversion> conversion = Lists.newArrayList();
}
