package moze_intel.projecte.gameObjs.blocks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import moze_intel.projecte.gameObjs.EnumRelayTier;
import moze_intel.projecte.gameObjs.tiles.RelayMK1Tile;
import moze_intel.projecte.gameObjs.tiles.RelayMK2Tile;
import moze_intel.projecte.gameObjs.tiles.RelayMK3Tile;
import moze_intel.projecte.utils.MathUtils;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;

public class Relay extends BlockDirection {

	private final EnumRelayTier tier;

	public Relay(EnumRelayTier tier, Properties props) {
		super(props);
		this.tier = tier;
	}

	public EnumRelayTier getTier() {
		return tier;
	}

	@Nonnull
	@Override
	@Deprecated
	public ActionResultType onBlockActivated(@Nonnull BlockState state, World world, @Nonnull BlockPos pos, @Nonnull PlayerEntity player, @Nonnull Hand hand, @Nonnull BlockRayTraceResult rtr) {
		if (!world.isRemote) {
			RelayMK1Tile te = WorldHelper.getTileEntity(RelayMK1Tile.class, world, pos, true);
			if (te != null) {
				NetworkHooks.openGui((ServerPlayerEntity) player, te, pos);
			}
		}
		return ActionResultType.SUCCESS;
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(@Nonnull BlockState state, @Nonnull IBlockReader world) {
		switch (tier) {
			case MK1:
				return new RelayMK1Tile();
			case MK2:
				return new RelayMK2Tile();
			case MK3:
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
	public int getComparatorInputOverride(@Nonnull BlockState state, @Nonnull World world, @Nonnull BlockPos pos) {
		RelayMK1Tile relay = WorldHelper.getTileEntity(RelayMK1Tile.class, world, pos, true);
		if (relay == null) {
			return 0;
		}
		return MathUtils.scaleToRedstone(relay.getStoredEmc(), relay.getMaximumEmc());
	}
}