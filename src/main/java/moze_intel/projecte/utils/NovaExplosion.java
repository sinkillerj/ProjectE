package moze_intel.projecte.utils;

import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.EntityBasedExplosionDamageCalculator;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerExplosion;
import net.minecraft.world.level.SimpleExplosionDamageCalculator;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;
import org.jetbrains.annotations.Nullable;

/**
 * Helper for creating nova-style explosions using the 26.1 {@link ServerExplosion} API.
 */
public final class NovaExplosion {

	private NovaExplosion() {
	}

	public static Holder<SoundEvent> defaultExplosionSound() {
		return SoundEvents.GENERIC_EXPLODE;
	}

	public static @Nullable ServerExplosion create(
			ServerLevel level,
			@Nullable Entity entity,
			double x,
			double y,
			double z,
			float radius,
			Explosion.BlockInteraction mode
	) {
		Vec3 center = new Vec3(x, y, z);
		ExplosionDamageCalculator calculator = entity == null
				? new SimpleExplosionDamageCalculator(true, true, Optional.empty(), Optional.empty())
				: new EntityBasedExplosionDamageCalculator(entity);
		DamageSource damageSource = entity == null
				? level.damageSources().generic()
				: entity instanceof LivingEntity living ? level.damageSources().mobAttack(living) : level.damageSources().generic();
		ServerExplosion explosion = new ServerExplosion(level, entity, damageSource, calculator, center, radius, false, mode);
		return EventHooks.onExplosionStart(level, explosion) ? null : explosion;
	}

	public static boolean explode(Level level, @Nullable Entity entity, double x, double y, double z, float radius, Explosion.BlockInteraction mode) {
		if (!(level instanceof ServerLevel serverLevel)) {
			return false;
		}
		ServerExplosion explosion = create(serverLevel, entity, x, y, z, radius, mode);
		if (explosion == null) {
			return false;
		}
		explosion.explode();
		return true;
	}
}
