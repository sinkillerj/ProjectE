package moze_intel.projecte.gameObjs.entity;

import javax.annotation.Nonnull;
import moze_intel.projecte.gameObjs.ObjHandler;
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
		super(ObjHandler.LENS_PROJECTILE, entity, world);
		this.charge = charge;
	}

	@Override
	protected void registerData() {
	}

	@Override
	public void tick() {
		super.tick();
		if (getEntityWorld().isRemote) {
			return;
		}
		if (ticksExisted > 400 || !getEntityWorld().isBlockPresent(getPosition())) {
			remove();
			return;
		}
		if (isInWater()) {
			playSound(SoundEvents.ENTITY_GENERIC_BURN, 0.7F, 1.6F + (rand.nextFloat() - rand.nextFloat()) * 0.4F);
			((ServerWorld) world).spawnParticle(ParticleTypes.LARGE_SMOKE, getPosX(), getPosY(), getPosZ(), 2, 0, 0, 0, 0);
			remove();
		}
	}

	@Override
	protected void onImpact(@Nonnull RayTraceResult mop) {
		if (!this.getEntityWorld().isRemote) {
			WorldHelper.createNovaExplosion(world, getThrower(), getPosX(), getPosY(), getPosZ(), Constants.EXPLOSIVE_LENS_RADIUS[charge]);
			remove();
		}
	}

	@Override
	public float getGravityVelocity() {
		return 0;
	}

	@Override
	public void writeAdditional(CompoundNBT nbt) {
		super.writeAdditional(nbt);
		nbt.putInt("Charge", charge);
	}

	@Override
	public void readAdditional(CompoundNBT nbt) {
		super.readAdditional(nbt);
		charge = nbt.getInt("Charge");
	}

	@Nonnull
	@Override
	public IPacket<?> createSpawnPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}
}