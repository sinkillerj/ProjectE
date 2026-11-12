package moze_intel.projecte.emc.mappers;

import net.neoforged.neoforge.fluids.FluidType;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.PotionIngredient;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.tags.FluidTags;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.component.DataComponents;
import moze_intel.projecte.utils.EMCHelper;
import moze_intel.projecte.api.nss.NSSFluid;
import moze_intel.projecte.api.nss.NSSItem;
import java.util.List;
import java.util.ArrayList;
import moze_intel.projecte.api.mapper.EMCMapper;
import moze_intel.projecte.api.mapper.IEMCMapper;
import moze_intel.projecte.api.mapper.collector.IMappingCollector;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import moze_intel.projecte.config.PEConfigTranslations;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.ResourceManager;

/** Maps the data-driven brewing recipes, including their potion components. */
@EMCMapper
public class BrewingMapper implements IEMCMapper<NormalizedSimpleStack, Long> {

	@Override
	public void addMappings(IMappingCollector<NormalizedSimpleStack, Long> mapper, ReloadableServerResources serverResources,
			RegistryAccess registryAccess, ResourceManager resourceManager) {
        mapper.addConversion(1, NSSItem.createItem(
                PotionContents.createItemStack(Items.POTION,
                        Potions.WATER)), EMCHelper.intMapOf(
                NSSItem.createItem(Items.GLASS_BOTTLE), 1,
                NSSFluid.createTag(FluidTags.WATER),
                FluidType.BUCKET_VOLUME / 3));
        for (var holder : serverResources.getRecipeManager().recipeMap().byType(RecipeType.BREWING)) {
            var recipe = holder.value();
            var output = recipe.getOutput().create();
            for (var input : matchingStacks(recipe.getInput(), registryAccess)) {
                for (var reagent : matchingStacks(recipe.getReagent(), registryAccess)) {
                    mapper.addConversion(3 * output.getCount(), NSSItem.createItem(output),
                            EMCHelper.intMapOf(
                                    NSSItem.createItem(input), 3,
                                    NSSItem.createItem(reagent), 1));
                }
            }
        }
    }

    private static List<ItemStack> matchingStacks(
            PotionIngredient ingredient, RegistryAccess registries) {
        var matches = new ArrayList<ItemStack>();
        ingredient.ingredient().items().forEach(item -> {
            if (ingredient.potions().isEmpty()) {
                matches.add(new ItemStack(item));
            } else {
                registries.lookupOrThrow(Registries.POTION).listElements().forEach(potion -> {
                    var stack = new ItemStack(item);
                    stack.set(DataComponents.POTION_CONTENTS,
                            new PotionContents(potion));
                    if (ingredient.test(stack)) matches.add(stack);
                });
            }
        });
        return matches;
    }

	@Override
	public String getName() {
		return PEConfigTranslations.MAPPING_BREWING_MAPPER.title();
	}

	@Override
	public String getTranslationKey() {
		return PEConfigTranslations.MAPPING_BREWING_MAPPER.getTranslationKey();
	}

	@Override
	public String getDescription() {
		return PEConfigTranslations.MAPPING_BREWING_MAPPER.tooltip();
	}
}
