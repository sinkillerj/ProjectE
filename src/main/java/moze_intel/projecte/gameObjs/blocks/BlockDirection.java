package moze_intel.projecte.gameObjs.blocks;

import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;

public abstract class BlockDirection extends Block
{

	public BlockDirection(Properties props)
	{
		super(props);
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> props)
	{
		props.add(BlockStateProperties.HORIZONTAL_FACING);
	}

	@Nonnull
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext ctx)
	{
		if (ctx.getPlayer() != null)
		{
			return getDefaultState().with(BlockStateProperties.HORIZONTAL_FACING, ctx.getPlayer().getHorizontalFacing().getOpposite());
		}
		return getDefaultState();
	}

	@Override
	@Deprecated
	public void onReplaced(BlockState state, World world, @Nonnull BlockPos pos, @Nonnull BlockState newState, boolean isMoving)
	{
		TileEntity tile = world.getTileEntity(pos);

		if (tile != null)
		{
			tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
					.ifPresent(inv -> WorldHelper.dropInventory(inv, world, pos));
		}

		super.onReplaced(state, world, pos, newState, isMoving);
	}
	
	@Override
	@Deprecated
	public void onBlockClicked(@Nonnull BlockState state, World world, @Nonnull BlockPos pos, @Nonnull PlayerEntity player)
	{
		if (world.isRemote)
		{
			return;
		}
		
		ItemStack stack = player.getHeldItem(Hand.MAIN_HAND);
		
		if (!stack.isEmpty() && stack.getItem() == ObjHandler.philosStone)
		{
			setFacingMeta(world, pos, player);
		}
	}

	private void setFacingMeta(World world, BlockPos pos, PlayerEntity player)
	{
		world.setBlockState(pos, world.getBlockState(pos).with(BlockStateProperties.HORIZONTAL_FACING, player.getHorizontalFacing().getOpposite()));
	}

}
