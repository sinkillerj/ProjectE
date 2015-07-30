package moze_intel.projecte.config;

import moze_intel.projecte.utils.PELogger;
import net.minecraftforge.common.config.Configuration;

import java.io.File;

public final class ProjectEConfig 
{
	public static boolean showUnlocalizedNames;
	public static boolean showODNames;
	public static boolean enableDebugLog;
	public static boolean showEMCTooltip;
	public static boolean showStatTooltip;
	public static boolean showPedestalTooltip;
	public static boolean showPedestalTooltipInGUI;

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

	public static boolean enableTimeWatch;

	public static boolean enableDarkPedestal;

	public static boolean craftableTome;
	public static boolean altCraftingMat;
	public static boolean useOldDamage;
	public static int archangelPedCooldown;
	public static int bodyPedCooldown;
	public static int evertidePedCooldown;
	public static int harvestPedCooldown;
	public static int ignitePedCooldown;
	public static int lifePedCooldown;
	public static int repairPedCooldown;
	public static int swrgPedCooldown;
	public static int soulPedCooldown;
	public static int volcanitePedCooldown;

	public static int zeroPedCooldown;
	public static int timePedBonus;
	public static float timePedMobSlowness;
	public static boolean interdictionMode;
	public static boolean pickaxeAoeVeinMining;
	public static boolean harvBandGrass;
	public static boolean useLootBalls;
	public static boolean pulsatingOverlay;

	public static void init(File configFile)
	{
		Configuration config = new Configuration(configFile);
		
		try
		{
			config.load();

			enableDebugLog = config.getBoolean("debugLogging", "misc", false, "Enable a more verbose debug logging");
			showUnlocalizedNames = config.getBoolean("unToolTips", "misc", false, "Show item unlocalized names in tooltips (useful for custom EMC registration)");
			showODNames = config.getBoolean("odToolTips", "misc", false, "Show item Ore Dictionary names in tooltips (useful for custom EMC registration)");
			showEMCTooltip = config.getBoolean("emcToolTips", "misc", true, "Show the EMC value as a tooltip on items and blocks");
			showStatTooltip = config.getBoolean("statToolTips", "misc", true, "Show stats as tooltips for various ProjectE blocks");
			showPedestalTooltip = config.getBoolean("pedestalToolTips", "misc", true, "Show DM pedestal functions in item tooltips");
			showPedestalTooltipInGUI = config.getBoolean("pedestalToolTipsInGUI", "misc", false, "Show pedestal function tooltips only in pedestal GUI");
			useLootBalls = config.getBoolean("useLootBalls", "misc", true, "Make loot balls for drops. Disabling this may potentially cause bad performance when large amounts of loot are spawned!");
			pulsatingOverlay = config.getBoolean("pulsatingOverlay", "misc", false, "The Philosopher's Stone overlay softly pulsates");

			enableAlcChest = config.getBoolean("enableAlcChest", "blocks", true, "Enable Alchemical Chest recipe");

			enableITorch = config.getBoolean("enableITorch", "blocks", true, "Enable Interdiction Torch recipe");

			enableCollector = config.getBoolean("enableCollector", "blocks", true, "Enable Energy Collector MK1 recipe");
			enableCollector2 = config.getBoolean("enableCollector2", "blocks", true, "Enable Energy Collector MK2 recipe");
			enableCollector3 = config.getBoolean("enableCollector3", "blocks", true, "Enable Energy Collector MK3 recipe");

			enableCondenser = config.getBoolean("enableCondenser", "blocks", true, "Enable Energy Condenser recipe");
			enableCondenser2 = config.getBoolean("enableCondenser2", "blocks", true, "Enable Energy Condenser MK2 recipe");

			enableRelay = config.getBoolean("enableRelay", "blocks", true, "Enable AntiMatter Relay MK1 recipe");
			enableRelay2 = config.getBoolean("enableRelay2", "blocks", true, "Enable AntiMatter Relay MK2 recipe");
			enableRelay3 = config.getBoolean("enableRelay3", "blocks", true, "Enable AntiMatter Relay MK3 recipe");

			enableTransTable = config.getBoolean("enableTransTable", "blocks", true, "Enable Transmutation Table recipe");

			enableRedFurnace = config.getBoolean("enableRedFurnace", "blocks", true, "Enable Red Matter Furnace recipe");
			enableDarkFurnace = config.getBoolean("enableDarkFurnace", "blocks", true, "Enable Dark Matter Furnace recipe");

			enableDarkPedestal = config.getBoolean("enableDarkPedestal", "blocks", true, "Enable DM Pedestal recipe");
			enableTimeWatch = config.getBoolean("enableTimeWatch", "items", true, "Enable Watch of Flowing Time");

			craftableTome = config.getBoolean("craftableTome", "difficulty", false, "The Tome of Knowledge can be crafted.");
			altCraftingMat = config.getBoolean("altCraftingMat", "difficulty", false, "If true some ProjectE items require a nether star instead of a diamond.");
			useOldDamage = config.getBoolean("useOldDamage", "difficulty", false, "If true the old damage amounts from 1.4.7 and before will be used for weapons.");

			config.getCategory("pedestalcooldown").setComment("Cooldown for various items within the pedestal. A cooldown of -1 will disable the functionality.\n" +
					"A cooldown of 0 will cause the actions happens every tick. Use caution as a very low value could cause TPS issues.");

			archangelPedCooldown = config.getInt("archangelPedCooldown", "pedestalcooldown", 100, -1, Integer.MAX_VALUE, "Delay between Archangel Smite shooting arrows while in the pedestal.");

			bodyPedCooldown = config.getInt("bodyPedCooldown", "pedestalcooldown", 10, -1, Integer.MAX_VALUE, "Delay between Body Stone healing 0.5 shanks while in the pedestal.");

			evertidePedCooldown = config.getInt("evertidePedCooldown", "pedestalcooldown", 20, -1, Integer.MAX_VALUE, "Delay between Evertide Amulet trying to start rain while in the pedestal.");

			harvestPedCooldown = config.getInt("harvestPedCooldown", "pedestalcooldown", 10, -1, Integer.MAX_VALUE, "Delay between Harvest Goddess trying to grow and harvest while in the pedestal.");

			ignitePedCooldown = config.getInt("ignitePedCooldown", "pedestalcooldown", 40, -1, Integer.MAX_VALUE, "Delay between Ignition Ring trying to light entities on fire while in the pedestal.");

			lifePedCooldown = config.getInt("lifePedCooldown", "pedestalcooldown", 5, -1, Integer.MAX_VALUE, "Delay between Life Stone healing both food and hunger by 0.5 shank/heart while in the pedestal.");

			repairPedCooldown = config.getInt("repairPedCooldown", "pedestalcooldown", 20, -1, Integer.MAX_VALUE, "Delay between Talisman of Repair trying to repair player items while in the pedestal.");

			swrgPedCooldown = config.getInt("swrgPedCooldown", "pedestalcooldown", 70, -1, Integer.MAX_VALUE, "Delay between SWRG trying to smite mobs while in the pedestal.");

			soulPedCooldown = config.getInt("soulPedCooldown", "pedestalcooldown", 10, -1, Integer.MAX_VALUE, "Delay between Soul Stone healing 0.5 hearts while in the pedestal.");

			volcanitePedCooldown = config.getInt("volcanitePedCooldown", "pedestalcooldown", 20, -1, Integer.MAX_VALUE, "Delay between Volcanite Amulet trying to stop rain while in the pedestal.");

			zeroPedCooldown = config.getInt("zeroPedCooldown", "pedestalcooldown", 40, -1, Integer.MAX_VALUE, "Delay between Zero Ring trying to extinguish entities and freezing ground while in the pedestal.");


			timePedBonus = config.getInt("timePedBonus", "effects", 18, 0, 256, "Bonus ticks given by the Watch of Flowing Time while in the pedestal. 0 = effectively no bonus.");
			timePedMobSlowness = config.getFloat("timePedMobSlowness", "effects", 0.10F, 0.0F, 1.0F, "Factor the Watch of Flowing Time slows down mobs by while in the pedestal. Set to 1.0 for no slowdown.");
			interdictionMode = config.getBoolean("interdictionMode", "effects", true, "If true the Interdiction Torch only affects hostile mobs. If false it affects all non blacklisted living entities.");

			pickaxeAoeVeinMining = config.getBoolean("pickaxeAoeVeinMining", "items", false, "Instead of vein mining the ore you right click with your Dark/Red Matter Pick/Star it vein mines all ores in an AOE around you like it did in ProjectE before version 1.4.4.");
			harvBandGrass = config.getBoolean("harvBandGrass", "items", false, "Allows the Harvest Goddess Band to passively grow tall grass, flowers, etc, on top of grass blocks.");
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
