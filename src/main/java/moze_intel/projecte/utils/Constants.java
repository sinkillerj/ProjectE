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

	public static final long[] MAX_KLEIN_EMC = new long[]{50_000, 200_000, 800_000, 3_200_000, 12_800_000, 51_200_000};
	public static final long[] RELAY_KLEIN_CHARGE_RATE = new long[]{16, 48, 160};

	public static final float[] EXPLOSIVE_LENS_RADIUS = new float[]{4.0F, 8.0F, 12.0F, 16.0F, 16.0F, 16.0F, 16.0F, 16.0F};
	public static final long[] EXPLOSIVE_LENS_COST = new long[]{384, 768, 1536, 2304, 2304, 2304, 2304, 2304};

	public static final long FREE_ARITHMETIC_VALUE = Long.MIN_VALUE;
	public static final long TILE_MAX_EMC = Long.MAX_VALUE;

	public static final long COLLECTOR_MK1_MAX = 10_000;
	public static final long COLLECTOR_MK2_MAX = 30_000;
	public static final long COLLECTOR_MK3_MAX = 60_000;
	public static final long COLLECTOR_MK1_GEN = 4;
	public static final long COLLECTOR_MK2_GEN = 12;
	public static final long COLLECTOR_MK3_GEN = 40;

	public static final long RELAY_MK1_OUTPUT = 64;
	public static final long RELAY_MK2_OUTPUT = 192;
	public static final long RELAY_MK3_OUTPUT = 640;

	public static final long RELAY_MK1_MAX = 100_000;
	public static final long RELAY_MK2_MAX = 1_000_000;
	public static final long RELAY_MK3_MAX = 10_000_000;

	public static final int COAL_BURN_TIME = 1_600;
	public static final int ALCH_BURN_TIME = COAL_BURN_TIME * 4;
	public static final int MOBIUS_BURN_TIME = ALCH_BURN_TIME * 4;
	public static final int AETERNALIS_BURN_TIME = MOBIUS_BURN_TIME * 4;

	public static final int MAX_CONDENSER_PROGRESS = 102;

	public static final int MAX_VEIN_SIZE = 250;

	public static final long ENCH_EMC_BONUS = 5_000;
}