package moze_intel.projecte.utils;

import moze_intel.projecte.emc.EMCMapper;
import moze_intel.projecte.emc.SimpleStack;
import net.minecraft.item.ItemStack;

import java.util.Comparator;

public final class Comparators
{
	public static final Comparator<ItemStack> ITEMSTACK_DESCENDING = new Comparator<ItemStack>()
	{
		@Override
		public int compare(ItemStack s1, ItemStack s2) 
		{
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
		}
	};
	
	public static final Comparator<SimpleStack> SIMPLESTACK_ASCENDING = new Comparator<SimpleStack>()
	{
		@Override
		public int compare(SimpleStack s1, SimpleStack s2)
		{
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
		}
	};
	
	public static final Comparator<Integer> INT_DESCENDING = new Comparator<Integer>()
	{
		@Override
		public int compare(Integer i1, Integer i2)
		{
			if (i1 < i2)
			{
				return 1;
			}
			
			if (i1 > 2)
			{
				return -1;
			}
			
			return 0;
		}
	};
}