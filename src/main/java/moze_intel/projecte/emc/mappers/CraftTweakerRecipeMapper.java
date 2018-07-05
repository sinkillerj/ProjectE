package moze_intel.projecte.emc.mappers;

import crafttweaker.api.recipes.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.versioning.VersionParser;

public class CraftTweakerRecipeMapper implements CraftingMapper.IRecipeMapper {
    private boolean ctCompat;

    public CraftTweakerRecipeMapper() {
        //Check to make sure it is a version of CraftTweaker that uses the new Recipe System
        ctCompat = Loader.isModLoaded("crafttweaker") && VersionParser.parseVersionReference("crafttweaker@[4.1.5,)").containsVersion(Loader.instance().getIndexedModList().get("crafttweaker").getProcessedVersion());
    }

    @Override
    public String getName() {
        return "CraftTweakerRecipeMapper";
    }

    @Override
    public String getDescription() {
        return "Maps `IRecipe` CraftTweaker crafting recipes that extend `MCRecipeShaped` or `MCRecipeShapeless";
    }

    @Override
    public boolean canHandle(IRecipe recipe) {
        return ctCompat && (recipe instanceof ICraftingRecipe);
    }
}