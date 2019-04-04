package moze_intel.projecte.emc.nbt.calculators;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.math3.fraction.BigFraction;

import net.minecraft.item.ItemStack;
import moze_intel.projecte.api.proxy.IItemNBTEmcCalculator;
import moze_intel.projecte.utils.EMCHelper;

public class EnchantmentCalculator implements IItemNBTEmcCalculator{

	@Override
	public Collection<String> allowedItems() {
		ArrayList<String> ans = new ArrayList<String>();
		ans.add("*");
		return ans;
	}

	@Override
	public boolean canProcessItem(ItemStack input) {
		return input.getTagCompound() != null && 
			   (input.getTagCompound().hasKey("StoredEnchantments") || 
			   (input.getEnchantmentTagList() != null && !input.getEnchantmentTagList().isEmpty()));
	}

	@Override
	public Operation getOperation(ItemStack input) {
		return canProcessItem(input)? Operation.OP_ADD : Operation.OP_NONE;
	}

	@Override
	public BigFraction getEMC(ItemStack input) {
		if(canProcessItem(input))
			return new BigFraction(EMCHelper.getEnchantEmcBonus(input));
		return BigFraction.ZERO;
	}

}
