package moze_intel.projecte.gameObjs.blocks;

import moze_intel.projecte.api.state.PEStateProps;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
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

	public BlockDirection(Material material)
	{
		super(material);
		this.setCreativeTab(ObjHandler.cTab);
	}

	@Nonnull
	@Override
	public BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, PEStateProps.FACING);
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return state.getValue(PEStateProps.FACING).getHorizontalIndex();
	}

	@Nonnull
	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return this.getDefaultState().withProperty(PEStateProps.FACING, EnumFacing.byHorizontalIndex(meta));
	}

	@Nonnull
	@Override
	public IBlockState getStateForPlacement(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ, int meta, @Nonnull EntityLivingBase placer, EnumHand hand)
	{
		return getStateFromMeta(meta).withProperty(PEStateProps.FACING, placer.getHorizontalFacing().getOpposite());
	}

	@Override
	public void breakBlock(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state)
	{
		TileEntity tile = world.getTileEntity(pos);

		if (tile != null)
		{
			IItemHandler inv = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
			WorldHelper.dropInventory(inv, world, pos);
		}

		super.breakBlock(world, pos, state);
	}
	
	@Override
	public void onBlockClicked(World world, BlockPos pos, EntityPlayer player)
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
		world.setBlockState(pos, world.getBlockState(pos).withProperty(PEStateProps.FACING, player.getHorizontalFacing().getOpposite()));
	}

}
