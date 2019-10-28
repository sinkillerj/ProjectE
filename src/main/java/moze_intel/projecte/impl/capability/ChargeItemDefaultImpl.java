package moze_intel.projecte.impl.capability;

import javax.annotation.Nonnull;
import moze_intel.projecte.api.capabilities.item.IItemCharge;
import net.minecraft.item.ItemStack;

public final class ChargeItemDefaultImpl implements IItemCharge {

	@Override
	public int getNumCharges(@Nonnull ItemStack stack) {
		//Return one instead of zero to make sure we don't have a division by zero error ever
		return 1;
	}
}