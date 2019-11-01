package moze_intel.projecte.gameObjs.tiles;

import javax.annotation.Nonnull;
import moze_intel.projecte.gameObjs.EnumCollectorTier;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.container.CollectorMK2Container;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;

public class CollectorMK2Tile extends CollectorMK1Tile {

	public CollectorMK2Tile() {
		super(ObjHandler.COLLECTOR_MK2_TILE, EnumCollectorTier.MK2);
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
}