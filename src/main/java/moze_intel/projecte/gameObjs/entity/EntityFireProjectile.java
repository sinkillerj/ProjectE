package moze_intel.projecte.gameObjs.entity;

import java.util.function.Predicate;
import moze_intel.projecte.gameObjs.items.ItemPE;
import moze_intel.projecte.gameObjs.registries.PEEntityTypes;
import moze_intel.projecte.gameObjs.registries.PEItems;
import moze_intel.projecte.utils.PlayerHelper;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
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
	protected void onHitBlock(@NotNull BlockHitResult result) {
		super.onHitBlock(result);
		if (!level().isClientSide() && getOwner() instanceof Player player) {
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
		Level level = level();
		for (BlockPos currentPos : WorldHelper.positionsAround(pos, radius)) {
			if (targetCheck.test(level.getBlockState(currentPos))) {
				PlayerHelper.checkedPlaceBlock(player, level, pos.immutable(), newState);
			}
		}
	}

	@Override
	protected void onHitEntity(@NotNull EntityHitResult result) {
		super.onHitEntity(result);
		if (!level().isClientSide() && getOwner() instanceof Player player) {
			ItemStack found = PlayerHelper.findFirstItem(player, fromArcana ? PEItems.ARCANA_RING : PEItems.IGNITION_RING);
			if (!found.isEmpty() && ItemPE.consumeFuel(player, found, 32, true)) {
				Entity ent = result.getEntity();
				ent.hurt(level().damageSources().inFire(), 5);
				ent.igniteForSeconds(5);
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
