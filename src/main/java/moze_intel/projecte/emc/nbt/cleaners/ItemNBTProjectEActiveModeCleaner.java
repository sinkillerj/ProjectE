package moze_intel.projecte.emc.nbt.cleaners;

import java.util.ArrayList;
import java.util.Collection;

import net.minecraft.item.ItemStack;
import moze_intel.projecte.api.item.IItemCharge;
import moze_intel.projecte.api.item.IItemEmc;
import moze_intel.projecte.api.proxy.IItemNBTFilter;
import moze_intel.projecte.gameObjs.items.ItemPE;

public class ItemNBTProjectEActiveModeCleaner implements IItemNBTFilter{

	@Override
	public boolean canFilterStack(ItemStack input) {
		return (input.getTag() != null && 
				(input.getTag().contains(ItemPE.TAG_ACTIVE) ||
				input.getTag().contains(ItemPE.TAG_MODE)||
				input.getTag().contains(IItemCharge.KEY)));
	}

	@Override
	public ItemStack getFilteredItemStack(ItemStack input) {
		ItemStack ans = input.copy();
		if(canFilterStack(input)){
			if(ans.getTag().contains(ItemPE.TAG_ACTIVE)){
				ans.getTag().remove(ItemPE.TAG_ACTIVE);
			}if(ans.getTag().contains(ItemPE.TAG_MODE)){
				ans.getTag().remove(ItemPE.TAG_MODE);
			}
			if(ans.getTag().contains(IItemCharge.KEY)){
				ans.getTag().remove(IItemCharge.KEY);
			}
		}
		return ans;
	}

	@Override
	public Collection<String> allowedItems() {
		ArrayList<String> ans = new ArrayList<String>();
		ans.add("*");
		return ans;
	}

}
