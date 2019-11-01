package moze_intel.projecte.gameObjs.items;

import javax.annotation.Nonnull;
import moze_intel.projecte.gameObjs.EnumFuelType;
import net.minecraft.item.ItemStack;

public class AlchemicalFuel extends ItemPE {

	@Nonnull
	private final EnumFuelType fuelType;

	public AlchemicalFuel(Properties props, @Nonnull EnumFuelType type) {
		super(props);
		this.fuelType = type;
	}

	@Override
	public int getBurnTime(ItemStack stack) {
		return fuelType.getBurnTime();
	}
}