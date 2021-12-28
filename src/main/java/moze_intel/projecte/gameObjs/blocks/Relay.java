package moze_intel.projecte.gameObjs.blocks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import moze_intel.projecte.gameObjs.EnumRelayTier;
import moze_intel.projecte.gameObjs.block_entities.RelayMK1Tile;
import moze_intel.projecte.gameObjs.registration.impl.BlockEntityTypeRegistryObject;
import moze_intel.projecte.gameObjs.registries.PEBlockEntityTypes;
import moze_intel.projecte.utils.MathUtils;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;

public class Relay extends BlockDirection implements PEEntityBlock<RelayMK1Tile> {

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
	public InteractionResult use(@Nonnull BlockState state, Level world, @Nonnull BlockPos pos, @Nonnull Player player, @Nonnull InteractionHand hand, @Nonnull BlockHitResult rtr) {
		if (!world.isClientSide) {
			RelayMK1Tile te = WorldHelper.getTileEntity(RelayMK1Tile.class, world, pos, true);
			if (te != null) {
				NetworkHooks.openGui((ServerPlayer) player, te, pos);
			}
		}
		return InteractionResult.SUCCESS;
	}

	@Nullable
	@Override
	public BlockEntityTypeRegistryObject<? extends RelayMK1Tile> getType() {
		return switch (tier) {
			case MK1 -> PEBlockEntityTypes.RELAY;
			case MK2 -> PEBlockEntityTypes.RELAY_MK2;
			case MK3 -> PEBlockEntityTypes.RELAY_MK3;
			default -> null;
		};
	}

	@Override
	@Deprecated
	public boolean hasAnalogOutputSignal(@Nonnull BlockState state) {
		return true;
	}

	@Override
	@Deprecated
	public int getAnalogOutputSignal(@Nonnull BlockState state, @Nonnull Level world, @Nonnull BlockPos pos) {
		RelayMK1Tile relay = WorldHelper.getTileEntity(RelayMK1Tile.class, world, pos, true);
		if (relay == null) {
			return 0;
		}
		return MathUtils.scaleToRedstone(relay.getStoredEmc(), relay.getMaximumEmc());
	}
}