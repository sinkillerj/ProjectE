package moze_intel.projecte.gameObjs.blocks;

import com.mojang.serialization.MapCodec;
import moze_intel.projecte.gameObjs.container.TransmutationContainer;
import moze_intel.projecte.gameObjs.registries.PEBlockTypes;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TransmutationStone extends DirectionalBlock implements SimpleWaterloggedBlock {

	private static final VoxelShape UP_SHAPE = Block.box(0, 0, 0, 16, 4, 16);
	private static final VoxelShape DOWN_SHAPE = Block.box(0, 12, 0, 16, 16, 16);
	private static final VoxelShape NORTH_SHAPE = Block.box(0, 0, 12, 16, 16, 16);
	private static final VoxelShape SOUTH_SHAPE = Block.box(0, 0, 0, 16, 16, 4);
	private static final VoxelShape WEST_SHAPE = Block.box(12, 0, 0, 16, 16, 16);
	private static final VoxelShape EAST_SHAPE = Block.box(0, 0, 0, 4, 16, 16);

	public TransmutationStone(Properties props) {
		super(props);
		this.registerDefaultState(getStateDefinition().any().setValue(FACING, Direction.UP).setValue(BlockStateProperties.WATERLOGGED, false));
	}

	@NotNull
	@Override
	protected MapCodec<? extends DirectionalBlock> codec() {
		return PEBlockTypes.TRANSMUTATION_TABLE.value();
	}

	@Override
	protected void createBlockStateDefinition(@NotNull StateDefinition.Builder<Block, BlockState> props) {
		super.createBlockStateDefinition(props);
		props.add(FACING, BlockStateProperties.WATERLOGGED);
	}

	@Override
	@Deprecated
	public boolean isPathfindable(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull PathComputationType type) {
		return false;
	}

	@NotNull
	@Override
	@Deprecated
	public VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext ctx) {
		Direction facing = state.getValue(FACING);
		return switch (facing) {
			case DOWN -> DOWN_SHAPE;
			case NORTH -> NORTH_SHAPE;
			case SOUTH -> SOUTH_SHAPE;
			case WEST -> WEST_SHAPE;
			case EAST -> EAST_SHAPE;
			case UP -> UP_SHAPE;
		};
	}

	@NotNull
	@Override
	@Deprecated
	public InteractionResult use(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand,
			@NotNull BlockHitResult rtr) {
		if (!level.isClientSide) {
			player.openMenu(new ContainerProvider(), b -> b.writeBoolean(false));
		}
		return InteractionResult.sidedSuccess(level.isClientSide);
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
		BlockState state = super.getStateForPlacement(context);
		return state == null ? null : state.setValue(FACING, context.getClickedFace()).setValue(BlockStateProperties.WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER);
	}

	@NotNull
	@Override
	@Deprecated
	public FluidState getFluidState(BlockState state) {
		return state.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@NotNull
	@Override
	@Deprecated
	public BlockState updateShape(@NotNull BlockState state, @NotNull Direction facing, @NotNull BlockState facingState, @NotNull LevelAccessor level,
			@NotNull BlockPos currentPos, @NotNull BlockPos facingPos) {
		if (state.getValue(BlockStateProperties.WATERLOGGED)) {
			level.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
		}
		return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
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

	private static class ContainerProvider implements MenuProvider {

		@Override
		public AbstractContainerMenu createMenu(int windowId, @NotNull Inventory playerInventory, @NotNull Player player) {
			return new TransmutationContainer(windowId, playerInventory);
		}

		@NotNull
		@Override
		public Component getDisplayName() {
			return PELang.TRANSMUTATION_TRANSMUTE.translate();
		}
	}
}