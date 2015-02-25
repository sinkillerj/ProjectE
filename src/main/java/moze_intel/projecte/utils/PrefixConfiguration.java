package moze_intel.projecte.utils;

import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;

public class PrefixConfiguration extends Configuration {
	final protected Configuration inner;
	final protected String prefix;
	public PrefixConfiguration(Configuration inner, String prefix) {
		this.inner = inner;
		this.prefix = prefix;
	}

	@Override
	public ConfigCategory getCategory(String name) {
		return this.inner.getCategory(this.prefix + name);
	}
}
