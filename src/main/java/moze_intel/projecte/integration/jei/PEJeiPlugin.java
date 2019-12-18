/*package moze_intel.projecte.integration.jei;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaRecipeCategoryUid;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import moze_intel.projecte.PECore;
import moze_intel.projecte.emc.FuelMapper;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.container.PhilosStoneContainer;
import moze_intel.projecte.integration.jei.collectors.CollectorRecipeCategory;
import moze_intel.projecte.integration.jei.collectors.FuelUpgradeRecipe;
import moze_intel.projecte.integration.jei.world_transmute.WorldTransmuteRecipeCategory;
import moze_intel.projecte.utils.EMCHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

@JeiPlugin
public class PEJeiPlugin implements IModPlugin {

	private static final ResourceLocation UID = new ResourceLocation(PECore.MODID, "main");

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
		registry.addRecipeCatalyst(new ItemStack(ObjHandler.philosStone), VanillaRecipeCategoryUid.CRAFTING, WorldTransmuteRecipeCategory.UID);
		registry.addRecipeCatalyst(new ItemStack(ObjHandler.collectorMK1), CollectorRecipeCategory.UID);
		registry.addRecipeCatalyst(new ItemStack(ObjHandler.collectorMK2), CollectorRecipeCategory.UID);
		registry.addRecipeCatalyst(new ItemStack(ObjHandler.collectorMK3), CollectorRecipeCategory.UID);
	}

	@Override
	public void registerRecipes(IRecipeRegistration registry) {
		registry.addRecipes(WorldTransmuteRecipeCategory.getAllTransmutations(), WorldTransmuteRecipeCategory.UID);

		List<FuelUpgradeRecipe> fuelRecipes = new ArrayList<>();
		for (Item i : FuelMapper.getFuelMap()) {
			ItemStack stack = new ItemStack(i);
			ItemStack fuelUpgrade = FuelMapper.getFuelUpgrade(stack);
			if (EMCHelper.getEmcValue(stack) <= EMCHelper.getEmcValue(fuelUpgrade)) {
				fuelRecipes.add(new FuelUpgradeRecipe(stack, fuelUpgrade));
			}
		}
		registry.addRecipes(fuelRecipes, CollectorRecipeCategory.UID);
	}
}*/