package moze_intel.projecte.emc.nbt.cleaners;

import java.util.ArrayList;
import java.util.Collection;

import net.minecraft.item.ItemStack;
import moze_intel.projecte.api.item.IItemEmc;
import moze_intel.projecte.api.proxy.IItemNBTFilter;
import moze_intel.projecte.gameObjs.items.ItemPE;

public class ItemNBTProjectEActiveModeCleaner implements IItemNBTFilter{

	@Override
	public boolean canFilterStack(ItemStack input) {
		return (input.getTagCompound() != null && 
				(input.getTagCompound().hasKey(ItemPE.TAG_ACTIVE) ||
				input.getTagCompound().hasKey(ItemPE.TAG_MODE)));
	}

	@Override
	public ItemStack getFilteredItemStack(ItemStack input) {
		ItemStack ans = input.copy();
		if(canFilterStack(input)){
			if(ans.getTagCompound().hasKey(ItemPE.TAG_ACTIVE)){
				ans.getTagCompound().removeTag(ItemPE.TAG_ACTIVE);
			}if(ans.getTagCompound().hasKey(ItemPE.TAG_MODE)){
				ans.getTagCompound().removeTag(ItemPE.TAG_MODE);
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
