package moze_intel.projecte.integration.crafttweaker.actions;

import crafttweaker.IAction;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import moze_intel.projecte.gameObjs.ObjHandler;
import moze_intel.projecte.gameObjs.customRecipes.RecipeShapelessHidden;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.registries.RegistryManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class SmeltingRecipeAction implements IAction {
    public static class Add extends SmeltingRecipeAction
    {
        private final Ingredient input;
        private final Ingredient fuel;
        private final ItemStack output;
        private final String name;

        public Add(String name, IItemStack output, IIngredient input, IIngredient fuel)
        {
            this.name = name;
            this.output = CraftTweakerMC.getItemStack(output);
            this.input = CraftTweakerMC.getIngredient(input);
            this.fuel = fuel == null ? Ingredient.fromItem(Items.COAL) : CraftTweakerMC.getIngredient(fuel);
        }

        @Override
        public void apply()
        {
            NonNullList<Ingredient> ingredients = NonNullList.create();
            ingredients.add(Ingredient.fromStacks(new ItemStack(ObjHandler.philosStone)));
            for (int i = 0; i < 7; i++)
            {
                ingredients.add(input);
            }
            ingredients.add(fuel);
            ForgeRegistries.RECIPES.register(new RecipeShapelessHidden("", this.output, ingredients).setRegistryName(name));
        }

        @Override
        public String describe()
        {
            return "Adding philosopher's stone smelting recipe for " + output;
        }
    }

    public static class Remove extends SmeltingRecipeAction
    {
        private IRecipe recipe;

        public Remove(IItemStack rem)
        {
            ItemStack remove = CraftTweakerMC.getItemStack(rem);
            List<IRecipe> recipes = ForgeRegistries.RECIPES.getValues();
            for(IRecipe irecipe : recipes)
            {
                if (irecipe instanceof RecipeShapelessHidden && remove.isItemEqual(irecipe.getRecipeOutput()))
                {
                    recipe = irecipe;
                    break;
                }
            }
        }

        @Override
        public void apply()
        {
            if (recipe != null)
            {
                RegistryManager.ACTIVE.getRegistry(GameData.RECIPES).remove(recipe.getRegistryName());
            }
        }

        @Override
        public String describe()
        {
            return "Removing philosopher's stone smelting recipe for " + recipe.getRecipeOutput().getDisplayName();
        }
    }

    public static class RemoveAll extends SmeltingRecipeAction {
        @Override
        public void apply() {
            Set<Map.Entry<ResourceLocation, IRecipe>> recipes = ForgeRegistries.RECIPES.getEntries();
            List<ResourceLocation> toRemove = new ArrayList<>();
            for (Map.Entry<ResourceLocation, IRecipe> recipe : recipes)
            {
                if (recipe.getValue() instanceof RecipeShapelessHidden)
                {
                    toRemove.add(recipe.getKey());
                }
            }
            ForgeRegistry<IRecipe> registry = RegistryManager.ACTIVE.getRegistry(GameData.RECIPES);
            toRemove.forEach(registry::remove);
        }

        @Override
        public String describe() {
            return "Removing all philosopher's stone smelting recipes";
        }
    }
}