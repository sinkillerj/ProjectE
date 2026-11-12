package moze_intel.projecte.config;

import moze_intel.projecte.config.value.CachedBooleanValue;
import moze_intel.projecte.config.value.CachedDoubleValue;
import moze_intel.projecte.config.value.CachedFloatValue;
import moze_intel.projecte.config.value.CachedIntValue;
import net.minecraft.SharedConstants;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * For config options that the server has absolute say over
 */
public final class ServerConfig extends BasePEConfig {

	private final ModConfigSpec configSpec;

	public final Difficulty difficulty;
	public final Items items;
	public final Effects effects;
	public final Misc misc;
	public final Cooldown cooldown;

	ServerConfig() {
		ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
		cooldown = new Cooldown(this, builder);
		difficulty = new Difficulty(this, builder);
		effects = new Effects(this, builder);
		items = new Items(this, builder);
		misc = new Misc(this, builder);
		configSpec = builder.build();
	}

	@Override
	public String getFileName() {
		return "server";
	}

	@Override
	public String getTranslation() {
		return "Server Config";
	}

	@Override
	public ModConfigSpec getConfigSpec() {
		return configSpec;
	}

	@Override
	public ModConfig.Type getConfigType() {
		return ModConfig.Type.SYNCED;
	}

	public static class Cooldown {

		public final Pedestal pedestal;
		public final Player player;

		private Cooldown(IPEConfig config, ModConfigSpec.Builder builder) {
			PEConfigTranslations.SERVER_COOLDOWN.applyToBuilder(builder).push("cooldown");
			pedestal = new Pedestal(config, builder);
			player = new Player(config, builder);
			builder.pop();
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

			private Pedestal(IPEConfig config, ModConfigSpec.Builder builder) {
				PEConfigTranslations.SERVER_COOLDOWN_PEDESTAL.applyToBuilder(builder).push("pedestal");
				archangel = CachedIntValue.wrap(config, PEConfigTranslations.SERVER_COOLDOWN_PEDESTAL_ARCHANGEL.applyToBuilder(builder)
						.defineInRange("archangel", 2 * SharedConstants.TICKS_PER_SECOND, -1, Integer.MAX_VALUE));
				body = CachedIntValue.wrap(config, PEConfigTranslations.SERVER_COOLDOWN_PEDESTAL_BODY_STONE.applyToBuilder(builder)
						.defineInRange("body", SharedConstants.TICKS_PER_SECOND / 2, -1, Integer.MAX_VALUE));
				evertide = CachedIntValue.wrap(config, PEConfigTranslations.SERVER_COOLDOWN_PEDESTAL_EVERTIDE.applyToBuilder(builder)
						.defineInRange("evertide", SharedConstants.TICKS_PER_SECOND, -1, Integer.MAX_VALUE));
				harvest = CachedIntValue.wrap(config, PEConfigTranslations.SERVER_COOLDOWN_PEDESTAL_HARVEST.applyToBuilder(builder)
						.defineInRange("harvest", SharedConstants.TICKS_PER_SECOND / 2, -1, Integer.MAX_VALUE));
				ignition = CachedIntValue.wrap(config, PEConfigTranslations.SERVER_COOLDOWN_PEDESTAL_IGNITION.applyToBuilder(builder)
						.defineInRange("ignition", 2 * SharedConstants.TICKS_PER_SECOND, -1, Integer.MAX_VALUE));
				life = CachedIntValue.wrap(config, PEConfigTranslations.SERVER_COOLDOWN_PEDESTAL_LIFE_STONE.applyToBuilder(builder)
						.defineInRange("life", SharedConstants.TICKS_PER_SECOND / 4, -1, Integer.MAX_VALUE));
				repair = CachedIntValue.wrap(config, PEConfigTranslations.SERVER_COOLDOWN_PEDESTAL_REPAIR.applyToBuilder(builder)
						.defineInRange("repair", SharedConstants.TICKS_PER_SECOND, -1, Integer.MAX_VALUE));
				swrg = CachedIntValue.wrap(config, PEConfigTranslations.SERVER_COOLDOWN_PEDESTAL_SWRG.applyToBuilder(builder)
						.defineInRange("swrg", (int) (3.5 * SharedConstants.TICKS_PER_SECOND), -1, Integer.MAX_VALUE));
				soul = CachedIntValue.wrap(config, PEConfigTranslations.SERVER_COOLDOWN_PEDESTAL_SOUL_STONE.applyToBuilder(builder)
						.defineInRange("soul", SharedConstants.TICKS_PER_SECOND / 2, -1, Integer.MAX_VALUE));
				volcanite = CachedIntValue.wrap(config, PEConfigTranslations.SERVER_COOLDOWN_PEDESTAL_VOLCANITE.applyToBuilder(builder)
						.defineInRange("volcanite", SharedConstants.TICKS_PER_SECOND, -1, Integer.MAX_VALUE));
				zero = CachedIntValue.wrap(config, PEConfigTranslations.SERVER_COOLDOWN_PEDESTAL_ZERO.applyToBuilder(builder)
						.defineInRange("zero", 2 * SharedConstants.TICKS_PER_SECOND, -1, Integer.MAX_VALUE));
				builder.pop();
			}
		}

		public static class Player {

			public final CachedIntValue projectile;
			public final CachedIntValue gemChest;
			public final CachedIntValue repair;
			public final CachedIntValue heal;
			public final CachedIntValue feed;

			private Player(IPEConfig config, ModConfigSpec.Builder builder) {
				PEConfigTranslations.SERVER_COOLDOWN_PLAYER.applyToBuilder(builder).push("player");
				projectile = CachedIntValue.wrap(config, PEConfigTranslations.SERVER_COOLDOWN_PLAYER_PROJECTILE.applyToBuilder(builder)
						.defineInRange("projectile", 0, -1, Integer.MAX_VALUE));
				gemChest = CachedIntValue.wrap(config, PEConfigTranslations.SERVER_COOLDOWN_PLAYER_GEM_CHESTPLATE.applyToBuilder(builder)
						.defineInRange("gemChest", 0, -1, Integer.MAX_VALUE));
				repair = CachedIntValue.wrap(config, PEConfigTranslations.SERVER_COOLDOWN_PLAYER_REPAIR.applyToBuilder(builder)
						.defineInRange("repair", SharedConstants.TICKS_PER_SECOND, -1, Integer.MAX_VALUE));
				heal = CachedIntValue.wrap(config, PEConfigTranslations.SERVER_COOLDOWN_PLAYER_HEAL.applyToBuilder(builder)
						.defineInRange("heal", SharedConstants.TICKS_PER_SECOND, -1, Integer.MAX_VALUE));
				feed = CachedIntValue.wrap(config, PEConfigTranslations.SERVER_COOLDOWN_PLAYER_FEED.applyToBuilder(builder)
						.defineInRange("feed", SharedConstants.TICKS_PER_SECOND, -1, Integer.MAX_VALUE));
				builder.pop();
			}
		}
	}

	public static class Difficulty {

		public final CachedBooleanValue offensiveAbilities;
		public final CachedFloatValue katarDeathAura;
		public final CachedDoubleValue covalenceLoss;
		public final CachedBooleanValue covalenceLossRounding;

		private Difficulty(IPEConfig config, ModConfigSpec.Builder builder) {
			PEConfigTranslations.SERVER_DIFFICULTY.applyToBuilder(builder).push("difficulty");
			offensiveAbilities = CachedBooleanValue.wrap(config, PEConfigTranslations.SERVER_DIFFICULTY_OFFENSIVE_ABILITIES.applyToBuilder(builder)
					.define("offensiveAbilities", false));
			katarDeathAura = CachedFloatValue.wrap(config, PEConfigTranslations.SERVER_DIFFICULTY_KATAR_DEATH_AURA.applyToBuilder(builder)
					.defineInRange("katarDeathAura", 1_000F, 0, Integer.MAX_VALUE));
			covalenceLoss = CachedDoubleValue.wrap(config, PEConfigTranslations.SERVER_DIFFICULTY_COVALENCE_LOSS.applyToBuilder(builder)
					.defineInRange("covalenceLoss", 1.0, 0.1, 1.0));
			covalenceLossRounding = CachedBooleanValue.wrap(config, PEConfigTranslations.SERVER_DIFFICULTY_COVALENCE_LOSS_ROUNDING.applyToBuilder(builder)
					.define("covalenceLossRounding", true));
			builder.pop();
		}
	}

	public static class Effects {

		public final CachedIntValue timePedBonus;
		public final CachedDoubleValue timePedMobSlowness;
		public final CachedBooleanValue interdictionMode;

		private Effects(IPEConfig config, ModConfigSpec.Builder builder) {
			PEConfigTranslations.SERVER_EFFECTS.applyToBuilder(builder).push("effects");
			timePedBonus = CachedIntValue.wrap(config, PEConfigTranslations.SERVER_EFFECTS_TIME_PEDESTAL_BONUS.applyToBuilder(builder)
					.defineInRange("timePedBonus", 18, 0, 256));
			timePedMobSlowness = CachedDoubleValue.wrap(config, PEConfigTranslations.SERVER_EFFECTS_TIME_PEDESTAL_MOB_SLOWNESS.applyToBuilder(builder)
					.defineInRange("timePedMobSlowness", 0.10, 0, 1));
			interdictionMode = CachedBooleanValue.wrap(config, PEConfigTranslations.SERVER_EFFECTS_INTERDICTION_MODE.applyToBuilder(builder)
					.define("interdictionMode", true));
			builder.pop();
		}
	}

	public static class Items {

		public final CachedBooleanValue pickaxeAoeVeinMining;
		public final CachedBooleanValue harvBandIndirect;
		public final CachedBooleanValue disableAllRadiusMining;
		public final CachedBooleanValue enableTimeWatch;
		public final CachedBooleanValue opEvertide;

		private Items(IPEConfig config, ModConfigSpec.Builder builder) {
			PEConfigTranslations.SERVER_ITEMS.applyToBuilder(builder).push("items");
			pickaxeAoeVeinMining = CachedBooleanValue.wrap(config, PEConfigTranslations.SERVER_ITEMS_PICKAXE_AOE_VEIN_MINING.applyToBuilder(builder)
					.define("pickaxeAoeVeinMining", false));
			harvBandIndirect = CachedBooleanValue.wrap(config, PEConfigTranslations.SERVER_ITEMS_HARVEST_BAND_INDIRECT.applyToBuilder(builder)
					.define("harvBandIndirect", false));
			disableAllRadiusMining = CachedBooleanValue.wrap(config, PEConfigTranslations.SERVER_ITEMS_DISABLE_ALL_RADIUS_MINING.applyToBuilder(builder)
					.define("disableAllRadiusMining", false));
			enableTimeWatch = CachedBooleanValue.wrap(config, PEConfigTranslations.SERVER_ITEMS_TIME_WATCH.applyToBuilder(builder)
					.define("enableTimeWatch", true));
			opEvertide = CachedBooleanValue.wrap(config, PEConfigTranslations.SERVER_ITEMS_OP_EVERTIDE.applyToBuilder(builder)
					.define("opEvertide", false));
			builder.pop();
		}
	}

	public static class Misc {

		public final CachedBooleanValue unsafeKeyBinds;
		public final CachedBooleanValue lookingAtDisplay;

		private Misc(IPEConfig config, ModConfigSpec.Builder builder) {
			PEConfigTranslations.SERVER_MISC.applyToBuilder(builder).push("misc");
			unsafeKeyBinds = CachedBooleanValue.wrap(config, PEConfigTranslations.SERVER_MISC_UNSAFE_KEY_BINDS.applyToBuilder(builder)
					.define("unsafeKeyBinds", false));
			lookingAtDisplay = CachedBooleanValue.wrap(config, PEConfigTranslations.SERVER_MISC_LOOKING_AT_DISPLAY.applyToBuilder(builder)
					.define("lookingAtDisplay", true));
			builder.pop();
		}
	}
}
