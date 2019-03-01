package moze_intel.projecte.gameObjs.blocks;

import io.netty.buffer.Unpooled;
import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.tiles.RelayMK1Tile;
import moze_intel.projecte.gameObjs.tiles.RelayMK2Tile;
import moze_intel.projecte.gameObjs.tiles.RelayMK3Tile;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.MathUtils;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;

public class Relay extends BlockDirection implements ITileEntityProvider
{
	private final int tier;
	
	public Relay(int tier, Properties props)
	{
		super(props);
		this.tier = tier;
	}
	
	@Override
	public boolean onBlockActivated(IBlockState state, World world, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		if (!world.isRemote)
		{
			TileEntity te = world.getTileEntity(pos);
			if (te instanceof RelayMK1Tile)
			{
				NetworkHooks.openGui((EntityPlayerMP) player, (RelayMK1Tile) te, pos);
			}
		}
		return true;
	}

	@Nonnull
	@Override
	public TileEntity createNewTileEntity(@Nonnull IBlockReader world)
	{
		switch (tier)
		{
			default:
			case 1: return new RelayMK1Tile();
			case 2: return new RelayMK2Tile();
			case 3: return new RelayMK3Tile();
		}
	}

	@Override
	public boolean hasComparatorInputOverride(IBlockState state)
	{
		return true;
	}

	@Override
	public int getComparatorInputOverride(IBlockState state, World world, BlockPos pos)
	{
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof RelayMK1Tile)
		{
			RelayMK1Tile relay = ((RelayMK1Tile) te);
			return MathUtils.scaleToRedstone(relay.getStoredEmc(), relay.getMaximumEmc());
		}
		return 0;
	}

	@Override
	public void onReplaced(IBlockState state, World world, BlockPos pos, IBlockState newState, boolean isMoving)
	{
		TileEntity te = world.getTileEntity(pos);
		if (te != null)
		{
			te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.DOWN)
					.ifPresent(inv -> WorldHelper.dropInventory(inv, world, pos));
		}
		super.onReplaced(state, world, pos, newState, isMoving);
	}

}
