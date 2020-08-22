package moze_intel.projecte.gameObjs.blocks;

import javax.annotation.Nonnull;
import moze_intel.projecte.gameObjs.items.PhilosophersStone;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;

public abstract class BlockDirection extends Block {

	public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;

	public BlockDirection(Properties props) {
		super(props);
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> props) {
		props.add(FACING);
	}

	@Nonnull
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext ctx) {
		if (ctx.getPlayer() != null) {
			return getDefaultState().with(FACING, ctx.getPlayer().getHorizontalFacing().getOpposite());
		}
		return getDefaultState();
	}

	@Override
	@Deprecated
	public void onReplaced(BlockState state, World world, @Nonnull BlockPos pos, @Nonnull BlockState newState, boolean isMoving) {
		if (state.getBlock() != newState.getBlock()) {
			TileEntity tile = world.getTileEntity(pos);
			if (tile != null) {
				tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(inv -> WorldHelper.dropInventory(inv, world, pos));
			}
			super.onReplaced(state, world, pos, newState, isMoving);
		}
	}

	@Override
	@Deprecated
	public void onBlockClicked(@Nonnull BlockState state, World world, @Nonnull BlockPos pos, @Nonnull PlayerEntity player) {
		if (!world.isRemote) {
			ItemStack stack = player.getHeldItem(Hand.MAIN_HAND);
			if (!stack.isEmpty() && stack.getItem() instanceof PhilosophersStone) {
				world.setBlockState(pos, world.getBlockState(pos).with(FACING, player.getHorizontalFacing().getOpposite()));
			}
		}
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
}