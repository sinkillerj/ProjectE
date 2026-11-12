package moze_intel.projecte.gameObjs.container.slots;

import moze_intel.projecte.api.inventory.PEItemStacksHandler;
import moze_intel.projecte.utils.PECombinedItemStacks;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;

public class InventoryContainerCopySlot extends InventoryContainerSlot implements IInventoryContainerSlot {

	public InventoryContainerCopySlot(ResourceHandler<ItemResource> itemHandler, int index, int x, int y) {
		super(itemHandler, index, x, y);
	}

	public InventoryContainerCopySlot(PEItemStacksHandler itemHandler, int index, int x, int y) {
		super(itemHandler, index, x, y);
	}

	public InventoryContainerCopySlot(PECombinedItemStacks itemHandler, int index, int x, int y) {
		super(itemHandler, index, x, y);
	}

	@Override
	public int getMaxStackSize(ItemStack stack) {
		return Math.min(getMaxStackSize(), stack.getMaxStackSize());
	}
}
