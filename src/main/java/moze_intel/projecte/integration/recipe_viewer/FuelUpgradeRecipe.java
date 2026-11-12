package moze_intel.projecte.integration.recipe_viewer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.proxy.IEMCProxy;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;

public record FuelUpgradeRecipe(Holder<Item> input, Holder<Item> output, long upgradeEMC) {

	private static final Codec<Holder<Item>> HOLDER_CODEC = BuiltInRegistries.ITEM.holderByNameCodec();
	public static final Codec<FuelUpgradeRecipe> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			HOLDER_CODEC.fieldOf("input").forGetter(FuelUpgradeRecipe::input),
			HOLDER_CODEC.fieldOf("output").forGetter(FuelUpgradeRecipe::output),
			Codec.LONG.fieldOf("upgrade_emc").forGetter(FuelUpgradeRecipe::upgradeEMC)
	).apply(instance, FuelUpgradeRecipe::new));

	public FuelUpgradeRecipe(Holder<Item> input, Holder<Item> output) {
		this(input, output, IEMCProxy.INSTANCE.getValue(output) - IEMCProxy.INSTANCE.getValue(input));
	}

	public Identifier syntheticId() {
		return PECore.rl("/fuel_upgrade/" + RecipeViewerHelper.stripForSynthetic(input) + "/" + RecipeViewerHelper.stripForSynthetic(output) + "/");
	}
}
