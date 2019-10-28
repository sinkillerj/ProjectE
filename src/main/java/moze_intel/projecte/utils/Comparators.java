package moze_intel.projecte.utils;

import java.util.Comparator;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public final class Comparators {

	public static final Comparator<ItemStack> ITEMSTACK_ASCENDING = (o1, o2) -> {
		if ((o1.isEmpty() && o2.isEmpty())) {
			return 0;
		}
		if (o1.isEmpty()) {
			return 1;
		}
		if (o2.isEmpty()) {
			return -1;
		}
		if (o1.getItem() != o2.getItem()) {
			// Same item id
			return o1.getCount() - o2.getCount();
		} else // Different id
		{
			return Item.getIdFromItem(o1.getItem()) - Item.getIdFromItem(o2.getItem());
		}
	};
}