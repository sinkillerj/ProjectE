package moze_intel.projecte.config;

import com.google.common.collect.ImmutableMap;
import moze_intel.projecte.utils.PELogger;
import net.minecraftforge.common.config.Configuration;

import java.io.File;
import java.util.Map;

public final class ProjectEConfig 
{
	private static Map<String, Boolean> booleanProps;
	private static Map<String, Float> floatProps;
	private static Map<String, Integer> intProps;

	public static boolean getBooleanProp(String property) {
		if (booleanProps.containsKey(property)) {
			return booleanProps.get(property);
		} else {
			PELogger.logWarn("Unknown boolean prop: " + property);
			return false;
		}
	}

	public static float getFloatProp(String property) {
		if (floatProps.containsKey(property)) {
			return floatProps.get(property);
		} else {
			PELogger.logWarn("Unknown float prop: " + property);
			return 0.0F;
		}
	}

	public static int getIntProp(String property) {
		if (intProps.containsKey(property)) {
			return intProps.get(property);
		} else {
			PELogger.logWarn("Unknown int prop: " + property);
			return 0;
		}
	}

	public static void init(File configFile)
	{
		Configuration config = new Configuration(configFile);
		
		try
		{
			config.load();

			ImmutableMap.Builder<String, Boolean> booleanBuilder = ImmutableMap.builder();
			ImmutableMap.Builder<String, Float> floatBuilder = ImmutableMap.builder();
			ImmutableMap.Builder<String, Integer> intBuilder = ImmutableMap.builder();

			booleanBuilder.put("enableDebugLog", config.getBoolean("debugLogging", "misc", false, "Enable a more verbose debug logging"));
			booleanBuilder.put("showUnlocalizedNames", config.getBoolean("unToolTips", "misc", false, "Show item unlocalized names in tooltips (useful for custom EMC registration)"));
			booleanBuilder.put("showODNames", config.getBoolean("odToolTips", "misc", false, "Show item Ore Dictionary names in tooltips (useful for custom EMC registration)"));
			booleanBuilder.put("showEMCTooltip", config.getBoolean("emcToolTips", "misc", true, "Show the EMC value as a tooltip on items and blocks"));
			booleanBuilder.put("showStatTooltip", config.getBoolean("statToolTips", "misc", true, "Show stats as tooltips for various ProjectE blocks"));
			booleanBuilder.put("showPedestalTooltip", config.getBoolean("pedestalToolTips", "misc", true, "Show DM pedestal functions in item tooltips"));
			booleanBuilder.put("showPedestalTooltipInGUI", config.getBoolean("pedestalToolTipsInGUI", "misc", false, "Show pedestal function tooltips only in pedestal GUI"));
			booleanBuilder.put("useLootBalls", config.getBoolean("useLootBalls", "misc", true, "Make loot balls for drops. Disabling this may potentially cause bad performance when large amounts of loot are spawned!"));
			booleanBuilder.put("pulsatingOverlay", config.getBoolean("pulsatingOverlay", "misc", false, "The Philosopher's Stone overlay softly pulsates"));
			booleanBuilder.put("unsafeKeyBinds", config.getBoolean("unsafeKeyBinds", "misc", false, "False requires your hand be empty for Gem Armor Offensive Abilities to be readied or triggered"));
			intBuilder.put("projectileCooldown", config.getInt("projectileCooldown", "misc", 0, 0, Integer.MAX_VALUE, "A cooldown (in ticks) for firing projectiles"));

			booleanBuilder.put("enableAlcChest", config.getBoolean("enableAlcChest", "blocks", true, "Enable Alchemical Chest recipe"));

			booleanBuilder.put("enableITorch", config.getBoolean("enableITorch", "blocks", true, "Enable Interdiction Torch recipe"));

			booleanBuilder.put("enableCollector", config.getBoolean("enableCollector", "blocks", true, "Enable Energy Collector MK1 recipe"));
			booleanBuilder.put("enableCollector2", config.getBoolean("enableCollector2", "blocks", true, "Enable Energy Collector MK2 recipe"));
			booleanBuilder.put("enableCollector3", config.getBoolean("enableCollector3", "blocks", true, "Enable Energy Collector MK3 recipe"));

			booleanBuilder.put("enableCondenser", config.getBoolean("enableCondenser", "blocks", true, "Enable Energy Condenser recipe"));
			booleanBuilder.put("enableCondenser2", config.getBoolean("enableCondenser2", "blocks", true, "Enable Energy Condenser MK2 recipe"));

			booleanBuilder.put("enableRelay", config.getBoolean("enableRelay", "blocks", true, "Enable AntiMatter Relay MK1 recipe"));
			booleanBuilder.put("enableRelay2", config.getBoolean("enableRelay2", "blocks", true, "Enable AntiMatter Relay MK2 recipe"));
			booleanBuilder.put("enableRelay3", config.getBoolean("enableRelay3", "blocks", true, "Enable AntiMatter Relay MK3 recipe"));

			booleanBuilder.put("enableTransTable", config.getBoolean("enableTransTable", "blocks", true, "Enable Transmutation Table recipe"));

			booleanBuilder.put("enableRedFurnace", config.getBoolean("enableRedFurnace", "blocks", true, "Enable Red Matter Furnace recipe"));
			booleanBuilder.put("enableDarkFurnace", config.getBoolean("enableDarkFurnace", "blocks", true, "Enable Dark Matter Furnace recipe"));

			booleanBuilder.put("enableDarkPedestal", config.getBoolean("enableDarkPedestal", "blocks", true, "Enable DM Pedestal recipe"));
			booleanBuilder.put("enableTimeWatch", config.getBoolean("enableTimeWatch", "items", true, "Enable Watch of Flowing Time"));

			booleanBuilder.put("craftableTome", config.getBoolean("craftableTome", "difficulty", false, "The Tome of Knowledge can be crafted."));
			booleanBuilder.put("altCraftingMat", config.getBoolean("altCraftingMat", "difficulty", false, "If true some ProjectE items require a nether star instead of a diamond."));
			booleanBuilder.put("useOldDamage", config.getBoolean("useOldDamage", "difficulty", false, "If true the old damage amounts from ProjectE 1.4.7 and before will be used for weapons."));
			booleanBuilder.put("offensiveAbilities", config.getBoolean("offensiveAbilities", "difficulty", true, "Set to false to disable Gem Armor offensive abilities (helmet zap and chestplate explosion)"));
			floatBuilder.put("katarDeathAura", config.getFloat("katarDeathAura", "difficulty", 1000F, 0, Integer.MAX_VALUE, "Amount of damage Katar 'C' key deals"));

			config.getCategory("pedestalcooldown").setComment("Cooldown for various items within the pedestal. A cooldown of -1 will disable the functionality.\n" +
					"A cooldown of 0 will cause the actions to happen every tick. Use caution as a very low value could cause TPS issues.");

			intBuilder.put("archangelPedCooldown", config.getInt("archangelPedCooldown", "pedestalcooldown", 100, -1, Integer.MAX_VALUE, "Delay between Archangel Smite shooting arrows while in the pedestal."));

			intBuilder.put("bodyPedCooldown", config.getInt("bodyPedCooldown", "pedestalcooldown", 10, -1, Integer.MAX_VALUE, "Delay between Body Stone healing 0.5 shanks while in the pedestal."));

			intBuilder.put("evertidePedCooldown", config.getInt("evertidePedCooldown", "pedestalcooldown", 20, -1, Integer.MAX_VALUE, "Delay between Evertide Amulet trying to start rain while in the pedestal."));

			intBuilder.put("harvestPedCooldown", config.getInt("harvestPedCooldown", "pedestalcooldown", 10, -1, Integer.MAX_VALUE, "Delay between Harvest Goddess trying to grow and harvest while in the pedestal."));

			intBuilder.put("ignitePedCooldown", config.getInt("ignitePedCooldown", "pedestalcooldown", 40, -1, Integer.MAX_VALUE, "Delay between Ignition Ring trying to light entities on fire while in the pedestal."));

			intBuilder.put("lifePedCooldown", config.getInt("lifePedCooldown", "pedestalcooldown", 5, -1, Integer.MAX_VALUE, "Delay between Life Stone healing both food and hunger by 0.5 shank/heart while in the pedestal."));

			intBuilder.put("repairPedCooldown", config.getInt("repairPedCooldown", "pedestalcooldown", 20, -1, Integer.MAX_VALUE, "Delay between Talisman of Repair trying to repair player items while in the pedestal."));

			intBuilder.put("swrgPedCooldown", config.getInt("swrgPedCooldown", "pedestalcooldown", 70, -1, Integer.MAX_VALUE, "Delay between SWRG trying to smite mobs while in the pedestal."));

			intBuilder.put("soulPedCooldown", config.getInt("soulPedCooldown", "pedestalcooldown", 10, -1, Integer.MAX_VALUE, "Delay between Soul Stone healing 0.5 hearts while in the pedestal."));

			intBuilder.put("volcanitePedCooldown", config.getInt("volcanitePedCooldown", "pedestalcooldown", 20, -1, Integer.MAX_VALUE, "Delay between Volcanite Amulet trying to stop rain while in the pedestal."));

			intBuilder.put("zeroPedCooldown", config.getInt("zeroPedCooldown", "pedestalcooldown", 40, -1, Integer.MAX_VALUE, "Delay between Zero Ring trying to extinguish entities and freezing ground while in the pedestal."));


			intBuilder.put("timePedBonus", config.getInt("timePedBonus", "effects", 18, 0, 256, "Bonus ticks given by the Watch of Flowing Time while in the pedestal. 0 = effectively no bonus."));
			floatBuilder.put("timePedMobSlowness", config.getFloat("timePedMobSlowness", "effects", 0.10F, 0.0F, 1.0F, "Factor the Watch of Flowing Time slows down mobs by while in the pedestal. Set to 1.0 for no slowdown."));
			booleanBuilder.put("interdictionMode", config.getBoolean("interdictionMode", "effects", true, "If true the Interdiction Torch only affects hostile mobs. If false it affects all non blacklisted living entities."));

			booleanBuilder.put("pickaxeAoeVeinMining", config.getBoolean("pickaxeAoeVeinMining", "items", false, "Instead of vein mining the ore you right click with your Dark/Red Matter Pick/Star it vein mines all ores in an AOE around you like it did in ProjectE before version 1.4.4."));
			booleanBuilder.put("harvBandGrass", config.getBoolean("harvBandGrass", "items", false, "Allows the Harvest Goddess Band to passively grow tall grass, flowers, etc, on top of grass blocks."));

			booleanProps = booleanBuilder.build();
			floatProps = floatBuilder.build();
			intProps = intBuilder.build();

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
