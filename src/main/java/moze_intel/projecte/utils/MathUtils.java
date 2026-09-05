package moze_intel.projecte.utils;

import java.math.BigInteger;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.network.chat.Component;
import net.minecraft.world.TickRateManager;

/**
 * Helper class for any method that turns numbers into other numbers. Named Utils to not clash with vanilla classes Notice: Please try to keep methods tidy and
 * alphabetically ordered. Thanks!
 */
public final class MathUtils {

	private static final BigInteger MAX_INTEGER = BigInteger.valueOf(Integer.MAX_VALUE);
	public static final BigInteger MAX_LONG = BigInteger.valueOf(Long.MAX_VALUE);

	/**
	 * Scales this proportion into redstone, where 0 means none, 15 means full, and the rest are an appropriate scaling.
	 */
	public static int scaleToRedstone(long currentAmount, long max) {
		if (currentAmount <= 0) {
			return 0;
		} else if (currentAmount >= max) {
			return 15;
		}
		//Integer arithmetic equivalent to Math.round(currentAmount * 13.0 / max + 1)
		//Avoids floating point overhead; numerator cannot overflow for realistic EMC storage values
		return (int) ((currentAmount * 26L + 3L * max) / (2L * max));
	}

	public static float tickToSec(int ticks, float tickRate) {
		if (tickRate <= TickRateManager.MIN_TICKRATE) {
			throw new IllegalArgumentException("Tick rate below minimum tick rate");
		}
		return ticks / tickRate;
	}

	/**
	 * Converts ticks to seconds, and adds the string unit on. If result is 0, then "every tick" is appended
	 */
	public static Component tickToSecFormatted(int ticks, float tickRate) {
		//Only used on the client
		float result = tickToSec(ticks, tickRate);
		if (result == 0) {
			return PELang.EVERY_TICK.translate(result);
		}
		return PELang.SECONDS.translate(result);
	}

	public static int secToTicks(float secs, float tickRate) {
		return Math.round(secs * tickRate);
	}

	public static int clampToInt(BigInteger bigInt) {
		return bigInt.compareTo(MAX_INTEGER) >= 0 ? Integer.MAX_VALUE : bigInt.intValue();
	}

	//Note: This does not clamp to negative so will error if a negative big int is passed that is out of long's bounds
	public static long clampToLong(BigInteger bigInt) {
		return bigInt.compareTo(MAX_LONG) >= 0 ? Long.MAX_VALUE : bigInt.longValue();
	}

	public static boolean isGreaterThanLong(BigInteger bigInt) {
		return bigInt.compareTo(MAX_LONG) > 0;
	}
}