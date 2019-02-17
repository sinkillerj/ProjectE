package moze_intel.projecte.gameObjs.customRecipes;

import moze_intel.projecte.PECore;
import moze_intel.projecte.gameObjs.ObjHandler;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.item.crafting.ShapelessRecipe;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.crafting.VanillaRecipeTypes;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import javax.annotation.Nonnull;

public class PhilStoneSmeltingHelper implements IResourceManagerReloadListener
{
    @Override
    public void onResourceManagerReload(@Nonnull IResourceManager resourceManager)
    {
        if (!ServerLifecycleHooks.getCurrentServer().getWorld(DimensionType.OVERWORLD)
                .getWorldInfo().getDisabledDataPacks().contains("mod:" + PECore.MODID))
        {
            RecipeManager mgr = ServerLifecycleHooks.getCurrentServer().getRecipeManager();
            for (FurnaceRecipe r : mgr.getRecipes(VanillaRecipeTypes.SMELTING))
            {
                if (r.getIngredients().isEmpty()
                        || r.getIngredients().get(0).hasNoMatchingItems()
                        || r.getRecipeOutput().isEmpty())
                {
                    continue;
                }

                Ingredient input = r.getIngredients().get(0);
                ItemStack output = r.getRecipeOutput().copy();
                output.setCount(output.getCount() * 7);

                String inputName = r.getId().toString().replace(':', '_');
                ResourceLocation recipeName = new ResourceLocation(PECore.MODID, "philstone_smelt_" + inputName);

                NonNullList<Ingredient> ingrs = NonNullList.from(Ingredient.EMPTY,
                        Ingredient.fromItems(ObjHandler.philosStone),
                        input, input, input, input, input, input, input,
                        Ingredient.fromItems(Items.COAL, Items.CHARCOAL));
                mgr.addRecipe(new RecipeShapelessHidden(recipeName, "projecte:philstone_smelt", output, ingrs));
            }
        }
    }
}
