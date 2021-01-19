package moze_intel.projecte.gameObjs.container.slots.transmutation;

import java.math.BigInteger;
import javax.annotation.Nonnull;
import moze_intel.projecte.gameObjs.container.inventory.TransmutationInventory;
import moze_intel.projecte.utils.EMCHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

public class SlotOutput extends SlotItemHandler {

	private final TransmutationInventory inv;

	public SlotOutput(TransmutationInventory inv, int index, int x, int y) {
		super(inv, index, x, y);
		this.inv = inv;
	}

	@Override
	protected void onSwapCraft(int amount) {
		decrStackSize(amount);
	}

	@Nonnull
	@Override
	public ItemStack decrStackSize(int amount) {
		ItemStack stack = getStack().copy();
		stack.setCount(amount);
		BigInteger emcValue = BigInteger.valueOf(EMCHelper.getEmcValue(stack)).multiply(BigInteger.valueOf(amount));
		if (emcValue.compareTo(inv.getAvailableEmc()) > 0) {
			//Requesting more emc than available
			//Container expects stacksize=0-Itemstack for 'nothing'
			stack.setCount(0);
			return stack;
		}
		if (inv.isServer()) {
			inv.removeEmc(emcValue);
		}
		return stack;
	}

	@Override
	public void putStack(@Nonnull ItemStack stack) {
	}

	@Override
	public boolean isItemValid(@Nonnull ItemStack stack) {
		return false;
	}

	@Override
	public boolean canTakeStack(PlayerEntity player) {
		return !getHasStack() || BigInteger.valueOf(EMCHelper.getEmcValue(getStack())).compareTo(inv.getAvailableEmc()) <= 0;
	}
}