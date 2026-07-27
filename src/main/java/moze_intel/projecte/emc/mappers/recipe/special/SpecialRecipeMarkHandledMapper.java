package moze_intel.projecte.emc.mappers.recipe.special;

import moze_intel.projecte.api.mapper.collector.IMappingCollector;
import moze_intel.projecte.api.mapper.recipe.INSSFakeGroupManager;
import moze_intel.projecte.api.mapper.recipe.IRecipeTypeMapper;
import moze_intel.projecte.api.mapper.recipe.RecipeTypeMapper;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import moze_intel.projecte.config.MappingConfig;
import moze_intel.projecte.config.PEConfigTranslations;
import moze_intel.projecte.emc.components.processor.DamageProcessor;
import moze_intel.projecte.emc.components.processor.DecoratedPotProcessor;
import moze_intel.projecte.emc.components.processor.DecoratedShieldProcessor;
import moze_intel.projecte.emc.components.processor.FireworkProcessor;
import moze_intel.projecte.emc.components.processor.FireworkStarProcessor;
import moze_intel.projecte.emc.components.processor.MapScaleProcessor;
import moze_intel.projecte.gameObjs.customRecipes.PhiloStoneSmeltingRecipe;
import moze_intel.projecte.gameObjs.customRecipes.RecipesCovalenceRepair;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.crafting.ArmorDyeRecipe;
import net.minecraft.world.item.crafting.BannerDuplicateRecipe;
import net.minecraft.world.item.crafting.BookCloningRecipe;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.DecoratedPotRecipe;
import net.minecraft.world.item.crafting.FireworkRocketRecipe;
import net.minecraft.world.item.crafting.FireworkStarFadeRecipe;
import net.minecraft.world.item.crafting.FireworkStarRecipe;
import net.minecraft.world.item.crafting.MapCloningRecipe;
import net.minecraft.world.item.crafting.MapExtendingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.RepairItemRecipe;
import net.minecraft.world.item.crafting.ShieldDecorationRecipe;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;

@RecipeTypeMapper
public class SpecialRecipeMarkHandledMapper implements IRecipeTypeMapper {

	@VisibleForTesting
	static final String ARMOR_DYE_SKIP_REASON = "Output color is calculated from and preserves the input item's data components.";

	@Override
	public final boolean handleRecipe(IMappingCollector<NormalizedSimpleStack, Long> mapper, RecipeHolder<?> recipeHolder, RegistryAccess registryAccess,
			INSSFakeGroupManager fakeGroupManager) {
		Recipe<?> recipe = recipeHolder.value();
		if (recipe instanceof CustomRecipe) {
			if (recipe instanceof ShieldDecorationRecipe) {
				return MappingConfig.isEnabled(DecoratedShieldProcessor.INSTANCE);
			} else if (recipe instanceof DecoratedPotRecipe) {
				return MappingConfig.isEnabled(DecoratedPotProcessor.INSTANCE);
			} else if (recipe instanceof RepairItemRecipe || recipe instanceof RecipesCovalenceRepair) {
				return MappingConfig.isEnabled(DamageProcessor.INSTANCE);
			} else if (recipe instanceof FireworkStarRecipe || recipe instanceof FireworkStarFadeRecipe) {
				return MappingConfig.isEnabled(FireworkStarProcessor.INSTANCE);
			} else if (recipe instanceof FireworkRocketRecipe) {
				return MappingConfig.isEnabled(FireworkProcessor.INSTANCE);
			}
			//TODO: Do we eventually want to try and figure out how to handle the armor dye recipe?
			//It is currently left unmapped and classified separately because its output is dynamic.
			//Not needed, it just recreates the smelting recipes
			return recipe instanceof PhiloStoneSmeltingRecipe
				   //Cloning recipes, creates something from itself, doesn't change overall emc values as amounts all balance out
					|| recipe instanceof BookCloningRecipe || recipe instanceof MapCloningRecipe || recipe instanceof BannerDuplicateRecipe;
		} else if (recipe instanceof MapExtendingRecipe) {
			return MappingConfig.isEnabled(MapScaleProcessor.INSTANCE);
		}
		return false;
	}

	@Override
	public final boolean canHandle(RecipeType<?> recipeType) {
		return recipeType == RecipeType.CRAFTING;
	}

	@Override
	@Nullable
	public final String getExpectedUnhandledReason(RecipeHolder<?> recipeHolder, RegistryAccess registryAccess) {
		return recipeHolder.value() instanceof ArmorDyeRecipe ? ARMOR_DYE_SKIP_REASON : null;
	}

	@Override
	public String getName() {
		return PEConfigTranslations.MAPPING_CRAFTING_MAPPER_MARK_HANDLED.title();
	}

	@Override
	public String getTranslationKey() {
		return PEConfigTranslations.MAPPING_CRAFTING_MAPPER_MARK_HANDLED.getTranslationKey();
	}

	@Override
	public String getDescription() {
		return PEConfigTranslations.MAPPING_CRAFTING_MAPPER_MARK_HANDLED.tooltip();
	}
}
