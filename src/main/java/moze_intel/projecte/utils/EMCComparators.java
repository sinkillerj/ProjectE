package moze_intel.projecte.utils;

import java.util.Comparator;

import moze_intel.projecte.emc.EMCMapper;
import moze_intel.projecte.emc.SimpleStack;
import net.minecraft.item.ItemStack;

public final class EMCComparators
{
	public static final Comparator<ItemStack> ITEMSTACK_DESCENDING = new Comparator<ItemStack>()
	{
		@Override
		public int compare(ItemStack s1, ItemStack s2) 
		{
			int emc1 = Utils.getEmcValue(s1);
			int emc2 = Utils.getEmcValue(s2);
			
			if (emc1 < emc2)
			{
				return 1;
			}
			else if (emc1 > emc2)
			{
				return -1;
			}
			else
			{
				return 0;
			}
		}
	};
	
	public static final Comparator<SimpleStack> SIMPLESTACK_ASCENDING = new Comparator<SimpleStack>()
	{
		@Override
		public int compare(SimpleStack s1, SimpleStack s2)
		{
			int emc1 = EMCMapper.emc.get(s1);
			int emc2 = EMCMapper.emc.get(s2);
			
			if (emc1 < emc2)
			{
				return -1;
			}
			else if (emc1 > emc2)
			{
				return 1;
			}
			else
			{
				return 0;
			}
		}
	};
}