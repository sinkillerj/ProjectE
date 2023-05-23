package moze_intel.projecte.config;

import moze_intel.projecte.config.value.CachedBooleanValue;
import moze_intel.projecte.config.value.CachedDoubleValue;
import moze_intel.projecte.config.value.CachedFloatValue;
import moze_intel.projecte.config.value.CachedIntValue;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

/**
 * For config options that the server has absolute say over
 */
public final class ServerConfig extends BasePEConfig {

  private final ForgeConfigSpec configSpec;

  public final Difficulty difficulty;
  public final Items items;
  public final Effects effects;
  public final Misc misc;
  public final Cooldown cooldown;

  ServerConfig() {
    ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
    builder.comment(
        "All of the config options in this file are server side and will be synced from server to client. ProjectE uses one \"server\" config file for "
            +
            "all worlds, for convenience in going from one world to another, but makes it be a \"server\" config file so that forge will automatically sync it when "
            +
            "we connect to a multiplayer server.")
        .push("server");
    difficulty = new Difficulty(this, builder);
    items = new Items(this, builder);
    effects = new Effects(this, builder);
    misc = new Misc(this, builder);
    cooldown = new Cooldown(this, builder);
    builder.pop();
    configSpec = builder.build();
  }

  @Override
  public String getFileName() {
    return "server";
  }

  @Override
  public ForgeConfigSpec getConfigSpec() {
    return configSpec;
  }

  @Override
  public ModConfig.Type getConfigType() {
    return ModConfig.Type.SERVER;
  }

  public static class Difficulty {

    public final CachedBooleanValue offensiveAbilities;
    public final CachedFloatValue katarDeathAura;
    public final CachedDoubleValue covalenceLoss;
    public final CachedBooleanValue covalenceLossRounding;

    private Difficulty(IPEConfig config, ForgeConfigSpec.Builder builder) {
      builder.push("difficulty");
      offensiveAbilities = CachedBooleanValue.wrap(config, builder
          .comment("Set to false to disable Gem Armor offensive abilities (helmet zap and chestplate explosion)")
          .define("offensiveAbilities", false));
      katarDeathAura = CachedFloatValue.wrap(config, builder
          .comment("Amount of damage Katar 'C' key deals")
          .defineInRange("katarDeathAura", 1_000F, 0, Integer.MAX_VALUE));
      covalenceLoss = CachedDoubleValue.wrap(config, builder
          .comment(
              "This ratio multiplies the EMC received when burning a item. Ex: setting this to 0.5 will return half of the EMC cost. Be careful with values greater than 1.0.")
          .defineInRange("covalenceLoss", 1.0, 0.0, Double.MAX_VALUE));
      covalenceLossRounding = CachedBooleanValue.wrap(config, builder
          .comment(
              "How rounding occurs when Covalence Loss results in a burn value less than 1 EMC. If true the value will be rounded up to 1. If false the value will be rounded down to 0.")
          .define("covalenceLossRounding", true));
      builder.pop();
    }
  }

  public static class Items {

    public final CachedBooleanValue pickaxeAoeVeinMining;
    public final CachedBooleanValue harvBandGrass;
    public final CachedBooleanValue disableAllRadiusMining;
    public final CachedBooleanValue enableTimeWatch;
    public final CachedBooleanValue opEvertide;

    private Items(IPEConfig config, ForgeConfigSpec.Builder builder) {
      builder.push("items");
      pickaxeAoeVeinMining = CachedBooleanValue.wrap(config, builder
          .comment(
              "Instead of vein mining the ore you right click with your Dark/Red Matter Pick/Star it vein mines all ores in an AOE around you like it did in ProjectE before version 1.4.4.")
          .define("pickaxeAoeVeinMining", false));
      harvBandGrass = CachedBooleanValue.wrap(config, builder
          .comment(
              "Allows the Harvest Goddess Band to passively grow tall grass, flowers, etc, on top of grass blocks.")
          .define("harvBandGrass", false));
      disableAllRadiusMining = CachedBooleanValue.wrap(config, builder
          .comment("If set to true, disables all radius-based mining functionality (right click of tools)")
          .define("disableAllRadiusMining", false));
      enableTimeWatch = CachedBooleanValue.wrap(config, builder
          .comment("Enable Watch of Flowing Time")
          .define("enableTimeWatch", true));
      opEvertide = CachedBooleanValue.wrap(config, builder
          .comment(
              "Allow the Evertide amulet to place water in dimensions that water evaporates. For example: The Nether.")
          .define("opEvertide", false));
      builder.pop();
    }
  }

  public static class Effects {

    public final CachedIntValue timePedBonus;
    public final CachedDoubleValue timePedMobSlowness;
    public final CachedBooleanValue interdictionMode;

    private Effects(IPEConfig config, ForgeConfigSpec.Builder builder) {
      builder.push("effects");
      timePedBonus = CachedIntValue.wrap(config, builder
          .comment("Bonus ticks given by the Watch of Flowing Time while in the pedestal. 0 = effectively no bonus.")
          .defineInRange("timePedBonus", 18, 0, 256));
      timePedMobSlowness = CachedDoubleValue.wrap(config, builder
          .comment(
              "Factor the Watch of Flowing Time slows down mobs by while in the pedestal. Set to 1.0 for no slowdown.")
          .defineInRange("timePedMobSlowness", 0.10, 0, 1));
      interdictionMode = CachedBooleanValue.wrap(config, builder
          .comment(
              "If true the Interdiction Torch only affects hostile mobs and projectiles. If false it affects all non blacklisted living entities.")
          .define("interdictionMode", true));
      builder.pop();
    }
  }

  public static class Misc {

    public final CachedBooleanValue unsafeKeyBinds;
    public final CachedBooleanValue hwylaTOPDisplay;

    private Misc(IPEConfig config, ForgeConfigSpec.Builder builder) {
      builder.push("misc");
      unsafeKeyBinds = CachedBooleanValue.wrap(config, builder
          .comment("False requires your hand be empty for Gem Armor Offensive Abilities to be readied or triggered")
          .define("unsafeKeyBinds", false));
      hwylaTOPDisplay = CachedBooleanValue.wrap(config, builder
          .comment("Shows the EMC value of blocks when looking at them in Hwyla or TOP")
          .define("hwylaTOPDisplay", true));
      builder.pop();
    }
  }

  public static class Cooldown {

    public final Pedestal pedestal;
    public final Player player;

    private Cooldown(IPEConfig config, ForgeConfigSpec.Builder builder) {
      builder.push("cooldown");
      builder.comment(
          "Cooldown (in ticks) for various features in ProjectE. A cooldown of -1 will disable the functionality.",
          "A cooldown of 0 will allow the actions to happen every tick. Use caution as a very low value on features that run automatically could cause TPS issues.")
          .push("cooldown");
      pedestal = new Pedestal(config, builder);
      player = new Player(config, builder);
      builder.pop();
    }

    public static class Player {

      public final CachedIntValue projectile;
      public final CachedIntValue gemChest;
      public final CachedIntValue repair;
      public final CachedIntValue heal;
      public final CachedIntValue feed;

      private Player(IPEConfig config, ForgeConfigSpec.Builder builder) {
        builder.comment("Cooldown for various items in regards to a player.")
            .push("player");
        projectile = CachedIntValue.wrap(config, builder
            .comment("A cooldown for firing projectiles")
            .defineInRange("projectile", 0, -1, Integer.MAX_VALUE));
        gemChest = CachedIntValue.wrap(config, builder
            .comment("A cooldown for Gem Chestplate explosion")
            .defineInRange("gemChest", 0, -1, Integer.MAX_VALUE));
        repair = CachedIntValue.wrap(config, builder
            .comment("Delay between Talisman of Repair trying to repair player items while in a player's inventory.")
            .defineInRange("repair", 20, -1, Integer.MAX_VALUE));
        heal = CachedIntValue.wrap(config, builder
            .comment("Delay between heal attempts while in a player's inventory. (Soul Stone, Life Stone, Gem Helmet)")
            .defineInRange("heal", 20, -1, Integer.MAX_VALUE));
        feed = CachedIntValue.wrap(config, builder
            .comment("Delay between feed attempts while in a player's inventory. (Body Stone, Life Stone, Gem Helmet)")
            .defineInRange("feed", 20, -1, Integer.MAX_VALUE));
        builder.pop();
      }
    }

    public static class Pedestal {

      public final CachedIntValue archangel;
      public final CachedIntValue body;
      public final CachedIntValue evertide;
      public final CachedIntValue harvest;
      public final CachedIntValue ignition;
      public final CachedIntValue life;
      public final CachedIntValue repair;
      public final CachedIntValue swrg;
      public final CachedIntValue soul;
      public final CachedIntValue volcanite;
      public final CachedIntValue zero;

      private Pedestal(IPEConfig config, ForgeConfigSpec.Builder builder) {
        builder.comment("Cooldown for various items within the pedestal.")
            .push("pedestal");
        archangel = CachedIntValue.wrap(config, builder
            .comment("Delay between Archangel Smite shooting arrows while in the pedestal.")
            .defineInRange("archangel", 40, -1, Integer.MAX_VALUE));
        body = CachedIntValue.wrap(config, builder
            .comment("Delay between Body Stone healing 0.5 shanks while in the pedestal.")
            .defineInRange("body", 10, -1, Integer.MAX_VALUE));
        evertide = CachedIntValue.wrap(config, builder
            .comment("Delay between Evertide Amulet trying to start rain while in the pedestal.")
            .defineInRange("evertide", 20, -1, Integer.MAX_VALUE));
        harvest = CachedIntValue.wrap(config, builder
            .comment("Delay between Harvest Goddess trying to grow and harvest while in the pedestal.")
            .defineInRange("harvest", 10, -1, Integer.MAX_VALUE));
        ignition = CachedIntValue.wrap(config, builder
            .comment("Delay between Ignition Ring trying to light entities on fire while in the pedestal.")
            .defineInRange("ignition", 40, -1, Integer.MAX_VALUE));
        life = CachedIntValue.wrap(config, builder
            .comment("Delay between Life Stone healing both food and hunger by 0.5 shank/heart while in the pedestal.")
            .defineInRange("life", 5, -1, Integer.MAX_VALUE));
        repair = CachedIntValue.wrap(config, builder
            .comment("Delay between Talisman of Repair trying to repair player items while in the pedestal.")
            .defineInRange("repair", 20, -1, Integer.MAX_VALUE));
        swrg = CachedIntValue.wrap(config, builder
            .comment("Delay between SWRG trying to smite mobs while in the pedestal.")
            .defineInRange("swrg", 70, -1, Integer.MAX_VALUE));
        soul = CachedIntValue.wrap(config, builder
            .comment("Delay between Soul Stone healing 0.5 hearts while in the pedestal.")
            .defineInRange("soul", 10, -1, Integer.MAX_VALUE));
        volcanite = CachedIntValue.wrap(config, builder
            .comment("Delay between Volcanite Amulet trying to stop rain while in the pedestal.")
            .defineInRange("volcanite", 20, -1, Integer.MAX_VALUE));
        zero = CachedIntValue.wrap(config, builder
            .comment("Delay between Zero Ring trying to extinguish entities and freezing ground while in the pedestal.")
            .defineInRange("zero", 40, -1, Integer.MAX_VALUE));
        builder.pop();
      }
    }
  }
}