package moze_intel.projecte.gameObjs.blocks;

import moze_intel.projecte.gameObjs.items.PhilosophersStone;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraftforge.items.CapabilityItemHandler;
import org.jetbrains.annotations.NotNull;

public abstract class BlockDirection extends Block {

	public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

	public BlockDirection(Properties props) {
		super(props);
	}

	@Override
	protected void createBlockStateDefinition(@NotNull StateDefinition.Builder<Block, BlockState> props) {
		super.createBlockStateDefinition(props);
		props.add(FACING);
	}

	@NotNull
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		if (ctx.getPlayer() != null) {
			return defaultBlockState().setValue(FACING, ctx.getPlayer().getDirection().getOpposite());
		}
		return defaultBlockState();
	}

	@Override
	@Deprecated
	public void onRemove(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState newState, boolean isMoving) {
		if (state.getBlock() != newState.getBlock()) {
			BlockEntity blockEntity = WorldHelper.getBlockEntity(level, pos);
			if (blockEntity != null) {
				blockEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(inv -> WorldHelper.dropInventory(inv, level, pos));
			}
			super.onRemove(state, level, pos, newState, isMoving);
		}
	}

	@Override
	@Deprecated
	public void attack(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Player player) {
		if (!level.isClientSide) {
			ItemStack stack = player.getMainHandItem();
			if (!stack.isEmpty() && stack.getItem() instanceof PhilosophersStone) {
				level.setBlockAndUpdate(pos, level.getBlockState(pos).setValue(FACING, player.getDirection().getOpposite()));
			}
		}
	}

	@NotNull
	@Override
	@Deprecated
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
	}

	@NotNull
	@Override
	@Deprecated
	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
	}
}