package moze_intel.projecte.utils;

import net.minecraft.client.resources.I18n;

public class TransmutationEMCFormatter {

	public static String formatEMC(Number emc) {
		String emcAsString = emc.toString();
		int length = emcAsString.length();
		int splits = (length - 1) / 3;
		if (splits < 3) {
			return Constants.EMC_FORMATTER.format(emc);
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
		return Constants.EMC_FORMATTER.format(value) + " " + I18n.format("pe.emc.postfix." + (splits - 3));
	}
}
