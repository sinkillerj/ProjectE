package moze_intel.projecte.gameObjs;

import javax.annotation.Nonnull;
import net.minecraft.util.IStringSerializable;

public enum EnumFuelType implements IStringSerializable {
	ALCHEMICAL_COAL("alchemical_coal", 1_600 * 4),//Four times the burn time of coal
	MOBIUS_FUEL("mobius_fuel", ALCHEMICAL_COAL.getBurnTime() * 4),
	AETERNALIS_FUEL("aeternalis_fuel", MOBIUS_FUEL.getBurnTime() * 4);

	private final String name;
	private final int burnTime;

	EnumFuelType(String name, int burnTime) {
		this.name = name;
		this.burnTime = burnTime;
	}

	@Nonnull
	@Override
	public String getSerializedName() {
		return name;
	}

	public int getBurnTime() {
		return burnTime;
	}

	@Override
	public String toString() {
		return getSerializedName();
	}
}