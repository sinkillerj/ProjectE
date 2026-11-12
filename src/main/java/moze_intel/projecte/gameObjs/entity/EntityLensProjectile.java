package moze_intel.projecte.gameObjs.entity;

import moze_intel.projecte.gameObjs.items.HyperkineticLens.ExplosiveLensCharge;
import moze_intel.projecte.gameObjs.registries.PEEntityTypes;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

public class EntityLensProjectile extends NoGravityThrowableProjectile {

	private ExplosiveLensCharge charge;

	public EntityLensProjectile(EntityType<EntityLensProjectile> type, Level level) {
		super(type, level);
		charge = ExplosiveLensCharge.UNCHARGED;
	}

	public EntityLensProjectile(Player entity, ExplosiveLensCharge charge) {
		super(PEEntityTypes.LENS_PROJECTILE.get(), entity, entity.level());
		this.charge = charge;
	}

	@Override
	protected void onInsideBlock(@NotNull BlockState state) {
		if (!level().isClientSide() && state.getFluidState().is(FluidTags.WATER)) {
			playSound(SoundEvents.GENERIC_BURN, 0.7F, 1.6F + (random.nextFloat() - random.nextFloat()) * 0.4F);
			((ServerLevel) level()).sendParticles(ParticleTypes.LARGE_SMOKE, getX(), getY(), getZ(), 2, 0, 0, 0, 0);
			discard();
		}
	}

	@Override
	protected void onHit(@NotNull HitResult result) {
		super.onHit(result);
		if (!level().isClientSide()) {
			WorldHelper.createNovaExplosion(level(), getOwner(), getX(), getY(), getZ(), charge.radius());
		}
	}

	@Override
	protected void addAdditionalSaveData(@NotNull ValueOutput output) {
		super.addAdditionalSaveData(output);
		output.putInt("charge", charge.ordinal());
	}

	@Override
	protected void readAdditionalSaveData(@NotNull ValueInput input) {
		super.readAdditionalSaveData(input);
		charge = ExplosiveLensCharge.BY_ID.apply(input.getIntOr("charge", 0));
	}
}
