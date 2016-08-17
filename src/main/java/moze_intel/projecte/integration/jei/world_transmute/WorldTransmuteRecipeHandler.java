package moze_intel.projecte.integration.jei.world_transmute;

import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;
import moze_intel.projecte.utils.WorldTransmutations;

import javax.annotation.Nonnull;

public class WorldTransmuteRecipeHandler implements IRecipeHandler<WorldTransmutations.Entry>
{
    @Nonnull
    @Override
    public Class<WorldTransmutations.Entry> getRecipeClass()
    {
        return WorldTransmutations.Entry.class;
    }

    @Nonnull
    @Override
    public String getRecipeCategoryUid()
    {
        return WorldTransmuteRecipeCategory.UID;
    }

    @Nonnull
    @Override
    public String getRecipeCategoryUid(@Nonnull WorldTransmutations.Entry e)
    {
        return getRecipeCategoryUid();
    }

    @Nonnull
    @Override
    public IRecipeWrapper getRecipeWrapper(@Nonnull WorldTransmutations.Entry recipe)
    {
        return new WorldTransmuteRecipeWrapper(recipe);
    }

    @Override
    public boolean isRecipeValid(@Nonnull WorldTransmutations.Entry recipe)
    {
        return recipe.input != null && recipe.outputs != null && recipe.outputs.getLeft() != null;
    }
}
