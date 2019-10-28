package moze_intel.projecte.gameObjs.items;

import moze_intel.projecte.gameObjs.EnumFuelType;
import moze_intel.projecte.utils.Constants;
import net.minecraft.item.ItemStack;

public class AlchemicalFuel extends ItemPE {

	private final EnumFuelType fuelType;

	public AlchemicalFuel(Properties props, EnumFuelType type) {
		super(props);
		this.fuelType = type;
	}

	@Override
	public int getBurnTime(ItemStack stack) {
		switch (fuelType) {
			case ALCHEMICAL_COAL:
				return Constants.ALCH_BURN_TIME;
			case MOBIUS_FUEL:
				return Constants.MOBIUS_BURN_TIME;
			case AETERNALIS_FUEL:
				return Constants.AETERNALIS_BURN_TIME;
			default:
				return -1;
		}
	}
}