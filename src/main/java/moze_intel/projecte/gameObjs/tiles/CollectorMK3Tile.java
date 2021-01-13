package moze_intel.projecte.gameObjs.tiles;

import javax.annotation.Nonnull;
import moze_intel.projecte.gameObjs.EnumCollectorTier;
import moze_intel.projecte.gameObjs.container.CollectorMK3Container;
import moze_intel.projecte.gameObjs.registries.PEBlocks;
import moze_intel.projecte.gameObjs.registries.PETileEntityTypes;
import moze_intel.projecte.utils.text.TextComponentUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.text.ITextComponent;

public class CollectorMK3Tile extends CollectorMK1Tile {

	public CollectorMK3Tile() {
		super(PETileEntityTypes.COLLECTOR_MK3.get(), EnumCollectorTier.MK3);
	}

	@Override
	protected int getInvSize() {
		return 16;
	}

	@Nonnull
	@Override
	public Container createMenu(int windowId, @Nonnull PlayerInventory playerInventory, @Nonnull PlayerEntity playerIn) {
		return new CollectorMK3Container(windowId, playerInventory, this);
	}

	@Nonnull
	@Override
	public ITextComponent getDisplayName() {
		return TextComponentUtil.build(PEBlocks.COLLECTOR_MK3);
	}
}