package moze_intel.projecte.utils;

import net.minecraft.item.ItemStack;

import java.util.Locale;

public abstract class ItemSearchHelper
{
	public static ItemSearchHelper create(String searchString) {
		return new DefaultSearch(searchString);
	}

	public final String searchString;
	public ItemSearchHelper(String searchString) {
		this.searchString = searchString;
	}

	public final boolean doesItemMatchFilter(ItemStack itemStack) {
		try {
			return this.doesItemMatchFilter_(itemStack);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	protected abstract boolean doesItemMatchFilter_(ItemStack itemStack);

	private static class DefaultSearch extends ItemSearchHelper
	{
		public DefaultSearch(String searchString)
		{
			super(searchString);
		}

		public boolean doesItemMatchFilter_(ItemStack stack)
		{
			String displayName;

			try
			{
				displayName = stack.getDisplayName().toLowerCase(Locale.ROOT);
			} catch (Exception e)
			{
				e.printStackTrace();
				//From old code... Not sure if intended to not remove items that crash on getDisplayName
				return true;
			}

			if (displayName == null)
			{
				return false;
			}
			else if (searchString.length() > 0 && !displayName.contains(searchString))
			{
				return false;
			}
			return true;
		}
	}
}
