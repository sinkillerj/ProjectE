package moze_intel.projecte.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfigBuilder;
import moze_intel.projecte.PECore;
import net.minecraftforge.common.ForgeConfigSpec;

import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

public final class ProjectEConfig
{
	public static void load()
	{
		SPEC.setConfig(CommentedFileConfig
				.builder(Paths.get("config", PECore.MODNAME, PECore.MODID + ".toml"))
				.build());
	}

	private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

	public static final Difficulty difficulty = new Difficulty();
	public static class Difficulty
	{
		public final ForgeConfigSpec.BooleanValue craftableTome;
		public final ForgeConfigSpec.BooleanValue offensiveAbilities;
		public final ForgeConfigSpec.DoubleValue katarDeathAura;
		public final ForgeConfigSpec.DoubleValue covalenceLoss;
		public final ForgeConfigSpec.BooleanValue covalenceLossRounding;
		Difficulty()
		{
			BUILDER.push("difficulty");
			craftableTome = BUILDER
				.comment("The Tome of Knowledge can be crafted.")
				.define("craftableTome", false);
			offensiveAbilities = BUILDER
				.comment("Set to false to disable Gem Armor offensive abilities (helmet zap and chestplate explosion)")
				.define("offensiveAbilities", false);
			katarDeathAura = BUILDER
				.comment("Amount of damage Katar 'C' key deals")
				.defineInRange("katarDeathAura", 1000F, 0, Integer.MAX_VALUE);
			covalenceLoss = BUILDER
				.comment("Adjusting this ratio changes how much EMC is received when burning a item. For example setting this to 0.5 will return half of the EMC cost.")
				.defineInRange("covalenceLoss", 1.0, 0.1, 1.0);
			covalenceLossRounding = BUILDER
				.comment("How rounding occurs when Covalence Loss results in a burn value less than 1 EMC. If true the value will be rounded up to 1. If false the value will be rounded down to 0.")
				.define("covalenceLossRounding", true);
			BUILDER.pop();
		}
	}

	public static final Items items = new Items();
	public static class Items
	{
		public final ForgeConfigSpec.BooleanValue pickaxeAoeVeinMining;
		public final ForgeConfigSpec.BooleanValue harvBandGrass;
		public final ForgeConfigSpec.BooleanValue disableAllRadiusMining;
		public final ForgeConfigSpec.BooleanValue enableTimeWatch;
		Items()
		{
			BUILDER.push("items");
			pickaxeAoeVeinMining = BUILDER
				.comment("Instead of vein mining the ore you right click with your Dark/Red Matter Pick/Star it vein mines all ores in an AOE around you like it did in ProjectE before version 1.4.4.")
				.define("pickaxeAoeVeinMining", false);
			harvBandGrass = BUILDER
				.comment("Allows the Harvest Goddess Band to passively grow tall grass, flowers, etc, on top of grass blocks.")
				.define("harvBandGrass", false);
			disableAllRadiusMining = BUILDER
				.comment("If set to true, disables all radius-based mining functionality (right click of tools)")
				.define("disableAllRadiusMining", false);
			enableTimeWatch = BUILDER
				.comment("Enable Watch of Flowing Time")
				.define("enableTimeWatch", true);
			BUILDER.pop();
		}
	}

	public static final Effects effects = new Effects();
	public static class Effects {
		public final ForgeConfigSpec.IntValue timePedBonus;
		public final ForgeConfigSpec.DoubleValue timePedMobSlowness;
		public final ForgeConfigSpec.ConfigValue<List<String>> timeWatchTEBlacklist;
		public final ForgeConfigSpec.BooleanValue interdictionMode;
		Effects()
		{
			BUILDER.push("effects");
			timePedBonus = BUILDER
				.comment("Bonus ticks given by the Watch of Flowing Time while in the pedestal. 0 = effectively no bonus.")
				.defineInRange("timePedBonus", 18, 0, 256);
			timePedMobSlowness = BUILDER
				.comment("Factor the Watch of Flowing Time slows down mobs by while in the pedestal. Set to 1.0 for no slowdown.")
				.defineInRange("timePedMobSlowness", 0.10, 0, 1);
			timeWatchTEBlacklist = BUILDER
				.comment("Tile entity ID's that the Watch of Flowing Time should not give extra ticks to.")
				.define("timeWatchTEBlacklist", Collections.singletonList("projecte:dm_pedestal"));
			interdictionMode = BUILDER
				.comment("If true the Interdiction Torch only affects hostile mobs. If false it affects all non blacklisted living entities.")
				.define("interdictionMode", true);
			BUILDER.pop();
		}
	}

	public static final Misc misc = new Misc();
	public static class Misc {
		public final ForgeConfigSpec.BooleanValue debugLogging;
		public final ForgeConfigSpec.BooleanValue tagToolTips;
		public final ForgeConfigSpec.BooleanValue emcToolTips;
		public final ForgeConfigSpec.BooleanValue statToolTips;
		public final ForgeConfigSpec.BooleanValue pedestalToolTips;
		public final ForgeConfigSpec.BooleanValue pulsatingOverlay;
		public final ForgeConfigSpec.BooleanValue unsafeKeyBinds;
		public final ForgeConfigSpec.IntValue projectileCooldown;
		public final ForgeConfigSpec.IntValue gemChestCooldown;
		Misc()
		{
			BUILDER.push("misc");
			debugLogging = BUILDER
				.comment("Enable a more verbose debug logging")
				.define("debugLogging", false);
			tagToolTips = BUILDER
				.comment("Show item tags in tooltips (useful for custom EMC registration)")
				.define("tagToolTips", false);
			emcToolTips = BUILDER
				.comment("Show the EMC value as a tooltip on items and blocks")
				.define("emcToolTips", true);
			statToolTips = BUILDER
				.comment("Show stats as tooltips for various ProjectE blocks")
				.define("statToolTips", true);
			pedestalToolTips = BUILDER
				.comment("Show DM pedestal functions in item tooltips")
				.define("pedestalToolTips", true);
			pulsatingOverlay = BUILDER
				.comment("The Philosopher's Stone overlay softly pulsates")
				.define("pulsatingOverlay", false);
			unsafeKeyBinds = BUILDER
				.comment("False requires your hand be empty for Gem Armor Offensive Abilities to be readied or triggered")
				.define("unsafeKeyBinds", false);
			projectileCooldown = BUILDER
				.comment("A cooldown (in ticks) for firing projectiles")
				.defineInRange("projectileCooldown", 0, 0, Integer.MAX_VALUE);
			gemChestCooldown = BUILDER
				.comment("A cooldown (in ticks) for Gem Chestplate explosion")
				.defineInRange("gemChestCooldown", 0, 0, Integer.MAX_VALUE);
			BUILDER.pop();
		}
	}

	public static final PedestalCooldown pedestalCooldown = new PedestalCooldown();
	public static class PedestalCooldown {
		public final ForgeConfigSpec.IntValue archangel;
		public final ForgeConfigSpec.IntValue body;
		public final ForgeConfigSpec.IntValue evertide;
		public final ForgeConfigSpec.IntValue harvest;
		public final ForgeConfigSpec.IntValue ignition;
		public final ForgeConfigSpec.IntValue life;
		public final ForgeConfigSpec.IntValue repair;
		public final ForgeConfigSpec.IntValue swrg;
		public final ForgeConfigSpec.IntValue soul;
		public final ForgeConfigSpec.IntValue volcanite;
		public final ForgeConfigSpec.IntValue zero;
		PedestalCooldown()
		{
			BUILDER.comment("Cooldown for various items within the pedestal. A cooldown of -1 will disable the functionality.",
					"A cooldown of 0 will cause the actions to happen every tick. Use caution as a very low value could cause TPS issues.")
					.push("pedestalCooldown");
			archangel = BUILDER
				.comment("Delay between Archangel Smite shooting arrows while in the pedestal.")
				.defineInRange("archangel", 40, -1, Integer.MAX_VALUE);
			body = BUILDER
				.comment("Delay between Body Stone healing 0.5 shanks while in the pedestal.")
				.defineInRange("body", 10, -1, Integer.MAX_VALUE);
			evertide = BUILDER
				.comment("Delay between Evertide Amulet trying to start rain while in the pedestal.")
				.defineInRange("evertide", 20, -1, Integer.MAX_VALUE);
			harvest = BUILDER
				.comment("Delay between Harvest Goddess trying to grow and harvest while in the pedestal.")
				.defineInRange("harvest", 10, -1, Integer.MAX_VALUE);
			ignition = BUILDER
				.comment("Delay between Ignition Ring trying to light entities on fire while in the pedestal.")
				.defineInRange("ignition", 40, -1, Integer.MAX_VALUE);
			life = BUILDER
				.comment("Delay between Life Stone healing both food and hunger by 0.5 shank/heart while in the pedestal.")
				.defineInRange("life", 5, -1, Integer.MAX_VALUE);
			repair = BUILDER
				.comment("Delay between Talisman of Repair trying to repair player items while in the pedestal.")
				.defineInRange("repair", 20, -1, Integer.MAX_VALUE);
			swrg = BUILDER
				.comment("Delay between SWRG trying to smite mobs while in the pedestal.")
				.defineInRange("swrg", 70, -1, Integer.MAX_VALUE);
			soul = BUILDER
				.comment("Delay between Soul Stone healing 0.5 hearts while in the pedestal.")
				.defineInRange("soul", 10, -1, Integer.MAX_VALUE);
			volcanite = BUILDER
				.comment("Delay between Volcanite Amulet trying to stop rain while in the pedestal.")
				.defineInRange("volcanite", 20, -1, Integer.MAX_VALUE);
			zero = BUILDER
				.comment("Delay between Zero Ring trying to extinguish entities and freezing ground while in the pedestal.")
				.defineInRange("zero", 40, -1, Integer.MAX_VALUE);
			BUILDER.pop();
		}
	}

	private static final ForgeConfigSpec SPEC = BUILDER.build();
}
