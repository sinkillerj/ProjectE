package moze_intel.projecte.gameObjs.blocks;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.gameObjs.tiles.DMPedestalTile;
import moze_intel.projecte.utils.WorldHelper;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.BlockFlags;

public class Pedestal extends Block implements IWaterLoggable {

	private static final VoxelShape SHAPE = VoxelShapes.or(
			Block.box(3, 0, 3, 13, 2, 13),
			VoxelShapes.or(
					Block.box(6, 2, 6, 10, 9, 10),
					Block.box(5, 9, 5, 11, 10, 11)
			)
	);

	public Pedestal(Properties props) {
		super(props);
		this.registerDefaultState(getStateDefinition().any().setValue(BlockStateProperties.WATERLOGGED, false));
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> props) {
		props.add(BlockStateProperties.WATERLOGGED);
	}

	@Nonnull
	@Override
	@Deprecated
	public VoxelShape getShape(@Nonnull BlockState state, @Nonnull IBlockReader world, @Nonnull BlockPos pos, @Nonnull ISelectionContext ctx) {
		return SHAPE;
	}

	/**
	 * @return True if there was an item and it got dropped, false otherwise.
	 */
	private boolean dropItem(World world, BlockPos pos) {
		DMPedestalTile tile = WorldHelper.getTileEntity(DMPedestalTile.class, world, pos);
		if (tile != null) {
			ItemStack stack = tile.getInventory().getStackInSlot(0);
			if (!stack.isEmpty()) {
				tile.getInventory().setStackInSlot(0, ItemStack.EMPTY);
				ItemEntity ent = new ItemEntity(world, pos.getX(), pos.getY() + 0.8, pos.getZ());
				ent.setItem(stack);
				world.addFreshEntity(ent);
				return true;
			}
		}
		return false;
	}

	@Override
	@Deprecated
	public void onRemove(BlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull BlockState newState, boolean isMoving) {
		if (state.getBlock() != newState.getBlock()) {
			dropItem(world, pos);
			super.onRemove(state, world, pos, newState, isMoving);
		}
	}

	@Override
	@Deprecated
	public void attack(@Nonnull BlockState state, World world, @Nonnull BlockPos pos, @Nonnull PlayerEntity player) {
		if (!world.isClientSide) {
			dropItem(world, pos);
		}
	}

	@Override
	public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest, FluidState fluid) {
		if (player.isCreative() && dropItem(world, pos)) {
			//If the player is creative, try to drop the item, and if we succeeded return false to cancel removing the pedestal
			// Note: we notify the block of an update to make sure that it re-appears visually on the client instead of having there
			// be a desync
			world.sendBlockUpdated(pos, state, state, BlockFlags.RERENDER_MAIN_THREAD);
			return false;
		}
		return super.removedByPlayer(state, world, pos, player, willHarvest, fluid);
	}

	@Nonnull
	@Override
	@Deprecated
	public ActionResultType use(@Nonnull BlockState state, World world, @Nonnull BlockPos pos, @Nonnull PlayerEntity player, @Nonnull Hand hand,
			@Nonnull BlockRayTraceResult rtr) {
		if (!world.isClientSide) {
			DMPedestalTile tile = WorldHelper.getTileEntity(DMPedestalTile.class, world, pos, true);
			if (tile == null) {
				return ActionResultType.FAIL;
			}
			ItemStack item = tile.getInventory().getStackInSlot(0);
			ItemStack stack = player.getItemInHand(hand);
			if (stack.isEmpty() && !item.isEmpty()) {
				item.getCapability(ProjectEAPI.PEDESTAL_ITEM_CAPABILITY).ifPresent(pedestalItem -> {
					tile.setActive(!tile.getActive());
					world.sendBlockUpdated(pos, state, state, BlockFlags.RERENDER_MAIN_THREAD);
				});
			} else if (!stack.isEmpty() && item.isEmpty()) {
				tile.getInventory().setStackInSlot(0, stack.split(1));
				if (stack.getCount() <= 0) {
					player.setItemInHand(hand, ItemStack.EMPTY);
				}
			}
		}
		return ActionResultType.SUCCESS;
	}

	// [VanillaCopy] Adapted from BlockNote
	@Override
	@Deprecated
	public void neighborChanged(@Nonnull BlockState state, World world, @Nonnull BlockPos pos, @Nonnull Block neighbor, @Nonnull BlockPos neighborPos, boolean isMoving) {
		boolean flag = world.hasNeighborSignal(pos);
		DMPedestalTile ped = WorldHelper.getTileEntity(DMPedestalTile.class, world, pos);
		if (ped != null && ped.previousRedstoneState != flag) {
			if (flag) {
				ItemStack stack = ped.getInventory().getStackInSlot(0);
				if (!stack.isEmpty()) {
					stack.getCapability(ProjectEAPI.PEDESTAL_ITEM_CAPABILITY).ifPresent(pedestalItem -> {
						ped.setActive(!ped.getActive());
						world.sendBlockUpdated(pos, state, state, BlockFlags.DEFAULT_AND_RERENDER);
					});
				}
			}
			ped.previousRedstoneState = flag;
			ped.markDirty(false, false);
		}
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(@Nonnull BlockState state, @Nonnull IBlockReader world) {
		return new DMPedestalTile();
	}

	@Override
	public void appendHoverText(@Nonnull ItemStack stack, @Nullable IBlockReader world, @Nonnull List<ITextComponent> tooltip, @Nonnull ITooltipFlag flags) {
		super.appendHoverText(stack, world, tooltip, flags);
		tooltip.add(PELang.PEDESTAL_TOOLTIP1.translate());
		tooltip.add(PELang.PEDESTAL_TOOLTIP2.translate());
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(@Nonnull BlockItemUseContext context) {
		BlockState state = super.getStateForPlacement(context);
		return state == null ? null : state.setValue(BlockStateProperties.WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER);
	}

	@Nonnull
	@Override
	@Deprecated
	public FluidState getFluidState(BlockState state) {
		return state.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Nonnull
	@Override
	@Deprecated
	public BlockState updateShape(@Nonnull BlockState state, @Nonnull Direction facing, @Nonnull BlockState facingState, @Nonnull IWorld world,
			@Nonnull BlockPos currentPos, @Nonnull BlockPos facingPos) {
		if (state.getValue(BlockStateProperties.WATERLOGGED)) {
			world.getLiquidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
		}
		return super.updateShape(state, facing, facingState, world, currentPos, facingPos);
	}
}