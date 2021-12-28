package moze_intel.projecte.gameObjs.container;

import moze_intel.projecte.gameObjs.blocks.Collector;
import moze_intel.projecte.gameObjs.container.slots.SlotGhost;
import moze_intel.projecte.gameObjs.container.slots.SlotPredicates;
import moze_intel.projecte.gameObjs.container.slots.ValidatedSlot;
import moze_intel.projecte.gameObjs.registration.impl.BlockRegistryObject;
import moze_intel.projecte.gameObjs.registries.PEBlocks;
import moze_intel.projecte.gameObjs.registries.PEContainerTypes;
import moze_intel.projecte.gameObjs.block_entities.CollectorMK2Tile;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.items.IItemHandler;

public class CollectorMK2Container extends CollectorMK1Container {

	public CollectorMK2Container(int windowId, Inventory playerInv, CollectorMK2Tile collector) {
		super(PEContainerTypes.COLLECTOR_MK2_CONTAINER, windowId, playerInv, collector);
	}

	@Override
	void initSlots() {
		IItemHandler aux = tile.getAux();
		IItemHandler main = tile.getInput();

		//Klein Star Slot
		this.addSlot(new ValidatedSlot(aux, CollectorMK2Tile.UPGRADING_SLOT, 140, 58, SlotPredicates.COLLECTOR_INV));
		int counter = 0;
		//Fuel Upgrade storage
		for (int i = 2; i >= 0; i--) {
			for (int j = 3; j >= 0; j--) {
				this.addSlot(new ValidatedSlot(main, counter++, 18 + i * 18, 8 + j * 18, SlotPredicates.COLLECTOR_INV));
			}
		}
		//Upgrade Result
		this.addSlot(new ValidatedSlot(aux, CollectorMK2Tile.UPGRADE_SLOT, 140, 13, SlotPredicates.ALWAYS_FALSE));
		//Upgrade Target
		this.addSlot(new SlotGhost(aux, CollectorMK2Tile.LOCK_SLOT, 169, 36, SlotPredicates.COLLECTOR_LOCK));
		addPlayerInventory(20, 84);
	}

	@Override
	protected BlockRegistryObject<Collector, ?> getValidBlock() {
		return PEBlocks.COLLECTOR_MK2;
	}
}