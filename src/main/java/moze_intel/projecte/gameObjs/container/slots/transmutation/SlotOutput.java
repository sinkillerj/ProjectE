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

	public SlotOutput(TransmutationInventory inv, int par2, int par3, int par4) {
		super(inv, par2, par3, par4);
		this.inv = inv;
	}

	@Nonnull
	@Override
	public ItemStack decrStackSize(int amount) {
		ItemStack stack = getStack().copy();
		stack.setCount(amount);
		BigInteger emcValue = BigInteger.valueOf(EMCHelper.getEmcValue(stack)).multiply(BigInteger.valueOf(amount));
		if (emcValue.compareTo(inv.getAvailableEMC()) > 0) {
			//Requesting more emc than available
			//Container expects stacksize=0-Itemstack for 'nothing'
			stack.setCount(0);
			return stack;
		}
		inv.removeEmc(emcValue);
		inv.checkForUpdates();
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
		return !getHasStack() || BigInteger.valueOf(EMCHelper.getEmcValue(getStack())).compareTo(inv.getAvailableEMC()) <= 0;
	}
}