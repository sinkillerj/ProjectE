package moze_intel.projecte.gameObjs.blocks;

import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.EnumMatterType;
import moze_intel.projecte.gameObjs.tiles.DMFurnaceTile;
import moze_intel.projecte.gameObjs.tiles.RMFurnaceTile;
import moze_intel.projecte.utils.Constants;
import net.minecraft.block.BlockFurnace;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;

public class MatterFurnace extends BlockFurnace
{
	private final EnumMatterType matterType;

	public MatterFurnace(Builder builder, EnumMatterType type)
	{
		super(builder);
		this.matterType = type;
	}

	@Override
	public boolean onBlockActivated(IBlockState state, World world, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		if (!world.isRemote)
		{
			if (matterType == EnumMatterType.RED_MATTER)
			{
				player.openGui(PECore.instance, Constants.RM_FURNACE_GUI, world, pos.getX(), pos.getY(), pos.getZ());
			}
			else
			{
				player.openGui(PECore.instance, Constants.DM_FURNACE_GUI, world, pos.getX(), pos.getY(), pos.getZ());
			}
		}
		
		return true;
	}
	
	@Nonnull
	@Override
	public TileEntity createNewTileEntity(IBlockReader world)
	{
		return matterType == EnumMatterType.RED_MATTER ? new RMFurnaceTile() : new DMFurnaceTile();
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
