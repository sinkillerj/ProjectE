package moze_intel.projecte.config;

import moze_intel.projecte.utils.ItemFilterMatcher;
import moze_intel.projecte.utils.PELogger;
import net.minecraftforge.common.config.Configuration;

import java.io.File;

public final class ProjectEConfig 
{
	public static boolean showUnlocalizedNames;
	public static boolean showODNames;
    public static boolean enableDebugLog;
	public static ItemFilterMatcher repairTalismanBlacklist = new ItemFilterMatcher(new String[]{});
	
	public static void init(File configFile)
	{
		Configuration config = new Configuration(configFile);
		
		try
		{
			config.load();

            enableDebugLog = config.getBoolean("debugLogging", "Misc", false, "Enable a more verbose debug logging");
			showUnlocalizedNames = config.getBoolean("unToolTips", "Misc", false, "Show item unlocalized names in tooltips (useful for custom EMC registration)");
			showODNames = config.getBoolean("odToolTips", "Misc", false, "Show item Ore Dictionary names in tooltips (useful for custom EMC registration)");
			repairTalismanBlacklist = new ItemFilterMatcher(config.getStringList("repairTalismanBlacklist", "Misc", new String[]{}, "Exclude Items from being repaired by the Repair talisman. Use * as Wildcard."));
			PELogger.logInfo("Loaded Repair Talisman Blacklist:" + repairTalismanBlacklist.toString());

			PELogger.logInfo("Loaded configuration file.");
		}
		catch (Exception e)
		{
			PELogger.logFatal("Caught exception while loading config file!");
			e.printStackTrace();
		}
		finally
		{
			if (config.hasChanged())
			{
				config.save();
			}
		}
	}
}
