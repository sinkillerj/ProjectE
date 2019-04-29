package moze_intel.projecte.emc.nbt.calculators;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.math3.fraction.BigFraction;

import net.minecraft.item.ItemStack;
import moze_intel.projecte.api.item.IItemEmc;
import moze_intel.projecte.api.proxy.IItemNBTEmcCalculator;
import moze_intel.projecte.utils.EMCHelper;

public class StoredEMCCalculator implements IItemNBTEmcCalculator{

	@Override
	public Collection<String> allowedItems() {
		ArrayList<String> ans = new ArrayList<String>();
		ans.add("*");
		return ans;
	}

	@Override
	public boolean canProcessItem(ItemStack input) {
		return (input.getTag() != null && input.getTag().contains("StoredEMC")) ||
				(input.getItem() instanceof IItemEmc);
	}

	@Override
	public Operation getOperation(ItemStack input) {
		return canProcessItem(input)? Operation.OP_ADD : Operation.OP_NONE;
	}

	@Override
	public BigFraction getEMC(ItemStack input) {
		if(input.getTag() != null && input.getTag().contains("StoredEMC")) {
			return new BigFraction(input.getTag().getDouble("StoredEMC"));
		} else if (input.getItem() instanceof IItemEmc) {
			return new BigFraction(((IItemEmc) input.getItem()).getStoredEmc(input));
		}
		return BigFraction.ZERO;
	}

}
