package moze_intel.projecte.emc.mappers.recipe;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.List;
import moze_intel.projecte.api.nss.NSSItem;
import moze_intel.projecte.api.nss.NormalizedSimpleStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Test crafting mapper recipe dispatch")
class CraftingMapperTest {

	@Test
	@DisplayName("Unhandled recipes do not carry over between mapper passes")
	void testUnhandledRecipesDoNotCarryOverBetweenMapperPasses() {
		List<String> recipes = List.of("special", "generic", "other");

		CraftingMapper.RecipeHandlingResult<String> firstPass = CraftingMapper.handleRecipes(recipes, recipe -> false);
		Assertions.assertEquals(0, firstPass.handled());
		Assertions.assertEquals(recipes, firstPass.unhandled());

		CraftingMapper.RecipeHandlingResult<String> secondPass = CraftingMapper.handleRecipes(recipes, "special"::equals);
		Assertions.assertEquals(1, secondPass.handled());
		Assertions.assertEquals(List.of("generic", "other"), secondPass.unhandled());
	}

	@Test
	@DisplayName("A fully handled mapper pass returns an empty unhandled list")
	void testFullyHandledMapperPassHasNoUnhandledRecipes() {
		List<String> recipes = List.of("first", "second");

		CraftingMapper.RecipeHandlingResult<String> result = CraftingMapper.handleRecipes(recipes, recipe -> true);

		Assertions.assertEquals(2, result.handled());
		Assertions.assertTrue(result.unhandled().isEmpty());
	}

	@Test
	@DisplayName("Expected dynamic recipes are separated from unexpected mapper gaps")
	void testExpectedUnhandledRecipesAreClassifiedSeparately() {
		List<String> recipes = List.of("armor_dye", "unknown");

		CraftingMapper.RecipeDiagnosticResult<String> result = CraftingMapper.classifyUnhandledRecipes(recipes,
				recipe -> "armor_dye".equals(recipe) ? "dynamic output" : null);

		Assertions.assertEquals(1, result.expected().size());
		Assertions.assertEquals("armor_dye", result.expected().getFirst().recipe());
		Assertions.assertEquals("dynamic output", result.expected().getFirst().reason());
		Assertions.assertEquals(List.of("unknown"), result.unexpected());
	}

	@Test
	@DisplayName("Expected recipe classifications require a documented reason")
	void testExpectedUnhandledRecipesRequireReason() {
		List<String> recipes = List.of("null_reason", "blank_reason");

		CraftingMapper.RecipeDiagnosticResult<String> result = CraftingMapper.classifyUnhandledRecipes(recipes,
				recipe -> "null_reason".equals(recipe) ? null : "  ");

		Assertions.assertTrue(result.expected().isEmpty());
		Assertions.assertEquals(recipes, result.unexpected());
	}

	@Test
	@DisplayName("Crafting remainders preserve the returned stack count")
	void testCraftingRemainderPreservesReturnedCount() {
		Object2IntMap<String> ingredients = new Object2IntOpenHashMap<>();

		BaseRecipeTypeMapper.subtractCraftingRemainder(ingredients, "empty_container", 3);

		Assertions.assertEquals(-3, ingredients.getInt("empty_container"),
				"A remainder stack containing multiple items must subtract every returned item");
	}

	@Test
	@DisplayName("Invalid crafting remainder counts fail closed")
	void testInvalidCraftingRemainderCountFailsClosed() {
		Object2IntMap<String> ingredients = new Object2IntOpenHashMap<>();

		Assertions.assertThrows(IllegalArgumentException.class,
				() -> BaseRecipeTypeMapper.subtractCraftingRemainder(ingredients, "empty_container", 0));
	}

	@Test
	@DisplayName("Crafting remainder accumulation cannot silently overflow")
	void testCraftingRemainderAccumulationCannotOverflow() {
		Object2IntMap<String> ingredients = new Object2IntOpenHashMap<>();
		ingredients.put("empty_container", Integer.MIN_VALUE);

		Assertions.assertThrows(ArithmeticException.class,
				() -> BaseRecipeTypeMapper.subtractCraftingRemainder(ingredients, "empty_container", 1));
	}

	@Test
	@DisplayName("A malformed ingredient alternative does not discard valid alternatives")
	void testMalformedAlternativeDoesNotDiscardValidAlternative() {
		ItemStack malformed = new ItemStack(Items.STONE);
		ItemStack valid = new ItemStack(Items.DIRT);

		Object2IntMap<NormalizedSimpleStack> normalizedMatches = new Object2IntOpenHashMap<>();
		List<ItemStack> matches = BaseRecipeTypeMapper.getNormalizableMatches(new ItemStack[]{malformed, valid},
				ResourceLocation.fromNamespaceAndPath("projecte", "malformed_alternative_test"), normalizedMatches, stack -> {
					if (stack.getItem() == Items.STONE) {
						throw new IllegalArgumentException("simulated malformed alternative");
					}
					return NSSItem.createItem(stack);
				});

		Assertions.assertEquals(1, matches.size(), "A broken alternative must be skipped without poisoning the usable choice");
		Assertions.assertSame(valid, matches.getFirst(), "The valid recipe-provided stack should remain available for remainder handling");
		Assertions.assertEquals(1, normalizedMatches.getInt(NSSItem.createItem(valid)));
	}

	@Test
	@DisplayName("Malformed recipe stacks fail closed instead of escaping normalization")
	void testMalformedRecipeStackFailsClosed() {
		ItemStack malformed = new ItemStack(Items.STONE);

		Assertions.assertNull(BaseRecipeTypeMapper.normalizeStack(malformed,
				ResourceLocation.fromNamespaceAndPath("projecte", "malformed_stack_test"), "ingredient",
				stack -> {
					throw new IllegalArgumentException("simulated malformed stack");
				}));
	}

	@Test
	@DisplayName("Unexpected normalizer failures are not hidden as malformed recipe data")
	void testUnexpectedNormalizerFailureEscapes() {
		ItemStack stack = new ItemStack(Items.STONE);

		Assertions.assertThrows(IllegalStateException.class, () -> BaseRecipeTypeMapper.normalizeStack(stack,
				ResourceLocation.fromNamespaceAndPath("projecte", "unexpected_normalizer_failure_test"), "ingredient",
				ignored -> {
					throw new IllegalStateException("simulated programming failure");
				}));
	}

}
