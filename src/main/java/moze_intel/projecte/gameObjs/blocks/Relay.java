package moze_intel.projecte.gameObjs.blocks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import moze_intel.projecte.gameObjs.tiles.RelayMK1Tile;
import moze_intel.projecte.gameObjs.tiles.RelayMK2Tile;
import moze_intel.projecte.gameObjs.tiles.RelayMK3Tile;
import moze_intel.projecte.utils.MathUtils;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;

public class Relay extends BlockDirection {

	private final int tier;

	public Relay(int tier, Properties props) {
		super(props);
		this.tier = tier;
	}

	@Override
	@Deprecated
	public boolean onBlockActivated(@Nonnull BlockState state, World world, @Nonnull BlockPos pos, @Nonnull PlayerEntity player, @Nonnull Hand hand, @Nonnull BlockRayTraceResult rtr) {
		if (!world.isRemote) {
			TileEntity te = world.getTileEntity(pos);
			if (te instanceof RelayMK1Tile) {
				NetworkHooks.openGui((ServerPlayerEntity) player, (RelayMK1Tile) te, pos);
			}
		}
		return true;
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(@Nonnull BlockState state, @Nonnull IBlockReader world) {
		switch (tier) {
			case 1:
				return new RelayMK1Tile();
			case 2:
				return new RelayMK2Tile();
			case 3:
				return new RelayMK3Tile();
			default:
				return null;
		}
	}

	@Override
	@Deprecated
	public boolean hasComparatorInputOverride(@Nonnull BlockState state) {
		return true;
	}

	@Override
	@Deprecated
	public int getComparatorInputOverride(@Nonnull BlockState state, World world, @Nonnull BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof RelayMK1Tile) {
			RelayMK1Tile relay = ((RelayMK1Tile) te);
			return MathUtils.scaleToRedstone(relay.getStoredEmc(), relay.getMaximumEmc());
		}
		return 0;
	}

	@Override
	public void onReplaced(BlockState state, World world, @Nonnull BlockPos pos, @Nonnull BlockState newState, boolean isMoving) {
		TileEntity te = world.getTileEntity(pos);
		if (te != null) {
			te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.DOWN).ifPresent(inv -> WorldHelper.dropInventory(inv, world, pos));
		}
		super.onReplaced(state, world, pos, newState, isMoving);
	}
}