package mapeper.projecte.neirecipecollector;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.List;

public class LowerCasePrefixPredicate implements Predicate<String>
{
	protected final String prefix;

	public LowerCasePrefixPredicate(String prefix)
	{
		this.prefix = prefix;
	}

	@Override
	public boolean apply(String input)
	{
		return input.toLowerCase().startsWith(prefix.toLowerCase());
	}

	public static List<String> autocompletionOptions(Collection<String> options, String input) {
		return Lists.newArrayList(Iterables.filter(options, new LowerCasePrefixPredicate(input)));
	}
}
