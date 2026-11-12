package moze_intel.projecte.gameObjs.container.slots.transmutation;

import it.unimi.dsi.fastutil.ints.IntList;
import java.math.BigInteger;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider.TargetUpdateType;
import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.api.capabilities.block_entity.IEmcStorage.EmcAction;
import moze_intel.projecte.api.capabilities.item.IItemEmcHolder;
import moze_intel.projecte.api.proxy.IEMCProxy;
import moze_intel.projecte.gameObjs.container.inventory.TransmutationInventory;
import moze_intel.projecte.gameObjs.container.slots.InventoryContainerSlot;
import moze_intel.projecte.gameObjs.container.slots.SlotPredicates;
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
			inv.syncChangedSlots(IntList.of(getSlotIndex()), TargetUpdateType.ALL);
		}
		return stack;
	}

	@Override
	protected void setStackCopy(@NotNull ItemStack stack) {
		super.setStackCopy(stack);
		if (inv.isServer()) {
			if (stack.isEmpty()) {
				inv.syncChangedSlots(IntList.of(getSlotIndex()), TargetUpdateType.ALL);
			} else {
				if (IEMCProxy.INSTANCE.hasValue(stack)) {
					inv.handleKnowledge(stack);
				}
				IItemEmcHolder emcHolder = stack.getCapability(PECapabilities.EMC_HOLDER_ITEM_CAPABILITY);
				if (emcHolder != null) {
					long actualExtracted = emcHolder.extractEmc(stack, emcHolder.getStoredEmc(stack), EmcAction.EXECUTE);
					if (actualExtracted > 0) {
						inv.syncChangedSlots(IntList.of(getSlotIndex()), TargetUpdateType.NONE);
						inv.addEmc(BigInteger.valueOf(actualExtracted));
					} else {
						//If we didn't move any EMC into the inventory we still need to sync the fact the slot changed so to update targets
						inv.syncChangedSlots(IntList.of(getSlotIndex()), TargetUpdateType.ALL);
					}
				} else {
					//If there is no capability we still need to sync the change
					inv.syncChangedSlots(IntList.of(getSlotIndex()), TargetUpdateType.ALL);
				}
			}
		}
	}

	@Override
	public int getMaxStackSize() {
		return 1;
	}
}
