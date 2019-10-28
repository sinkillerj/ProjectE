package moze_intel.projecte.emc.mappers.customConversions.json;

import java.util.ArrayList;
import java.util.List;

public class ConversionGroup {

	public String comment;
	public final List<CustomConversion> conversions = new ArrayList<>();

	public static ConversionGroup merge(ConversionGroup left, ConversionGroup right) {
		ConversionGroup res = new ConversionGroup();
		res.conversions.addAll(left.conversions);
		res.conversions.addAll(right.conversions);
		return res;
	}
}