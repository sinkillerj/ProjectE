package moze_intel.projecte.gameObjs;

import javax.annotation.Nonnull;
import net.minecraft.util.StringRepresentable;

public enum EnumRelayTier implements StringRepresentable {
	MK1("relay_mk1", 64, 100_000),
	MK2("relay_mk2", 192, 1_000_000),
	MK3("relay_mk3", 640, 10_000_000);

	private final String name;
	private final long chargeRate;
	private final long storage;

	EnumRelayTier(String name, long chargeRate, long storage) {
		this.name = name;
		this.chargeRate = chargeRate;
		this.storage = storage;
	}

	@Nonnull
	@Override
	public String getSerializedName() {
		return name;
	}

	public long getChargeRate() {
		return chargeRate;
	}

	public long getStorage() {
		return storage;
	}

	@Override
	public String toString() {
		return getSerializedName();
	}
}