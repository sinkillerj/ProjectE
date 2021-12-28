package moze_intel.projecte.gameObjs.container;

import moze_intel.projecte.gameObjs.blocks.Collector;
import moze_intel.projecte.gameObjs.container.slots.SlotGhost;
import moze_intel.projecte.gameObjs.container.slots.SlotPredicates;
import moze_intel.projecte.gameObjs.container.slots.ValidatedSlot;
import moze_intel.projecte.gameObjs.registration.impl.BlockRegistryObject;
import moze_intel.projecte.gameObjs.registries.PEBlocks;
import moze_intel.projecte.gameObjs.registries.PEContainerTypes;
import moze_intel.projecte.gameObjs.block_entities.CollectorMK3Tile;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.items.IItemHandler;

public class CollectorMK3Container extends CollectorMK1Container {

	public CollectorMK3Container(int windowId, Inventory invPlayer, CollectorMK3Tile collector) {
		super(PEContainerTypes.COLLECTOR_MK3_CONTAINER, windowId, invPlayer, collector);
	}

	@Override
	void initSlots(Inventory invPlayer) {
		IItemHandler aux = tile.getAux();
		IItemHandler main = tile.getInput();

		//Klein Star Slot
		this.addSlot(new ValidatedSlot(aux, CollectorMK3Tile.UPGRADING_SLOT, 158, 58, SlotPredicates.COLLECTOR_INV));
		int counter = 0;
		//Fuel Upgrade Slot
		for (int i = 3; i >= 0; i--) {
			for (int j = 3; j >= 0; j--) {
				this.addSlot(new ValidatedSlot(main, counter++, 18 + i * 18, 8 + j * 18, SlotPredicates.COLLECTOR_INV));
			}
		}
		//Upgrade Result
		this.addSlot(new ValidatedSlot(aux, CollectorMK3Tile.UPGRADE_SLOT, 158, 13, SlotPredicates.ALWAYS_FALSE));
		//Upgrade Target
		this.addSlot(new SlotGhost(aux, CollectorMK3Tile.LOCK_SLOT, 187, 36, SlotPredicates.COLLECTOR_LOCK));
		addPlayerInventory(invPlayer, 30, 84);
	}

	@Override
	protected BlockRegistryObject<Collector, ?> getValidBlock() {
		return PEBlocks.COLLECTOR_MK3;
	}
}