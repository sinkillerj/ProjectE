package moze_intel.projecte.emc.mappers;

import com.google.common.collect.Iterables;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModAPIManager;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.versioning.VersionParser;

public class CraftTweakerRecipeMapper implements CraftingMapper.IRecipeMapper {
    private boolean ctCompat;

    public CraftTweakerRecipeMapper() {
        if (Loader.isModLoaded("crafttweaker")) { //Check to make sure it is a version of CraftTweaker that uses the new Recipe System
            for (ModContainer mod : Iterables.concat(Loader.instance().getActiveModList(), ModAPIManager.INSTANCE.getAPIList())) {
                if (mod.getModId().equals("crafttweaker")) {
                    ctCompat = VersionParser.parseVersionReference("crafttweaker@[4.1.5,)").containsVersion(mod.getProcessedVersion());
                    break;
                }
            }
        }
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
        //Make sure no imports are added that could cause problems
        return ctCompat && (recipe instanceof crafttweaker.mc1120.recipes.MCRecipeShaped || recipe instanceof crafttweaker.mc1120.recipes.MCRecipeShapeless);
    }
}