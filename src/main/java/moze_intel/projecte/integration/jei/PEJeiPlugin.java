package moze_intel.projecte.integration.jei;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaRecipeCategoryUid;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

@JeiPlugin
public class PEJeiPlugin implements IModPlugin {

	private static final ResourceLocation UID = PECore.rl("main");

	@Nonnull
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
		registration.addRecipeTransferHandler(PhilosStoneContainer.class, VanillaRecipeCategoryUid.CRAFTING, 1, 9, 10, 36);
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registry) {
		registry.addRecipeCatalyst(new ItemStack(PEItems.PHILOSOPHERS_STONE), VanillaRecipeCategoryUid.CRAFTING, WorldTransmuteRecipeCategory.UID);
		registry.addRecipeCatalyst(new ItemStack(PEBlocks.COLLECTOR), CollectorRecipeCategory.UID);
		registry.addRecipeCatalyst(new ItemStack(PEBlocks.COLLECTOR_MK2), CollectorRecipeCategory.UID);
		registry.addRecipeCatalyst(new ItemStack(PEBlocks.COLLECTOR_MK3), CollectorRecipeCategory.UID);
		registry.addRecipeCatalyst(new ItemStack(PEBlocks.DARK_MATTER_FURNACE), VanillaRecipeCategoryUid.FURNACE, VanillaRecipeCategoryUid.FUEL);
		registry.addRecipeCatalyst(new ItemStack(PEBlocks.RED_MATTER_FURNACE), VanillaRecipeCategoryUid.FURNACE, VanillaRecipeCategoryUid.FUEL);
	}

	@Override
	public void registerGuiHandlers(IGuiHandlerRegistration registry) {
		registry.addRecipeClickArea(GUIDMFurnace.class, 73, 34, 25, 16, VanillaRecipeCategoryUid.FURNACE, VanillaRecipeCategoryUid.FUEL);
		registry.addRecipeClickArea(GUIRMFurnace.class, 88, 35, 25, 17, VanillaRecipeCategoryUid.FURNACE, VanillaRecipeCategoryUid.FUEL);
		registry.addRecipeClickArea(AbstractCollectorScreen.MK1.class, 138, 31, 10, 24, CollectorRecipeCategory.UID);
		registry.addRecipeClickArea(AbstractCollectorScreen.MK2.class, 138 + 16, 31, 10, 24, CollectorRecipeCategory.UID);
		registry.addRecipeClickArea(AbstractCollectorScreen.MK3.class, 138 + 34, 31, 10, 24, CollectorRecipeCategory.UID);
	}

	@Override
	public void registerRecipes(IRecipeRegistration registry) {
		registry.addRecipes(WorldTransmuteRecipeCategory.getAllTransmutations(), WorldTransmuteRecipeCategory.UID);
		List<FuelUpgradeRecipe> fuelRecipes = new ArrayList<>();
		//TODO - 1.18: Figure this out it doesn't have recipes in time due to having moved it all back so that we can properly
		// read stuff from tags
		for (Item i : FuelMapper.getFuelMap()) {
			ItemStack stack = new ItemStack(i);
			ItemStack fuelUpgrade = FuelMapper.getFuelUpgrade(stack);
			if (EMCHelper.getEmcValue(stack) <= EMCHelper.getEmcValue(fuelUpgrade)) {
				fuelRecipes.add(new FuelUpgradeRecipe(stack, fuelUpgrade));
			}
		}
		registry.addRecipes(fuelRecipes, CollectorRecipeCategory.UID);
	}
}