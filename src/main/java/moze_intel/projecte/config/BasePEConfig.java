package moze_intel.projecte.config;

import java.util.ArrayList;
import java.util.List;
import moze_intel.projecte.config.value.CachedPrimitiveValue;
import moze_intel.projecte.config.value.CachedResolvableConfigValue;

public abstract class BasePEConfig implements IPEConfig {

	private final List<CachedResolvableConfigValue<?, ?>> cachedConfigValues = new ArrayList<>();
	private final List<CachedPrimitiveValue<?>> cachedPrimitiveValues = new ArrayList<>();

	@Override
	public void clearCache() {
		cachedConfigValues.forEach(CachedResolvableConfigValue::clearCache);
		cachedPrimitiveValues.forEach(CachedPrimitiveValue::clearCache);
	}

	@Override
	public <T, R> void addCachedValue(CachedResolvableConfigValue<T, R> configValue) {
		cachedConfigValues.add(configValue);
	}

	@Override
	public <T> void addCachedValue(CachedPrimitiveValue<T> configValue) {
		cachedPrimitiveValues.add(configValue);
	}
}