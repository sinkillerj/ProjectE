package moze_intel.projecte.config;

import java.util.Collections;
import java.util.List;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;

/**
 * For config options that the server has absolute say over
 */
public final class ServerConfig {

	public final Difficulty difficulty;
	public final Items items;
	public final Effects effects;
	public final Misc misc;
	public final Cooldown cooldown;

	ServerConfig(ForgeConfigSpec.Builder builder) {
		builder.comment("All of the config options in this file are server side and will be synced from server to client. ProjectE uses one \"server\" config file for " +
				"all worlds, for convenience in going from one world to another, but makes it be a \"server\" config file so that forge will automatically sync it when " +
				"we connect to a multiplayer server.")
				.push("server");
		difficulty = new Difficulty(builder);
		items = new Items(builder);
		effects = new Effects(builder);
		misc = new Misc(builder);
		cooldown = new Cooldown(builder);
		builder.pop();
	}

	public static class Difficulty {

		public final BooleanValue craftableTome;
		public final BooleanValue offensiveAbilities;
		public final DoubleValue katarDeathAura;
		public final DoubleValue covalenceLoss;
		public final BooleanValue covalenceLossRounding;

		private Difficulty(ForgeConfigSpec.Builder builder) {
			builder.push("difficulty");
			craftableTome = builder
					.comment("The Tome of Knowledge can be crafted.")
					.define("craftableTome", false);
			offensiveAbilities = builder
					.comment("Set to false to disable Gem Armor offensive abilities (helmet zap and chestplate explosion)")
					.define("offensiveAbilities", false);
			katarDeathAura = builder
					.comment("Amount of damage Katar 'C' key deals")
					.defineInRange("katarDeathAura", 1_000F, 0, Integer.MAX_VALUE);
			covalenceLoss = builder
					.comment("Adjusting this ratio changes how much EMC is received when burning a item. For example setting this to 0.5 will return half of the EMC cost.")
					.defineInRange("covalenceLoss", 1.0, 0.1, 1.0);
			covalenceLossRounding = builder
					.comment("How rounding occurs when Covalence Loss results in a burn value less than 1 EMC. If true the value will be rounded up to 1. If false the value will be rounded down to 0.")
					.define("covalenceLossRounding", true);
			builder.pop();
		}
	}

	public static class Items {

		public final BooleanValue pickaxeAoeVeinMining;
		public final BooleanValue harvBandGrass;
		public final BooleanValue disableAllRadiusMining;
		public final BooleanValue enableTimeWatch;
		public final BooleanValue opEvertide;

		private Items(ForgeConfigSpec.Builder builder) {
			builder.push("items");
			pickaxeAoeVeinMining = builder
					.comment("Instead of vein mining the ore you right click with your Dark/Red Matter Pick/Star it vein mines all ores in an AOE around you like it did in ProjectE before version 1.4.4.")
					.define("pickaxeAoeVeinMining", false);
			harvBandGrass = builder
					.comment("Allows the Harvest Goddess Band to passively grow tall grass, flowers, etc, on top of grass blocks.")
					.define("harvBandGrass", false);
			disableAllRadiusMining = builder
					.comment("If set to true, disables all radius-based mining functionality (right click of tools)")
					.define("disableAllRadiusMining", false);
			enableTimeWatch = builder
					.comment("Enable Watch of Flowing Time")
					.define("enableTimeWatch", true);
			opEvertide = builder
					.comment("Allow the Evertide amulet to place water in dimensions that water evaporates. For example: The Nether.")
					.define("opEvertide", false);
			builder.pop();
		}
	}

	public static class Effects {

		public final IntValue timePedBonus;
		public final DoubleValue timePedMobSlowness;
		public final ConfigValue<List<? extends String>> timeWatchTEBlacklist;
		public final BooleanValue interdictionMode;

		private Effects(ForgeConfigSpec.Builder builder) {
			builder.push("effects");
			timePedBonus = builder
					.comment("Bonus ticks given by the Watch of Flowing Time while in the pedestal. 0 = effectively no bonus.")
					.defineInRange("timePedBonus", 18, 0, 256);
			timePedMobSlowness = builder
					.comment("Factor the Watch of Flowing Time slows down mobs by while in the pedestal. Set to 1.0 for no slowdown.")
					.defineInRange("timePedMobSlowness", 0.10, 0, 1);
			timeWatchTEBlacklist = builder
					.comment("Tile entity ID's that the Watch of Flowing Time should not give extra ticks to.")
					.defineList("timeWatchTEBlacklist", Collections.singletonList("projecte:dm_pedestal"),
							element -> element instanceof String && ResourceLocation.tryCreate((String) element) != null);
			interdictionMode = builder
					.comment("If true the Interdiction Torch only affects hostile mobs. If false it affects all non blacklisted living entities.")
					.define("interdictionMode", true);
			builder.pop();
		}
	}

	public static class Misc {

		public final BooleanValue unsafeKeyBinds;

		private Misc(ForgeConfigSpec.Builder builder) {
			builder.push("misc");
			unsafeKeyBinds = builder
					.comment("False requires your hand be empty for Gem Armor Offensive Abilities to be readied or triggered")
					.define("unsafeKeyBinds", false);
			builder.pop();
		}
	}

	public static class Cooldown {

		public final Pedestal pedestal;
		public final Player player;

		private Cooldown(ForgeConfigSpec.Builder builder) {
			builder.push("cooldown");
			builder.comment("Cooldown (in ticks) for various features in ProjectE. A cooldown of -1 will disable the functionality.",
					"A cooldown of 0 will allow the actions to happen every tick. Use caution as a very low value on features that run automatically could cause TPS issues.")
					.push("cooldown");
			pedestal = new Pedestal(builder);
			player = new Player(builder);
			builder.pop();
		}

		public static class Player {

			public final IntValue projectile;
			public final IntValue gemChest;
			public final IntValue repair;
			public final IntValue heal;
			public final IntValue feed;

			private Player(ForgeConfigSpec.Builder builder) {
				builder.comment("Cooldown for various items in regards to a player.")
						.push("player");
				projectile = builder
						.comment("A cooldown for firing projectiles")
						.defineInRange("projectile", 0, -1, Integer.MAX_VALUE);
				gemChest = builder
						.comment("A cooldown for Gem Chestplate explosion")
						.defineInRange("gemChest", 0, -1, Integer.MAX_VALUE);
				repair = builder
						.comment("Delay between Talisman of Repair trying to repair player items while in a player's inventory.")
						.defineInRange("repair", 20, -1, Integer.MAX_VALUE);
				heal = builder
						.comment("Delay between heal attempts while in a player's inventory. (Soul Stone, Life Stone, Gem Helmet)")
						.defineInRange("heal", 20, -1, Integer.MAX_VALUE);
				feed = builder
						.comment("Delay between feed attempts while in a player's inventory. (Body Stone, Life Stone, Gem Helmet)")
						.defineInRange("feed", 20, -1, Integer.MAX_VALUE);
				builder.pop();
			}
		}

		public static class Pedestal {

			public final IntValue archangel;
			public final IntValue body;
			public final IntValue evertide;
			public final IntValue harvest;
			public final IntValue ignition;
			public final IntValue life;
			public final IntValue repair;
			public final IntValue swrg;
			public final IntValue soul;
			public final IntValue volcanite;
			public final IntValue zero;

			private Pedestal(ForgeConfigSpec.Builder builder) {
				builder.comment("Cooldown for various items within the pedestal.")
						.push("pedestal");
				archangel = builder
						.comment("Delay between Archangel Smite shooting arrows while in the pedestal.")
						.defineInRange("archangel", 40, -1, Integer.MAX_VALUE);
				body = builder
						.comment("Delay between Body Stone healing 0.5 shanks while in the pedestal.")
						.defineInRange("body", 10, -1, Integer.MAX_VALUE);
				evertide = builder
						.comment("Delay between Evertide Amulet trying to start rain while in the pedestal.")
						.defineInRange("evertide", 20, -1, Integer.MAX_VALUE);
				harvest = builder
						.comment("Delay between Harvest Goddess trying to grow and harvest while in the pedestal.")
						.defineInRange("harvest", 10, -1, Integer.MAX_VALUE);
				ignition = builder
						.comment("Delay between Ignition Ring trying to light entities on fire while in the pedestal.")
						.defineInRange("ignition", 40, -1, Integer.MAX_VALUE);
				life = builder
						.comment("Delay between Life Stone healing both food and hunger by 0.5 shank/heart while in the pedestal.")
						.defineInRange("life", 5, -1, Integer.MAX_VALUE);
				repair = builder
						.comment("Delay between Talisman of Repair trying to repair player items while in the pedestal.")
						.defineInRange("repair", 20, -1, Integer.MAX_VALUE);
				swrg = builder
						.comment("Delay between SWRG trying to smite mobs while in the pedestal.")
						.defineInRange("swrg", 70, -1, Integer.MAX_VALUE);
				soul = builder
						.comment("Delay between Soul Stone healing 0.5 hearts while in the pedestal.")
						.defineInRange("soul", 10, -1, Integer.MAX_VALUE);
				volcanite = builder
						.comment("Delay between Volcanite Amulet trying to stop rain while in the pedestal.")
						.defineInRange("volcanite", 20, -1, Integer.MAX_VALUE);
				zero = builder
						.comment("Delay between Zero Ring trying to extinguish entities and freezing ground while in the pedestal.")
						.defineInRange("zero", 40, -1, Integer.MAX_VALUE);
				builder.pop();
			}
		}
	}
}