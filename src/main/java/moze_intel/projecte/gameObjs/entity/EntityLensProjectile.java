package moze_intel.projecte.gameObjs.entity;

import javax.annotation.Nonnull;
import moze_intel.projecte.gameObjs.registries.PEEntityTypes;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkHooks;

public class EntityLensProjectile extends ThrowableEntity {

	private int charge;

	public EntityLensProjectile(EntityType<EntityLensProjectile> type, World world) {
		super(type, world);
	}

	public EntityLensProjectile(PlayerEntity entity, int charge, World world) {
		super(PEEntityTypes.LENS_PROJECTILE.get(), entity, world);
		this.charge = charge;
	}

	@Override
	protected void defineSynchedData() {
	}

	@Override
	public void tick() {
		super.tick();
		if (getCommandSenderWorld().isClientSide) {
			return;
		}
		if (tickCount > 400 || !getCommandSenderWorld().isLoaded(blockPosition())) {
			remove();
			return;
		}
		if (isInWater()) {
			playSound(SoundEvents.GENERIC_BURN, 0.7F, 1.6F + (random.nextFloat() - random.nextFloat()) * 0.4F);
			((ServerWorld) level).sendParticles(ParticleTypes.LARGE_SMOKE, getX(), getY(), getZ(), 2, 0, 0, 0, 0);
			remove();
		}
	}

	@Override
	protected void onHit(@Nonnull RayTraceResult mop) {
		if (!this.getCommandSenderWorld().isClientSide) {
			WorldHelper.createNovaExplosion(level, getOwner(), getX(), getY(), getZ(), Constants.EXPLOSIVE_LENS_RADIUS[charge]);
			remove();
		}
	}

	@Override
	public float getGravity() {
		return 0;
	}

	@Override
	public void addAdditionalSaveData(@Nonnull CompoundNBT nbt) {
		super.addAdditionalSaveData(nbt);
		nbt.putInt("Charge", charge);
	}

	@Override
	public void readAdditionalSaveData(@Nonnull CompoundNBT nbt) {
		super.readAdditionalSaveData(nbt);
		charge = nbt.getInt("Charge");
	}

	@Nonnull
	@Override
	public IPacket<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	public boolean ignoreExplosion() {
		return true;
	}
}