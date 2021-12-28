package moze_intel.projecte.gameObjs.block_entities;

import javax.annotation.Nonnull;
import moze_intel.projecte.gameObjs.EnumRelayTier;
import moze_intel.projecte.gameObjs.container.RelayMK3Container;
import moze_intel.projecte.gameObjs.registries.PEBlockEntityTypes;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;

public class RelayMK3Tile extends RelayMK1Tile {

	public RelayMK3Tile(BlockPos pos, BlockState state) {
		super(PEBlockEntityTypes.RELAY_MK3, pos, state, 21, EnumRelayTier.MK3);
	}

	@Nonnull
	@Override
	public AbstractContainerMenu createMenu(int windowId, @Nonnull Inventory playerInventory, @Nonnull Player player) {
		return new RelayMK3Container(windowId, playerInventory, this);
	}

	@Nonnull
	@Override
	public Component getDisplayName() {
		return PELang.GUI_RELAY_MK3.translate();
	}

	@Override
	protected double getBonusToAdd() {
		return 0.5;
	}
}