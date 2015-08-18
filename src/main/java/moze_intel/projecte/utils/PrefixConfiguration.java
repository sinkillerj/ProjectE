package moze_intel.projecte.utils;

import com.google.common.base.Strings;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;

public class PrefixConfiguration extends Configuration {
	protected final Configuration inner;
	protected final String prefix;
	public PrefixConfiguration(Configuration inner, String prefix) {
		if (prefix.endsWith(".")) throw new IllegalArgumentException("Prefix is not allowed to end with a dot.");
		this.inner = inner;
		this.prefix = prefix;
	}

	@Override
	public ConfigCategory getCategory(String name) {
		if (Strings.isNullOrEmpty(name)) return this.inner.getCategory(this.prefix);
		return this.inner.getCategory(this.prefix + "." + name);
	}
}
