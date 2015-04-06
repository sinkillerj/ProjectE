package moze_intel.projecte.utils;

import java.util.List;

/**
 * Helper class for Collections of any kind.
 * Notice: Please try to keep methods tidy and alphabetically ordered. Thanks!
 */
public final class CollectionHelper
{
	public static Object getRandomListEntry(List<?> list, Object toExclude)
	{
		Object obj;

		do
		{
			int random = MathUtils.randomIntInRange(list.size() - 1, 0);
			obj = list.get(random);
		}
		while(obj.equals(toExclude));

		return obj;
	}
}
