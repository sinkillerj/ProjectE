package moze_intel.projecte.gameObjs.container;

import javax.annotation.Nonnull;
import moze_intel.projecte.gameObjs.registration.impl.ContainerTypeRegistryObject;
import moze_intel.projecte.gameObjs.tiles.ChestTileEmc;
import net.minecraft.entity.player.PlayerEntity;

public abstract class ChestTileEmcContainer<TILE extends ChestTileEmc> extends PEContainer {

	protected final TILE tile;

	protected ChestTileEmcContainer(ContainerTypeRegistryObject<? extends ChestTileEmcContainer<TILE>> typeRO, int windowId, TILE tile) {
		super(typeRO, windowId);
		this.tile = tile;
		this.tile.numPlayersUsing++;
	}

	@Override
	public void removed(@Nonnull PlayerEntity player) {
		super.removed(player);
		tile.numPlayersUsing--;
	}
}