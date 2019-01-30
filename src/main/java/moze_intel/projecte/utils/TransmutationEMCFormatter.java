package moze_intel.projecte.utils;

public class TransmutationEMCFormatter {
	public static String EMCFormat(double EMC) {
		String postFix = "";
		double magnitude = 1;

		String formats[] = {"Quadrillion", "Trillion", "Billion"};
		Double magnitudes[] = {1e15, 1e12, 1e9};

		for (int i=0; i <formats.length; i++) {
			double testMag = magnitudes[i];
			if (EMC >= testMag) {
				magnitude = testMag;
				postFix = formats[i];
				break;
			}
		}

		return Constants.EMC_FORMATTER.format(EMC / magnitude) + " " + postFix;
	}
}
