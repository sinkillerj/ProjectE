package moze_intel.projecte.gameObjs.blocks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import moze_intel.projecte.gameObjs.tiles.AlchChestTile;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

public class AlchemicalChest extends BlockDirection implements IWaterLoggable {

	private static final VoxelShape SHAPE = Block.makeCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 14.0D, 15.0D);

	public AlchemicalChest(Properties props) {
		super(props);
		this.setDefaultState(getStateContainer().getBaseState().with(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH).with(BlockStateProperties.WATERLOGGED, false));
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> props) {
		super.fillStateContainer(props);
		props.add(BlockStateProperties.WATERLOGGED);
	}

	@Nonnull
	@Override
	@Deprecated
	public VoxelShape getShape(@Nonnull BlockState state, @Nonnull IBlockReader world, @Nonnull BlockPos pos, @Nonnull ISelectionContext ctx) {
		return SHAPE;
	}

	@Nonnull
	@Override
	@Deprecated
	public BlockRenderType getRenderType(@Nonnull BlockState state) {
		return BlockRenderType.ENTITYBLOCK_ANIMATED;
	}

	@Override
	@Deprecated
	public boolean onBlockActivated(@Nonnull BlockState state, World world, @Nonnull BlockPos pos, @Nonnull PlayerEntity player, @Nonnull Hand hand, @Nonnull BlockRayTraceResult rtr) {
		if (!world.isRemote) {
			TileEntity te = world.getTileEntity(pos);
			if (te instanceof AlchChestTile) {
				NetworkHooks.openGui((ServerPlayerEntity) player, (AlchChestTile) te, pos);
			}
		}
		return true;
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(@Nonnull BlockState state, @Nonnull IBlockReader world) {
		return new AlchChestTile();
	}

	@Override
	@Deprecated
	public boolean hasComparatorInputOverride(@Nonnull BlockState state) {
		return true;
	}

	@Override
	@Deprecated
	public int getComparatorInputOverride(@Nonnull BlockState state, World world, @Nonnull BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);
		if (te != null) {
			return te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).map(ItemHandlerHelper::calcRedstoneFromInventory).orElse(0);
		}
		return 0;
	}

	@Nonnull
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return super.getStateForPlacement(context).with(BlockStateProperties.WATERLOGGED, context.getWorld().getFluidState(context.getPos()).getFluid() == Fluids.WATER);
	}

	@Nonnull
	@Override
	@Deprecated
	public IFluidState getFluidState(BlockState state) {
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
}