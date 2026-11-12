package moze_intel.projecte.gameObjs.container.slots.transmutation;

import java.math.BigInteger;
import moze_intel.projecte.api.proxy.IEMCProxy;
import moze_intel.projecte.gameObjs.container.inventory.TransmutationInventory;
import moze_intel.projecte.gameObjs.container.slots.InventoryContainerSlot;
import moze_intel.projecte.utils.ItemHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

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

	@NotNull
	@Override
	public ItemStack remove(int amount) {
		if (amount == 0) {
			return ItemStack.EMPTY;
		}
		ItemStack stack = ItemHelper.size(getItem(), amount);
		long emcValue = IEMCProxy.INSTANCE.getValue(stack);
		BigInteger bigEmcValue = BigInteger.valueOf(emcValue);
		if (amount > 1) {
			bigEmcValue = bigEmcValue.multiply(BigInteger.valueOf(amount));
			if (bigEmcValue.compareTo(inv.getAvailableEmc()) > 0) {
				//Requesting more emc than available
				//Container expects stacksize=0-Itemstack for 'nothing'
				return ItemStack.EMPTY;
			}
		} else if (emcValue > inv.getAvailableEmcAsLong()) {
			//Requesting more emc than available
			//Container expects stacksize=0-Itemstack for 'nothing'
			return ItemStack.EMPTY;
		}
		if (inv.isServer()) {
			inv.removeEmc(bigEmcValue);
		}
		return stack;
	}

	@Override
	protected void setStackCopy(@NotNull ItemStack stack) {
	}

	@Override
	public boolean mayPlace(@NotNull ItemStack stack) {
		return false;
	}

	@Override
	public boolean mayPickup(@NotNull Player player) {
		return !hasItem() || IEMCProxy.INSTANCE.getValue(getItem()) <= inv.getAvailableEmcAsLong();
	}
}
