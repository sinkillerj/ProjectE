package moze_intel.projecte.emc.mappers.recipe.special;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.ArmorDyeRecipe;
import net.minecraft.world.item.crafting.BookCloningRecipe;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Test special recipe diagnostic classification")
class SpecialRecipeMarkHandledMapperTest {

	@Test
	@DisplayName("Armor dye is an expected dynamic recipe skip")
	void testArmorDyeIsExpectedUnhandled() {
		SpecialRecipeMarkHandledMapper mapper = new SpecialRecipeMarkHandledMapper();
		RecipeHolder<?> recipe = new RecipeHolder<>(ResourceLocation.fromNamespaceAndPath("projecte", "armor_dye_test"),
				new ArmorDyeRecipe(CraftingBookCategory.MISC));
		Assertions.assertEquals(SpecialRecipeMarkHandledMapper.ARMOR_DYE_SKIP_REASON, mapper.getExpectedUnhandledReason(recipe, null));
	}

	@Test
	@DisplayName("Other special recipes are not silently classified as expected skips")
	void testOtherSpecialRecipesRemainUnclassified() {
		SpecialRecipeMarkHandledMapper mapper = new SpecialRecipeMarkHandledMapper();
		RecipeHolder<?> recipe = new RecipeHolder<>(ResourceLocation.fromNamespaceAndPath("projecte", "book_clone_test"),
				new BookCloningRecipe(CraftingBookCategory.MISC));
		Assertions.assertNull(mapper.getExpectedUnhandledReason(recipe, null));
	}
}
