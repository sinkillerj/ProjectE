package moze_intel.projecte.gameObjs.container;

import javax.annotation.Nonnull;
import moze_intel.projecte.gameObjs.container.slots.InventoryContainerSlot;
import moze_intel.projecte.gameObjs.registries.PEBlocks;
import moze_intel.projecte.gameObjs.registries.PEContainerTypes;
import moze_intel.projecte.gameObjs.tiles.AlchChestTile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class AlchChestContainer extends ChestTileEmcContainer<AlchChestTile> {

	public AlchChestContainer(int windowId, PlayerInventory invPlayer, AlchChestTile tile) {
		super(PEContainerTypes.ALCH_CHEST_CONTAINER, windowId, tile);
		IItemHandler inv = this.tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElseThrow(NullPointerException::new);
		//Chest Inventory
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 13; j++) {
				this.addSlot(new InventoryContainerSlot(inv, j + i * 13, 12 + j * 18, 5 + i * 18));
			}
		}
		addPlayerInventory(invPlayer, 48, 152);
	}

	@Override
	public boolean stillValid(@Nonnull PlayerEntity player) {
		return stillValid(player, tile, PEBlocks.ALCHEMICAL_CHEST);
	}
}