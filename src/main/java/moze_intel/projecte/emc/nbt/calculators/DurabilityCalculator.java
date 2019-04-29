package moze_intel.projecte.emc.nbt.calculators;

import java.util.ArrayList;
import java.util.Collection;

import net.minecraft.item.ItemStack;

import org.apache.commons.math3.fraction.BigFraction;

import moze_intel.projecte.api.proxy.IItemNBTEmcCalculator;
import moze_intel.projecte.utils.ItemHelper;

public class DurabilityCalculator implements IItemNBTEmcCalculator {

	@Override
	public Collection<String> allowedItems() {
		ArrayList<String> ans = new ArrayList<String>();
		ans.add("*");
		return ans;
	}

	@Override
	public boolean canProcessItem(ItemStack input) {
		return ItemHelper.isDamageable(input);
	}

	@Override
	public Operation getOperation(
			ItemStack input) {
		return canProcessItem(input)? Operation.OP_MUlTIPLY : Operation.OP_NONE;
	}

	@Override
	public BigFraction getEMC(ItemStack input) {
		if(!canProcessItem(input)) return BigFraction.ONE;
		int relDamage = (input.getMaxDamage() + 1 - input.getDamage());
		if (relDamage <= 0){
			return BigFraction.ONE;
		}
		return new BigFraction(relDamage, input.getMaxDamage());
	}

}
