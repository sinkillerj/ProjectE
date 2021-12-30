package moze_intel.projecte.gameObjs.blocks;

import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.capabilities.item.IItemEmcHolder;
import moze_intel.projecte.gameObjs.EnumCollectorTier;
import moze_intel.projecte.gameObjs.block_entities.CollectorMK1BlockEntity;
import moze_intel.projecte.gameObjs.registration.impl.BlockEntityTypeRegistryObject;
import moze_intel.projecte.gameObjs.registries.PEBlockEntityTypes;
import moze_intel.projecte.utils.MathUtils;
import moze_intel.projecte.utils.WorldHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.network.NetworkHooks;

public class Collector extends BlockDirection implements PEEntityBlock<CollectorMK1BlockEntity> {

	private final EnumCollectorTier tier;

	public Collector(EnumCollectorTier tier, Properties props) {
		super(props);
		this.tier = tier;
	}

	public EnumCollectorTier getTier() {
		return tier;
	}

	@Nonnull
	@Override
	@Deprecated
	public InteractionResult use(@Nonnull BlockState state, Level level, @Nonnull BlockPos pos, @Nonnull Player player, @Nonnull InteractionHand hand,
			@Nonnull BlockHitResult hit) {
		if (!level.isClientSide) {
			CollectorMK1BlockEntity collector = WorldHelper.getBlockEntity(CollectorMK1BlockEntity.class, level, pos, true);
			if (collector != null) {
				NetworkHooks.openGui((ServerPlayer) player, collector, pos);
			}
		}
		return InteractionResult.SUCCESS;
	}

	@Nullable
	@Override
	@Deprecated
	public MenuProvider getMenuProvider(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos) {
		return WorldHelper.getBlockEntity(CollectorMK1BlockEntity.class, level, pos, true);
	}

	@Nullable
	@Override
	public BlockEntityTypeRegistryObject<? extends CollectorMK1BlockEntity> getType() {
		return switch (tier) {
			case MK1 -> PEBlockEntityTypes.COLLECTOR;
			case MK2 -> PEBlockEntityTypes.COLLECTOR_MK2;
			case MK3 -> PEBlockEntityTypes.COLLECTOR_MK3;
		};
	}

	@Override
	@Deprecated
	public boolean triggerEvent(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, int id, int param) {
		super.triggerEvent(state, level, pos, id, param);
		return triggerBlockEntityEvent(state, level, pos, id, param);
	}

	@Override
	@Deprecated
	public boolean hasAnalogOutputSignal(@Nonnull BlockState state) {
		return true;
	}

	@Override
	@Deprecated
	public int getAnalogOutputSignal(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos) {
		CollectorMK1BlockEntity collector = WorldHelper.getBlockEntity(CollectorMK1BlockEntity.class, level, pos, true);
		if (collector == null) {
			//If something went wrong fallback to default implementation
			return super.getAnalogOutputSignal(state, level, pos);
		}
		Optional<IItemHandler> cap = collector.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, Direction.UP).resolve();
		if (cap.isEmpty()) {
			//If something went wrong fallback to default implementation
			return super.getAnalogOutputSignal(state, level, pos);
		}
		ItemStack charging = cap.get().getStackInSlot(CollectorMK1BlockEntity.UPGRADING_SLOT);
		if (!charging.isEmpty()) {
			Optional<IItemEmcHolder> holderCapability = charging.getCapability(ProjectEAPI.EMC_HOLDER_ITEM_CAPABILITY).resolve();
			if (holderCapability.isPresent()) {
				IItemEmcHolder emcHolder = holderCapability.get();
				return MathUtils.scaleToRedstone(emcHolder.getStoredEmc(charging), emcHolder.getMaximumEmc(charging));
			}
			return MathUtils.scaleToRedstone(collector.getStoredEmc(), collector.getEmcToNextGoal());
		}
		return MathUtils.scaleToRedstone(collector.getStoredEmc(), collector.getMaximumEmc());
	}

	@Override
	@Deprecated
	public void onRemove(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull BlockState newState, boolean isMoving) {
		if (state.getBlock() != newState.getBlock()) {
			CollectorMK1BlockEntity ent = WorldHelper.getBlockEntity(CollectorMK1BlockEntity.class, level, pos);
			if (ent != null) {
				//Clear the ghost slot so calling super doesn't drop the item in it
				ent.clearLocked();
			}
			super.onRemove(state, level, pos, newState, isMoving);
		}
	}
}