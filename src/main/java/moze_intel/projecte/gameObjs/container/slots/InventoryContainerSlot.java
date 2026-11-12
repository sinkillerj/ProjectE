package moze_intel.projecte.gameObjs.container.slots;

import moze_intel.projecte.api.inventory.PEItemStacksHandler;
import moze_intel.projecte.utils.PECombinedItemStacks;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.IndexModifier;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot;

public class InventoryContainerSlot extends ResourceHandlerSlot implements IInventoryContainerSlot {

	public InventoryContainerSlot(ResourceHandler<ItemResource> itemHandler, int index, int x, int y) {
		super(itemHandler, PEItemStacksHandler.requireModifier(itemHandler), index, x, y);
	}

	public InventoryContainerSlot(PEItemStacksHandler itemHandler, int index, int x, int y) {
		super(itemHandler, itemHandler, index, x, y);
	}

	public InventoryContainerSlot(PECombinedItemStacks itemHandler, int index, int x, int y) {
		super(itemHandler, itemHandler, index, x, y);
	}

	public InventoryContainerSlot(ResourceHandler<ItemResource> handler, IndexModifier<ItemResource> modifier, int index, int x, int y) {
		super(handler, modifier, index, x, y);
	}

	@Override
	public int getMaxStackSize(ItemStack stack) {
		return Math.min(getMaxStackSize(), stack.getMaxStackSize());
	}
}
