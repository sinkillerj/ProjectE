package moze_intel.projecte.gameObjs.container.slots.transmutation;

import javax.annotation.Nonnull;
import moze_intel.projecte.gameObjs.container.inventory.TransmutationInventory;
import moze_intel.projecte.gameObjs.container.slots.InventoryContainerSlot;
import moze_intel.projecte.gameObjs.items.Tome;
import moze_intel.projecte.utils.EMCHelper;
import net.minecraft.item.ItemStack;

public class SlotUnlearn extends InventoryContainerSlot {

	private final TransmutationInventory inv;

	public SlotUnlearn(TransmutationInventory inv, int index, int x, int y) {
		super(inv, index, x, y);
		this.inv = inv;
	}

	@Override
	public boolean mayPlace(@Nonnull ItemStack stack) {
		return !this.hasItem() && (EMCHelper.doesItemHaveEmc(stack) || stack.getItem() instanceof Tome);
	}

	@Override
	public void set(@Nonnull ItemStack stack) {
		if (inv.isServer() && !stack.isEmpty()) {
			inv.handleUnlearn(stack.copy());
		}
		super.set(stack);
	}

	@Override
	public int getMaxStackSize() {
		return 1;
	}
}