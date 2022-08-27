package moze_intel.projecte.gameObjs.container.slots.transmutation;

import java.math.BigInteger;
import java.util.Collections;
import java.util.Optional;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider.TargetUpdateType;
import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.api.capabilities.block_entity.IEmcStorage.EmcAction;
import moze_intel.projecte.api.capabilities.item.IItemEmcHolder;
import moze_intel.projecte.gameObjs.container.inventory.TransmutationInventory;
import moze_intel.projecte.gameObjs.container.slots.InventoryContainerSlot;
import moze_intel.projecte.gameObjs.container.slots.SlotPredicates;
import moze_intel.projecte.utils.EMCHelper;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class SlotLock extends InventoryContainerSlot {

	private final TransmutationInventory inv;

	public SlotLock(TransmutationInventory inv, int index, int x, int y) {
		super(inv, index, x, y);
		this.inv = inv;
	}

	@Override
	public boolean mayPlace(@NotNull ItemStack stack) {
		return SlotPredicates.RELAY_INV.test(stack);
	}

	@NotNull
	@Override
	public ItemStack remove(int amount) {
		ItemStack stack = super.remove(amount);
		//Decrease the size of the stack
		if (!stack.isEmpty() && inv.isServer()) {
			//Sync the change to the client
			inv.syncChangedSlots(Collections.singletonList(getSlotIndex()), TargetUpdateType.ALL);
		}
		return stack;
	}

	@Override
	public void initialize(@NotNull ItemStack stack) {
		//Note: We don't need to copy any of the logic from set as initialize is only ever called on the client
		super.initialize(stack);
	}

	@Override
	public void set(@NotNull ItemStack stack) {
		super.set(stack);
		if (inv.isServer()) {
			if (stack.isEmpty()) {
				inv.syncChangedSlots(Collections.singletonList(getSlotIndex()), TargetUpdateType.ALL);
			} else {
				if (EMCHelper.doesItemHaveEmc(stack)) {
					inv.handleKnowledge(stack);
				}
				Optional<IItemEmcHolder> capability = stack.getCapability(PECapabilities.EMC_HOLDER_ITEM_CAPABILITY).resolve();
				if (capability.isPresent()) {
					IItemEmcHolder emcHolder = capability.get();
					long actualExtracted = emcHolder.extractEmc(stack, emcHolder.getStoredEmc(stack), EmcAction.EXECUTE);
					if (actualExtracted > 0) {
						inv.syncChangedSlots(Collections.singletonList(getSlotIndex()), TargetUpdateType.NONE);
						inv.addEmc(BigInteger.valueOf(actualExtracted));
					} else {
						//If we didn't move any EMC into the inventory we still need to sync the fact the slot changed so to update targets
						inv.syncChangedSlots(Collections.singletonList(getSlotIndex()), TargetUpdateType.ALL);
					}
				} else {
					//If there is no capability we still need to sync the change
					inv.syncChangedSlots(Collections.singletonList(getSlotIndex()), TargetUpdateType.ALL);
				}
			}
		}
	}

	@Override
	public int getMaxStackSize() {
		return 1;
	}
}