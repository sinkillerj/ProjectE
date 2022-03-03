package moze_intel.projecte.integration.jei.collectors;

import moze_intel.projecte.utils.EMCHelper;
import net.minecraft.world.item.ItemStack;

public record FuelUpgradeRecipe(ItemStack input, ItemStack output, long upgradeEMC) {

	public FuelUpgradeRecipe(ItemStack input, ItemStack output) {
		this(input, output, EMCHelper.getEmcValue(output) - EMCHelper.getEmcValue(input));
	}
}