package moze_intel.projecte.gameObjs.blocks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import moze_intel.projecte.gameObjs.container.TransmutationContainer;
import moze_intel.projecte.gameObjs.registries.PEBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class TransmutationStone extends DirectionalBlock implements IWaterLoggable {

	private static final VoxelShape UP_SHAPE = Block.makeCuboidShape(0, 0, 0, 16, 4, 16);
	private static final VoxelShape DOWN_SHAPE = Block.makeCuboidShape(0, 12, 0, 16, 16, 16);
	private static final VoxelShape NORTH_SHAPE = Block.makeCuboidShape(0, 0, 12, 16, 16, 16);
	private static final VoxelShape SOUTH_SHAPE = Block.makeCuboidShape(0, 0, 0, 16, 16, 4);
	private static final VoxelShape WEST_SHAPE = Block.makeCuboidShape(12, 0, 0, 16, 16, 16);
	private static final VoxelShape EAST_SHAPE = Block.makeCuboidShape(0, 0, 0, 4, 16, 16);

	public TransmutationStone(Properties props) {
		super(props);
		this.setDefaultState(getStateContainer().getBaseState().with(FACING, Direction.UP).with(BlockStateProperties.WATERLOGGED, false));
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> props) {
		props.add(FACING).add(BlockStateProperties.WATERLOGGED);
	}

	@Nonnull
	@Override
	@Deprecated
	public VoxelShape getShape(@Nonnull BlockState state, @Nonnull IBlockReader world, @Nonnull BlockPos pos, @Nonnull ISelectionContext ctx) {
		Direction facing = state.get(FACING);
		switch (facing) {
			case DOWN:
				return DOWN_SHAPE;
			case NORTH:
				return NORTH_SHAPE;
			case SOUTH:
				return SOUTH_SHAPE;
			case WEST:
				return WEST_SHAPE;
			case EAST:
				return EAST_SHAPE;
			case UP:
			default:
				return UP_SHAPE;
		}
	}

	@Nonnull
	@Override
	@Deprecated
	public ActionResultType onBlockActivated(@Nonnull BlockState state, World world, @Nonnull BlockPos pos, @Nonnull PlayerEntity player, @Nonnull Hand hand, @Nonnull BlockRayTraceResult rtr) {
		if (!world.isRemote) {
			NetworkHooks.openGui((ServerPlayerEntity) player, new ContainerProvider(), b -> b.writeBoolean(false));
		}
		return ActionResultType.SUCCESS;
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		BlockState state = super.getStateForPlacement(context);
		return state == null ? null : state.with(FACING, context.getFace()).with(BlockStateProperties.WATERLOGGED, context.getWorld().getFluidState(context.getPos()).getFluid() == Fluids.WATER);
	}

	@Nonnull
	@Override
	@Deprecated
	public FluidState getFluidState(BlockState state) {
		return state.get(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
	}

	@Nonnull
	@Override
	@Deprecated
	public BlockState updatePostPlacement(BlockState state, Direction facing, @Nonnull BlockState facingState, @Nonnull IWorld world, @Nonnull BlockPos currentPos,
			@Nonnull BlockPos facingPos) {
		if (state.get(BlockStateProperties.WATERLOGGED)) {
			world.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(world));
		}
		return super.updatePostPlacement(state, facing, facingState, world, currentPos, facingPos);
	}

	@Nonnull
	@Override
	@Deprecated
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.with(FACING, rot.rotate(state.get(FACING)));
	}

	@Nonnull
	@Override
	@Deprecated
	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.rotate(mirrorIn.toRotation(state.get(FACING)));
	}

	private static class ContainerProvider implements INamedContainerProvider {

		@Override
		public Container createMenu(int windowId, @Nonnull PlayerInventory playerInventory, @Nonnull PlayerEntity player) {
			return new TransmutationContainer(windowId, playerInventory, Hand.OFF_HAND);
		}

		@Nonnull
		@Override
		public ITextComponent getDisplayName() {
			return new TranslationTextComponent(PEBlocks.TRANSMUTATION_TABLE.getBlock().getTranslationKey());
		}
	}
}