package moze_intel.projecte.config;

import moze_intel.projecte.utils.PELogger;
import net.minecraftforge.common.config.Configuration;

import java.io.File;

public final class ProjectEConfig 
{
	public static boolean showODNames;
	public static boolean enableDebugLog;
	public static boolean showEMCTooltip;
	public static boolean showStatTooltip;
	public static boolean showPedestalTooltip;

	public static boolean enableTimeWatch;

	public static boolean craftableTome;
	public static boolean altCraftingMat;
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
	public static boolean pulsatingOverlay;
	public static boolean unsafeKeyBinds;
	public static boolean offensiveAbilities;
	public static float katarDeathAura;
	public static double covalenceLoss;
	public static int projectileCooldown;
	public static boolean disableAllRadiusMining;
	public static int gemChestCooldown;

	public static void init(File configFile)
	{
		Configuration config = new Configuration(configFile);
		
		try
		{
			config.load();

			enableDebugLog = config.getBoolean("debugLogging", "misc", false, "Enable a more verbose debug logging");
			showODNames = config.getBoolean("odToolTips", "misc", false, "Show item Ore Dictionary names in tooltips (useful for custom EMC registration)");
			showEMCTooltip = config.getBoolean("emcToolTips", "misc", true, "Show the EMC value as a tooltip on items and blocks");
			showStatTooltip = config.getBoolean("statToolTips", "misc", true, "Show stats as tooltips for various ProjectE blocks");
			showPedestalTooltip = config.getBoolean("pedestalToolTips", "misc", true, "Show DM pedestal functions in item tooltips");
			pulsatingOverlay = config.getBoolean("pulsatingOverlay", "misc", false, "The Philosopher's Stone overlay softly pulsates");
			unsafeKeyBinds = config.getBoolean("unsafeKeyBinds", "misc", false, "False requires your hand be empty for Gem Armor Offensive Abilities to be readied or triggered");
			projectileCooldown = config.getInt("projectileCooldown", "misc", 0, 0, Integer.MAX_VALUE, "A cooldown (in ticks) for firing projectiles");
			gemChestCooldown = config.getInt("gemChestCooldown", "misc", 0, 0, Integer.MAX_VALUE, "A cooldown (in ticks) for Gem Chestplate explosion");

			enableTimeWatch = config.getBoolean("enableTimeWatch", "items", true, "Enable Watch of Flowing Time");

			craftableTome = config.getBoolean("craftableTome", "difficulty", false, "The Tome of Knowledge can be crafted.");
			altCraftingMat = config.getBoolean("altCraftingMat", "difficulty", false, "If true some ProjectE items require a nether star instead of a diamond.");
			offensiveAbilities = config.getBoolean("offensiveAbilities", "difficulty", true, "Set to false to disable Gem Armor offensive abilities (helmet zap and chestplate explosion)");
			katarDeathAura = config.getFloat("katarDeathAura", "difficulty", 1000F, 0, Integer.MAX_VALUE, "Amount of damage Katar 'C' key deals");

			covalenceLoss = config.get("difficulty", "covalenceLoss", 1.0, "Adjusting this ratio changes how much EMC is received when burning a item. For example setting this to 0.5 will return half of the EMC cost.", 0.1, 1.0).getDouble(1.0);

			config.getCategory("pedestalcooldown").setComment("Cooldown for various items within the pedestal. A cooldown of -1 will disable the functionality.\n" +
					"A cooldown of 0 will cause the actions to happen every tick. Use caution as a very low value could cause TPS issues.");

			archangelPedCooldown = config.getInt("archangelPedCooldown", "pedestalcooldown", 40, -1, Integer.MAX_VALUE, "Delay between Archangel Smite shooting arrows while in the pedestal.");

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
			disableAllRadiusMining = config.getBoolean("disableAllRadiusMining", "items", false, "If set to true, disables all radius-based mining functionaliy (right click of tools)");
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
