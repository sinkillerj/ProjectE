package moze_intel.projecte.utils;

import net.minecraft.core.Holder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.clock.ServerClockManager;
import net.minecraft.world.clock.WorldClock;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public final class LevelHelper {

	private LevelHelper() {
	}

	public static void setWeather(@NotNull ServerLevel level, int durationTicks, boolean raining, boolean thundering) {
		MinecraftServer server = level.getServer();
		if (server == null) {
			return;
		}
		server.setWeatherParameters(durationTicks, durationTicks, raining, thundering);
	}

	public static long getDefaultClockTime(@NotNull ServerLevel level) {
		return level.getDefaultClockTime();
	}

	public static void setDefaultClockTime(@NotNull ServerLevel level, long totalTicks) {
		level.dimensionType().defaultClock().ifPresent(clock -> setClockTime(level, clock, totalTicks));
	}

	public static void addDefaultClockTicks(@NotNull ServerLevel level, long ticks) {
		setDefaultClockTime(level, Math.addExact(getDefaultClockTime(level), ticks));
	}

	private static void setClockTime(@NotNull ServerLevel level, Holder<WorldClock> clock, long totalTicks) {
		if (level.clockManager() instanceof ServerClockManager clockManager) {
			clockManager.setTotalTicks(clock, totalTicks);
		}
	}

	@NotNull
	public static LightningBolt createLightning(@NotNull ServerLevel level, @NotNull Vec3 pos) {
		LightningBolt lightning = EntityTypes.LIGHTNING_BOLT.create(level, EntitySpawnReason.TRIGGERED);
		if (lightning == null) {
			throw new IllegalStateException("Failed to create lightning bolt");
		}
		lightning.setPos(pos.x, pos.y, pos.z);
		return lightning;
	}
}
