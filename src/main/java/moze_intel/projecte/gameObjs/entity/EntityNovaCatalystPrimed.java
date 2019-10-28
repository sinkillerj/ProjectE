package moze_intel.projecte.gameObjs.entity;

import javax.annotation.Nonnull;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.world.World;

public class EntityNovaCatalystPrimed extends TNTEntity {

	public EntityNovaCatalystPrimed(EntityType<EntityNovaCatalystPrimed> type, World world) {
		super(type, world);
		setFuse(20);
	}

	public EntityNovaCatalystPrimed(World world, double x, double y, double z, LivingEntity placer) {
		super(world, x, y, z, placer);
		setFuse(20);
	}

	@Nonnull
	@Override
	public EntityType<?> getType() {
		return ObjHandler.NOVA_CATALYST_PRIMED;
	}

	// [VanillaCopy] super need exact override to do our own explosion
	@Override
	public void tick() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		if (!this.hasNoGravity()) {
			this.setMotion(this.getMotion().add(0.0D, -0.04D, 0.0D));
		}

		this.move(MoverType.SELF, this.getMotion());
		this.setMotion(this.getMotion().scale(0.98D));
		if (this.onGround) {
			this.setMotion(this.getMotion().mul(0.7D, -0.5D, 0.7D));
		}

		this.setFuse(this.getFuse() - 1);
		if (this.getFuse() <= 0) {
			this.remove();
			if (!this.world.isRemote) {
				this.explode();
			}
		} else {
			this.handleWaterMovement();
			this.world.addParticle(ParticleTypes.SMOKE, this.posX, this.posY + 0.5D, this.posZ, 0.0D, 0.0D, 0.0D);
		}
	}

	private void explode() {
		WorldHelper.createNovaExplosion(world, this, posX, posY, posZ, 16.0F);
	}
}