package moze_intel.projecte.utils;

import net.minecraft.client.resources.I18n;

public class TransmutationEMCFormatter {
	public static String EMCFormat(double EMC) {
		String postFix = "";
		double magnitude = 1;

		Double magnitudes[] = {1e18, 1e15, 1e12};

		for (int i=0; i < magnitudes.length; i++) {
			double testMag = magnitudes[i];
			if (EMC >= testMag) {
				magnitude = testMag;
				postFix =  I18n.format("pe.emc.postfix." + i);
				break;
			}
		}

		return Constants.SINGLE_DP_EMC_FORMATTER.format(EMC / magnitude) + " " + postFix;
	}
}
