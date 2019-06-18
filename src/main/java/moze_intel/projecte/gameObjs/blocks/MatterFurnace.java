package moze_intel.projecte.gameObjs.blocks;

import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.EnumMatterType;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.tiles.DMFurnaceTile;
import moze_intel.projecte.gameObjs.tiles.RMFurnaceTile;
import moze_intel.projecte.utils.Constants;
import net.minecraft.block.BlockState;
import net.minecraft.block.FurnaceBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;

public class MatterFurnace extends FurnaceBlock
{
	private final EnumMatterType matterType;

	public MatterFurnace(Properties props, EnumMatterType type)
	{
		super(props);
		this.matterType = type;
	}

	@Override
	public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, Direction side, float hitX, float hitY, float hitZ)
	{
		if (!world.isRemote)
		{
			TileEntity te = world.getTileEntity(pos);

			if (te != null && te.getType() == ObjHandler.DM_FURNACE_TILE)
			{
				NetworkHooks.openGui((ServerPlayerEntity) player, (DMFurnaceTile) te, pos);
			}
			else if (te != null && te.getType() == ObjHandler.RM_FURNACE_TILE)
			{
				NetworkHooks.openGui((ServerPlayerEntity) player, (RMFurnaceTile) te, pos);
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
	public int getComparatorInputOverride(BlockState state, World world, BlockPos pos)
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
