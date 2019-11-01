package moze_intel.projecte.gameObjs.tiles;

import javax.annotation.Nonnull;
import moze_intel.projecte.gameObjs.EnumCollectorTier;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.container.CollectorMK3Container;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;

public class CollectorMK3Tile extends CollectorMK1Tile {

	public CollectorMK3Tile() {
		super(ObjHandler.COLLECTOR_MK3_TILE, EnumCollectorTier.MK3);
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
}