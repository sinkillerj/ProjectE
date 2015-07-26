package moze_intel.projecte.emc.mappers.customConversions.json;

import moze_intel.projecte.emc.NormalizedSimpleStack;

import java.util.Map;

public class CustomConversionFile
{
	public String forMod;
	public String version;
	public String comment;
	public Map<String, ConversionGroup> groups;
	public Map<NormalizedSimpleStack, Integer> setValueBefore;
	public Map<NormalizedSimpleStack, Integer> setValueAfter;
}
