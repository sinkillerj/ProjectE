package moze_intel.projecte.utils;

import java.io.Closeable;
import java.io.IOException;

/**
 * Helper class for File IO of any sort. Might be unneeded.
 * Notice: Please try to keep methods tidy and alphabetically ordered. Thanks!
 */
public final class FileHelper
{
	public static void closeStream(Closeable c)
	{
		if (c != null)
		{
			try
			{
				c.close();
			}
			catch (IOException e)
			{
				PELogger.logFatal("IO Error: couldn't close stream!");
				e.printStackTrace();
			}
		}
	}
}
