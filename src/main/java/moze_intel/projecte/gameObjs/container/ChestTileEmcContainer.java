package moze_intel.projecte.gameObjs.container;

import javax.annotation.Nonnull;
import moze_intel.projecte.gameObjs.block_entities.ChestTileEmc;
import moze_intel.projecte.gameObjs.registration.impl.ContainerTypeRegistryObject;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;

public abstract class ChestTileEmcContainer<TILE extends ChestTileEmc> extends PEContainer {

	protected final TILE tile;

	protected ChestTileEmcContainer(ContainerTypeRegistryObject<? extends ChestTileEmcContainer<TILE>> typeRO, int windowId, Inventory playerInv, TILE tile) {
		super(typeRO, windowId, playerInv);
		this.tile = tile;
		this.tile.startOpen(playerInv.player);
	}

	@Override
	public void removed(@Nonnull Player player) {
		super.removed(player);
		tile.stopOpen(player);
	}

	public boolean blockEntityMatches(ChestTileEmc chest) {
		return chest == tile;
	}
}