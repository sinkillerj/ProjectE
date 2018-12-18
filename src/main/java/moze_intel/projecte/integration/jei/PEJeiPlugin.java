package moze_intel.projecte.integration.jei;

import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.ingredients.IModIngredientRegistration;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.container.PhilosStoneContainer;
import moze_intel.projecte.integration.jei.collectors.CollectorRecipeCategory;
import moze_intel.projecte.integration.jei.mappers.JEICompatMapper;
import moze_intel.projecte.integration.jei.mappers.JEIFuelMapper;
import moze_intel.projecte.integration.jei.world_transmute.WorldTransmuteRecipeCategory;
import moze_intel.projecte.utils.WorldTransmutations;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLCommonHandler;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

@JEIPlugin
public class PEJeiPlugin implements IModPlugin
{
    public static IJeiRuntime RUNTIME = null;
    private static List<JEICompatMapper> mappers = new ArrayList<>();

    @Override
    public void registerItemSubtypes(@Nonnull ISubtypeRegistry subtypeRegistry) {}

    @Override
    public void registerIngredients(@Nonnull IModIngredientRegistration registry) {}

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        registry.addRecipeCategories(new WorldTransmuteRecipeCategory(registry.getJeiHelpers().getGuiHelper()));
        registry.addRecipeCategories(new CollectorRecipeCategory(registry.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void register(@Nonnull IModRegistry registry)
    {
        // todo finish this, add alchbag
        registry.addRecipes(WorldTransmutations.getWorldTransmutations(), WorldTransmuteRecipeCategory.UID);
        registry.getRecipeTransferRegistry().addRecipeTransferHandler(PhilosStoneContainer.class, VanillaRecipeCategoryUid.CRAFTING, 1, 9, 10, 36);

        registry.addRecipeCatalyst(new ItemStack(ObjHandler.philosStone, 1), WorldTransmuteRecipeCategory.UID);
        registry.addRecipeCatalyst(new ItemStack(ObjHandler.collectorMK1, 1), CollectorRecipeCategory.UID);
        registry.addRecipeCatalyst(new ItemStack(ObjHandler.collectorMK2, 1), CollectorRecipeCategory.UID);
        registry.addRecipeCatalyst(new ItemStack(ObjHandler.collectorMK3, 1), CollectorRecipeCategory.UID);

        mappers.add(new JEIFuelMapper());
    }

    @Override
    public void onRuntimeAvailable(@Nonnull IJeiRuntime jeiRuntime)
    {
        RUNTIME = jeiRuntime;
    }

    public static void refresh()
    {
        if (FMLCommonHandler.instance().getSide().isClient())
        {
            Minecraft.getMinecraft().addScheduledTask(() -> mappers.forEach(JEICompatMapper::refresh));
        }
    }

}
