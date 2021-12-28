package moze_intel.projecte.integration.jei.collectors;

import moze_intel.projecte.utils.EMCHelper;
import net.minecraft.world.item.ItemStack;

public class FuelUpgradeRecipe {

	private final ItemStack input;
	private final ItemStack output;
	private final long upgradeEMC;

	public FuelUpgradeRecipe(ItemStack input, ItemStack output) {
		this.input = input;
		this.output = output;
		this.upgradeEMC = EMCHelper.getEmcValue(output) - EMCHelper.getEmcValue(input);
	}

	public ItemStack getInput() {
		return input;
	}

	public ItemStack getOutput() {
		return output;
	}

	public long getUpgradeEMC() {
		return upgradeEMC;
	}
}