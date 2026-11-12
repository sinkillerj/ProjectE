package moze_intel.projecte.utils;

import moze_intel.projecte.api.inventory.PEItemStacksHandler;
import net.neoforged.neoforge.transfer.CombinedResourceHandler;
import net.neoforged.neoforge.transfer.IndexModifier;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;

/**
 * Combined item handler with direct slot mutation for {@link net.neoforged.neoforge.transfer.item.ResourceHandlerSlot}.
 */
public class PECombinedItemStacks extends CombinedResourceHandler<ItemResource> implements IndexModifier<ItemResource> {

	@SafeVarargs
	public PECombinedItemStacks(ResourceHandler<ItemResource>... handlers) {
		super(handlers);
	}

	@Override
	public void set(int index, ItemResource resource, int amount) {
		int handlerIndex = getHandlerIndex(index);
		ResourceHandler<ItemResource> handler = getHandlerFromIndex(handlerIndex);
		int slot = getSlotFromIndex(index, handlerIndex);
		PEItemStacksHandler.requireModifier(handler).set(slot, resource, amount);
	}

	public PEItemStacksHandler getStackHandler(int handlerIndex) {
		return (PEItemStacksHandler) getHandlerFromIndex(handlerIndex);
	}
}
