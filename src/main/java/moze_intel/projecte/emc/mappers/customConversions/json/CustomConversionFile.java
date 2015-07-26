package moze_intel.projecte.emc.mappers.customConversions.json;

import java.util.Map;

public class CustomConversionFile
{
	public String forMod;
	public String version;
	public String comment;
	public Map<String, ConversionGroup> groups;
	public FixedValues values;
}
