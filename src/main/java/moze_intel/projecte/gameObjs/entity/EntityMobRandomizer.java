package moze_intel.projecte.gameObjs.entity;

import javax.annotation.Nonnull;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.EntityRandomizerHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IRendersAsItem;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityMobRandomizer extends ThrowableEntity implements IRendersAsItem {

	public EntityMobRandomizer(EntityType<EntityMobRandomizer> type, World world) {
		super(type, world);
	}

	public EntityMobRandomizer(PlayerEntity entity, World world) {
		super(ObjHandler.MOB_RANDOMIZER, entity, world);
	}

	@Override
	protected void registerData() {
	}

	@Override
	public void tick() {
		super.tick();

		if (!this.getEntityWorld().isRemote) {
			if (ticksExisted > 400 || this.isInWater() || !this.getEntityWorld().isBlockLoaded(new BlockPos(this))) {
				this.remove();
			}
		}
	}

	@Override
	public float getGravityVelocity() {
		return 0;
	}

	@Override
	protected void onImpact(@Nonnull RayTraceResult mop) {
		if (this.getEntityWorld().isRemote) {
			for (int i = 0; i < 4; ++i) {
				this.getEntityWorld().addParticle(ParticleTypes.PORTAL, this.posX, this.posY + this.rand.nextDouble() * 2.0D, this.posZ, this.rand.nextGaussian(), 0.0D, this.rand.nextGaussian());
			}
			return;
		}

		if (this.isInWater() || !(mop instanceof EntityRayTraceResult)
			|| !(((EntityRayTraceResult) mop).getEntity() instanceof MobEntity) || !(getThrower() instanceof PlayerEntity)) {
			remove();
			return;
		}

		MobEntity ent = ((MobEntity) ((EntityRayTraceResult) mop).getEntity());
		MobEntity randomized = EntityRandomizerHelper.getRandomEntity(this.getEntityWorld(), ent);

		if (randomized != null && EMCHelper.consumePlayerFuel(((PlayerEntity) getThrower()), 384) != -1) {
			ent.remove();
			randomized.setLocationAndAngles(ent.posX, ent.posY, ent.posZ, ent.rotationYaw, ent.rotationPitch);
			randomized.onInitialSpawn(world, world.getDifficultyForLocation(new BlockPos(randomized)), SpawnReason.CONVERSION, null, null);
			this.getEntityWorld().addEntity(randomized);
			randomized.spawnExplosionParticle();
		}
		remove();
	}

	@Nonnull
	@Override
	public ItemStack getItem() {
		return new ItemStack(ObjHandler.mobRandomizer);
	}
}