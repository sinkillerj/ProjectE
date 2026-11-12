package moze_intel.projecte.gameObjs.entity;

import net.minecraft.SharedConstants;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

public abstract class NoGravityThrowableProjectile extends ThrowableProjectile {

	protected NoGravityThrowableProjectile(EntityType<? extends ThrowableProjectile> type, Level level) {
		super(type, level);
		setNoGravity(true);
	}

	protected NoGravityThrowableProjectile(EntityType<? extends ThrowableProjectile> type, LivingEntity shooter, Level level) {
		this(type, level);
		setOwner(shooter);
	}

	@Override
	protected void defineSynchedData(@NotNull SynchedEntityData.Builder builder) {
	}

	@Override
	public boolean ignoreExplosion(@NotNull Explosion explosion) {
		return true;
	}

	@Override
	public void tick() {
		super.tick();
		if (!this.level().isClientSide()) {
			if (tickCount > (20 * SharedConstants.TICKS_PER_SECOND) || !level().isLoaded(blockPosition())) {
				discard();
			}
		}
	}

	@Override
	protected void onHit(@NotNull HitResult result) {
		super.onHit(result);
		if (!level().isClientSide()) {
			level().broadcastEntityEvent(this, EntityEvent.DEATH);
			discard();
		}
	}
}
