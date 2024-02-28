package moze_intel.projecte.integration.jei;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import moze_intel.projecte.PECore;
import moze_intel.projecte.emc.FuelMapper;
import moze_intel.projecte.gameObjs.container.PhilosStoneContainer;
import moze_intel.projecte.gameObjs.gui.AbstractCollectorScreen;
import moze_intel.projecte.gameObjs.gui.GUIDMFurnace;
import moze_intel.projecte.gameObjs.gui.GUIRMFurnace;
import moze_intel.projecte.gameObjs.items.ItemPE;
import moze_intel.projecte.gameObjs.registries.PEBlocks;
import moze_intel.projecte.gameObjs.registries.PEItems;
import moze_intel.projecte.integration.jei.collectors.CollectorRecipeCategory;
import moze_intel.projecte.integration.jei.collectors.FuelUpgradeRecipe;
import moze_intel.projecte.integration.jei.world_transmute.WorldTransmuteRecipeCategory;
import moze_intel.projecte.utils.EMCHelper;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;

@JeiPlugin
public class PEJeiPlugin implements IModPlugin {

	private static final ResourceLocation UID = PECore.rl("main");

	private static final IIngredientSubtypeInterpreter<ItemStack> PROJECTE_INTERPRETER = (stack, context) -> {
		if (context == UidContext.Ingredient) {
			long stored = ItemPE.getEmc(stack);
			if (stored > 0) {
				return Long.toString(stored);
			}
		}
		return IIngredientSubtypeInterpreter.NONE;
	};

	@NotNull
	@Override
	public ResourceLocation getPluginUid() {
		return UID;
	}

	public static void registerItemSubtypes(ISubtypeRegistration registry, Collection<? extends Holder<? extends ItemLike>> itemProviders) {
		for (Holder<? extends ItemLike> itemProvider : itemProviders) {
			registry.registerSubtypeInterpreter(itemProvider.value().asItem(), PROJECTE_INTERPRETER);
		}
	}

	@Override
	public void registerItemSubtypes(ISubtypeRegistration registry) {
		registerItemSubtypes(registry, PEItems.ITEMS.getEntries());
		registerItemSubtypes(registry, PEBlocks.BLOCKS.getSecondaryEntries());
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registry) {
		registry.addRecipeCategories(new WorldTransmuteRecipeCategory(registry.getJeiHelpers().getGuiHelper()));
		registry.addRecipeCategories(new CollectorRecipeCategory(registry.getJeiHelpers().getGuiHelper()));
	}

	@Override
	public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
		registration.addRecipeTransferHandler(PhilosStoneContainer.class, MenuType.CRAFTING, RecipeTypes.CRAFTING, 1, 9, 10, 36);
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registry) {
		registry.addRecipeCatalyst(PEItems.PHILOSOPHERS_STONE.asStack(), RecipeTypes.CRAFTING, WorldTransmuteRecipeCategory.RECIPE_TYPE);
		registry.addRecipeCatalyst(new ItemStack(PEBlocks.COLLECTOR), CollectorRecipeCategory.RECIPE_TYPE);
		registry.addRecipeCatalyst(new ItemStack(PEBlocks.COLLECTOR_MK2), CollectorRecipeCategory.RECIPE_TYPE);
		registry.addRecipeCatalyst(new ItemStack(PEBlocks.COLLECTOR_MK3), CollectorRecipeCategory.RECIPE_TYPE);
		registry.addRecipeCatalyst(new ItemStack(PEBlocks.DARK_MATTER_FURNACE), RecipeTypes.SMELTING, RecipeTypes.FUELING);
		registry.addRecipeCatalyst(new ItemStack(PEBlocks.RED_MATTER_FURNACE), RecipeTypes.SMELTING, RecipeTypes.FUELING);
	}

	@Override
	public void registerGuiHandlers(IGuiHandlerRegistration registry) {
		registry.addRecipeClickArea(GUIDMFurnace.class, 73, 34, 25, 16, RecipeTypes.SMELTING, RecipeTypes.FUELING);
		registry.addRecipeClickArea(GUIRMFurnace.class, 88, 35, 25, 17, RecipeTypes.SMELTING, RecipeTypes.FUELING);
		registry.addRecipeClickArea(AbstractCollectorScreen.MK1.class, 138, 31, 10, 24, CollectorRecipeCategory.RECIPE_TYPE);
		registry.addRecipeClickArea(AbstractCollectorScreen.MK2.class, 138 + 16, 31, 10, 24, CollectorRecipeCategory.RECIPE_TYPE);
		registry.addRecipeClickArea(AbstractCollectorScreen.MK3.class, 138 + 34, 31, 10, 24, CollectorRecipeCategory.RECIPE_TYPE);
	}

	@Override
	public void registerRecipes(IRecipeRegistration registry) {
		registry.addRecipes(WorldTransmuteRecipeCategory.RECIPE_TYPE, WorldTransmuteRecipeCategory.getAllTransmutations());
	}

	@Override
	public void onRuntimeAvailable(@NotNull IJeiRuntime jeiRuntime) {
		List<FuelUpgradeRecipe> recipes = new ArrayList<>();
		for (Item i : FuelMapper.getFuelMap()) {
			ItemStack stack = new ItemStack(i);
			ItemStack fuelUpgrade = FuelMapper.getFuelUpgrade(stack);
			if (EMCHelper.getEmcValue(stack) <= EMCHelper.getEmcValue(fuelUpgrade)) {
				recipes.add(new FuelUpgradeRecipe(stack, fuelUpgrade));
			}
		}
		jeiRuntime.getRecipeManager().addRecipes(CollectorRecipeCategory.RECIPE_TYPE, recipes);
	}
}