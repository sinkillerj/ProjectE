package moze_intel.projecte.config;

import moze_intel.projecte.utils.PELogger;
import net.minecraftforge.common.config.Configuration;

import java.io.File;

public final class ProjectEConfig 
{
	public static boolean showUnlocalizedNames;
	public static boolean showODNames;
	public static boolean enableDebugLog;

	public static boolean enableAlcChest;
	public static boolean enableITorch;
	public static boolean enableCollector;
	public static boolean enableCollector2;
	public static boolean enableCollector3;
	public static boolean enableCondenser;
	public static boolean enableCondenser2;
	public static boolean enableRelay;
	public static boolean enableRelay2;
	public static boolean enableRelay3;
	public static boolean enableTransTable;
	public static boolean enableRedFurnace;
	public static boolean enableDarkFurnace;
	
	public static void init(File configFile)
	{
		Configuration config = new Configuration(configFile);
		
		try
		{
			config.load();

			enableDebugLog = config.getBoolean("debugLogging", "misc", false, "Enable a more verbose debug logging");
			showUnlocalizedNames = config.getBoolean("unToolTips", "misc", false, "Show item unlocalized names in tooltips (useful for custom EMC registration)");
			showODNames = config.getBoolean("odToolTips", "misc", false, "Show item Ore Dictionary names in tooltips (useful for custom EMC registration)");

			enableAlcChest = config.getBoolean("enableAlcChest", "blocks", true, "Enable Alchemical Chest recipe.");

			enableITorch = config.getBoolean("enableITorch", "blocks", true, "Enable Interdiction Torch recipe.");

			enableCollector = config.getBoolean("enableCollector", "blocks", true, "Enable Energy Collector MK1 recipe.");
			enableCollector2 = config.getBoolean("enableCollector2", "blocks", true, "Enable Energy Collector MK2 recipe.");
			enableCollector3 = config.getBoolean("enableCollector3", "blocks", true, "Enable Energy Collector MK3 recipe.");

			enableCondenser = config.getBoolean("enableCondenser", "blocks", true, "Enable Energy Condenser recipe.");
			enableCondenser2 = config.getBoolean("enableCondenser2", "blocks", true, "Enable Energy Condenser MK2 recipe.");

			enableRelay = config.getBoolean("enableRelay", "blocks", true, "Enable AntiMatter Relay MK1 recipe.");
			enableRelay2 = config.getBoolean("enableRelay2", "blocks", true, "Enable AntiMatter Relay MK2 recipe.");
			enableRelay3 = config.getBoolean("enableRelay3", "blocks", true, "Enable AntiMatter Relay MK3 recipe.");

			enableTransTable = config.getBoolean("enableTransTable", "blocks", true, "Enable Transmutation Table recipe.");

			enableRedFurnace = config.getBoolean("enableRedFurnace", "blocks", true, "Enable Red Matter Furnace recipe.");
			enableDarkFurnace = config.getBoolean("enableDarkFurnace", "blocks", true, "Enable Dark Matter Furnace recipe.");
			
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
