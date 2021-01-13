package moze_intel.projecte.utils;

import java.math.BigInteger;
import java.text.DecimalFormat;
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

	public static final float[] EXPLOSIVE_LENS_RADIUS = new float[]{4.0F, 8.0F, 12.0F, 16.0F, 16.0F, 16.0F, 16.0F, 16.0F};
	public static final long[] EXPLOSIVE_LENS_COST = new long[]{384, 768, 1536, 2304, 2304, 2304, 2304, 2304};

	public static final long FREE_ARITHMETIC_VALUE = Long.MIN_VALUE;
	public static final long TILE_MAX_EMC = Long.MAX_VALUE;

	public static final int MAX_CONDENSER_PROGRESS = 102;

	public static final int MAX_VEIN_SIZE = 250;

	public static final String NBT_KEY_STORED_EMC = "StoredEMC";
	public static final String NBT_KEY_GEM_WHITELIST = "Whitelist";
	public static final String NBT_KEY_COOLDOWN = "Cooldown";
	public static final String NBT_KEY_ACTIVE = "Active";
	public static final String NBT_KEY_MODE = "Mode";
	public static final String NBT_KEY_STEP_ASSIST = "StepAssist";
	public static final String NBT_KEY_NIGHT_VISION = "NightVision";
	public static final String NBT_KEY_UNPROCESSED_EMC = "UnprocessedEMC";
	public static final String NBT_KEY_GEM_CONSUMED = "Consumed";
	public static final String NBT_KEY_GEM_ITEMS = "Items";
	public static final String NBT_KEY_TIME_MODE = "TimeMode";
	public static final String NBT_KEY_STORED_XP = "StoredXP";
}