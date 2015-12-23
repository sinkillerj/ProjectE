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
	public static final Comparator<ItemStack> ITEMSTACK_CREATE_EMC_DESCENDING = new Comparator<ItemStack>()
	{
		@Override
		public int compare(ItemStack s1, ItemStack s2) 
		{
			int emc1 = EMCHelper.getEmcValueForCreationOrZero(s1);
			int emc2 = EMCHelper.getEmcValueForCreationOrZero(s2);
			
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

	public static final Comparator<ItemStack> ITEMSTACK_ASCENDING = new Comparator<ItemStack>() {
		@Override
		public int compare(ItemStack o1, ItemStack o2)
		{
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
		}
	};

	public static final Comparator<SimpleStack> SIMPLESTACK_CREATE_EMC_ASCENDING = new Comparator<SimpleStack>()
	{
		@Override
		public int compare(SimpleStack s1, SimpleStack s2)
		{
			//TODO Need to use the correct EMC value
			int emc1 = EMCMapper.emc.containsKey(s1) ? EMCMapper.emc.get(s1) : 0;
			int emc2 = EMCMapper.emc.containsKey(s2) ? EMCMapper.emc.get(s2) : 0;
			
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

	public static final Comparator<AbstractPage> PAGE_HEADER = new Comparator<AbstractPage>() {
		@Override
		public int compare(AbstractPage o1, AbstractPage o2) {
			return StatCollector.translateToLocal(o1.getHeaderText()).compareToIgnoreCase(StatCollector.translateToLocal(o2.getHeaderText()));
		}
	};
}