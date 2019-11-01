package moze_intel.projecte.gameObjs.entity;

import javax.annotation.Nonnull;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.items.ItemPE;
import moze_intel.projecte.utils.PlayerHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkHooks;

public class EntitySWRGProjectile extends ThrowableEntity {

	private boolean fromArcana = false;

	public EntitySWRGProjectile(EntityType<EntitySWRGProjectile> type, World world) {
		super(type, world);
	}

	public EntitySWRGProjectile(PlayerEntity player, boolean fromArcana, World world) {
		super(ObjHandler.SWRG_PROJECTILE, player, world);
		this.fromArcana = fromArcana;
	}

	@Override
	protected void registerData() {
	}

	@Override
	public void tick() {
		super.tick();

		if (!world.isRemote && ticksExisted > 400) {
			remove();
			return;
		}

		// Undo the 0.99 (0.8 in water) drag applied in superclass
		double inverse = 1D / (isInWater() ? 0.8D : 0.99D);
		this.setMotion(this.getMotion().scale(inverse));

		if (!world.isRemote && isAlive() && posY > world.getHeight() && world.isRaining()) {
			world.getWorldInfo().setThundering(true);
			remove();
		}
	}

	@Override
	public float getGravityVelocity() {
		return 0;
	}

	@Override
	protected void onImpact(@Nonnull RayTraceResult mop) {
		if (world.isRemote) {
			return;
		}

		if (!(getThrower() instanceof PlayerEntity)) {
			remove();
			return;
		}

		PlayerEntity player = ((PlayerEntity) getThrower());
		ItemStack found = PlayerHelper.findFirstItem(player, fromArcana ? ObjHandler.arcana : ObjHandler.swrg);

		if (mop instanceof BlockRayTraceResult) {
			if (!found.isEmpty() && ItemPE.consumeFuel(player, found, 768, true)) {
				BlockPos pos = ((BlockRayTraceResult) mop).getPos();

				LightningBoltEntity lightning = new LightningBoltEntity(world, pos.getX(), pos.getY(), pos.getZ(), false);
				((ServerWorld) world).addLightningBolt(lightning);

				if (world.isThundering()) {
					for (int i = 0; i < 3; i++) {
						LightningBoltEntity bonus = new LightningBoltEntity(world, pos.getX() + world.rand.nextGaussian(), pos.getY() + world.rand.nextGaussian(), pos.getZ() + world.rand.nextGaussian(), false);
						((ServerWorld) world).addLightningBolt(bonus);
					}
				}
			}
		} else if (mop instanceof EntityRayTraceResult) {
			if (((EntityRayTraceResult) mop).getEntity() instanceof LivingEntity && !found.isEmpty() && ItemPE.consumeFuel(player, found, 64, true)) {
				LivingEntity e = (LivingEntity) ((EntityRayTraceResult) mop).getEntity();

				// Minor damage so we count as the attacker for launching the mob
				e.attackEntityFrom(DamageSource.causePlayerDamage(player), 1F);

				// Fake onGround before knockBack so you can re-launch mobs that have already been launched
				boolean oldOnGround = e.onGround;
				e.onGround = true;
				e.knockBack(player, 5F, -getMotion().getX() * 0.25, -getMotion().getZ() * 0.25);
				e.onGround = oldOnGround;
				e.setMotion(e.getMotion().mul(1, 3, 1));
			}
		}
		remove();
	}

	@Override
	public void readAdditional(CompoundNBT compound) {
		super.readAdditional(compound);
		fromArcana = compound.getBoolean("fromArcana");
	}

	@Override
	public void writeAdditional(CompoundNBT compound) {
		super.writeAdditional(compound);
		compound.putBoolean("fromArcana", fromArcana);
	}

	@Nonnull
	@Override
	public IPacket<?> createSpawnPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}
}