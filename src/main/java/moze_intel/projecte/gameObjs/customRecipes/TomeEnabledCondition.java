package moze_intel.projecte.gameObjs.customRecipes;

import com.mojang.serialization.Codec;
import moze_intel.projecte.config.ProjectEConfig;
import moze_intel.projecte.gameObjs.registries.PERecipeConditions;
import net.neoforged.neoforge.common.conditions.ICondition;

public class TomeEnabledCondition implements ICondition {

	public static final TomeEnabledCondition INSTANCE = new TomeEnabledCondition();

	private TomeEnabledCondition() {
	}

	@Override
	public boolean test(IContext context) {
		return ProjectEConfig.common.craftableTome.get();
	}

	@Override
	public Codec<? extends ICondition> codec() {
		return PERecipeConditions.TOME_ENABLED.value();
	}
}