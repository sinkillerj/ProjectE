package moze_intel.projecte.gameObjs.blocks;

import java.util.List;
import java.util.function.Consumer;
import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.api.capabilities.item.IItemEmcHolder;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.EnumCollectorTier;
import moze_intel.projecte.gameObjs.block_entities.CollectorMK1BlockEntity;
import moze_intel.projecte.gameObjs.registration.impl.BlockEntityTypeRegistryObject;
import moze_intel.projecte.gameObjs.registries.PEBlockEntityTypes;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.MathUtils;
import moze_intel.projecte.utils.WorldHelper;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import moze_intel.projecte.api.inventory.PEItemStacksHandler;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Collector extends BlockDirection implements PEEntityBlock<CollectorMK1BlockEntity> {

	private final EnumCollectorTier tier;

	public Collector(EnumCollectorTier tier, Properties props) {
		super(props);
		this.tier = tier;
	}

	public EnumCollectorTier getTier() {
		return tier;
	}

	@NotNull
	@Override
	@Deprecated
	protected InteractionResult useWithoutItem(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull BlockHitResult hit) {
		if (level.isClientSide()) {
			return InteractionResult.SUCCESS;
		}
		CollectorMK1BlockEntity collector = WorldHelper.getBlockEntity(CollectorMK1BlockEntity.class, level, pos, true);
		if (collector != null) {
			player.openMenu(collector, pos);
		}
		return InteractionResult.CONSUME;
	}

	public void addTooltip(ItemStack stack, Consumer<Component> tooltip) {
		if (ProjectEConfig.client.statToolTips.get()) {
			tooltip.accept(PELang.EMC_MAX_GEN_RATE.translateColored(ChatFormatting.DARK_PURPLE, ChatFormatting.BLUE, EMCHelper.formatEmc(tier.getGenRate())));
			tooltip.accept(PELang.EMC_MAX_STORAGE.translateColored(ChatFormatting.DARK_PURPLE, ChatFormatting.BLUE, EMCHelper.formatEmc(tier.getStorage())));
		}
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
	public boolean triggerEvent(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, int id, int param) {
		super.triggerEvent(state, level, pos, id, param);
		return triggerBlockEntityEvent(state, level, pos, id, param);
	}

	@Override
	@Deprecated
	public boolean hasAnalogOutputSignal(@NotNull BlockState state) {
		return true;
	}

	@Deprecated
	public int getAnalogOutputSignal(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Direction direction) {
		CollectorMK1BlockEntity collector = WorldHelper.getBlockEntity(CollectorMK1BlockEntity.class, level, pos, true);
		if (collector == null) {
			return 0;
		}
		ResourceHandler<ItemResource> handler = WorldHelper.getItemHandler(level, pos, state, collector, Direction.UP);
		if (handler == null) {
			return 0;
		}
		ItemStack charging = PEItemStacksHandler.getStack(handler, CollectorMK1BlockEntity.UPGRADING_SLOT);
		if (charging.isEmpty()) {
			return MathUtils.scaleToRedstone(collector.getStoredEmc(), collector.getMaximumEmc());
		}
		IItemEmcHolder emcHolder = charging.getCapability(PECapabilities.EMC_HOLDER_ITEM_CAPABILITY);
		if (emcHolder != null) {
			return MathUtils.scaleToRedstone(emcHolder.getStoredEmc(charging), emcHolder.getMaximumEmc(charging));
		}
		return MathUtils.scaleToRedstone(collector.getStoredEmc(), collector.getEmcToNextGoal());
	}

	@Override
	public void onBlockStateChange(LevelReader level, BlockPos pos, BlockState oldState, BlockState newState) {
		if (oldState.getBlock() != newState.getBlock() && level instanceof Level world) {
			CollectorMK1BlockEntity ent = WorldHelper.getBlockEntity(CollectorMK1BlockEntity.class, world, pos);
			if (ent != null) {
				ent.clearLocked();
			}
		}
		super.onBlockStateChange(level, pos, oldState, newState);
	}
}
