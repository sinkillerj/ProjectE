package moze_intel.projecte.gameObjs.container.slots;

import net.minecraft.inventory.IInventory;

/**
 * Helper marker class for telling apart the hot bar while attempting to move items
 *
 * @implNote From Mekanism
 */
public class HotBarSlot extends InsertableSlot {

    public HotBarSlot(IInventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }
}