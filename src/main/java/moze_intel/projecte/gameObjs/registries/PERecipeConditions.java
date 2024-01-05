package moze_intel.projecte.gameObjs.registries;

import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.customRecipes.FullKleinStarsCondition;
import moze_intel.projecte.gameObjs.customRecipes.TomeEnabledCondition;
import moze_intel.projecte.gameObjs.registration.DeferredCodecHolder;
import moze_intel.projecte.gameObjs.registration.DeferredCodecRegister;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class PERecipeConditions {

	public static final DeferredCodecRegister<ICondition> CONDITION_CODECS = new DeferredCodecRegister<>(NeoForgeRegistries.Keys.CONDITION_CODECS, PECore.MODID);

	public static final DeferredCodecHolder<ICondition, TomeEnabledCondition> TOME_ENABLED = CONDITION_CODECS.registerUnit("tome_enabled", () -> TomeEnabledCondition.INSTANCE);
	public static final DeferredCodecHolder<ICondition, FullKleinStarsCondition> FULL_KLEIN_STARS = CONDITION_CODECS.registerUnit("full_klein_stars", () -> FullKleinStarsCondition.INSTANCE);
}