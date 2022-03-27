package moze_intel.projecte.gameObjs.entity;

import moze_intel.projecte.gameObjs.items.ItemPE;
import moze_intel.projecte.gameObjs.registries.PEEntityTypes;
import moze_intel.projecte.gameObjs.registries.PEItems;
import moze_intel.projecte.utils.PlayerHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

public class EntitySWRGProjectile extends ThrowableProjectile {

	private boolean fromArcana = false;

	public EntitySWRGProjectile(EntityType<EntitySWRGProjectile> type, Level level) {
		super(type, level);
	}

	public EntitySWRGProjectile(Player player, boolean fromArcana, Level level) {
		super(PEEntityTypes.SWRG_PROJECTILE.get(), player, level);
		this.fromArcana = fromArcana;
	}

	@Override
	protected void defineSynchedData() {
	}

	@Override
	public void tick() {
		super.tick();
		if (!level.isClientSide && tickCount > 400) {
			discard();
			return;
		}

		// Undo the 0.99 (0.8 in water) drag applied in superclass
		double inverse = 1D / (isInWater() ? 0.8D : 0.99D);
		this.setDeltaMovement(this.getDeltaMovement().scale(inverse));
		if (!level.isClientSide && isAlive() && getY() > level.getMaxBuildHeight() && level.isRaining()) {
			if (level.getLevelData() instanceof ServerLevelData levelData) {
				levelData.setThundering(true);
			}
			discard();
		}
	}

	@Override
	public float getGravity() {
		return 0;
	}

	@Override
	protected void onHit(@NotNull HitResult result) {
		super.onHit(result);
		discard();
	}

	@Override
	protected void onHitBlock(@NotNull BlockHitResult result) {
		super.onHitBlock(result);
		if (!level.isClientSide && getOwner() instanceof ServerPlayer player) {
			ItemStack found = PlayerHelper.findFirstItem(player, fromArcana ? PEItems.ARCANA_RING.get() : PEItems.SWIFTWOLF_RENDING_GALE.get());
			if (!found.isEmpty() && ItemPE.consumeFuel(player, found, 768, true)) {
				BlockPos pos = result.getBlockPos();
				LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(level);
				if (lightning != null) {
					lightning.moveTo(Vec3.atCenterOf(pos));
					lightning.setCause(player);
					level.addFreshEntity(lightning);
				}
				if (level.isThundering()) {
					for (int i = 0; i < 3; i++) {
						LightningBolt bonus = EntityType.LIGHTNING_BOLT.create(level);
						if (bonus != null) {
							bonus.moveTo(pos.getX() + 0.5 + level.random.nextGaussian(), pos.getY() + 0.5 + level.random.nextGaussian(),
									pos.getZ() + 0.5 + level.random.nextGaussian());
							bonus.setCause(player);
							level.addFreshEntity(bonus);
						}
					}
				}
			}
		}
	}

	@Override
	protected void onHitEntity(@NotNull EntityHitResult result) {
		super.onHitEntity(result);
		if (!level.isClientSide && result.getEntity() instanceof LivingEntity e && getOwner() instanceof Player player) {
			ItemStack found = PlayerHelper.findFirstItem(player, fromArcana ? PEItems.ARCANA_RING.get() : PEItems.SWIFTWOLF_RENDING_GALE.get());
			if (!found.isEmpty() && ItemPE.consumeFuel(player, found, 64, true)) {
				// Minor damage, so we count as the attacker for launching the mob
				e.hurt(DamageSource.playerAttack(player), 1F);

				// Fake onGround before knockBack, so you can re-launch mobs that have already been launched
				boolean oldOnGround = e.isOnGround();
				e.setOnGround(true);
				e.knockback(5F, -getDeltaMovement().x() * 0.25, -getDeltaMovement().z() * 0.25);
				e.setOnGround(oldOnGround);
				e.setDeltaMovement(e.getDeltaMovement().multiply(1, 3, 1));
			}
		}
	}

	@Override
	public void readAdditionalSaveData(@NotNull CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		fromArcana = compound.getBoolean("fromArcana");
	}

	@Override
	public void addAdditionalSaveData(@NotNull CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putBoolean("fromArcana", fromArcana);
	}

	@NotNull
	@Override
	public Packet<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	public boolean ignoreExplosion() {
		return true;
	}
}