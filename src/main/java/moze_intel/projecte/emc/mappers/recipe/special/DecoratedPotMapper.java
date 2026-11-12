package moze_intel.projecte.emc.mappers.recipe.special;

import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.ArrayList;
import java.util.List;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.mapper.collector.IMappingCollector;
import moze_intel.projecte.api.mapper.recipe.INSSFakeGroupManager;
import moze_intel.projecte.api.mapper.recipe.INSSFakeGroupManager.FakeGroupData;
import moze_intel.projecte.api.nss.NSSItem;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import moze_intel.projecte.config.PEConfigTranslations;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.EMCHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import java.util.Optional;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.DecoratedPotRecipe;
import net.minecraft.world.level.block.entity.PotDecorations;

//@RecipeTypeMapper//TODO: Evaluate if we want to eventually move from the component processor to just premapping values for all pots
public class DecoratedPotMapper extends SpecialRecipeMapper<DecoratedPotRecipe> {

	private static final Identifier DECORATED_POT = BuiltInRegistries.ITEM.getKey(Items.DECORATED_POT);

	@Override
	protected Class<DecoratedPotRecipe> getRecipeClass() {
		return DecoratedPotRecipe.class;
	}

	@Override
	protected boolean handleRecipe(IMappingCollector<NormalizedSimpleStack, Long> mapper, RegistryAccess registryAccess, INSSFakeGroupManager fakeGroupManager) {
		Iterable<Holder<Item>> ingredients = BuiltInRegistries.ITEM.getTagOrEmpty(ItemTags.DECORATED_POT_INGREDIENTS);
		int size = 0;
		for (Holder<Item> ignored : ingredients) {
			size++;
		}
		if (size == 0) {
			return false;
		}
		int recipeCount = 0;
		int uniqueInputs = 0;
		record IngredientData(Item item, NSSItem nss) {
		}
		List<IngredientData> ingredientData = new ArrayList<>(size);
		for (Holder<Item> ingredient : ingredients) {
			ingredientData.add(new IngredientData(ingredient.value(), NSSItem.createItem(ingredient)));
		}
		for (IngredientData back : ingredientData) {
			for (IngredientData left : ingredientData) {
				for (IngredientData right : ingredientData) {
					for (IngredientData front : ingredientData) {
						PotDecorations decorations = new PotDecorations(
								Optional.of(new ItemStackTemplate(back.item())),
								Optional.of(new ItemStackTemplate(left.item())),
								Optional.of(new ItemStackTemplate(right.item())),
								Optional.of(new ItemStackTemplate(front.item())));
						NSSItem nssDecorated = createDecoratedPotItem(decorations);
						//Batch known inputs into a single calculation pass by using a fake group
						Object2IntMap<NormalizedSimpleStack> nssIngredients = getIngredients(back.nss(), left.nss(), right.nss(), front.nss());
						FakeGroupData group = fakeGroupManager.getOrCreateFakeGroupDirect(nssIngredients, false);
						mapper.addConversion(1, nssDecorated, EMCHelper.intMapOf(group.dummy(), 1));
						recipeCount++;
						if (group.created()) {
							uniqueInputs++;
						}
					}
				}
			}
		}
		PECore.debugLog("{} Statistics:", getName());
		PECore.debugLog("Found {} Decorated Pot Combinations. With {} unique combinations.", recipeCount, uniqueInputs);
		return true;
	}

	private NSSItem createDecoratedPotItem(PotDecorations decorations) {
		return NSSItem.createItem(DECORATED_POT, DataComponentPatch.builder().set(DataComponents.POT_DECORATIONS, decorations).build());
	}

	private Object2IntMap<NormalizedSimpleStack> getIngredients(NSSItem nssBack, NSSItem nssLeft, NSSItem nssRight, NSSItem nssFront) {
		Object2IntMap<NormalizedSimpleStack> ingredients = new Object2IntArrayMap<>(4);
		ingredients.put(nssBack, 1);
		ingredients.mergeInt(nssLeft, 1, Constants.INT_SUM);
		ingredients.mergeInt(nssRight, 1, Constants.INT_SUM);
		ingredients.mergeInt(nssFront, 1, Constants.INT_SUM);
		return ingredients;
	}

	@Override
	public String getName() {
		return PEConfigTranslations.MAPPING_CRAFTING_MAPPER_DECORATED_POT.title();
	}

	@Override
	public String getTranslationKey() {
		return PEConfigTranslations.MAPPING_CRAFTING_MAPPER_DECORATED_POT.getTranslationKey();
	}

	@Override
	public String getDescription() {
		return PEConfigTranslations.MAPPING_CRAFTING_MAPPER_DECORATED_POT.tooltip();
	}
}
