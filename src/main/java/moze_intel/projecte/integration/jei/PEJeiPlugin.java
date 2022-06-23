package moze_intel.projecte.integration.jei;

import java.util.ArrayList;
import java.util.List;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import moze_intel.projecte.PECore;
import moze_intel.projecte.emc.FuelMapper;
import moze_intel.projecte.gameObjs.container.PhilosStoneContainer;
import moze_intel.projecte.gameObjs.gui.AbstractCollectorScreen;
import moze_intel.projecte.gameObjs.gui.GUIDMFurnace;
import moze_intel.projecte.gameObjs.gui.GUIRMFurnace;
import moze_intel.projecte.gameObjs.registries.PEBlocks;
import moze_intel.projecte.gameObjs.registries.PEItems;
import moze_intel.projecte.integration.jei.collectors.CollectorRecipeCategory;
import moze_intel.projecte.integration.jei.collectors.FuelUpgradeRecipe;
import moze_intel.projecte.integration.jei.world_transmute.WorldTransmuteRecipeCategory;
import moze_intel.projecte.utils.EMCHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

@JeiPlugin
public class PEJeiPlugin implements IModPlugin {

	private static final ResourceLocation UID = PECore.rl("main");

	@NotNull
	@Override
	public ResourceLocation getPluginUid() {
		return UID;
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
		registry.addRecipeCatalyst(new ItemStack(PEItems.PHILOSOPHERS_STONE), RecipeTypes.CRAFTING, WorldTransmuteRecipeCategory.RECIPE_TYPE);
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