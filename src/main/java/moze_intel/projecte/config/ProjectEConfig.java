package moze_intel.projecte.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import moze_intel.projecte.PECore;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ForgeConfigSpec;

import java.nio.file.Paths;
import java.util.*;

public final class ProjectEConfig
{
	private static final ForgeConfigSpec SPEC = new ForgeConfigSpec.Builder()
			.push("difficulty")
			.comment("The Tome of Knowledge can be crafted.")
			.define("craftableTome", false)

			.comment("Set to false to disable Gem Armor offensive abilities (helmet zap and chestplate explosion)")
			.define("offensiveAbilities", false)

			.comment("Amount of damage Katar 'C' key deals")
			.defineInRange("katarDeathAura", 1000F, 0, Integer.MAX_VALUE)

			.comment("Adjusting this ratio changes how much EMC is received when burning a item. For example setting this to 0.5 will return half of the EMC cost.")
			.defineInRange("covalenceLoss", 1.0, 0.1, 1.0)
			.pop()

			.push("items")
			.comment("Instead of vein mining the ore you right click with your Dark/Red Matter Pick/Star it vein mines all ores in an AOE around you like it did in ProjectE before version 1.4.4.")
			.define("pickaxeAoeVeinMining", false)

			.comment("Allows the Harvest Goddess Band to passively grow tall grass, flowers, etc, on top of grass blocks.")
			.define("harvBandGrass", false)

			.comment("If set to true, disables all radius-based mining functionality (right click of tools)")
			.define("disableAllRadiusMining", false)

			.comment("Enable Watch of Flowing Time")
			.define("enableTimeWatch", true)
			.pop()

			.push("effects")
			.comment("Bonus ticks given by the Watch of Flowing Time while in the pedestal. 0 = effectively no bonus.")
			.defineInRange("timePedBonus", 18, 0, 256)

			.comment("Factor the Watch of Flowing Time slows down mobs by while in the pedestal. Set to 1.0 for no slowdown.")
			.defineInRange("timePedMobSlowness", 0.10, 0, 1)

			.comment("Tile entity ID's that the Watch of Flowing Time should not give extra ticks to.")
			.define("timeWatchTEBlacklist", Collections.singletonList("projecte:dm_pedestal"))

			.comment("If true the Interdiction Torch only affects hostile mobs. If false it affects all non blacklisted living entities.")
			.define("interdictionMode", true)
			.pop()

			.push("misc")
			.comment("Enable a more verbose debug logging")
			.define("debugLogging", false)

			.comment("Show item tags in tooltips (useful for custom EMC registration)")
			.define("tagToolTips", false)

			.comment("Show the EMC value as a tooltip on items and blocks")
			.define("emcToolTips", true)

			.comment("Show stats as tooltips for various ProjectE blocks")
			.define("statToolTips", true)

			.comment("Show DM pedestal functions in item tooltips")
			.define("pedestalToolTips", true)

			.comment("The Philosopher's Stone overlay softly pulsates")
			.define("pulsatingOverlay", false)

			.comment("False requires your hand be empty for Gem Armor Offensive Abilities to be readied or triggered")
			.define("unsafeKeyBinds", false)

			.comment("A cooldown (in ticks) for firing projectiles")
			.defineInRange("projectileCooldown", 0, 0, Integer.MAX_VALUE)

			.comment("A cooldown (in ticks) for Gem Chestplate explosion")
			.defineInRange("gemChestCooldown", 0, 0, Integer.MAX_VALUE)
			.pop()

			.comment("Cooldown for various items within the pedestal. A cooldown of -1 will disable the functionality.",
					"A cooldown of 0 will cause the actions to happen every tick. Use caution as a very low value could cause TPS issues.")
			.push("pedestalCooldown")
			.comment("Delay between Archangel Smite shooting arrows while in the pedestal.")
			.defineInRange("archangel", 40, -1, Integer.MAX_VALUE)

			.comment("Delay between Body Stone healing 0.5 shanks while in the pedestal.")
			.defineInRange("body", 10, -1, Integer.MAX_VALUE)

			.comment("Delay between Evertide Amulet trying to start rain while in the pedestal.")
			.defineInRange("evertide", 20, -1, Integer.MAX_VALUE)

			.comment("Delay between Harvest Goddess trying to grow and harvest while in the pedestal.")
			.defineInRange("harvest", 10, -1, Integer.MAX_VALUE)

			.comment("Delay between Ignition Ring trying to light entities on fire while in the pedestal.")
			.defineInRange("ignition", 40, -1, Integer.MAX_VALUE)

			.comment("Delay between Life Stone healing both food and hunger by 0.5 shank/heart while in the pedestal.")
			.defineInRange("life", 5, -1, Integer.MAX_VALUE)

			.comment("Delay between Talisman of Repair trying to repair player items while in the pedestal.")
			.defineInRange("repair", 20, -1, Integer.MAX_VALUE)

			.comment("Delay between SWRG trying to smite mobs while in the pedestal.")
			.defineInRange("swrg", 70, -1, Integer.MAX_VALUE)

			.comment("Delay between Soul Stone healing 0.5 hearts while in the pedestal.")
			.defineInRange("soul", 10, -1, Integer.MAX_VALUE)

			.comment("Delay between Volcanite Amulet trying to stop rain while in the pedestal.")
			.defineInRange("volcanite", 20, -1, Integer.MAX_VALUE)

			.comment("Delay between Zero Ring trying to extinguish entities and freezing ground while in the pedestal.")
			.defineInRange("zero", 40, -1, Integer.MAX_VALUE)
			.pop()

			.build();

	public static void load()
	{
		config.load();

		if (!SPEC.isCorrect(config)) {
			SPEC.correct(config);
			config.save();
		}

		config.close();

		difficulty = new Difficulty();
		items = new Items();
		effects = new Effects();
		misc = new Misc();
		pedestalCooldown = new PedestalCooldown();
	}

	public static final CommentedFileConfig config = CommentedFileConfig.builder(Paths.get("config", PECore.MODNAME, PECore.MODID + ".toml"))
			.writingMode(WritingMode.REPLACE)
			.build();

	public static Difficulty difficulty;
	public static class Difficulty
	{
		public final boolean craftableTome = config.get("difficulty.craftableTome");
		public final boolean offensiveAbilities = config.get("difficulty.offensiveAbilities");
		public final float katarDeathAura = config.<Double>get("difficulty.katarDeathAura").floatValue();
		public final double covalenceLoss = config.get("difficulty.covalenceLoss");
	}

	public static Items items;
	public static class Items
	{
		public final boolean pickaxeAoeVeinMining = config.get("items.pickaxeAoeVeinMining");
		public final boolean harvBandGrass = config.get("items.harvBandGrass");
		public final boolean disableAllRadiusMining = config.get("items.disableAllRadiusMining");
		public final boolean enableTimeWatch = config.get("items.enableTimeWatch");
	}

	public static Effects effects;
	public static class Effects {
		public final int timePedBonus = config.get("effects.timePedBonus");
		public final double timePedMobSlowness = config.get("effects.timePedMobSlowness");
		public final Set<String> timeWatchTEBlacklist = new HashSet<>(config.<List<String>>get("effects.timeWatchTEBlacklist"));
		public final boolean interdictionMode = config.get("effects.interdictionMode");
	}

	public static Misc misc;
	public static class Misc {
		public final boolean debugLogging = config.get("misc.debugLogging");
		public final boolean tagToolTips = config.get("misc.tagToolTips");
		public final boolean emcToolTips = config.get("misc.emcToolTips");
		public final boolean statToolTips = config.get("misc.statToolTips");
		public final boolean pedestalToolTips = config.get("misc.pedestalToolTips");
		public final boolean pulsatingOverlay = config.get("misc.pulsatingOverlay");
		public final boolean unsafeKeyBinds = config.get("misc.unsafeKeyBinds");
		public final int projectileCooldown = config.get("misc.projectileCooldown");
		public final int gemChestCooldown = config.get("misc.gemChestCooldown");
	}

	public static PedestalCooldown pedestalCooldown;
	public static class PedestalCooldown {
		public final int archangel = config.get("pedestalCooldown.archangel");
		public final int body = config.get("pedestalCooldown.body");
		public final int evertide = config.get("pedestalCooldown.evertide");
		public final int harvest = config.get("pedestalCooldown.harvest");
		public final int ignition = config.get("pedestalCooldown.ignition");
		public final int life = config.get("pedestalCooldown.life");
		public final int repair = config.get("pedestalCooldown.repair");
		public final int swrg = config.get("pedestalCooldown.swrg");
		public final int soul = config.get("pedestalCooldown.soul");
		public final int volcanite = config.get("pedestalCooldown.volcanite");
		public final int zero = config.get("pedestalCooldown.zero");
	}
}
