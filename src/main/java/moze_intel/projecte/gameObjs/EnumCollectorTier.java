package moze_intel.projecte.gameObjs;

import javax.annotation.Nonnull;
import net.minecraft.util.IStringSerializable;

public enum EnumCollectorTier implements IStringSerializable {
	MK1("collector_mk1", 4, 10_000),
	MK2("collector_mk2", 12, 30_000),
	MK3("collector_mk3", 40, 60_000);

	private final String name;
	private final long genRate;
	private final long storage;

	EnumCollectorTier(String name, long genRate, long storage) {
		this.name = name;
		this.genRate = genRate;
		this.storage = storage;
	}

	@Nonnull
	@Override
	public String getSerializedName() {
		return name;
	}

	public long getGenRate() {
		return genRate;
	}

	public long getStorage() {
		return storage;
	}

	@Override
	public String toString() {
		return getSerializedName();
	}
}