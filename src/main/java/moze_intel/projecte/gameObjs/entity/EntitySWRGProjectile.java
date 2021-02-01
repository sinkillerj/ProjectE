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
		if (!world.isRemote && isAlive() && getPosY() > world.getHeight() && world.isRaining()) {
			if (world.getWorldInfo() instanceof IServerWorldInfo) {
				((IServerWorldInfo) world.getWorldInfo()).setThundering(true);
			}
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
		Entity thrower = func_234616_v_();
		if (!(thrower instanceof PlayerEntity)) {
			remove();
			return;
		}
		PlayerEntity player = (PlayerEntity) thrower;
		ItemStack found = PlayerHelper.findFirstItem(player, fromArcana ? PEItems.ARCANA_RING.get() : PEItems.SWIFTWOLF_RENDING_GALE.get());
		if (mop instanceof BlockRayTraceResult) {
			if (!found.isEmpty() && ItemPE.consumeFuel(player, found, 768, true)) {
				BlockPos pos = ((BlockRayTraceResult) mop).getPos();

				LightningBoltEntity lightning = EntityType.LIGHTNING_BOLT.create(world);
				if (lightning != null) {
					lightning.moveForced(Vector3d.copyCentered(pos));
					lightning.setCaster((ServerPlayerEntity) player);
					world.addEntity(lightning);
				}

				if (world.isThundering()) {
					for (int i = 0; i < 3; i++) {
						LightningBoltEntity bonus = EntityType.LIGHTNING_BOLT.create(world);
						if (bonus != null) {
							bonus.moveForced(pos.getX() + 0.5 + world.rand.nextGaussian(), pos.getY() + 0.5 + world.rand.nextGaussian(), pos.getZ() + 0.5 + world.rand.nextGaussian());
							bonus.setCaster((ServerPlayerEntity) player);
							world.addEntity(bonus);
						}
					}
				}
			}
		} else if (mop instanceof EntityRayTraceResult) {
			if (((EntityRayTraceResult) mop).getEntity() instanceof LivingEntity && !found.isEmpty() && ItemPE.consumeFuel(player, found, 64, true)) {
				LivingEntity e = (LivingEntity) ((EntityRayTraceResult) mop).getEntity();
				// Minor damage so we count as the attacker for launching the mob
				e.attackEntityFrom(DamageSource.causePlayerDamage(player), 1F);

				// Fake onGround before knockBack so you can re-launch mobs that have already been launched
				boolean oldOnGround = e.isOnGround();
				e.setOnGround(true);
				e.applyKnockback(5F, -getMotion().getX() * 0.25, -getMotion().getZ() * 0.25);
				e.setOnGround(oldOnGround);
				e.setMotion(e.getMotion().mul(1, 3, 1));
			}
		}
		remove();
	}

	@Override
	public void readAdditional(@Nonnull CompoundNBT compound) {
		super.readAdditional(compound);
		fromArcana = compound.getBoolean("fromArcana");
	}

	@Override
	public void writeAdditional(@Nonnull CompoundNBT compound) {
		super.writeAdditional(compound);
		compound.putBoolean("fromArcana", fromArcana);
	}

	@Nonnull
	@Override
	public IPacket<?> createSpawnPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	public boolean isImmuneToExplosions() {
		return true;
	}
}