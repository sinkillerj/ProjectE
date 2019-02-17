package moze_intel.projecte.gameObjs.blocks;

import io.netty.buffer.Unpooled;
import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.EnumMatterType;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.tiles.DMFurnaceTile;
import moze_intel.projecte.gameObjs.tiles.RMFurnaceTile;
import moze_intel.projecte.utils.Constants;
import net.minecraft.block.BlockFurnace;
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
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;

public class MatterFurnace extends BlockFurnace
{
	private final EnumMatterType matterType;

	public MatterFurnace(Properties props, EnumMatterType type)
	{
		super(props);
		this.matterType = type;
	}

	@Override
	public boolean onBlockActivated(IBlockState state, World world, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		if (!world.isRemote)
		{
			PacketBuffer buf = new PacketBuffer(Unpooled.buffer());
			buf.writeBlockPos(pos);
			TileEntity te = world.getTileEntity(pos);

			if (te != null && te.getType() == ObjHandler.DM_FURNACE_TILE)
			{
				NetworkHooks.openGui((EntityPlayerMP) player, (DMFurnaceTile) te, buf);
			}
			else if (te != null && te.getType() == ObjHandler.RM_FURNACE_TILE)
			{
				NetworkHooks.openGui((EntityPlayerMP) player, (RMFurnaceTile) te, buf);
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
