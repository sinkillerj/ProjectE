package moze_intel.projecte.integration.jei;

import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.ingredients.IModIngredientRegistration;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.integration.jei.world_transmute.WorldTransmuteRecipeCategory;
import moze_intel.projecte.integration.jei.world_transmute.WorldTransmuteRecipeHandler;
import moze_intel.projecte.utils.WorldTransmutations;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

@JEIPlugin
public class PEJeiPlugin implements IModPlugin
{
    public static IJeiRuntime RUNTIME = null;

    @Override
    public void registerItemSubtypes(ISubtypeRegistry subtypeRegistry) {}

    @Override
    public void registerIngredients(IModIngredientRegistration registry) {}

    @Override
    public void register(@Nonnull IModRegistry registry)
    {
        // todo finish this, add alchbag and klein
        registry.addRecipeCategories(new WorldTransmuteRecipeCategory(registry.getJeiHelpers().getGuiHelper()));
        registry.addRecipeHandlers(new WorldTransmuteRecipeHandler());
        registry.addRecipes(WorldTransmutations.getWorldTransmutations());

        registry.addRecipeCategoryCraftingItem(new ItemStack(ObjHandler.philosStone), VanillaRecipeCategoryUid.CRAFTING);
    }

    @Override
    public void onRuntimeAvailable(@Nonnull IJeiRuntime jeiRuntime)
    {
        RUNTIME = jeiRuntime;
    }

}
