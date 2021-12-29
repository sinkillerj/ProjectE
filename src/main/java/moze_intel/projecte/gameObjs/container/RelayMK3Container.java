package moze_intel.projecte.gameObjs.container;

import moze_intel.projecte.gameObjs.block_entities.RelayMK3Tile;
import moze_intel.projecte.gameObjs.blocks.Relay;
import moze_intel.projecte.gameObjs.container.slots.SlotPredicates;
import moze_intel.projecte.gameObjs.container.slots.ValidatedSlot;
import moze_intel.projecte.gameObjs.registration.impl.BlockRegistryObject;
import moze_intel.projecte.gameObjs.registries.PEBlocks;
import moze_intel.projecte.gameObjs.registries.PEContainerTypes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.items.IItemHandler;

public class RelayMK3Container extends RelayMK1Container {

	public RelayMK3Container(int windowId, Inventory playerInv, RelayMK3Tile relay) {
		super(PEContainerTypes.RELAY_MK3_CONTAINER, windowId, playerInv, relay);
	}

	@Override
	void initSlots() {
		IItemHandler input = tile.getInput();
		IItemHandler output = tile.getOutput();
		//Klein star charge
		this.addSlot(new ValidatedSlot(output, 0, 164, 58, SlotPredicates.EMC_HOLDER));
		//Burn slot
		this.addSlot(new ValidatedSlot(input, 0, 104, 58, SlotPredicates.RELAY_INV));
		int counter = 1;
		//Inventory buffer
		for (int i = 3; i >= 0; i--) {
			for (int j = 4; j >= 0; j--) {
				this.addSlot(new ValidatedSlot(input, counter++, 28 + i * 18, 18 + j * 18, SlotPredicates.RELAY_INV));
			}
		}
		addPlayerInventory(26, 113);
	}

	@Override
	protected BlockRegistryObject<Relay, ?> getValidBlock() {
		return PEBlocks.RELAY_MK3;
	}
}