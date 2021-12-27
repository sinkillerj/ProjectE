package moze_intel.projecte.gameObjs.container.slots.transmutation;

import java.math.BigInteger;
import javax.annotation.Nonnull;
import moze_intel.projecte.gameObjs.container.inventory.TransmutationInventory;
import moze_intel.projecte.gameObjs.container.slots.InventoryContainerSlot;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.utils.ItemHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class SlotOutput extends InventoryContainerSlot {

	private final TransmutationInventory inv;

	public SlotOutput(TransmutationInventory inv, int index, int x, int y) {
		super(inv, index, x, y);
		this.inv = inv;
	}

	@Override
	protected void onSwapCraft(int amount) {
		remove(amount);
	}

	@Nonnull
	@Override
	public ItemStack remove(int amount) {
		ItemStack stack = ItemHelper.size(getItem(), amount);
		BigInteger emcValue = BigInteger.valueOf(EMCHelper.getEmcValue(stack));
		if (amount > 1) {
			emcValue = emcValue.multiply(BigInteger.valueOf(amount));
		}
		if (emcValue.compareTo(inv.getAvailableEmc()) > 0) {
			//Requesting more emc than available
			//Container expects stacksize=0-Itemstack for 'nothing'
			return ItemStack.EMPTY;
		}
		if (inv.isServer()) {
			inv.removeEmc(emcValue);
		}
		return stack;
	}

	@Override
	public void set(@Nonnull ItemStack stack) {
	}

	@Override
	public boolean mayPlace(@Nonnull ItemStack stack) {
		return false;
	}

	@Override
	public boolean mayPickup(PlayerEntity player) {
		return !hasItem() || BigInteger.valueOf(EMCHelper.getEmcValue(getItem())).compareTo(inv.getAvailableEmc()) <= 0;
	}
}