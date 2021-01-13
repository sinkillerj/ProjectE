package moze_intel.projecte.gameObjs.tiles;

import javax.annotation.Nonnull;
import moze_intel.projecte.gameObjs.EnumCollectorTier;
import moze_intel.projecte.gameObjs.container.CollectorMK2Container;
import moze_intel.projecte.gameObjs.registries.PEBlocks;
import moze_intel.projecte.gameObjs.registries.PETileEntityTypes;
import moze_intel.projecte.utils.text.TextComponentUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.text.ITextComponent;

public class CollectorMK2Tile extends CollectorMK1Tile {

	public CollectorMK2Tile() {
		super(PETileEntityTypes.COLLECTOR_MK2.get(), EnumCollectorTier.MK2);
	}

	@Override
	protected int getInvSize() {
		return 12;
	}

	@Nonnull
	@Override
	public Container createMenu(int windowId, @Nonnull PlayerInventory playerInventory, @Nonnull PlayerEntity playerIn) {
		return new CollectorMK2Container(windowId, playerInventory, this);
	}

	@Nonnull
	@Override
	public ITextComponent getDisplayName() {
		return TextComponentUtil.build(PEBlocks.COLLECTOR_MK2);
	}
}