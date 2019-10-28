package moze_intel.projecte.emc.mappers.customConversions.json;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;

public class FixedValues {

	@SerializedName("before")
	public Map<NormalizedSimpleStack, Long> setValueBefore = new HashMap<>();
	@SerializedName("after")
	public Map<NormalizedSimpleStack, Long> setValueAfter = new HashMap<>();
	public List<CustomConversion> conversion = new ArrayList<>();

	public void merge(FixedValues other) {
		setValueBefore.putAll(other.setValueBefore);
		setValueAfter.putAll(other.setValueAfter);
		conversion.addAll(other.conversion);
	}
}