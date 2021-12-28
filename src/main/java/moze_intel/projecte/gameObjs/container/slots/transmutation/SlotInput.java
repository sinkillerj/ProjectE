package moze_intel.projecte.gameObjs.container.slots.transmutation;

import java.math.BigInteger;
import java.util.Collections;
import java.util.Optional;
import javax.annotation.Nonnull;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider.TargetUpdateType;
import moze_intel.projecte.api.capabilities.item.IItemEmcHolder;
import moze_intel.projecte.api.capabilities.tile.IEmcStorage.EmcAction;
import moze_intel.projecte.gameObjs.container.inventory.TransmutationInventory;
import moze_intel.projecte.gameObjs.container.slots.InventoryContainerSlot;
import moze_intel.projecte.gameObjs.container.slots.SlotPredicates;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.MathUtils;
import net.minecraft.world.item.ItemStack;

public class SlotInput extends InventoryContainerSlot {

	private final TransmutationInventory inv;

	public SlotInput(TransmutationInventory inv, int index, int x, int y) {
		super(inv, index, x, y);
		this.inv = inv;
	}

	@Override
	public boolean mayPlace(@Nonnull ItemStack stack) {
		return SlotPredicates.RELAY_INV.test(stack);
	}

	@Nonnull
	@Override
	public ItemStack remove(int amount) {
		ItemStack stack = super.remove(amount);
		//Decrease the size of the stack
		if (!stack.isEmpty() && inv.isServer()) {
			//Sync the change to the client
			inv.syncChangedSlots(Collections.singletonList(getSlotIndex()), TargetUpdateType.IF_NEEDED);
		}
		return stack;
	}

	@Override
	public void set(@Nonnull ItemStack stack) {
		super.set(stack);
		if (inv.isServer()) {
			if (stack.isEmpty()) {
				inv.syncChangedSlots(Collections.singletonList(getSlotIndex()), TargetUpdateType.ALL);
			} else {
				if (EMCHelper.doesItemHaveEmc(stack)) {
					inv.handleKnowledge(stack);
				}
				Optional<IItemEmcHolder> capability = stack.getCapability(ProjectEAPI.EMC_HOLDER_ITEM_CAPABILITY).resolve();
				if (capability.isPresent()) {
					IItemEmcHolder emcHolder = capability.get();
					//Get the emc that the inventory has that is not in any stars
					long shrunkenAvailableEMC = MathUtils.clampToLong(inv.provider.getEmc());
					//try to insert it
					long actualInserted = emcHolder.insertEmc(stack, shrunkenAvailableEMC, EmcAction.EXECUTE);
					if (actualInserted > 0) {
						//if we actually managed to insert some sync the slots changed, but don't update targets
						// as that will be done by removing the emc and syncing how much is stored there
						inv.syncChangedSlots(Collections.singletonList(getSlotIndex()), TargetUpdateType.NONE);
						inv.removeEmc(BigInteger.valueOf(actualInserted));
					} else if (emcHolder.getStoredEmc(stack) > 0) {
						//If we didn't manage to insert any into our star, and we do have emc stored
						// update the targets
						inv.syncChangedSlots(Collections.singletonList(getSlotIndex()), TargetUpdateType.ALL);
					} else {
						//If we didn't manage to insert any into our star, and we don't have any emc stored
						// don't bother updating the targets
						inv.syncChangedSlots(Collections.singletonList(getSlotIndex()), TargetUpdateType.NONE);
					}
				} else {
					//Update the fact the slots changed but don't bother updating targets
					inv.syncChangedSlots(Collections.singletonList(getSlotIndex()), TargetUpdateType.NONE);
				}
			}
		}
	}

	@Override
	public int getMaxStackSize() {
		return 1;
	}
}