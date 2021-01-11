package moze_intel.projecte.config.value;

import java.util.ArrayList;
import java.util.List;
import moze_intel.projecte.config.IPEConfig;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

/**
 * From Mekanism
 */
public class CachedPrimitiveValue<T> {

	protected final ConfigValue<T> internal;
	private List<Runnable> invalidationListeners;
	protected boolean resolved;

	protected CachedPrimitiveValue(IPEConfig config, ConfigValue<T> internal) {
		this.internal = internal;
		config.addCachedValue(this);
	}

	public void addInvalidationListener(Runnable listener) {
		if (invalidationListeners == null) {
			invalidationListeners = new ArrayList<>();
		}
		invalidationListeners.add(listener);
	}

	public void clearCache() {
		resolved = false;
		if (invalidationListeners != null) {
			invalidationListeners.forEach(Runnable::run);
		}
	}
}