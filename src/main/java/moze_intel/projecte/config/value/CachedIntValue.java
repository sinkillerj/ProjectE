package moze_intel.projecte.config.value;

import java.util.function.IntSupplier;
import moze_intel.projecte.config.IPEConfig;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

/**
 * From Mekanism
 */
public class CachedIntValue extends CachedValue<Integer> implements IntSupplier {

	private boolean resolved;
	private int cachedValue;

	private CachedIntValue(IPEConfig config, ConfigValue<Integer> internal) {
		super(config, internal);
	}

	public static CachedIntValue wrap(IPEConfig config, ConfigValue<Integer> internal) {
		return new CachedIntValue(config, internal);
	}

	public int getOrDefault() {
		if (resolved || isLoaded()) {
			return get();
		}
		return internal.getDefault();
	}

	public int get() {
		if (!resolved) {
			//If we don't have a cached value or need to resolve it again, get it from the actual ConfigValue
			cachedValue = internal.get();
			resolved = true;
		}
		return cachedValue;
	}

	@Override
	public int getAsInt() {
		return get();
	}

	public void set(int value) {
		internal.set(value);
		cachedValue = value;
	}

	@Override
	protected boolean clearCachedValue(boolean checkChanged) {
		if (!resolved) {
			//Isn't cached don't need to clear it or run any invalidation listeners
			return false;
		}
		int oldCachedValue = cachedValue;
		resolved = false;
		//Return if we are meant to check the changed ones, and it is different than it used to be
		return checkChanged && oldCachedValue != get();
	}
}