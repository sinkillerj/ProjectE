package moze_intel.projecte.gameObjs.blocks;

import moze_intel.projecte.api.item.IItemEmc;
import moze_intel.projecte.gameObjs.tiles.CollectorMK1Tile;
import moze_intel.projecte.gameObjs.tiles.CollectorMK2Tile;
import moze_intel.projecte.gameObjs.tiles.CollectorMK3Tile;
import moze_intel.projecte.utils.MathUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Collector extends BlockDirection
{
	private final int tier;
	
	public Collector(int tier, Properties props)
	{
		super(props);
		this.tier = tier;
	}
	
	@Override
	public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit)
	{
		if (!world.isRemote)
		{
			TileEntity te = world.getTileEntity(pos);
			if (te instanceof CollectorMK1Tile)
			{
				NetworkHooks.openGui((ServerPlayerEntity) player, (CollectorMK1Tile) te, pos);
			}
		}
		return true;
	}

	@Nullable
	@Override
	public INamedContainerProvider getContainer(BlockState state, World world, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof CollectorMK1Tile) {
			return ((CollectorMK1Tile) te);
		}
		return null;
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(@Nonnull BlockState state, @Nonnull IBlockReader world) {
		switch (tier) {
			case 1: return new CollectorMK1Tile();
			case 2: return new CollectorMK2Tile();
			case 3: return new CollectorMK3Tile();
			default: return null;
		}
	}

	@Override
	public boolean hasComparatorInputOverride(BlockState state)
	{
		return true;
	}

	@Override
	public int getComparatorInputOverride(BlockState state, World world, BlockPos pos)
	{
		CollectorMK1Tile tile = ((CollectorMK1Tile) world.getTileEntity(pos));
		ItemStack charging = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.UP).orElseThrow(NullPointerException::new).getStackInSlot(CollectorMK1Tile.UPGRADING_SLOT);
		if (!charging.isEmpty())
		{
			if (charging.getItem() instanceof IItemEmc)
			{
				IItemEmc itemEmc = ((IItemEmc) charging.getItem());
				long max = itemEmc.getMaximumEmc(charging);
				long current = itemEmc.getStoredEmc(charging);
				return MathUtils.scaleToRedstone(current, max);
			} else
			{
				long needed = tile.getEmcToNextGoal();
				long current = tile.getStoredEmc();
				return MathUtils.scaleToRedstone(current, needed);
			}
		} else
		{
			return MathUtils.scaleToRedstone(tile.getStoredEmc(), tile.getMaximumEmc());
		}
	}

	@Override
	public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving)
	{
		TileEntity ent = world.getTileEntity(pos);
		if (ent != null)
		{
			ent.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.UP).ifPresent(handler -> {
				for (int i = 0; i < handler.getSlots(); i++)
				{
					if (i != CollectorMK1Tile.LOCK_SLOT && !handler.getStackInSlot(i).isEmpty())
					{
						InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), handler.getStackInSlot(i));
					}
				}
			});
		}
		super.onReplaced(state, world, pos, newState, isMoving);
	}
}
