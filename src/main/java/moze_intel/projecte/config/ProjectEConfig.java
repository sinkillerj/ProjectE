package moze_intel.projecte.config;

import moze_intel.projecte.PECore;
import net.minecraftforge.common.config.Config;

@Config(modid = PECore.MODID, name = PECore.MODNAME + "/" + PECore.MODNAME)
public final class ProjectEConfig 
{
	public static final Difficulty difficulty = new Difficulty();
	public static class Difficulty
	{
		@Config.Comment("The Tome of Knowledge can be crafted.")
		public boolean craftableTome = false;

		@Config.Comment("Set to false to disable Gem Armor offensive abilities (helmet zap and chestplate explosion)")
		public boolean offensiveAbilities = true;

		@Config.RangeDouble(min = 0, max = Integer.MAX_VALUE)
		@Config.Comment("Amount of damage Katar 'C' key deals")
		public float katarDeathAura = 1000F;

		@Config.RangeDouble(min = 0.1, max = 1.0)
		@Config.Comment("Adjusting this ratio changes how much EMC is received when burning a item. For example setting this to 0.5 will return half of the EMC cost.")
		public double covalenceLoss = 1.0;
	}

	public static Items items = new Items();
	public static class Items
	{
		@Config.Comment("Instead of vein mining the ore you right click with your Dark/Red Matter Pick/Star it vein mines all ores in an AOE around you like it did in ProjectE before version 1.4.4.")
		public boolean pickaxeAoeVeinMining = false;

		@Config.Comment("Allows the Harvest Goddess Band to passively grow tall grass, flowers, etc, on top of grass blocks.")
		public boolean harvBandGrass = false;

		@Config.Comment("If set to true, disables all radius-based mining functionaliy (right click of tools)")
		public boolean disableAllRadiusMining = false;

		@Config.Comment("Enable Watch of Flowing Time")
		public boolean enableTimeWatch = true;
	}

	public static final Effects effects = new Effects();
	public static class Effects {
		@Config.RangeInt(min = 0, max = 256)
		@Config.Comment("Bonus ticks given by the Watch of Flowing Time while in the pedestal. 0 = effectively no bonus.")
		public int timePedBonus = 18;

		@Config.RangeDouble(min = 0, max = 1)
		@Config.Comment("Factor the Watch of Flowing Time slows down mobs by while in the pedestal. Set to 1.0 for no slowdown.")
		public float timePedMobSlowness = 0.10F;

		// TODO 1.13 move to tags
		@Config.Comment("Block ID's that the Watch of Flowing Time should not give extra random ticks to")
		public String[] timeWatchBlockBlacklist = {};

		@Config.Comment("Tile entity ID's that the Watch of Flowing Time should not give extra ticks to.")
		public String[] timeWatchTEBlacklist = {
			"projecte:dm_pedestal"
		};

		@Config.Comment("If true the Interdiction Torch only affects hostile mobs. If false it affects all non blacklisted living entities.")
		public boolean interdictionMode = true;
	}

	public static Misc misc = new Misc();
	public static class Misc {
		@Config.Comment("Enable a more verbose debug logging")
		public boolean debugLogging = false;

		@Config.Comment("Show item Ore Dictionary names in tooltips (useful for custom EMC registration)")
		public boolean odToolTips = false;

		@Config.Comment("Show the EMC value as a tooltip on items and blocks")
		public boolean emcToolTips = true;

		@Config.Comment("Show stats as tooltips for various ProjectE blocks")
		public boolean statToolTips = true;

		@Config.Comment("Show DM pedestal functions in item tooltips")
		public boolean pedestalToolTips = true;

		@Config.Comment("The Philosopher's Stone overlay softly pulsates")
		public boolean pulsatingOverlay = false;

		@Config.Comment("False requires your hand be empty for Gem Armor Offensive Abilities to be readied or triggered")
		public boolean unsafeKeyBinds = false;

		@Config.RangeInt(min = 0)
		@Config.Comment("A cooldown (in ticks) for firing projectiles")
		public int projectileCooldown = 0;

		@Config.RangeInt(min = 0)
		@Config.Comment("A cooldown (in ticks) for Gem Chestplate explosion")
		public int gemChestCooldown = 0;
	}

	@Config.Comment({"Cooldown for various items within the pedestal. A cooldown of -1 will disable the functionality.",
					 "A cooldown of 0 will cause the actions to happen every tick. Use caution as a very low value could cause TPS issues."})
	public static final PedestalCooldown pedestalCooldown = new PedestalCooldown();
	public static class PedestalCooldown {
		@Config.RangeInt(min = -1)
		@Config.Comment("Delay between Archangel Smite shooting arrows while in the pedestal.")
		public int archangelPedCooldown = 40;

		@Config.RangeInt(min = -1)
		@Config.Comment("Delay between Body Stone healing 0.5 shanks while in the pedestal.")
		public int bodyPedCooldown = 10;

		@Config.RangeInt(min = -1)
		@Config.Comment("Delay between Evertide Amulet trying to start rain while in the pedestal.")
		public int evertidePedCooldown = 20;

		@Config.RangeInt(min = -1)
		@Config.Comment("Delay between Harvest Goddess trying to grow and harvest while in the pedestal.")
		public int harvestPedCooldown = 10;

		@Config.RangeInt(min = -1)
		@Config.Comment("Delay between Ignition Ring trying to light entities on fire while in the pedestal.")
		public int ignitePedCooldown = 40;

		@Config.RangeInt(min = -1)
		@Config.Comment("Delay between Life Stone healing both food and hunger by 0.5 shank/heart while in the pedestal.")
		public int lifePedCooldown = 5;

		@Config.RangeInt(min = -1)
		@Config.Comment("Delay between Talisman of Repair trying to repair player items while in the pedestal.")
		public int repairPedCooldown = 20;

		@Config.RangeInt(min = -1)
		@Config.Comment("Delay between SWRG trying to smite mobs while in the pedestal.")
		public int swrgPedCooldown = 70;

		@Config.RangeInt(min = -1)
		@Config.Comment("Delay between Soul Stone healing 0.5 hearts while in the pedestal.")
		public int soulPedCooldown = 10;

		@Config.RangeInt(min = -1)
		@Config.Comment("Delay between Volcanite Amulet trying to stop rain while in the pedestal.")
		public int volcanitePedCooldown = 20;

		@Config.RangeInt(min = -1)
		@Config.Comment("Delay between Zero Ring trying to extinguish entities and freezing ground while in the pedestal.")
		public int zeroPedCooldown = 40;
	}
}
