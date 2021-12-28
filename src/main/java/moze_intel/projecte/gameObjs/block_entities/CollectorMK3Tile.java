package moze_intel.projecte.gameObjs.block_entities;

import javax.annotation.Nonnull;
import moze_intel.projecte.gameObjs.EnumCollectorTier;
import moze_intel.projecte.gameObjs.container.CollectorMK3Container;
import moze_intel.projecte.gameObjs.registries.PEBlocks;
import moze_intel.projecte.gameObjs.registries.PEBlockEntityTypes;
import moze_intel.projecte.utils.text.TextComponentUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;

public class CollectorMK3Tile extends CollectorMK1Tile {

	public CollectorMK3Tile(BlockPos pos, BlockState state) {
		super(PEBlockEntityTypes.COLLECTOR_MK3, pos, state, EnumCollectorTier.MK3);
	}

	@Override
	protected int getInvSize() {
		return 16;
	}

	@Nonnull
	@Override
	public AbstractContainerMenu createMenu(int windowId, @Nonnull Inventory playerInventory, @Nonnull Player playerIn) {
		return new CollectorMK3Container(windowId, playerInventory, this);
	}

	@Nonnull
	@Override
	public Component getDisplayName() {
		return TextComponentUtil.build(PEBlocks.COLLECTOR_MK3);
	}
}