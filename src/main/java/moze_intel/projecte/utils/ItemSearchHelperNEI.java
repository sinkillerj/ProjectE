package moze_intel.projecte.utils;

import codechicken.nei.ItemList;
import codechicken.nei.SearchField;
import codechicken.nei.api.ItemFilter;
import net.minecraft.item.ItemStack;

import java.util.LinkedList;
import java.util.List;

public class ItemSearchHelperNEI extends ItemSearchHelper
{

	ItemFilter filter;
	public ItemSearchHelperNEI(String searchString)
	{
		super(searchString);
		filter = getFilter(searchString);
	}

	public ItemFilter getFilter(String s_filter)
	{
		//based on https://github.com/Chicken-Bones/NotEnoughItems/blob/a1879a96548d17f5c4d95b40956d68f6f9db82f8/src/codechicken/nei/SearchField.java#L124-L139
		List<ItemFilter> primary = new LinkedList<>();
		List<ItemFilter> secondary = new LinkedList<>();
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

