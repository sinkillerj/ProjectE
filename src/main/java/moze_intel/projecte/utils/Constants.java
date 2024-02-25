package moze_intel.projecte.utils;

import java.math.BigInteger;
import java.text.NumberFormat;

public final class Constants {

	public static final NumberFormat EMC_FORMATTER = getFormatter();

	private static NumberFormat getFormatter() {
		NumberFormat format = NumberFormat.getInstance();
		//Only ever use a single decimal point for our formatter,
		// because the majority of the time we are a whole number
		// except for when we are abbreviating
		format.setMaximumFractionDigits(1);
		return format;
	}

	public static final BigInteger MAX_EXACT_TRANSMUTATION_DISPLAY = BigInteger.valueOf(1_000_000_000_000L);
	public static final BigInteger MAX_INTEGER = BigInteger.valueOf(Integer.MAX_VALUE);
	public static final BigInteger MAX_LONG = BigInteger.valueOf(Long.MAX_VALUE);

	public static final float[] EXPLOSIVE_LENS_RADIUS = new float[]{4.0F, 8.0F, 12.0F, 16.0F, 16.0F, 16.0F, 16.0F, 16.0F};
	public static final long[] EXPLOSIVE_LENS_COST = new long[]{384, 768, 1536, 2304, 2304, 2304, 2304, 2304};

	public static final long BLOCK_ENTITY_MAX_EMC = Long.MAX_VALUE;

	public static final int MAX_CONDENSER_PROGRESS = 102;

	public static final int MAX_VEIN_SIZE = 250;
}