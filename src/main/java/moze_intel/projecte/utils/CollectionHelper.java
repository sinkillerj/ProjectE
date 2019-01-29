package moze_intel.projecte.utils;

import java.util.List;

/**
 * Helper class for Collections of any kind.
 * Notice: Please try to keep methods tidy and alphabetically ordered. Thanks!
 */
public final class CollectionHelper
{
	public static <T> T getRandomListEntry(List<T> list, T toExclude)
	{
		if (list.size() == 1 && list.contains(toExclude))
		{
			return toExclude;
		}
		T obj;

		do
		{
			int random = MathUtils.randomIntInRange(0, list.size() - 1);
			obj = list.get(random);
		}
		while(obj.equals(toExclude));

		return obj;
	}
}
