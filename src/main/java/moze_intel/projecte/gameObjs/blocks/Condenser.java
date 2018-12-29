package moze_intel.projecte.gameObjs.blocks;

import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.tiles.CondenserTile;
import moze_intel.projecte.utils.Constants;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;

public class Condenser extends AlchemicalChest
{
	public Condenser(Builder builder)
	{
		super(builder);
	}
	
	@Nonnull
	@Override
	public TileEntity createTileEntity(@Nonnull IBlockState state, @Nonnull IBlockReader world)
	{
		return new CondenserTile();
	}
	
	@Override
	public boolean onBlockActivated(IBlockState state, World world, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		if (!world.isRemote) 
		{
			player.openGui(PECore.instance, Constants.CONDENSER_GUI, world, pos.getX(), pos.getY(), pos.getZ());
		}
		
		return true;
	}

	@Override
	public int getComparatorInputOverride(IBlockState state, World world, BlockPos pos)
	{
		TileEntity te = world.getTileEntity(pos);
		if (te != null)
		{
			return te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
					.map(ItemHandlerHelper::calcRedstoneFromInventory)
					.orElse(0);
		}
		return 0;
	}
}
