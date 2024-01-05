package moze_intel.projecte.config.value;

import java.util.function.BooleanSupplier;
import moze_intel.projecte.config.IPEConfig;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;

/**
 * From Mekanism
 */
public class CachedBooleanValue extends CachedValue<Boolean> implements BooleanSupplier {

	private boolean resolved;
	private boolean cachedValue;

	private CachedBooleanValue(IPEConfig config, ConfigValue<Boolean> internal) {
		super(config, internal);
	}

	public static CachedBooleanValue wrap(IPEConfig config, ConfigValue<Boolean> internal) {
		return new CachedBooleanValue(config, internal);
	}

	public boolean getOrDefault() {
		if (resolved || isLoaded()) {
			return get();
		}
		return internal.getDefault();
	}

	public boolean get() {
		if (!resolved) {
			//If we don't have a cached value or need to resolve it again, get it from the actual ConfigValue
			cachedValue = internal.get();
			resolved = true;
		}
		return cachedValue;
	}

	@Override
	public boolean getAsBoolean() {
		return get();
	}

	public void set(boolean value) {
		internal.set(value);
		cachedValue = value;
	}

	@Override
	protected boolean clearCachedValue(boolean checkChanged) {
		if (!resolved) {
			//Isn't cached don't need to clear it or run any invalidation listeners
			return false;
		}
		boolean oldCachedValue = cachedValue;
		resolved = false;
		//Return if we are meant to check the changed ones, and it is different than it used to be
		return checkChanged && oldCachedValue != get();
	}
}