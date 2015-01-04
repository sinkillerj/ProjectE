package moze_intel.projecte.emc.mappers;

import moze_intel.projecte.emc.IMappingCollector;
import moze_intel.projecte.emc.IngredientMap;
import moze_intel.projecte.emc.NormalizedSimpleStack;
import moze_intel.projecte.utils.PELogger;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import java.util.*;

public class CraftingMapper implements IEMCMapper<NormalizedSimpleStack> {

    List<IRecipeMapper> recipeMappers = new LinkedList<IRecipeMapper>();
    Set<Class> canNotMap = new HashSet<Class>();

    @Override
    public void addMappings(IMappingCollector<NormalizedSimpleStack> mapper) {
        Iterator<IRecipe> iter = CraftingManager.getInstance().getRecipeList().iterator();
        while (iter.hasNext()) {
            IRecipe recipe = iter.next();
            boolean handled = false;
            ItemStack recipeOutput = recipe.getRecipeOutput();
            if (recipeOutput == null) continue;
            for (IRecipeMapper recipeMapper: recipeMappers) {
                if (recipeMapper.canHandle(recipe)) {
                    handled = true;
                    Iterable<IngredientMap<NormalizedSimpleStack>> ingredientMaps = recipeMapper.getIngredientsFor(recipe);
                    if (ingredientMaps != null) {
                        for (IngredientMap<NormalizedSimpleStack> ingredientMap: ingredientMaps) {
                            mapper.addConversionMultiple(recipeOutput.stackSize, new NormalizedSimpleStack(recipeOutput), ingredientMap.getMap());
                        }
                    }
                    break;
                }
            }
            if (!handled) {
                if (!canNotMap.contains(recipe.getClass())) {
                    canNotMap.add(recipe.getClass());
                    PELogger.logWarn("Can not map Crafting Recipes with Type: " + recipe.getClass().getName());
                }
            }
        }
    }


    protected static interface IRecipeMapper {
        public boolean canHandle(IRecipe recipe);
        public Iterable<IngredientMap<NormalizedSimpleStack>> getIngredientsFor(IRecipe recipe);
    }

    //TODO implement IRecipeMapper for ShapedRecipes, ShapelessRecipes, ShapedOreRecipe, ShapelessOreRecipe
    protected static class VanillaRecipeMapper implements IRecipeMapper{

        @Override
        public boolean canHandle(IRecipe recipe) {
            return recipe instanceof ShapedRecipes || recipe instanceof ShapelessRecipes;
        }

        @Override
        public Iterable<IngredientMap<NormalizedSimpleStack>> getIngredientsFor(IRecipe recipe) {
            IngredientMap<NormalizedSimpleStack> inputs = new IngredientMap<NormalizedSimpleStack>();
            Iterable<ItemStack> recipeItems = null;
            if (recipe instanceof ShapedRecipes)
            {
                recipeItems = Arrays.asList(((ShapedRecipes) recipe).recipeItems);
            }
            else if (recipe instanceof ShapelessRecipes)
            {
                recipeItems = ((ShapelessRecipes) recipe).recipeItems;
            }
            for (ItemStack recipeItem: recipeItems) {
                if (recipeItem == null) continue;
                inputs.addIngredient(new NormalizedSimpleStack(recipeItem), recipeItem.stackSize);
            }
            return Arrays.asList(inputs);
        }
    }

    protected static class VanillaOreRecipeMapper implements IRecipeMapper{

        public static<T> Iterable<Iterable<T>> recursiveRecipeInput(Iterable<T> recipeInputFixed, List<Iterable<T>> recipeInputsWithOptions) {
            List<Iterable<T>> out = new ArrayList<Iterable<T>>();
            Stack s = new Stack<T>();
            for (T fixedInput: recipeInputFixed)
                s.add(fixedInput);
            recursiveRecipeInput(recipeInputsWithOptions,0,out,s);
            return out;
        }
        public static<T> void recursiveRecipeInput(List<Iterable<T>> objects, int index, List<Iterable<T>> out, Stack<T> currentIngredients) {
            if (index < objects.size()) {
                Iterable<T> next = objects.get(index);
                for (T o : next) {
                    currentIngredients.push(o);
                    recursiveRecipeInput(objects, index + 1, out, currentIngredients);
                    currentIngredients.pop();
                }
            } else if (index == objects.size()) {
                List recipeInput = new LinkedList();
                for(T is: currentIngredients) {
                    recipeInput.add(is);
                }
                out.add(recipeInput);
            }
        }

        @Override
        public boolean canHandle(IRecipe recipe) {
            return recipe instanceof ShapedOreRecipe || recipe instanceof ShapelessOreRecipe;
        }

        @Override
        public Iterable<IngredientMap<NormalizedSimpleStack>> getIngredientsFor(IRecipe recipe) {
            List<IngredientMap<NormalizedSimpleStack>> inputs = new LinkedList<IngredientMap<NormalizedSimpleStack>>();
            Iterable<Object> recipeItems = null;
            if (recipe instanceof ShapedOreRecipe)
            {
                recipeItems = Arrays.asList(((ShapedOreRecipe) recipe).getInput());
            }
            else if (recipe instanceof ShapelessRecipes)
            {
                recipeItems = ((ShapelessOreRecipe) recipe).getInput();
            }
            ArrayList<Iterable<NormalizedSimpleStack>> variableInputs = new ArrayList<Iterable<NormalizedSimpleStack>>();
            ArrayList<NormalizedSimpleStack> fixedInputs = new ArrayList<NormalizedSimpleStack>();
            for (Object recipeItem: recipeItems) {
                if (recipeItem instanceof ItemStack) {
                    fixedInputs.add(new NormalizedSimpleStack((ItemStack)recipeItem));
                } else if (recipeItem instanceof Iterable) {
                    List<NormalizedSimpleStack> recipeItemOptions = new LinkedList<NormalizedSimpleStack>();
                    for (Object option: (Iterable)recipeItem) {
                        if (option instanceof ItemStack) {
                            recipeItemOptions.add(new NormalizedSimpleStack((ItemStack)option));
                        } else {
                            PELogger.logWarn("Can not map recipe " + recipe + " because found " + option.toString() + " instead of ItemStack");
                            return null;
                        }
                    }
                    variableInputs.add(recipeItemOptions);
                }
                for (Iterable<NormalizedSimpleStack> recipeIngredients: recursiveRecipeInput(fixedInputs, variableInputs)) {
                    IngredientMap<NormalizedSimpleStack> ingredientMap = new IngredientMap<NormalizedSimpleStack>();
                    for (NormalizedSimpleStack i: recipeIngredients) {
                        ingredientMap.addIngredient(i, 1);
                    }
                    inputs.add(ingredientMap);
                }
            }
            return inputs;
        }
    }
}
