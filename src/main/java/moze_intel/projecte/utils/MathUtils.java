package moze_intel.projecte.utils;

import net.minecraft.util.StatCollector;

import java.util.Random;

/**
 * Helper class for any method that turns numbers into other numbers.
 * Named Utils to not clash with vanilla classes
 * Notice: Please try to keep methods tidy and alphabetically ordered. Thanks!
 */
public final class MathUtils
{
	public static int parseInteger(String string)
	{
		int value;

		try
		{
			value = Integer.parseInt(string);
		}
		catch (NumberFormatException e)
		{
			return -1;
		}

		return value;
	}

	public static int randomIntInRange(int min, int max)
	{
		Random rand = new Random();
		return rand.nextInt((max - min) + 1) + min;
	}

	public static double tickToSec(int ticks)
	{
		return ticks / 20.0D;
	}

	public static String tickToSecFormatted(int ticks)
	{
		double result = tickToSec(ticks);
		if (result == 0.0D)
		{
			return result + " " + StatCollector.translateToLocal("pe.misc.seconds") + " (" + StatCollector.translateToLocal("pe.misc.every_tick") + ")";
		}
		else
		{
			return result + " " + StatCollector.translateToLocal("pe.misc.seconds");
		}
	}

	public static int secToTicks(double secs)
	{
		return (int) Math.round(secs * 20.0D);
	}
}
