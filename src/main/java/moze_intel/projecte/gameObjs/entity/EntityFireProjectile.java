package moze_intel.projecte.gameObjs.entity;

import java.util.function.Predicate;
import moze_intel.projecte.gameObjs.items.ItemPE;
import moze_intel.projecte.gameObjs.registries.PEEntityTypes;
import moze_intel.projecte.gameObjs.registries.PEItems;
import moze_intel.projecte.utils.PlayerHelper;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

public class EntityFireProjectile extends NoGravityThrowableProjectile {

	private boolean fromArcana = false;

	public EntityFireProjectile(EntityType<EntityFireProjectile> type, Level level) {
		super(type, level);
	}

	public EntityFireProjectile(Player entity, boolean fromArcana, Level level) {
		super(PEEntityTypes.FIRE_PROJECTILE.get(), entity, level);
		this.fromArcana = fromArcana;
	}

	@Override
	protected void onHit(@NotNull HitResult result) {
		super.onHit(result);
		discard();
	}

	@Override
	protected void onHitBlock(@NotNull BlockHitResult result) {
		super.onHitBlock(result);
		if (!level().isClientSide && getOwner() instanceof Player player) {
			BlockPos pos = result.getBlockPos();
			BlockState state = level().getBlockState(pos);
			if (state.is(Blocks.OBSIDIAN)) {
				level().setBlockAndUpdate(pos, Blocks.LAVA.defaultBlockState());
			} else if (state.is(BlockTags.SAND)) {
				placeAOE(player, pos, 2, Blocks.GLASS.defaultBlockState(), s -> s.is(BlockTags.SAND));
			} else {
				placeAOE(player, pos, 1, Blocks.FIRE.defaultBlockState(), BlockStateBase::isAir);
			}
		}
	}

	private void placeAOE(Player player, BlockPos pos, int radius, BlockState newState, Predicate<BlockState> targetCheck) {
		for (BlockPos currentPos : WorldHelper.positionsAround(pos, radius)) {
			if (targetCheck.test(level().getBlockState(currentPos))) {
				PlayerHelper.checkedPlaceBlock(player, pos.immutable(), newState);
			}
		}
	}

	@Override
	protected void onHitEntity(@NotNull EntityHitResult result) {
		super.onHitEntity(result);
		if (!level().isClientSide && getOwner() instanceof Player player) {
			ItemStack found = PlayerHelper.findFirstItem(player, fromArcana ? PEItems.ARCANA_RING.get() : PEItems.IGNITION_RING.get());
			if (!found.isEmpty() && ItemPE.consumeFuel(player, found, 32, true)) {
				Entity ent = result.getEntity();
				ent.setSecondsOnFire(5);
				ent.hurt(level().damageSources().inFire(), 5);
			}
		}
	}

	@Override
	protected void defineSynchedData() {
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

	@Override
	public boolean ignoreExplosion(@NotNull Explosion explosion) {
		return true;
	}
}