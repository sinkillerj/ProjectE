package moze_intel.projecte.gameObjs.items;

import moze_intel.projecte.gameObjs.EnumFuelType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AlchemicalFuel extends ItemPE {

	@NotNull
	private final EnumFuelType fuelType;

	public AlchemicalFuel(Properties props, @NotNull EnumFuelType type) {
		super(props);
		this.fuelType = type;
	}

	@Override
	public int getBurnTime(ItemStack stack, @Nullable RecipeType<?> recipeType) {
		return fuelType.getBurnTime();
	}
}