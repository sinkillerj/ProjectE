package moze_intel.projecte.utils;

import codechicken.nei.ItemList;
import codechicken.nei.SearchField;
import codechicken.nei.api.ItemFilter;
import cpw.mods.fml.common.Loader;

import net.minecraft.item.ItemStack;

import java.util.LinkedList;
import java.util.List;

public abstract class ItemSearchHelper
{
	public static ItemSearchHelper create(String searchString) {
		if (Loader.isModLoaded("NotEnoughItems")) {
			return new NEISearch(searchString);
		} else {
			return new DefaultSearch(searchString);
		}
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
				displayName = stack.getDisplayName();
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
			else if (searchString.length() > 0 && !displayName.toLowerCase().contains(searchString))
			{
				return false;
			}
			return true;
		}
	}

	private static class NEISearch extends ItemSearchHelper
	{

		ItemFilter filter;
		public NEISearch(String searchString)
		{
			super(searchString);
			filter = getFilter(searchString);
		}

		public ItemFilter getFilter(String s_filter)
		{
			//based on https://github.com/Chicken-Bones/NotEnoughItems/blob/a1879a96548d17f5c4d95b40956d68f6f9db82f8/src/codechicken/nei/SearchField.java#L124-L139
			List<ItemFilter> primary = new LinkedList<ItemFilter>();
			List<ItemFilter> secondary = new LinkedList<ItemFilter>();
			for (SearchField.ISearchProvider p : SearchField.searchProviders) {
				ItemFilter filter = p.getFilter(s_filter);
				if (filter != null)
					(p.isPrimary() ? primary : secondary).add(filter);
			}

			if (!primary.isEmpty()) return new ItemList.AnyMultiItemFilter(primary);
			if (!secondary.isEmpty()) return new ItemList.AnyMultiItemFilter(secondary);
			return new ItemList.EverythingItemFilter();
		}

		@Override
		public boolean doesItemMatchFilter_(ItemStack itemStack)
		{
			return filter.matches(itemStack);
		}
	}
}
