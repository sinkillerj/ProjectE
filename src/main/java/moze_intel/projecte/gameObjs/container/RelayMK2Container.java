package moze_intel.projecte.gameObjs.container;

import moze_intel.projecte.gameObjs.blocks.Relay;
import moze_intel.projecte.gameObjs.container.slots.SlotPredicates;
import moze_intel.projecte.gameObjs.container.slots.ValidatedSlot;
import moze_intel.projecte.gameObjs.registration.impl.BlockRegistryObject;
import moze_intel.projecte.gameObjs.registries.PEBlocks;
import moze_intel.projecte.gameObjs.registries.PEContainerTypes;
import moze_intel.projecte.gameObjs.block_entities.RelayMK2Tile;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.items.IItemHandler;

public class RelayMK2Container extends RelayMK1Container {

	public RelayMK2Container(int windowId, Inventory playerInv, RelayMK2Tile relay) {
		super(PEContainerTypes.RELAY_MK2_CONTAINER, windowId, playerInv, relay);
	}

	@Override
	void initSlots() {
		IItemHandler input = tile.getInput();
		IItemHandler output = tile.getOutput();
		//Klein star slot
		this.addSlot(new ValidatedSlot(output, 0, 144, 44, SlotPredicates.EMC_HOLDER));
		//Burn slot
		this.addSlot(new ValidatedSlot(input, 0, 84, 44, SlotPredicates.RELAY_INV));
		int counter = 1;
		//Inventory buffer
		for (int i = 2; i >= 0; i--) {
			for (int j = 3; j >= 0; j--) {
				this.addSlot(new ValidatedSlot(input, counter++, 26 + i * 18, 18 + j * 18, SlotPredicates.RELAY_INV));
			}
		}
		addPlayerInventory(16, 101);
	}

	@Override
	protected BlockRegistryObject<Relay, ?> getValidBlock() {
		return PEBlocks.RELAY_MK2;
	}
}