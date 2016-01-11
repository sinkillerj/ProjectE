package moze_intel.projecte.utils;

import moze_intel.projecte.emc.EMCMapper;
import moze_intel.projecte.emc.SimpleStack;
import moze_intel.projecte.manual.AbstractPage;

import java.util.Comparator;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

public final class Comparators
{
	public static final Comparator<ItemStack> ITEMSTACK_EMC_DESCENDING = (s1, s2) -> {
        int emc1 = EMCHelper.getEmcValue(s1);
        int emc2 = EMCHelper.getEmcValue(s2);

        if (emc1 < emc2)
        {
            return 1;
        }

        if (emc1 > emc2)
        {
            return -1;
        }

        return 0;
    };

	public static final Comparator<ItemStack> ITEMSTACK_ASCENDING = (o1, o2) -> {
        if ((o1 == null && o2 == null))
        {
            return 0;
        }
        if (o1 == null)
        {
            return 1;
        }
        if (o2 == null)
        {
            return -1;
        }
        if (ItemHelper.areItemStacksEqualIgnoreNBT(o1, o2))
        {
            // Same item id, same meta
            return o1.stackSize - o2.stackSize;
        }
        else // Different id or different meta
        {
            // Different id
            if (o1.getItem() != o2.getItem())
            {
                return Item.getIdFromItem(o1.getItem()) - Item.getIdFromItem(o2.getItem());
            }
            else
            {
                // Different meta
                return o1.getItemDamage() - o2.getItemDamage();
            }

        }
    };

	public static final Comparator<SimpleStack> SIMPLESTACK_ASCENDING = (s1, s2) -> {
        int emc1 = EMCMapper.getEmcValue(s1);
        int emc2 = EMCMapper.getEmcValue(s2);

        if (emc1 < emc2)
        {
            return -1;
        }

        if (emc1 > emc2)
        {
            return 1;
        }

        return 0;
    };
	
	public static final Comparator<Integer> INT_DESCENDING = (i1, i2) -> {
        if (i1 < i2)
        {
            return 1;
        }

        if (i1 > 2)
        {
            return -1;
        }

        return 0;
    };

	public static final Comparator<AbstractPage> PAGE_HEADER = (o1, o2) -> StatCollector.translateToLocal(o1.getHeaderText()).compareToIgnoreCase(StatCollector.translateToLocal(o2.getHeaderText()));
}