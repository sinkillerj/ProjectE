package mapeper.projecte.neirecipecollector;

import com.google.common.base.Predicate;

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
}
