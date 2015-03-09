package moze_intel.projecte.utils;

import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;

public class PrefixConfiguration extends Configuration {
	final protected Configuration inner;
	final protected String prefix;
	public PrefixConfiguration(Configuration inner, String prefix) {
		if (prefix.endsWith(".")) throw new IllegalArgumentException("Prefix is not allowed to end with a dot.");
		this.inner = inner;
		this.prefix = prefix;
	}

	@Override
	public ConfigCategory getCategory(String name) {
		if (name == null || "".equals(name)) return this.inner.getCategory(this.prefix);
		return this.inner.getCategory(this.prefix + "." + name);
	}
}
