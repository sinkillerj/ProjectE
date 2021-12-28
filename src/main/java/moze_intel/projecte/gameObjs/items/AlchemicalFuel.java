package moze_intel.projecte.gameObjs.items;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import moze_intel.projecte.gameObjs.EnumFuelType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;

public class AlchemicalFuel extends ItemPE {

	@Nonnull
	private final EnumFuelType fuelType;

	public AlchemicalFuel(Properties props, @Nonnull EnumFuelType type) {
		super(props);
		this.fuelType = type;
	}

	@Override
	public int getBurnTime(ItemStack stack, @Nullable RecipeType<?> recipeType) {
		return fuelType.getBurnTime();
	}
}