package moze_intel.projecte.utils;

import java.math.BigInteger;
import net.minecraft.client.resources.I18n;

public class TransmutationEMCFormatter {

	public static String formatEMC(long emc) {
		String postFix = "";
		double magnitude = 1;

		double[] magnitudes = {1e12, 1e15, 1e18};

		for (int i=magnitudes.length - 1; i >= 0; i--) {
			double testMag = magnitudes[i];
			if (emc >= testMag) {
				magnitude = testMag;
				postFix =  I18n.format("pe.emc.postfix." + i);
				break;
			}
		}

		return Constants.SINGLE_DP_EMC_FORMATTER.format(emc / magnitude) + " " + postFix;
	}

	public static String formatEMC(BigInteger emc) {
		String emcAsString =  emc.toString();
		int length = emcAsString.length();
		int splits = (length - 1) / 3;
		if (splits < 3) {
			return Constants.SINGLE_DP_EMC_FORMATTER.format(emc);
		}
		//Otherwise we need to manually format it
		int extraDigits = length % 3;
		double value;
		if (extraDigits == 0) {
			value = Double.parseDouble(emcAsString.substring(0, 3) + "." + emcAsString.substring(3, 5));
		} else if (extraDigits == 1) {
			value = Double.parseDouble(emcAsString.substring(0, 1) + "." + emcAsString.substring(1, 3));
		} else {//if (extraDigits == 2)
			value = Double.parseDouble(emcAsString.substring(0, 2) + "." + emcAsString.substring(2, 4));
		}
		return Constants.SINGLE_DP_EMC_FORMATTER.format(value) + " " + I18n.format("pe.emc.postfix." + (splits - 3));
	}
}
