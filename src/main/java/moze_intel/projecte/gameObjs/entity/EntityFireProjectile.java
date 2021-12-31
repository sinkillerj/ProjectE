package moze_intel.projecte.gameObjs.entity;

import javax.annotation.Nonnull;
import moze_intel.projecte.gameObjs.items.ItemPE;
import moze_intel.projecte.gameObjs.registries.PEEntityTypes;
import moze_intel.projecte.gameObjs.registries.PEItems;
import moze_intel.projecte.utils.PlayerHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.network.NetworkHooks;

public class EntityFireProjectile extends ThrowableProjectile {

	private boolean fromArcana = false;

	public EntityFireProjectile(EntityType<EntityFireProjectile> type, Level level) {
		super(type, level);
	}

	public EntityFireProjectile(Player entity, boolean fromArcana, Level level) {
		super(PEEntityTypes.FIRE_PROJECTILE.get(), entity, level);
		this.fromArcana = fromArcana;
	}

	@Override
	public float getGravity() {
		return 0;
	}

	@Override
	protected void onHit(@Nonnull HitResult result) {
		super.onHit(result);
		discard();
	}

	@Override
	protected void onHitBlock(@Nonnull BlockHitResult result) {
		super.onHitBlock(result);
		if (!level.isClientSide && getOwner() instanceof ServerPlayer player) {
			BlockPos pos = result.getBlockPos();
			Block block = level.getBlockState(pos).getBlock();
			if (block == Blocks.OBSIDIAN) {
				level.setBlockAndUpdate(pos, Blocks.LAVA.defaultBlockState());
			} else if (block == Blocks.SAND) {
				BlockPos.betweenClosedStream(pos.offset(-2, -2, -2), pos.offset(2, 2, 2)).forEach(currentPos -> {
					if (level.getBlockState(currentPos).getBlock() == Blocks.SAND) {
						PlayerHelper.checkedPlaceBlock(player, pos.immutable(), Blocks.GLASS.defaultBlockState());
					}
				});
			} else {
				BlockPos.betweenClosedStream(pos.offset(-1, -1, -1), pos.offset(1, 1, 1)).forEach(currentPos -> {
					if (level.isEmptyBlock(currentPos)) {
						PlayerHelper.checkedPlaceBlock(player, currentPos.immutable(), Blocks.FIRE.defaultBlockState());
					}
				});
			}
		}
	}

	@Override
	protected void onHitEntity(@Nonnull EntityHitResult result) {
		super.onHitEntity(result);
		if (!level.isClientSide && getOwner() instanceof Player player) {
			ItemStack found = PlayerHelper.findFirstItem(player, fromArcana ? PEItems.ARCANA_RING.get() : PEItems.IGNITION_RING.get());
			if (!found.isEmpty() && ItemPE.consumeFuel(player, found, 32, true)) {
				Entity ent = result.getEntity();
				ent.setSecondsOnFire(5);
				ent.hurt(DamageSource.IN_FIRE, 5);
			}
		}
	}

	@Override
	protected void defineSynchedData() {
	}

	@Override
	public void readAdditionalSaveData(@Nonnull CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		fromArcana = compound.getBoolean("fromArcana");
	}

	@Override
	public void addAdditionalSaveData(@Nonnull CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putBoolean("fromArcana", fromArcana);
	}

	@Nonnull
	@Override
	public Packet<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	public boolean ignoreExplosion() {
		return true;
	}
}