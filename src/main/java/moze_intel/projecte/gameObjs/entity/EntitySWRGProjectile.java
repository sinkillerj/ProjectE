package moze_intel.projecte.gameObjs.entity;

import moze_intel.projecte.gameObjs.items.ItemPE;
import moze_intel.projecte.gameObjs.registries.PEEntityTypes;
import moze_intel.projecte.gameObjs.registries.PEItems;
import moze_intel.projecte.utils.LevelHelper;
import moze_intel.projecte.utils.PlayerHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.NotNull;

public class EntitySWRGProjectile extends NoGravityThrowableProjectile {

	private boolean fromArcana = false;

	public EntitySWRGProjectile(EntityType<EntitySWRGProjectile> type, Level level) {
		super(type, level);
	}

	public EntitySWRGProjectile(Player player, boolean fromArcana, Level level) {
		super(PEEntityTypes.SWRG_PROJECTILE.get(), player, level);
		this.fromArcana = fromArcana;
	}

	@Override
	public void tick() {
		super.tick();
		if (isAlive()) {
			// Undo the 0.99 (0.8 in water) drag applied in superclass
			double inverse = 1D / (isInWater() ? 0.8D : 0.99D);
			this.setDeltaMovement(this.getDeltaMovement().scale(inverse));
			if (!level().isClientSide() && isAlive() && getY() > level().getMaxY() && level().isRaining()) {
				if (level() instanceof ServerLevel serverLevel) {
					LevelHelper.setWeather(serverLevel, 0, true, true);
				}
				discard();
			}
		}
	}

	@Override
	protected void onHitBlock(@NotNull BlockHitResult result) {
		super.onHitBlock(result);
		if (!level().isClientSide() && getOwner() instanceof ServerPlayer player && level() instanceof ServerLevel serverLevel) {
			ItemStack found = PlayerHelper.findFirstItem(player, fromArcana ? PEItems.ARCANA_RING : PEItems.SWIFTWOLF_RENDING_GALE);
			if (!found.isEmpty() && ItemPE.consumeFuel(player, found, 768, true)) {
				BlockPos pos = result.getBlockPos();
				LightningBolt lightning = LevelHelper.createLightning(serverLevel, net.minecraft.world.phys.Vec3.atCenterOf(pos));
				lightning.setCause(player);
				level().addFreshEntity(lightning);
				if (level().isThundering()) {
					for (int i = 0; i < 3; i++) {
						LightningBolt bonus = LevelHelper.createLightning(serverLevel, new Vec3(
								pos.getX() + 0.5 + level().getRandom().nextGaussian(),
								pos.getY() + 0.5 + level().getRandom().nextGaussian(),
								pos.getZ() + 0.5 + level().getRandom().nextGaussian()));
						bonus.setCause(player);
						level().addFreshEntity(bonus);
					}
				}
			}
		}
	}

	@Override
	protected void onHitEntity(@NotNull EntityHitResult result) {
		super.onHitEntity(result);
		if (!level().isClientSide() && result.getEntity() instanceof LivingEntity e && getOwner() instanceof Player player) {
			ItemStack found = PlayerHelper.findFirstItem(player, fromArcana ? PEItems.ARCANA_RING : PEItems.SWIFTWOLF_RENDING_GALE);
			if (!found.isEmpty() && ItemPE.consumeFuel(player, found, 64, true)) {
				// Minor damage, so we count as the attacker for launching the mob
				e.hurt(level().damageSources().playerAttack(player), 1F);

				// Fake onGround before knockBack, so you can re-launch mobs that have already been launched
				boolean oldOnGround = e.onGround();
				e.setOnGround(true);
				e.knockback(5F, -getDeltaMovement().x() * 0.25, -getDeltaMovement().z() * 0.25, level().damageSources().playerAttack(player), 1.0F);
				e.setOnGround(oldOnGround);
				e.setDeltaMovement(e.getDeltaMovement().multiply(1, 3, 1));
			}
		}
	}

	@Override
	protected void readAdditionalSaveData(@NotNull ValueInput input) {
		super.readAdditionalSaveData(input);
		fromArcana = input.getBooleanOr("fromArcana", false);
	}

	@Override
	protected void addAdditionalSaveData(@NotNull ValueOutput output) {
		super.addAdditionalSaveData(output);
		output.putBoolean("fromArcana", fromArcana);
	}
}
