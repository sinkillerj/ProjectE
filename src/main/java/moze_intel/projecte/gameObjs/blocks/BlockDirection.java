package moze_intel.projecte.gameObjs.blocks;

import moze_intel.projecte.api.state.PEStateProps;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;

public abstract class BlockDirection extends Block
{

	public BlockDirection(Builder builder)
	{
		super(builder);
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder)
	{
		builder.add(PEStateProps.FACING);
	}

	@Nonnull
	@Override
	public IBlockState getStateForPlacement(BlockItemUseContext ctx)
	{
		if (ctx.getPlayer() != null)
		{
			return getDefaultState().with(PEStateProps.FACING, ctx.getPlayer().getHorizontalFacing().getOpposite());
		}
		return getDefaultState();
	}

	@Override
	public void onReplaced(IBlockState state, World world, BlockPos pos, IBlockState newState, boolean isMoving)
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
	public void onBlockClicked(IBlockState state, World world, BlockPos pos, EntityPlayer player)
	{
		if (world.isRemote)
		{
			return;
		}
		
		ItemStack stack = player.getHeldItem(EnumHand.MAIN_HAND);
		
		if (!stack.isEmpty() && stack.getItem() == ObjHandler.philosStone)
		{
			setFacingMeta(world, pos, player);
		}
	}

	private void setFacingMeta(World world, BlockPos pos, EntityPlayer player)
	{
		world.setBlockState(pos, world.getBlockState(pos).with(PEStateProps.FACING, player.getHorizontalFacing().getOpposite()));
	}

}
