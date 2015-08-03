package moze_intel.projecte.utils;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Helper class for Collections of any kind.
 * Notice: Please try to keep methods tidy and alphabetically ordered. Thanks!
 */
public final class CollectionHelper
{
	/**
	 * Splits list into length-long sublists.
	 */
	public static <T> List<List<T>> splitToLength(List<T> list, int length)
	{
		List<List<T>> parts = Lists.newArrayList();
		for (int i = 0; i < list.size(); i += length)
		{
			parts.add(Lists.newArrayList(list.subList(i, Math.min(list.size(), i + length))));
		}
		return parts;
	}

	public static <T> T getRandomListEntry(List<T> list, T toExclude)
	{
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
