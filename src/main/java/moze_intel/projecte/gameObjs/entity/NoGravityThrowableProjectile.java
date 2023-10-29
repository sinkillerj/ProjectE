package moze_intel.projecte.gameObjs.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public abstract class NoGravityThrowableProjectile extends ThrowableProjectile {

	protected NoGravityThrowableProjectile(EntityType<? extends ThrowableProjectile> type, Level level) {
		super(type, level);
	}

	protected NoGravityThrowableProjectile(EntityType<? extends ThrowableProjectile> type, LivingEntity shooter, Level level) {
		super(type, shooter, level);
	}

	@Override
	public float getGravity() {
		return 0;
	}

	@Override
	public void tick() {
		super.tick();
		if (!this.level().isClientSide) {
			if (tickCount > 400 || getDeltaMovement().equals(Vec3.ZERO) || !level().isLoaded(blockPosition())) {
				discard();
			}
		}
	}
}