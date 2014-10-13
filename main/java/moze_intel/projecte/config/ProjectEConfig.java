package moze_intel.projecte.config;

import java.io.File;

import moze_intel.projecte.utils.PELogger;
import net.minecraftforge.common.config.Configuration;

public class ProjectEConfig 
{
	public static boolean showUnlocalizedNames;
	public static boolean showODNames;
	
	public static void init(File configFile)
	{
		Configuration config = new Configuration(configFile);
		
		try
		{
			config.load();
			
			showUnlocalizedNames = config.getBoolean("Show unlocalized names in tool-tips", "Misc", false, "Set this to true to show item unlocalized names (usefull for custom EMC registration)");
			showODNames = config.getBoolean("Show Ore-Dictionary names in tool-tips", "Misc", false, "Set this to true to show item Ore Dictionary names (usefull for custom EMC registration)");
			
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
