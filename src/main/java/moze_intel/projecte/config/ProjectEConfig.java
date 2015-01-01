package moze_intel.projecte.config;

import moze_intel.projecte.utils.PELogger;
import net.minecraftforge.common.config.Configuration;

import java.io.File;

public final class ProjectEConfig 
{
	/**
	 * Control Config Variable
	 */
	public static Configuration config;

	/**
	 * Configurable Variables
	 */
	public static boolean showUnlocalizedNames;
	public static boolean showODNames;
	public static boolean enableDebugLog;
	public static boolean showEMCTooltip;
	public static boolean showStatTooltip;

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

	/**
	 * Categories
	 */
	public static final String MISC_CATEGORY = "misc";
	public static final String BLOCKS_CATEGORY = "blocks";
	
	public static void init(File configFile)
	{
		config = new Configuration(configFile);
		
		try
		{
			config.load();

			enableDebugLog = config.getBoolean("debugLogging", MISC_CATEGORY, false, "Enable a more verbose debug logging");
			showUnlocalizedNames = config.getBoolean("unToolTips", MISC_CATEGORY, false, "Show item unlocalized names in tooltips (useful for custom EMC registration)");
			showODNames = config.getBoolean("odToolTips", MISC_CATEGORY, false, "Show item Ore Dictionary names in tooltips (useful for custom EMC registration)");
			showEMCTooltip = config.getBoolean("emcToolTips", MISC_CATEGORY, true, "Show the EMC value as a tooltip on items and blocks");
			showStatTooltip = config.getBoolean("statToolTips", MISC_CATEGORY, true, "Show stats as tooltips for various ProjectE blocks");

			enableAlcChest = config.getBoolean("enableAlcChest", BLOCKS_CATEGORY, true, "Enable Alchemical Chest recipe.");

			enableITorch = config.getBoolean("enableITorch", BLOCKS_CATEGORY, true, "Enable Interdiction Torch recipe.");

			enableCollector = config.getBoolean("enableCollector", BLOCKS_CATEGORY, true, "Enable Energy Collector MK1 recipe.");
			enableCollector2 = config.getBoolean("enableCollector2", BLOCKS_CATEGORY, true, "Enable Energy Collector MK2 recipe.");
			enableCollector3 = config.getBoolean("enableCollector3", BLOCKS_CATEGORY, true, "Enable Energy Collector MK3 recipe.");

			enableCondenser = config.getBoolean("enableCondenser", BLOCKS_CATEGORY, true, "Enable Energy Condenser recipe.");
			enableCondenser2 = config.getBoolean("enableCondenser2", BLOCKS_CATEGORY, true, "Enable Energy Condenser MK2 recipe.");

			enableRelay = config.getBoolean("enableRelay", BLOCKS_CATEGORY, true, "Enable AntiMatter Relay MK1 recipe.");
			enableRelay2 = config.getBoolean("enableRelay2", BLOCKS_CATEGORY, true, "Enable AntiMatter Relay MK2 recipe.");
			enableRelay3 = config.getBoolean("enableRelay3", BLOCKS_CATEGORY, true, "Enable AntiMatter Relay MK3 recipe.");

			enableTransTable = config.getBoolean("enableTransTable", BLOCKS_CATEGORY, true, "Enable Transmutation Table recipe.");

			enableRedFurnace = config.getBoolean("enableRedFurnace", BLOCKS_CATEGORY, true, "Enable Red Matter Furnace recipe.");
			enableDarkFurnace = config.getBoolean("enableDarkFurnace", BLOCKS_CATEGORY, true, "Enable Dark Matter Furnace recipe.");
			
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
