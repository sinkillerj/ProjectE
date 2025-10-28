package moze_intel.projecte.integration.recipe_viewer.jei;

import java.util.Collection;
import java.util.List;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IIngredientAliasRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.capabilities.item.IModeChanger;
import moze_intel.projecte.gameObjs.container.PhilosStoneContainer;
import moze_intel.projecte.gameObjs.gui.AbstractCollectorScreen;
import moze_intel.projecte.gameObjs.gui.GUIDMFurnace;
import moze_intel.projecte.gameObjs.gui.GUIRMFurnace;
import moze_intel.projecte.gameObjs.registries.PEBlocks;
import moze_intel.projecte.gameObjs.registries.PEDataComponentTypes;
import moze_intel.projecte.gameObjs.registries.PEItems;
import moze_intel.projecte.integration.IntegrationHelper;
import moze_intel.projecte.integration.recipe_viewer.RecipeViewerHelper;
import moze_intel.projecte.integration.recipe_viewer.alias.ProjectEAliasMapping;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.fml.ModList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@JeiPlugin
public class PEJeiPlugin implements IModPlugin {

	private static final ResourceLocation UID = PECore.rl("main");

	private static final ISubtypeInterpreter<ItemStack> PROJECTE_INTERPRETER = new ISubtypeInterpreter<>() {
		@Nullable
		@Override
		public Object getSubtypeData(@NotNull ItemStack stack, @NotNull UidContext context) {
			if (context == UidContext.Ingredient) {
				Object mode = null;
				if (stack.getItem() instanceof IModeChanger<?> modeChanger) {
					mode = modeChanger.getMode(stack);
				}
				Long stored = stack.get(PEDataComponentTypes.STORED_EMC);
				if (stored != null && stored > 0) {
					return mode == null ? stored : List.of(mode, stored);
				}
				return mode;
			}
			return null;
		}

		@NotNull
		@Override
		public String getLegacyStringSubtypeInfo(@NotNull ItemStack stack, @NotNull UidContext context) {
			if (context == UidContext.Ingredient) {
				Object mode = null;
				if (stack.getItem() instanceof IModeChanger<?> modeChanger) {
					mode = modeChanger.getMode(stack);
				}
				Long stored = stack.get(PEDataComponentTypes.STORED_EMC);
				if (stored != null && stored > 0) {
					return mode == null ? stored.toString() : mode + ";" + stored;
				} else if (mode != null) {
					return mode.toString();
				}
			}
			return "";
		}
	};

	@NotNull
	@Override
	public ResourceLocation getPluginUid() {
		return UID;
	}

	private static boolean shouldLoad() {
		//Skip handling if both EMI and JEI are loaded as otherwise some things behave strangely
		return !ModList.get().isLoaded(IntegrationHelper.EMI_MODID);
	}

	public static void registerItemSubtypes(ISubtypeRegistration registry, Collection<? extends Holder<? extends ItemLike>> itemProviders) {
		for (Holder<? extends ItemLike> itemProvider : itemProviders) {
			registry.registerSubtypeInterpreter(itemProvider.value().asItem(), PROJECTE_INTERPRETER);
		}
	}

	@Override
	public void registerItemSubtypes(@NotNull ISubtypeRegistration registry) {
		if (shouldLoad()) {
			registerItemSubtypes(registry, PEItems.ITEMS.getEntries());
			registerItemSubtypes(registry, PEBlocks.BLOCKS.getSecondaryEntries());
		}
	}

	@Override
	public void registerCategories(@NotNull IRecipeCategoryRegistration registry) {
		if (shouldLoad()) {
			registry.addRecipeCategories(new WorldTransmuteRecipeCategory(registry.getJeiHelpers().getGuiHelper()));
			registry.addRecipeCategories(new CollectorRecipeCategory(registry.getJeiHelpers().getGuiHelper()));
		}
	}

	@Override
	public void registerRecipeTransferHandlers(@NotNull IRecipeTransferRegistration registration) {
		if (shouldLoad()) {
			registration.addRecipeTransferHandler(PhilosStoneContainer.class, MenuType.CRAFTING, RecipeTypes.CRAFTING, 1, 9, 10, 36);
		}
	}

	@Override
	public void registerRecipeCatalysts(@NotNull IRecipeCatalystRegistration registry) {
		if (shouldLoad()) {
			registry.addRecipeCatalyst(new ItemStack(PEBlocks.COLLECTOR), CollectorRecipeCategory.RECIPE_TYPE);
			registry.addRecipeCatalyst(new ItemStack(PEBlocks.COLLECTOR_MK2), CollectorRecipeCategory.RECIPE_TYPE);
			registry.addRecipeCatalyst(new ItemStack(PEBlocks.COLLECTOR_MK3), CollectorRecipeCategory.RECIPE_TYPE);
			registry.addRecipeCatalyst(new ItemStack(PEBlocks.DARK_MATTER_FURNACE), RecipeTypes.SMELTING, RecipeTypes.FUELING);
			registry.addRecipeCatalyst(new ItemStack(PEBlocks.RED_MATTER_FURNACE), RecipeTypes.SMELTING, RecipeTypes.FUELING);
		}
	}

	@Override
	public void registerGuiHandlers(@NotNull IGuiHandlerRegistration registry) {
		if (shouldLoad()) {
			registry.addRecipeClickArea(GUIDMFurnace.class, 73, 34, 25, 16, RecipeTypes.SMELTING, RecipeTypes.FUELING);
			registry.addRecipeClickArea(GUIRMFurnace.class, 88, 35, 25, 17, RecipeTypes.SMELTING, RecipeTypes.FUELING);
			registry.addRecipeClickArea(AbstractCollectorScreen.MK1.class, 138, 31, 10, 24, CollectorRecipeCategory.RECIPE_TYPE);
			registry.addRecipeClickArea(AbstractCollectorScreen.MK2.class, 138 + 16, 31, 10, 24, CollectorRecipeCategory.RECIPE_TYPE);
			registry.addRecipeClickArea(AbstractCollectorScreen.MK3.class, 138 + 34, 31, 10, 24, CollectorRecipeCategory.RECIPE_TYPE);
		}
	}

	@Override
	public void registerIngredientAliases(@NotNull IIngredientAliasRegistration registration) {
		new ProjectEAliasMapping().addAliases(new JEIAliasHelper(registration));
	}

	@Override
	public void registerRecipes(@NotNull IRecipeRegistration registry) {
		if (shouldLoad()) {
			registry.addRecipes(WorldTransmuteRecipeCategory.RECIPE_TYPE, List.copyOf(RecipeViewerHelper.getAllTransmutations()));
			registry.addRecipes(CollectorRecipeCategory.RECIPE_TYPE, RecipeViewerHelper.getFuelUpgrades());
		}
	}
}