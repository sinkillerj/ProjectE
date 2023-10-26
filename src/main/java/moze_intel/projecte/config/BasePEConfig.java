package moze_intel.projecte.config;

import java.util.ArrayList;
import java.util.List;
import moze_intel.projecte.config.value.CachedValue;

public abstract class BasePEConfig implements IPEConfig {

	private final List<CachedValue<?>> cachedConfigValues = new ArrayList<>();

	@Override
	public void clearCache(boolean unloading) {
		for (CachedValue<?> cachedConfigValue : cachedConfigValues) {
			cachedConfigValue.clearCache(unloading);
		}
	}

	@Override
	public <T> void addCachedValue(CachedValue<T> configValue) {
		cachedConfigValues.add(configValue);
	}
}