package moze_intel.projecte.config.value;

import java.util.function.DoubleSupplier;
import moze_intel.projecte.config.IPEConfig;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

/**
 * From Mekanism
 */
public class CachedDoubleValue extends CachedPrimitiveValue<Double> implements DoubleSupplier {

	private double cachedValue;

	private CachedDoubleValue(IPEConfig config, ConfigValue<Double> internal) {
		super(config, internal);
	}

	public static CachedDoubleValue wrap(IPEConfig config, ConfigValue<Double> internal) {
		return new CachedDoubleValue(config, internal);
	}

	public double get() {
		if (!resolved) {
			//If we don't have a cached value or need to resolve it again, get it from the actual ConfigValue
			cachedValue = internal.get();
			resolved = true;
		}
		return cachedValue;
	}

	@Override
	public double getAsDouble() {
		return get();
	}

	public void set(double value) {
		internal.set(value);
		cachedValue = value;
	}
}