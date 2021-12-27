package moze_intel.projecte.gameObjs.container.slots;

import net.minecraft.inventory.IInventory;

/**
 * Helper marker class for telling apart the main inventory while attempting to move items
 *
 * @implNote From Mekanism
 */
public class MainInventorySlot extends InsertableSlot {

    public MainInventorySlot(IInventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }
}