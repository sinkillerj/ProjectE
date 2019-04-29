package moze_intel.projecte.emc.nbt.cleaners;

import java.util.ArrayList;
import java.util.Collection;

import net.minecraft.item.ItemStack;
import moze_intel.projecte.api.proxy.IItemNBTFilter;

public class ItemNBTNameTagCleaner implements IItemNBTFilter{

	@Override
	public boolean canFilterStack(ItemStack input) {
		return input.getTag() != null && input.getTag().contains("display");
	}

	@Override
	public ItemStack getFilteredItemStack(ItemStack input) {
		ItemStack ans = input.copy();
		if(canFilterStack(input))
			ans.getTag().remove("display");
		return ans;
	}

	@Override
	public Collection<String> allowedItems() {
		ArrayList<String> ans = new ArrayList<String>();
		ans.add("*");
		return ans;
	}

}
