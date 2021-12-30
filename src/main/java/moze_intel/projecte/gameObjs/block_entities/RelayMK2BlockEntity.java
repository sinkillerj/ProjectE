package moze_intel.projecte.gameObjs.block_entities;

import javax.annotation.Nonnull;
import moze_intel.projecte.gameObjs.EnumRelayTier;
import moze_intel.projecte.gameObjs.container.RelayMK2Container;
import moze_intel.projecte.gameObjs.registries.PEBlockEntityTypes;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;

public class RelayMK2BlockEntity extends RelayMK1BlockEntity {

	public RelayMK2BlockEntity(BlockPos pos, BlockState state) {
		super(PEBlockEntityTypes.RELAY_MK2, pos, state, 13, EnumRelayTier.MK2);
	}

	@Nonnull
	@Override
	public AbstractContainerMenu createMenu(int windowId, @Nonnull Inventory playerInventory, @Nonnull Player player) {
		return new RelayMK2Container(windowId, playerInventory, this);
	}

	@Nonnull
	@Override
	public Component getDisplayName() {
		return PELang.GUI_RELAY_MK2.translate();
	}

	@Override
	protected double getBonusToAdd() {
		return 0.15;
	}
}