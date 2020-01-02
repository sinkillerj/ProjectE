package moze_intel.projecte.gameObjs.blocks;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.gameObjs.tiles.DMPedestalTile;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
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
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class Pedestal extends Block implements IWaterLoggable {

	private static final VoxelShape SHAPE = VoxelShapes.or(
			Block.makeCuboidShape(3, 0, 3, 13, 2, 13),
			VoxelShapes.or(
					Block.makeCuboidShape(6, 2, 6, 10, 9, 10),
					Block.makeCuboidShape(5, 9, 5, 11, 10, 11)
			)
	);

	public Pedestal(Properties props) {
		super(props);
		this.setDefaultState(getStateContainer().getBaseState().with(BlockStateProperties.WATERLOGGED, false));
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> props) {
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
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof DMPedestalTile) {
			DMPedestalTile tile = (DMPedestalTile) te;
			ItemStack stack = tile.getInventory().getStackInSlot(0);
			if (!stack.isEmpty()) {
				tile.getInventory().setStackInSlot(0, ItemStack.EMPTY);
				ItemEntity ent = new ItemEntity(world, pos.getX(), pos.getY() + 0.8, pos.getZ());
				ent.setItem(stack);
				world.addEntity(ent);
				return true;
			}
		}
		return false;
	}

	@Override
	@Deprecated
	public void onReplaced(BlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull BlockState newState, boolean isMoving) {
		if (state.getBlock() != newState.getBlock()) {
			dropItem(world, pos);
			super.onReplaced(state, world, pos, newState, isMoving);
		}
	}

	@Override
	@Deprecated
	public void onBlockClicked(@Nonnull BlockState state, World world, @Nonnull BlockPos pos, @Nonnull PlayerEntity player) {
		if (!world.isRemote) {
			dropItem(world, pos);
			world.notifyBlockUpdate(pos, state, state, 8);
		}
	}

	@Override
	public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest, IFluidState fluid) {
		if (player.isCreative() && dropItem(world, pos)) {
			//If the player is creative, try to drop the item, and if we succeeded return false to cancel removing the pedestal
			// Note: we notify the block of an update to make sure that it re-appears visually on the client instead of having there
			// be a desync
			world.notifyBlockUpdate(pos, state, state, 8);
			return false;
		}
		return super.removedByPlayer(state, world, pos, player, willHarvest, fluid);
	}

	@Nonnull
	@Override
	@Deprecated
	public ActionResultType func_225533_a_(@Nonnull BlockState state, World world, @Nonnull BlockPos pos, @Nonnull PlayerEntity player, @Nonnull Hand hand, @Nonnull BlockRayTraceResult rtr) {
		if (!world.isRemote) {
			TileEntity te = world.getTileEntity(pos);
			if (!(te instanceof DMPedestalTile)) {
				return ActionResultType.FAIL;
			}
			DMPedestalTile tile = (DMPedestalTile) te;
			ItemStack item = tile.getInventory().getStackInSlot(0);
			ItemStack stack = player.getHeldItem(hand);
			if (stack.isEmpty() && !item.isEmpty()) {
				item.getCapability(ProjectEAPI.PEDESTAL_ITEM_CAPABILITY).ifPresent(pedestalItem -> {
					tile.setActive(!tile.getActive());
					world.notifyBlockUpdate(pos, state, state, 8);
				});
			} else if (!stack.isEmpty() && item.isEmpty()) {
				tile.getInventory().setStackInSlot(0, stack.split(1));
				if (stack.getCount() <= 0) {
					player.setHeldItem(hand, ItemStack.EMPTY);
				}
				world.notifyBlockUpdate(pos, state, state, 8);
			}
		}
		return ActionResultType.SUCCESS;
	}

	// [VanillaCopy] Adapted from BlockNote
	@Override
	@Deprecated
	public void neighborChanged(@Nonnull BlockState state, World world, @Nonnull BlockPos pos, @Nonnull Block neighbor, @Nonnull BlockPos neighborPos, boolean isMoving) {
		boolean flag = world.isBlockPowered(pos);
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof DMPedestalTile) {
			DMPedestalTile ped = (DMPedestalTile) te;
			if (ped.previousRedstoneState != flag) {
				if (flag) {
					ItemStack stack = ped.getInventory().getStackInSlot(0);
					if (!stack.isEmpty()) {
						stack.getCapability(ProjectEAPI.PEDESTAL_ITEM_CAPABILITY).ifPresent(pedestalItem -> {
							ped.setActive(!ped.getActive());
							world.notifyBlockUpdate(pos, state, state, 11);
						});
					}
				}
				ped.previousRedstoneState = flag;
			}
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
	@OnlyIn(Dist.CLIENT)
	public void addInformation(@Nonnull ItemStack stack, @Nullable IBlockReader world, List<ITextComponent> tooltip, @Nonnull ITooltipFlag flags) {
		tooltip.add(new TranslationTextComponent("pe.pedestal.tooltip1"));
		tooltip.add(new TranslationTextComponent("pe.pedestal.tooltip2"));
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		BlockState state = super.getStateForPlacement(context);
		return state == null ? null : state.with(BlockStateProperties.WATERLOGGED, context.getWorld().getFluidState(context.getPos()).getFluid() == Fluids.WATER);
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