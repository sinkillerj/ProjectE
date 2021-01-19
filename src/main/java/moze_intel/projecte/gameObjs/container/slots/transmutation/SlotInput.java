package moze_intel.projecte.gameObjs.container.slots.transmutation;

import java.math.BigInteger;
import java.util.Collections;
import javax.annotation.Nonnull;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider.TargetUpdateType;
import moze_intel.projecte.api.capabilities.tile.IEmcStorage.EmcAction;
import moze_intel.projecte.gameObjs.container.inventory.TransmutationInventory;
import moze_intel.projecte.gameObjs.container.slots.SlotPredicates;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.MathUtils;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

public class SlotInput extends SlotItemHandler {

	private final TransmutationInventory inv;

	public SlotInput(TransmutationInventory inv, int index, int x, int y) {
		super(inv, index, x, y);
		this.inv = inv;
	}

	@Override
	public boolean isItemValid(@Nonnull ItemStack stack) {
		return SlotPredicates.RELAY_INV.test(stack);
	}

	@Nonnull
	@Override
	public ItemStack decrStackSize(int amount) {
		ItemStack stack = super.decrStackSize(amount);
		//Decrease the size of the stack
		if (!stack.isEmpty() && inv.isServer()) {
			//Sync the change to the client
			inv.syncChangedSlots(Collections.singletonList(getSlotIndex()), TargetUpdateType.IF_NEEDED);
		}
		return stack;
	}

	@Override
	public void putStack(@Nonnull ItemStack stack) {
		super.putStack(stack);
		if (inv.isServer()) {
			if (stack.isEmpty()) {
				inv.syncChangedSlots(Collections.singletonList(getSlotIndex()), TargetUpdateType.ALL);
			} else {
				if (EMCHelper.doesItemHaveEmc(stack)) {
					inv.handleKnowledge(stack);
				}
				stack.getCapability(ProjectEAPI.EMC_HOLDER_ITEM_CAPABILITY).ifPresent(emcHolder -> {
					long shrunkenAvailableEMC = MathUtils.clampToLong(inv.getAvailableEmc());
					long actualInserted = emcHolder.insertEmc(stack, shrunkenAvailableEMC, EmcAction.EXECUTE);
					if (actualInserted > 0) {
						inv.syncChangedSlots(Collections.singletonList(getSlotIndex()), TargetUpdateType.NONE);
						inv.removeEmc(BigInteger.valueOf(actualInserted));
					}
				});
			}
		}
	}

	@Override
	public int getSlotStackLimit() {
		return 1;
	}

	@Override
	public int getItemStackLimit(@Nonnull ItemStack stack) {
		return 1;
	}
}