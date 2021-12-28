package moze_intel.projecte.gameObjs.block_entities;

import javax.annotation.Nonnull;
import moze_intel.projecte.gameObjs.container.RMFurnaceContainer;
import moze_intel.projecte.gameObjs.registries.PEBlockEntityTypes;
import moze_intel.projecte.utils.text.PELang;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;

public class RMFurnaceTile extends DMFurnaceTile {

	public RMFurnaceTile(BlockPos pos, BlockState state) {
		super(PEBlockEntityTypes.RED_MATTER_FURNACE, pos, state, 3, 4);
	}

	@Override
	protected int getInvSize() {
		return 13;
	}

	@Override
	protected float getOreDoubleChance() {
		return 1F;
	}

	@Override
	public int getCookProgressScaled(int value) {
		return (furnaceCookTime + (isBurning() && canSmelt() ? 1 : 0)) * value / ticksBeforeSmelt;
	}

	@Nonnull
	@Override
	public AbstractContainerMenu createMenu(int windowId, @Nonnull Inventory inv, @Nonnull Player player) {
		return new RMFurnaceContainer(windowId, inv, this);
	}

	@Nonnull
	@Override
	public Component getDisplayName() {
		return PELang.GUI_RED_MATTER_FURNACE.translate();
	}
}