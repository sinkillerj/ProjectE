package moze_intel.projecte.gameObjs.entity;

import javax.annotation.Nonnull;
import moze_intel.projecte.gameObjs.items.ItemPE;
import moze_intel.projecte.gameObjs.registries.PEEntityTypes;
import moze_intel.projecte.gameObjs.registries.PEItems;
import moze_intel.projecte.utils.PlayerHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.storage.IServerWorldInfo;
import net.minecraftforge.fml.network.NetworkHooks;

public class EntitySWRGProjectile extends ThrowableEntity {

	private boolean fromArcana = false;

	public EntitySWRGProjectile(EntityType<EntitySWRGProjectile> type, World world) {
		super(type, world);
	}

	public EntitySWRGProjectile(PlayerEntity player, boolean fromArcana, World world) {
		super(PEEntityTypes.SWRG_PROJECTILE.get(), player, world);
		this.fromArcana = fromArcana;
	}

	@Override
	protected void defineSynchedData() {
	}

	@Override
	public void tick() {
		super.tick();
		if (!level.isClientSide && tickCount > 400) {
			remove();
			return;
		}

		// Undo the 0.99 (0.8 in water) drag applied in superclass
		double inverse = 1D / (isInWater() ? 0.8D : 0.99D);
		this.setDeltaMovement(this.getDeltaMovement().scale(inverse));
		if (!level.isClientSide && isAlive() && getY() > level.getMaxBuildHeight() && level.isRaining()) {
			if (level.getLevelData() instanceof IServerWorldInfo) {
				((IServerWorldInfo) level.getLevelData()).setThundering(true);
			}
			remove();
		}
	}

	@Override
	public float getGravity() {
		return 0;
	}

	@Override
	protected void onHit(@Nonnull RayTraceResult mop) {
		if (level.isClientSide) {
			return;
		}
		Entity thrower = getOwner();
		if (!(thrower instanceof PlayerEntity)) {
			remove();
			return;
		}
		PlayerEntity player = (PlayerEntity) thrower;
		ItemStack found = PlayerHelper.findFirstItem(player, fromArcana ? PEItems.ARCANA_RING.get() : PEItems.SWIFTWOLF_RENDING_GALE.get());
		if (mop instanceof BlockRayTraceResult) {
			if (!found.isEmpty() && ItemPE.consumeFuel(player, found, 768, true)) {
				BlockPos pos = ((BlockRayTraceResult) mop).getBlockPos();

				LightningBoltEntity lightning = EntityType.LIGHTNING_BOLT.create(level);
				if (lightning != null) {
					lightning.moveTo(Vector3d.atCenterOf(pos));
					lightning.setCause((ServerPlayerEntity) player);
					level.addFreshEntity(lightning);
				}

				if (level.isThundering()) {
					for (int i = 0; i < 3; i++) {
						LightningBoltEntity bonus = EntityType.LIGHTNING_BOLT.create(level);
						if (bonus != null) {
							bonus.moveTo(pos.getX() + 0.5 + level.random.nextGaussian(), pos.getY() + 0.5 + level.random.nextGaussian(), pos.getZ() + 0.5 + level.random.nextGaussian());
							bonus.setCause((ServerPlayerEntity) player);
							level.addFreshEntity(bonus);
						}
					}
				}
			}
		} else if (mop instanceof EntityRayTraceResult) {
			if (((EntityRayTraceResult) mop).getEntity() instanceof LivingEntity && !found.isEmpty() && ItemPE.consumeFuel(player, found, 64, true)) {
				LivingEntity e = (LivingEntity) ((EntityRayTraceResult) mop).getEntity();
				// Minor damage so we count as the attacker for launching the mob
				e.hurt(DamageSource.playerAttack(player), 1F);

				// Fake onGround before knockBack so you can re-launch mobs that have already been launched
				boolean oldOnGround = e.isOnGround();
				e.setOnGround(true);
				e.knockback(5F, -getDeltaMovement().x() * 0.25, -getDeltaMovement().z() * 0.25);
				e.setOnGround(oldOnGround);
				e.setDeltaMovement(e.getDeltaMovement().multiply(1, 3, 1));
			}
		}
		remove();
	}

	@Override
	public void readAdditionalSaveData(@Nonnull CompoundNBT compound) {
		super.readAdditionalSaveData(compound);
		fromArcana = compound.getBoolean("fromArcana");
	}

	@Override
	public void addAdditionalSaveData(@Nonnull CompoundNBT compound) {
		super.addAdditionalSaveData(compound);
		compound.putBoolean("fromArcana", fromArcana);
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