package moze_intel.projecte.gameObjs.blocks;

import java.util.List;
import java.util.function.Consumer;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.EnumRelayTier;
import moze_intel.projecte.gameObjs.block_entities.RelayMK1BlockEntity;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Relay extends BlockDirection implements PEEntityBlock<RelayMK1BlockEntity> {

	private final EnumRelayTier tier;

	public Relay(EnumRelayTier tier, Properties props) {
		super(props);
		this.tier = tier;
	}

	public EnumRelayTier getTier() {
		return tier;
	}

	@NotNull
	@Override
	@Deprecated
	protected InteractionResult useWithoutItem(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull BlockHitResult rtr) {
		if (level.isClientSide()) {
			return InteractionResult.SUCCESS;
		}
		RelayMK1BlockEntity relay = WorldHelper.getBlockEntity(RelayMK1BlockEntity.class, level, pos, true);
		if (relay != null) {
			player.openMenu(relay, pos);
		}
		return InteractionResult.CONSUME;
	}

	public void addTooltip(ItemStack stack, Consumer<Component> tooltip) {
		if (ProjectEConfig.client.statToolTips.get()) {
			tooltip.accept(PELang.EMC_MAX_OUTPUT_RATE.translateColored(ChatFormatting.DARK_PURPLE, ChatFormatting.BLUE, EMCHelper.formatEmc(tier.getChargeRate())));
			tooltip.accept(PELang.EMC_MAX_STORAGE.translateColored(ChatFormatting.DARK_PURPLE, ChatFormatting.BLUE, EMCHelper.formatEmc(tier.getStorage())));
		}
	}

	@Nullable
	@Override
	public BlockEntityTypeRegistryObject<? extends RelayMK1BlockEntity> getType() {
		return switch (tier) {
			case MK1 -> PEBlockEntityTypes.RELAY;
			case MK2 -> PEBlockEntityTypes.RELAY_MK2;
			case MK3 -> PEBlockEntityTypes.RELAY_MK3;
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
		RelayMK1BlockEntity relay = WorldHelper.getBlockEntity(RelayMK1BlockEntity.class, level, pos, true);
		if (relay == null) {
			return 0;
		}
		return MathUtils.scaleToRedstone(relay.getStoredEmc(), relay.getMaximumEmc());
	}
}
