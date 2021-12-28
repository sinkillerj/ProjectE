package moze_intel.projecte.gameObjs.container;

import javax.annotation.Nonnull;
import moze_intel.projecte.gameObjs.registration.impl.ContainerTypeRegistryObject;
import moze_intel.projecte.gameObjs.block_entities.ChestTileEmc;
import net.minecraft.world.entity.player.Player;

public abstract class ChestTileEmcContainer<TILE extends ChestTileEmc> extends PEContainer {

	protected final TILE tile;

	protected ChestTileEmcContainer(ContainerTypeRegistryObject<? extends ChestTileEmcContainer<TILE>> typeRO, int windowId, TILE tile) {
		super(typeRO, windowId);
		this.tile = tile;
		this.tile.numPlayersUsing++;
	}

	@Override
	public void removed(@Nonnull Player player) {
		super.removed(player);
		tile.numPlayersUsing--;
	}
}