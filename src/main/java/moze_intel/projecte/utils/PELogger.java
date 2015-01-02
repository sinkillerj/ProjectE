package moze_intel.projecte.utils;

import moze_intel.projecte.PECore;

import moze_intel.projecte.config.ProjectEConfig;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class PELogger 
{
	private static Logger logger = LogManager.getLogger(PECore.MODID);

	public static void log(Level level, String msg)
	{
		logger.log(level, msg);
	}
	
	public static void logInfo(String msg)
	{
		logger.info(msg);
	}
	
	public static void logWarn(String msg)
	{
		logger.warn(msg);
	}
	
	public static void logFatal(String msg)
	{
		logger.fatal(msg);
	}

	public static void logDebug(String msg)
	{
		if (ProjectEConfig.enableDebugLog)
		{
			//logger.debug() doesn't seem to work
			logger.info(msg);
		}
	}
}
