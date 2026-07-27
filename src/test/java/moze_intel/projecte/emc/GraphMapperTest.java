package moze_intel.projecte.emc;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import java.util.Collections;
import java.util.List;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.mapper.arithmetic.IValueArithmetic;
import moze_intel.projecte.api.mapper.collector.IExtendedMappingCollector;
import moze_intel.projecte.emc.arithmetic.HiddenBigFractionArithmetic;
import moze_intel.projecte.emc.collector.LongToBigFractionCollector;
import moze_intel.projecte.emc.generator.BigFractionToLongGenerator;
import moze_intel.projecte.utils.EMCHelper;
import org.apache.commons.math3.fraction.BigFraction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

@Timeout(3000)
@DisplayName("Test graph mapper")
class GraphMapperTest {

	@BeforeEach
	void setup() {
		SimpleGraphMapper<String, BigFraction, IValueArithmetic<BigFraction>> mapper = new SimpleGraphMapper<>(new HiddenBigFractionArithmetic());
		valueGenerator = new BigFractionToLongGenerator<>(mapper);
		mappingCollector = new LongToBigFractionCollector<>(mapper);
	}

	private BigFractionToLongGenerator<String> valueGenerator;
	private IExtendedMappingCollector<String, Long, IValueArithmetic<BigFraction>> mappingCollector;

	@Test
	@DisplayName("Test generating simple values")
	void testGenerateValuesSimple() {
		mappingCollector.setValueBefore("a1", 1L);
		mappingCollector.addConversion(1, "c4", List.of("a1", "a1", "a1", "a1"));
		mappingCollector.addConversion(1, "b2", List.of("a1", "a1"));

		Object2LongMap<String> values = valueGenerator.generateValues();
		Assertions.assertEquals(1, values.getLong("a1"));
		Assertions.assertEquals(2, values.getLong("b2"));
		Assertions.assertEquals(4, values.getLong("c4"));

	}

	@Test
	@DisplayName("Test generating simple values from multiple recipes")
	void testGenerateValuesSimpleMultiRecipe() {
		mappingCollector.setValueBefore("a1", 1L);
		//2 Recipes for c4
		mappingCollector.addConversion(1, "c4", List.of("a1", "a1", "a1", "a1"));
		mappingCollector.addConversion(2, "c4", List.of("b2", "b2"));
		mappingCollector.addConversion(1, "b2", List.of("a1", "a1"));

		Object2LongMap<String> values = valueGenerator.generateValues();
		Assertions.assertEquals(1, values.getLong("a1"));
		Assertions.assertEquals(2, values.getLong("b2"));
		Assertions.assertEquals(2, values.getLong("c4")); //2 * c4 = 2 * b2 => 2 * (2) = 2 * (2)
	}

	@Test
	@DisplayName("Test recipe output count is part of conversion identity when higher yield is added second")
	void testGenerateValuesSameIngredientsDifferentOutputCountHigherYieldSecond() {
		assertOutputCountIdentity(false);
	}

	@Test
	@DisplayName("Test recipe output count is part of conversion identity when lower yield is added second")
	void testGenerateValuesSameIngredientsDifferentOutputCountLowerYieldSecond() {
		assertOutputCountIdentity(true);
	}

	private void assertOutputCountIdentity(boolean higherYieldFirst) {
		mappingCollector.setValueBefore("ingredient", 10L);
		if (higherYieldFirst) {
			mappingCollector.addConversion(2, "output", List.of("ingredient"));
			mappingCollector.addConversion(1, "output", List.of("ingredient"));
		} else {
			mappingCollector.addConversion(1, "output", List.of("ingredient"));
			mappingCollector.addConversion(2, "output", List.of("ingredient"));
		}

		Assertions.assertEquals(5, valueGenerator.generateValues().getLong("output"));
	}

	@Test
	@DisplayName("Test conversion arithmetic is part of conversion identity when lower result is added second")
	void testGenerateValuesSameRecipeDifferentArithmeticLowerResultSecond() {
		assertArithmeticIdentity(false);
	}

	@Test
	@DisplayName("Test conversion arithmetic is part of conversion identity when higher result is added second")
	void testGenerateValuesSameRecipeDifferentArithmeticHigherResultSecond() {
		assertArithmeticIdentity(true);
	}

	private void assertArithmeticIdentity(boolean halvingArithmeticFirst) {
		mappingCollector.setValueBefore("ingredient", 10L);
		if (halvingArithmeticFirst) {
			mappingCollector.addConversion(1, "output", List.of("ingredient"), halvingArithmetic());
			mappingCollector.addConversion(1, "output", List.of("ingredient"));
		} else {
			mappingCollector.addConversion(1, "output", List.of("ingredient"));
			mappingCollector.addConversion(1, "output", List.of("ingredient"), halvingArithmetic());
		}

		Assertions.assertEquals(5, valueGenerator.generateValues().getLong("output"));
	}

	private IValueArithmetic<BigFraction> halvingArithmetic() {
		return new HiddenBigFractionArithmetic() {
			@Override
			public BigFraction div(BigFraction a, long b) {
				return super.div(a, b).divide(2);
			}
		};
	}

	@Test
	@DisplayName("Test generating simple values from multiple recipes, with an empty alternate")
	void testGenerateValuesSimpleMultiRecipeWithEmptyAlternative() {
		mappingCollector.setValueBefore("a1", 1L);
		//2 Recipes for c4
		mappingCollector.addConversion(1, "c4", List.of("a1", "a1", "a1", "a1"));
		mappingCollector.addConversion(1, "c4", Collections.emptyList());
		mappingCollector.addConversion(1, "b2", List.of("a1", "a1"));

		Object2LongMap<String> values = valueGenerator.generateValues();
		Assertions.assertEquals(1, values.getLong("a1"));
		Assertions.assertEquals(2, values.getLong("b2"));
		Assertions.assertEquals(4, values.getLong("c4")); //2 * c4 = 2 * b2 => 2 * (2) = 2 * (2)
	}

	@Test
	@DisplayName("Test generating and fixing simple values after inheriting")
	void testGenerateValuesSimpleFixedAfterInherit() {
		mappingCollector.setValueBefore("a1", 1L);
		mappingCollector.addConversion(1, "c4", List.of("a1", "a1", "a1", "a1"));
		mappingCollector.addConversion(1, "b2", List.of("a1", "a1"));
		mappingCollector.setValueAfter("b2", 20L);

		Object2LongMap<String> values = valueGenerator.generateValues();
		Assertions.assertEquals(1, values.getLong("a1"));
		Assertions.assertEquals(20, values.getLong("b2"));
		Assertions.assertEquals(4, values.getLong("c4"));
	}

	@Test
	@DisplayName("Test generating and fixing simple values without inheriting")
	void testGenerateValuesSimpleFixedDoNotInherit() {
		mappingCollector.setValueBefore("a1", 1L);
		mappingCollector.addConversion(1, "b2", List.of("a1", "a1"));
		mappingCollector.addConversion(1, "c4", List.of("b2", "b2"));
		mappingCollector.setValueBefore("b2", 0L);
		mappingCollector.setValueAfter("b2", 20L);

		Object2LongMap<String> values = valueGenerator.generateValues();
		Assertions.assertEquals(1, values.getLong("a1"));
		Assertions.assertEquals(20, values.getLong("b2"));
		Assertions.assertEquals(0, values.getLong("c4"));
	}

	@Test
	@DisplayName("Test generating and fixing simple values multiple recipes without inheriting")
	void testGenerateValuesSimpleFixedDoNotInheritMultiRecipes() {
		mappingCollector.setValueBefore("a1", 1L);
		mappingCollector.addConversion(1, "c", List.of("a1", "a1"));
		mappingCollector.addConversion(1, "c", List.of("a1", "b"));
		mappingCollector.setValueBefore("b", 0L);
		mappingCollector.setValueAfter("b", 20L);

		Object2LongMap<String> values = valueGenerator.generateValues();
		Assertions.assertEquals(1, values.getLong("a1"));
		Assertions.assertEquals(20, values.getLong("b"));
		Assertions.assertEquals(2, values.getLong("c"));
	}

	@Test
	@DisplayName("Test generating simple values with a given min value")
	void testGenerateValuesSimpleSelectMinValue() {
		mappingCollector.setValueBefore("a1", 1L);
		mappingCollector.setValueBefore("b2", 2L);
		mappingCollector.addConversion(1, "c", List.of("a1", "a1"));
		mappingCollector.addConversion(1, "c", List.of("b2", "b2"));

		Object2LongMap<String> values = valueGenerator.generateValues();
		Assertions.assertEquals(1, values.getLong("a1"));
		Assertions.assertEquals(2, values.getLong("b2"));
		Assertions.assertEquals(2, values.getLong("c"));
	}

	@Test
	@DisplayName("Test generating simple values with a given min value, with dependency")
	void testGenerateValuesSimpleSelectMinValueWithDependency() {
		mappingCollector.setValueBefore("a1", 1L);
		mappingCollector.setValueBefore("b2", 2L);
		mappingCollector.addConversion(1, "c", List.of("a1", "a1"));
		mappingCollector.addConversion(1, "c", List.of("b2", "b2"));
		mappingCollector.addConversion(1, "d", List.of("c", "c"));

		Object2LongMap<String> values = valueGenerator.generateValues();
		Assertions.assertEquals(1, values.getLong("a1"));
		Assertions.assertEquals(2, values.getLong("b2"));
		Assertions.assertEquals(2, values.getLong("c"));
		Assertions.assertEquals(4, values.getLong("d"));
	}

	@Test
	@DisplayName("Test generating EMC values for a crafting table")
	void testGenerateValuesSimpleWoodToWorkBench() {
		mappingCollector.setValueBefore("planks", 1L);
		mappingCollector.addConversion(4, "planks", List.of("wood"));
		mappingCollector.addConversion(1, "workbench", List.of("planks", "planks", "planks", "planks"));

		Object2LongMap<String> values = valueGenerator.generateValues();
		Assertions.assertEquals(0, values.getLong("wood"));
		Assertions.assertEquals(1, values.getLong("planks"));
		Assertions.assertEquals(4, values.getLong("workbench"));
	}

	@Test
	@DisplayName("Test generating EMC values for wood")
	void testGenerateValuesWood() {
		for (char i : "ABCD".toCharArray()) {
			mappingCollector.setValueBefore("wood" + i, 32L);
			mappingCollector.addConversion(4, "planks" + i, List.of("wood" + i));
		}

		for (char i : "ABCD".toCharArray()) {
			mappingCollector.addConversion(4, "planks" + i, List.of("wood"));
		}

		for (char i : "ABCD".toCharArray()) {
			for (char j : "ABCD".toCharArray()) {
				mappingCollector.addConversion(4, "stick", List.of("planks" + i, "planks" + j));
			}
		}
		mappingCollector.addConversion(1, "crafting_table", List.of("planksA", "planksA", "planksA", "planksA"));
		for (char i : "ABCD".toCharArray()) {
			for (char j : "ABCD".toCharArray()) {
				mappingCollector.addConversion(1, "wooden_hoe", List.of("stick", "stick", "planks" + i, "planks" + j));
			}
		}

		Object2LongMap<String> values = valueGenerator.generateValues();
		for (char i : "ABCD".toCharArray()) {
			Assertions.assertEquals(32, values.getLong("wood" + i));
		}
		for (char i : "ABCD".toCharArray()) {
			Assertions.assertEquals(8, values.getLong("planks" + i));
		}
		Assertions.assertEquals(4, values.getLong("stick"));
		Assertions.assertEquals(32, values.getLong("crafting_table"));
		Assertions.assertEquals(24, values.getLong("wooden_hoe"));

	}

	@Test
	@DisplayName("Test generating values with deep conversion")
	void testGenerateValuesDeepConversions() {
		mappingCollector.setValueBefore("a1", 1L);
		mappingCollector.addConversion(1, "b1", List.of("a1"));
		mappingCollector.addConversion(1, "c1", List.of("b1"));

		Object2LongMap<String> values = valueGenerator.generateValues();
		Assertions.assertEquals(1, values.getLong("a1"));
		Assertions.assertEquals(1, values.getLong("b1"));
		Assertions.assertEquals(1, values.getLong("c1"));
	}

	@Test
	@DisplayName("Test generating values with deep invalid conversion")
	void testGenerateValuesDeepInvalidConversion() {
		mappingCollector.setValueBefore("a1", 1L);
		mappingCollector.addConversion(1, "b", List.of("a1", "invalid1"));
		mappingCollector.addConversion(1, "invalid1", List.of("a1", "invalid2"));
		mappingCollector.addConversion(1, "invalid2", List.of("a1", "invalid3"));

		Object2LongMap<String> values = valueGenerator.generateValues();
		Assertions.assertEquals(1, values.getLong("a1"));
		Assertions.assertEquals(0, values.getLong("b"));
		Assertions.assertEquals(0, values.getLong("invalid1"));
		Assertions.assertEquals(0, values.getLong("invalid2"));
		Assertions.assertEquals(0, values.getLong("invalid3"));
	}

	@Test
	@DisplayName("Test generating values from multiple deep recipes with invalid conversion")
	void testGenerateValuesMultiRecipeDeepInvalid() {
		mappingCollector.setValueBefore("a1", 1L);
		mappingCollector.addConversion(1, "b2", List.of("a1", "a1"));
		mappingCollector.addConversion(1, "b2", List.of("invalid1"));
		mappingCollector.addConversion(1, "invalid1", List.of("a1", "invalid2"));

		Object2LongMap<String> values = valueGenerator.generateValues();
		Assertions.assertEquals(1, values.getLong("a1"));
		Assertions.assertEquals(2, values.getLong("b2"));
		Assertions.assertEquals(0, values.getLong("invalid1"));
		Assertions.assertEquals(0, values.getLong("invalid2"));
	}

	@Test
	@DisplayName("Test generating values from multiple deep recipes with invalid ingredient")
	void testGenerateValuesMultiRecipesInvalidIngredient() {
		mappingCollector.setValueBefore("a1", 1L);
		mappingCollector.addConversion(1, "b2", List.of("a1", "a1"));
		mappingCollector.addConversion(1, "b2", List.of("invalid"));

		Object2LongMap<String> values = valueGenerator.generateValues();
		Assertions.assertEquals(1, values.getLong("a1"));
		Assertions.assertEquals(2, values.getLong("b2"));
		Assertions.assertEquals(0, values.getLong("invalid"));
	}

	@Test
	@DisplayName("Test ingredient alternatives choose the cheapest valid value when the cheap option is added last")
	void testIngredientAlternativesChooseCheapestValidValueCheapLast() {
		assertCheapestAlternative(false);
	}

	@Test
	@DisplayName("Test ingredient alternatives choose the cheapest valid value when the cheap option is added first")
	void testIngredientAlternativesChooseCheapestValidValueCheapFirst() {
		assertCheapestAlternative(true);
	}

	private void assertCheapestAlternative(boolean cheapFirst) {
		mappingCollector.setValueBefore("cheap", 10L);
		mappingCollector.setValueBefore("expensive", 30L);
		mappingCollector.addConversion(1, "ingredient_group", List.of(cheapFirst ? "cheap" : "expensive"));
		mappingCollector.addConversion(1, "ingredient_group", List.of("invalid"));
		mappingCollector.addConversion(1, "ingredient_group", List.of(cheapFirst ? "expensive" : "cheap"));
		mappingCollector.addConversion(1, "output", List.of("ingredient_group"));

		Object2LongMap<String> values = valueGenerator.generateValues();
		Assertions.assertEquals(10, values.getLong("ingredient_group"));
		Assertions.assertEquals(10, values.getLong("output"));
	}

	@Test
	@DisplayName("Test generating values from recipes that have a cycle")
	void testGenerateValuesCycleRecipe() {
		mappingCollector.setValueBefore("a1", 1L);
		mappingCollector.addConversion(1, "cycle-1", List.of("a1"));
		mappingCollector.addConversion(1, "cycle-2", List.of("cycle-1"));
		mappingCollector.addConversion(1, "cycle-1", List.of("cycle-2"));

		Object2LongMap<String> values = valueGenerator.generateValues();
		Assertions.assertEquals(1, values.getLong("a1"));
		Assertions.assertEquals(1, values.getLong("cycle-1"));
		Assertions.assertEquals(1, values.getLong("cycle-2"));
	}

	@Test
	@DisplayName("Test generating values from recipes that have a large cycle")
	void testGenerateValuesBigCycleRecipe() {
		mappingCollector.setValueBefore("a1", 1L);
		mappingCollector.addConversion(1, "cycle-1", List.of("a1"));
		mappingCollector.addConversion(1, "cycle-2", List.of("cycle-1"));
		mappingCollector.addConversion(1, "cycle-3", List.of("cycle-2"));
		mappingCollector.addConversion(1, "cycle-4", List.of("cycle-3"));
		mappingCollector.addConversion(1, "cycle-5", List.of("cycle-4"));
		mappingCollector.addConversion(1, "cycle-1", List.of("cycle-5"));

		Object2LongMap<String> values = valueGenerator.generateValues();
		Assertions.assertEquals(1, values.getLong("a1"));
		Assertions.assertEquals(1, values.getLong("cycle-1"));
		Assertions.assertEquals(1, values.getLong("cycle-2"));
		Assertions.assertEquals(1, values.getLong("cycle-3"));
		Assertions.assertEquals(1, values.getLong("cycle-4"));
		Assertions.assertEquals(1, values.getLong("cycle-5"));
	}

	@Test
	@DisplayName("Test unanchored cycles do not create EMC")
	void testGenerateValuesUnanchoredCycle() {
		mappingCollector.addConversion(1, "cycle-1", List.of("cycle-2"));
		mappingCollector.addConversion(1, "cycle-2", List.of("cycle-1"));

		Object2LongMap<String> values = valueGenerator.generateValues();
		Assertions.assertEquals(0, values.getLong("cycle-1"));
		Assertions.assertEquals(0, values.getLong("cycle-2"));
	}

	@Test
	@DisplayName("Test anchored cycle results are independent of conversion registration order")
	void testGenerateValuesCycleRegistrationOrderIndependent() {
		mappingCollector.addConversion(1, "cycle-1", List.of("cycle-2"));
		mappingCollector.addConversion(1, "cycle-2", List.of("cycle-1"));
		mappingCollector.addConversion(1, "cycle-1", List.of("anchor"));
		mappingCollector.setValueBefore("anchor", 7L);

		Object2LongMap<String> values = valueGenerator.generateValues();
		Assertions.assertEquals(7, values.getLong("cycle-1"));
		Assertions.assertEquals(7, values.getLong("cycle-2"));
	}

	@Test
	@DisplayName("Test changing a fixed anchor recalculates the existing graph")
	void testGenerateValuesChangedAnchorDoesNotRetainStaleValues() {
		mappingCollector.setValueBefore("anchor", 5L);
		mappingCollector.addConversion(1, "derived", List.of("anchor", "anchor"));

		Object2LongMap<String> initialValues = valueGenerator.generateValues();
		Assertions.assertEquals(10, initialValues.getLong("derived"));

		mappingCollector.setValueBefore("anchor", 11L);
		Object2LongMap<String> recalculatedValues = valueGenerator.generateValues();
		Assertions.assertEquals(22, recalculatedValues.getLong("derived"));
	}

	@Test
	@DisplayName("Test forced conversions are cleared after their dependency chain becomes invalid")
	void testForcedConversionClearedAfterDependencyInvalidation() {
		mappingCollector.setValueBefore("root", 1L);
		mappingCollector.addConversion(1, "cycle", List.of("root"));
		mappingCollector.addConversion(2, "cycle", List.of("cycle"));

		//Amplify the final positive fraction before the exploit cycle is invalidated. Each regular output is
		//correctly cleared in turn; the forced output must be cleared as well rather than retain its last value.
		mappingCollector.addConversion(1, "amplified-1", EMCHelper.intMapOf("cycle", Integer.MAX_VALUE));
		mappingCollector.addConversion(1, "amplified-2", EMCHelper.intMapOf("amplified-1", Integer.MAX_VALUE));
		mappingCollector.addConversion(1, "amplified-3", EMCHelper.intMapOf("amplified-2", Integer.MAX_VALUE));
		mappingCollector.setValueFromConversion(1, "forced", List.of("amplified-3"));

		Object2LongMap<String> values = valueGenerator.generateValues();
		Assertions.assertEquals(0, values.getLong("cycle"));
		Assertions.assertEquals(0, values.getLong("amplified-1"));
		Assertions.assertEquals(0, values.getLong("amplified-2"));
		Assertions.assertEquals(0, values.getLong("amplified-3"));
		Assertions.assertEquals(0, values.getLong("forced"));
	}

	@Test
	@DisplayName("Test generating EMC values for fuels and matter")
	void testGenerateValuesFuelAndMatter() {
		final String coal = "coal";
		final String aCoal = "alchemicalCoal";
		final String aCoalBlock = "alchemicalCoalBlock";
		final String mFuel = "mobiusFuel";
		final String mFuelBlock = "mobiusFuelBlock";
		final String aFuel = "aeternalisFuel";
		final String aFuelBlock = "aeternalisFuelBlock";
		String repeat;

		mappingCollector.setValueBefore(coal, 128L);

		mappingCollector.addConversion(1, aCoal, List.of(coal, coal, coal, coal));
		mappingCollector.addConversion(4, aCoal, List.of(mFuel));
		mappingCollector.addConversion(9, aCoal, List.of(aCoalBlock));
		repeat = aCoal;
		mappingCollector.addConversion(1, aCoalBlock, List.of(repeat, repeat, repeat, repeat, repeat, repeat, repeat, repeat, repeat));

		mappingCollector.addConversion(1, mFuel, List.of(aCoal, aCoal, aCoal, aCoal));
		mappingCollector.addConversion(4, mFuel, List.of(aFuel));
		mappingCollector.addConversion(9, mFuel, List.of(mFuelBlock));
		repeat = mFuel;
		mappingCollector.addConversion(1, mFuelBlock, List.of(repeat, repeat, repeat, repeat, repeat, repeat, repeat, repeat, repeat));

		mappingCollector.addConversion(1, aFuel, List.of(mFuel, mFuel, mFuel, mFuel));
		mappingCollector.addConversion(9, aFuel, List.of(aFuelBlock));
		repeat = aFuel;
		mappingCollector.addConversion(1, aFuelBlock, List.of(repeat, repeat, repeat, repeat, repeat, repeat, repeat, repeat, repeat));

		mappingCollector.setValueBefore("diamondBlock", 73728L);
		final String dMatter = "darkMatter";
		final String dMatterBlock = "darkMatterBlock";

		mappingCollector.addConversion(1, dMatter, List.of(aFuel, aFuel, aFuel, aFuel, aFuel, aFuel, aFuel, aFuel, "diamondBlock"));
		mappingCollector.addConversion(1, dMatter, List.of(dMatterBlock));
		mappingCollector.addConversion(4, dMatterBlock, List.of(dMatter, dMatter, dMatter, dMatter));

		final String rMatter = "redMatter";
		final String rMatterBlock = "redMatterBlock";
		mappingCollector.addConversion(1, rMatter, List.of(aFuel, aFuel, aFuel, dMatter, dMatter, dMatter, aFuel, aFuel, aFuel));
		mappingCollector.addConversion(1, rMatter, List.of(rMatterBlock));
		mappingCollector.addConversion(4, rMatterBlock, List.of(rMatter, rMatter, rMatter, rMatter));

		Object2LongMap<String> values = valueGenerator.generateValues();
		Assertions.assertEquals(128, values.getLong(coal));
		Assertions.assertEquals(512, values.getLong(aCoal));
		Assertions.assertEquals(4608, values.getLong(aCoalBlock));
		Assertions.assertEquals(2048, values.getLong(mFuel));
		Assertions.assertEquals(18432, values.getLong(mFuelBlock));
		Assertions.assertEquals(8192, values.getLong(aFuel));
		Assertions.assertEquals(73728, values.getLong(aFuelBlock));
		Assertions.assertEquals(73728, values.getLong("diamondBlock"));
		Assertions.assertEquals(139264, values.getLong(dMatter));
		Assertions.assertEquals(139264, values.getLong(dMatterBlock));
		Assertions.assertEquals(466944, values.getLong(rMatter));
		Assertions.assertEquals(466944, values.getLong(rMatterBlock));
	}

	@Test
	@DisplayName("Test generating EMC values for things made from wools")
	void testGenerateValuesWool() {
		final String[] dyes = new String[]{"Blue", "Brown", "White", "Other"};
		final int[] dyeValue = new int[]{864, 176, 48, 16};
		for (int i = 0; i < dyes.length; i++) {
			mappingCollector.setValueBefore("dye" + dyes[i], (long) dyeValue[i]);
			mappingCollector.addConversion(1, "wool" + dyes[i], List.of("woolWhite", "dye" + dyes[i]));
		}
		mappingCollector.setValueBefore("string", 12L);
		mappingCollector.addConversion(1, "woolWhite", List.of("string", "string", "string", "string"));

		mappingCollector.setValueBefore("stick", 4L);
		mappingCollector.setValueBefore("plank", 8L);
		for (String dye : dyes) {
			mappingCollector.addConversion(1, "bed", List.of("plank", "plank", "plank", "wool" + dye, "wool" + dye, "wool" + dye));
			mappingCollector.addConversion(3, "carpet" + dye, List.of("wool" + dye, "wool" + dye));
			mappingCollector.addConversion(1, "painting", List.of("wool" + dye, "stick", "stick", "stick", "stick", "stick", "stick", "stick", "stick"));
		}

		Object2LongMap<String> values = valueGenerator.generateValues();
		for (int i = 0; i < dyes.length; i++) {
			Assertions.assertEquals(dyeValue[i], values.getLong("dye" + dyes[i]));
		}
		Assertions.assertEquals(12, values.getLong("string"));
		Assertions.assertEquals(48, values.getLong("woolWhite"));
		Assertions.assertEquals(224, values.getLong("woolBrown"));
		Assertions.assertEquals(912, values.getLong("woolBlue"));
		Assertions.assertEquals(64, values.getLong("woolOther"));

		Assertions.assertEquals(32, values.getLong("carpetWhite"));
		Assertions.assertEquals(149, values.getLong("carpetBrown"));
		Assertions.assertEquals(608, values.getLong("carpetBlue"));
		Assertions.assertEquals(42, values.getLong("carpetOther"));

		Assertions.assertEquals(168, values.getLong("bed"));
		Assertions.assertEquals(80, values.getLong("painting"));
	}

	@Test
	@DisplayName("Test generating values for arbitrary bucket")
	void testGenerateValuesBucketRecipe() {
		mappingCollector.setValueBefore("somethingElse", 9L);
		mappingCollector.setValueBefore("container", 23L);
		mappingCollector.setValueBefore("fluid", 17L);
		mappingCollector.addConversion(1, "filledContainer", List.of("container", "fluid"));

		//Recipe that only consumes fluid:
		mappingCollector.addConversion(1, "fluidCraft", EMCHelper.intMapOf(
				"container", -1,
				"filledContainer", 1,
				"somethingElse", 2
		));

		Object2LongMap<String> values = valueGenerator.generateValues();
		Assertions.assertEquals(9, values.getLong("somethingElse"));
		Assertions.assertEquals(23, values.getLong("container"));
		Assertions.assertEquals(17, values.getLong("fluid"));
		Assertions.assertEquals(17 + 23, values.getLong("filledContainer"));
		Assertions.assertEquals(17 + 2 * 9, values.getLong("fluidCraft"));

	}

	@Test
	@DisplayName("Test generating EMC value for water bucket")
	void testGenerateValuesWaterBucketRecipe() {
		mappingCollector.setValueBefore("somethingElse", 9L);
		mappingCollector.setValueBefore("container", 23L);
		mappingCollector.setValueBefore("fluid", ProjectEAPI.FREE_ARITHMETIC_VALUE);
		mappingCollector.addConversion(1, "filledContainer", List.of("container", "fluid"));

		//Recipe that only consumes fluid:
		mappingCollector.addConversion(1, "fluidCraft", EMCHelper.intMapOf(
				"container", -1,
				"filledContainer", 1,
				"somethingElse", 2
		));

		Object2LongMap<String> values = valueGenerator.generateValues();
		Assertions.assertEquals(9, values.getLong("somethingElse"));
		Assertions.assertEquals(23, values.getLong("container"));
		Assertions.assertEquals(0, values.getLong("fluid"));
		Assertions.assertEquals(23, values.getLong("filledContainer"));
		Assertions.assertEquals(2 * 9, values.getLong("fluidCraft"));

	}

	@Test
	@DisplayName("Test generating EMC from cyclic recipes with exploits")
	void testGenerateValuesCycleRecipeExploit() {
		mappingCollector.setValueBefore("a1", 1L);
		//Exploitable Cycle Recipe
		mappingCollector.addConversion(1, "exploitable", List.of("a1"));
		mappingCollector.addConversion(2, "exploitable", List.of("exploitable"));

		//Not-exploitable Cycle Recipe
		mappingCollector.addConversion(1, "notExploitable", List.of("a1"));
		mappingCollector.addConversion(2, "notExploitable", List.of("notExploitable", "notExploitable"));

		Object2LongMap<String> values = valueGenerator.generateValues();
		Assertions.assertEquals(1, values.getLong("a1"));
		Assertions.assertEquals(0, values.getLong("exploitable"));
		Assertions.assertEquals(1, values.getLong("notExploitable"));
	}

	@Test
	@DisplayName("Test generating EMC from cyclic recipes with delayed exploit")
	void testGenerateValuesDelayedCycleRecipeExploit() {
		mappingCollector.setValueBefore("a1", 1L);
		//Exploitable Cycle Recipe
		mappingCollector.addConversion(1, "exploitable1", List.of("a1"));
		mappingCollector.addConversion(2, "exploitable2", List.of("exploitable1"));
		mappingCollector.addConversion(1, "exploitable1", List.of("exploitable2"));

		Object2LongMap<String> values = valueGenerator.generateValues();
		Assertions.assertEquals(1, values.getLong("a1"));
		Assertions.assertEquals(0, values.getLong("exploitable1"));
		Assertions.assertEquals(0, values.getLong("exploitable2"));
	}

	@Test
	@DisplayName("Test generating EMC from cyclic recipes with exploits 2")
	void testGenerateValuesCycleRecipeExploit2() {
		mappingCollector.setValueBefore("a1", 1L);
		//Exploitable Cycle Recipe
		mappingCollector.addConversion(1, "exploitable", List.of("a1"));
		mappingCollector.addConversion(2, "exploitable", List.of("exploitable"));
		mappingCollector.addConversion(1, "b", List.of("exploitable"));

		//Not-exploitable Cycle Recipe
		mappingCollector.addConversion(1, "notExploitable", List.of("a1"));
		mappingCollector.addConversion(2, "notExploitable", List.of("notExploitable", "notExploitable"));

		Object2LongMap<String> values = valueGenerator.generateValues();
		Assertions.assertEquals(1, values.getLong("a1"));
		Assertions.assertEquals(0, values.getLong("exploitable"));
		Assertions.assertEquals(1, values.getLong("notExploitable"));
		Assertions.assertEquals(0, values.getLong("b"));
	}

	@Test
	@DisplayName("Test generating EMC for coal to fire charge with wildcard")
	void testGenerateValuesCoalToFireChargeWithWildcard() {
		String[] logTypes = new String[]{"logA", "logB", "logC"};
		String[] log2Types = new String[]{"log2A", "log2B", "log2C"};
		String[] coalTypes = new String[]{"coal0", "coal1"};

		mappingCollector.setValueBefore("coalore", 0L);
		mappingCollector.setValueBefore("coal0", 128L);
		mappingCollector.setValueBefore("gunpowder", 192L);
		mappingCollector.setValueBefore("blazepowder", 768L);

		for (String logType : logTypes) {
			mappingCollector.setValueBefore(logType, 32L);
			mappingCollector.addConversion(1, "log*", List.of(logType));
		}
		for (String log2Type : log2Types) {
			mappingCollector.setValueBefore(log2Type, 32L);
			mappingCollector.addConversion(1, "log2*", List.of(log2Type));
		}
		mappingCollector.addConversion(1, "coal1", List.of("log*"));
		for (String coalType : coalTypes) {
			mappingCollector.addConversion(1, "coal*", List.of(coalType));
			mappingCollector.addConversion(3, "firecharge", List.of(coalType, "gunpowder", "blazepowder"));
		}
		mappingCollector.addConversion(1, "firecharge*", List.of("firecharge"));
		mappingCollector.addConversion(1, "coalblock", EMCHelper.intMapOf("coal0", 9));

		//Philosophers stone smelting 7xCoalOre -> 7xCoal
		mappingCollector.addConversion(7, "coal0", EMCHelper.intMapOf(
				"coalore", 7,
				"coal*", 1
		));

		//Philosophers stone smelting logs
		mappingCollector.addConversion(7, "coal1", EMCHelper.intMapOf(
				"log*", 7,
				"coal*", 1
		));

		//Philosophers stone smelting log2s
		mappingCollector.addConversion(7, "coal1", EMCHelper.intMapOf(
				"log2*", 7,
				"coal*", 1
		));

		//Smelting single coal ore
		mappingCollector.addConversion(1, "coal0", List.of("coalore"));
		//Coal Block
		mappingCollector.addConversion(9, "coal0", List.of("coalblock"));

		Object2LongMap<String> values = valueGenerator.generateValues();
		for (String logType : logTypes) {
			Assertions.assertEquals(32, values.getLong(logType));
		}
		Assertions.assertEquals(32, values.getLong("log*"));
		Assertions.assertEquals(128, values.getLong("coal0"));
		Assertions.assertEquals(32, values.getLong("coal1"));
		Assertions.assertEquals(32, values.getLong("coal*"));
		Assertions.assertEquals(330, values.getLong("firecharge"));
	}

	@Test
	@DisplayName("Test generating EMC values for chisel's Anti Block recipe")
	void testGenerateValuesChisel2AntiBlock() {
		final String gDust = "glowstone dust";
		final String stone = "stone";

		final String[] dyes = new String[]{"Blue", "Brown", "White", "Other"};
		final int[] dyeValue = new int[]{864, 176, 48, 16};
		for (int i = 0; i < dyes.length; i++) {
			mappingCollector.setValueBefore("dye" + dyes[i], (long) dyeValue[i]);
			mappingCollector.addConversion(8, "antiblock" + dyes[i], List.of(
					"antiblock_all", "antiblock_all", "antiblock_all",
					"antiblock_all", "dye" + dyes[i], "antiblock_all",
					"antiblock_all", "antiblock_all", "antiblock_all"
			));
			mappingCollector.addConversion(1, "antiblock_all", List.of("antiblock" + dyes[i]));
		}

		mappingCollector.setValueBefore(gDust, 384L);
		mappingCollector.setValueBefore(stone, 1L);
		mappingCollector.addConversion(8, "antiblockWhite", List.of(
				stone, stone, stone,
				stone, gDust, stone,
				stone, stone, stone));

		Object2LongMap<String> values = valueGenerator.generateValues();
		Assertions.assertEquals((8 + 384) / 8, values.getLong("antiblockWhite"));
		for (int i = 0; i < dyes.length; i++) {
			Assertions.assertEquals(dyeValue[i], values.getLong("dye" + dyes[i]));
			if (!dyes[i].equals("White")) {
				Assertions.assertEquals((dyeValue[i] + ((8 + 384) / 8) * 8) / 8, values.getLong("antiblock" + dyes[i]));
			}
		}
	}

	@Test
	@DisplayName("Test generating values from a zero count ingredient")
	void testGenerateValuesZeroCountIngredientDependency() {
		mappingCollector.setValueBefore("a", 2L);
		mappingCollector.setValueBefore("b", 3L);
		mappingCollector.setValueBefore("notConsume1", 1L);
		mappingCollector.addConversion(1, "c1", EMCHelper.intMapOf(
				"a", 1,
				"b", 1,
				"notConsume1", 0
		));
		mappingCollector.addConversion(1, "c2", EMCHelper.intMapOf(
				"a", 1,
				"b", 1,
				"notConsume2", 0
		));

		Object2LongMap<String> values = valueGenerator.generateValues();
		Assertions.assertEquals(2, values.getLong("a"));
		Assertions.assertEquals(3, values.getLong("b"));
		Assertions.assertEquals(1, values.getLong("notConsume1"));
		Assertions.assertEquals(0, values.getLong("notConsume2"));
		Assertions.assertEquals(5, values.getLong("c1"));
		//Even though notConsume2 has no EMC value, it is not actually consumed so need to be
		// taken into account when calculating the EMC value of c2
		Assertions.assertEquals(5, values.getLong("c2"));
	}


	@Test
	@DisplayName("Test generating values with a free alternative recipe")
	void testGenerateValuesFreeAlternatives() {
		mappingCollector.setValueBefore("freeWater", ProjectEAPI.FREE_ARITHMETIC_VALUE);
		mappingCollector.setValueBefore("waterBottle", 0L);
		mappingCollector.addConversion(1, "waterGroup", List.of("freeWater"));
		mappingCollector.addConversion(1, "waterGroup", List.of("waterBottle"));
		mappingCollector.setValueBefore("a", 3L);
		mappingCollector.addConversion(1, "result", List.of("a", "waterGroup"));

		Object2LongMap<String> values = valueGenerator.generateValues();
		Assertions.assertEquals(3, values.getLong("a"));
		Assertions.assertEquals(0, values.getLong("freeWater"));
		Assertions.assertEquals(0, values.getLong("waterBottle"));
		Assertions.assertEquals(0, values.getLong("waterGroup"));
		Assertions.assertEquals(3, values.getLong("result"));
	}

	@Test
	@DisplayName("Test generating values with a free alternative recipe, when we have a negative ingredient")
	void testGenerateValuesFreeAlternativesWithNegativeIngredients() {
		mappingCollector.setValueBefore("bucket", 768L);
		mappingCollector.setValueBefore("waterBucket", 768L);
		mappingCollector.setValueBefore("waterBottle", 0L);
		mappingCollector.addConversion(1, "waterGroup", EMCHelper.intMapOf(
				"waterBucket", 1,
				"bucket", -1
		));
		mappingCollector.addConversion(1, "waterGroup", List.of("waterBottle"));
		mappingCollector.setValueBefore("a", 3L);
		mappingCollector.addConversion(1, "result", List.of("a", "waterGroup"));

		Object2LongMap<String> values = valueGenerator.generateValues();
		Assertions.assertEquals(3, values.getLong("a"));
		Assertions.assertEquals(768, values.getLong("bucket"));
		Assertions.assertEquals(768, values.getLong("waterBucket"));
		Assertions.assertEquals(0, values.getLong("waterGroup"));
		Assertions.assertEquals(3, values.getLong("result"));
	}


	@Test
	@DisplayName("Test overflow from recipe")
	void testOverflowWithIngredients() {
		mappingCollector.setValueBefore("a", Long.MAX_VALUE / 2 + 1);
		mappingCollector.setValueBefore("b", Long.MAX_VALUE / 2 + 1);
		mappingCollector.addConversion(1, "c", List.of("a", "b"));

		Object2LongMap<String> values = valueGenerator.generateValues();
		Assertions.assertEquals(Long.MAX_VALUE / 2 + 1, values.getLong("a"));
		Assertions.assertEquals(Long.MAX_VALUE / 2 + 1, values.getLong("b"));
		Assertions.assertEquals(0, values.getLong("c"));
	}

	@Test
	@DisplayName("Test overflow with an already set in range amount")
	void testOverflowWithAmount() {
		mappingCollector.setValueBefore("a", Long.MAX_VALUE / 2);
		mappingCollector.addConversion(3, "a", List.of("something"));

		Object2LongMap<String> values = valueGenerator.generateValues();
		Assertions.assertEquals(Long.MAX_VALUE / 2, values.getLong("a"));
	}

	@Test
	@DisplayName("Test extreme overflow")
	void testLargeOverflow() {
		mappingCollector.setValueBefore("a", Long.MAX_VALUE);
		mappingCollector.addConversion(1, "b", List.of("a", "a", "a", "a", "a", "a", "a", "a", "a"));

		//TODO
		// 9_223_372_036_854_775_807
		// 9_223_372_036_854_775_807
		// 9_223_372_036_854_775_807
		// 9_223_372_036_854_775_807
		// 9_223_372_036_854_775_807
		// 9_223_372_036_854_775_807
		// 9_223_372_036_854_775_807
		// 9_223_372_036_854_775_807
		// 9_223_372_036_854_775_807

		Object2LongMap<String> values = valueGenerator.generateValues();
		Assertions.assertEquals(0, values.getLong("b"));
	}

	@Test
	@DisplayName("Test simple overflow hidden by result being within bounds")
	void testHiddenOverflow() {
		mappingCollector.setValueBefore("a", Long.MAX_VALUE);
		mappingCollector.addConversion(2, "b", List.of("a", "a"));
		mappingCollector.addConversion(5, "c", List.of("a", "a", "a", "a", "a"));

		Object2LongMap<String> values = valueGenerator.generateValues();
		Assertions.assertEquals(Long.MAX_VALUE, values.getLong("b"));
		Assertions.assertEquals(Long.MAX_VALUE, values.getLong("c"));
	}

	@Test
	@DisplayName("Test overwriting conversion with smaller value")
	void testOverwriteConversions() {
		mappingCollector.setValueBefore("a", 1L);
		mappingCollector.setValueFromConversion(1, "b", List.of("a", "a", "a"));
		mappingCollector.addConversion(1, "b", List.of("a"));
		mappingCollector.addConversion(1, "c", List.of("b", "b"));
		Object2LongMap<String> values = valueGenerator.generateValues();
		Assertions.assertEquals(1, values.getLong("a"));
		Assertions.assertEquals(3, values.getLong("b"));
		Assertions.assertEquals(6, values.getLong("c"));

	}

	@Test
	@DisplayName("Test equivalent forced conversion remains active when a normal conversion is added first")
	void testEquivalentForcedConversionNormalFirst() {
		assertEquivalentForcedConversion(true);
	}

	@Test
	@DisplayName("Test equivalent forced conversion remains active when a normal conversion is added second")
	void testEquivalentForcedConversionNormalSecond() {
		assertEquivalentForcedConversion(false);
	}

	private void assertEquivalentForcedConversion(boolean normalFirst) {
		mappingCollector.setValueBefore("ingredient", 10L);
		if (normalFirst) {
			mappingCollector.addConversion(1, "output", List.of("ingredient"));
			mappingCollector.setValueFromConversion(1, "output", List.of("ingredient"));
		} else {
			mappingCollector.setValueFromConversion(1, "output", List.of("ingredient"));
			mappingCollector.addConversion(1, "output", List.of("ingredient"));
		}
		mappingCollector.addConversion(1, "downstream", List.of("output", "output"));

		Object2LongMap<String> values = valueGenerator.generateValues();
		Assertions.assertEquals(10, values.getLong("output"));
		Assertions.assertEquals(20, values.getLong("downstream"));
	}

	@Test
	@DisplayName("Test replacing a forced conversion removes the old dependency")
	void testReplacingForcedConversionRemovesOldDependency() {
		mappingCollector.setValueBefore("old", 10L);
		mappingCollector.setValueBefore("replacement", 30L);
		mappingCollector.setValueFromConversion(1, "output", List.of("old"));
		mappingCollector.setValueFromConversion(1, "output", List.of("replacement"));
		mappingCollector.addConversion(1, "downstream", List.of("output", "output"));

		Object2LongMap<String> values = valueGenerator.generateValues();
		Assertions.assertEquals(30, values.getLong("output"));
		Assertions.assertEquals(60, values.getLong("downstream"));
	}

	@Test
	@DisplayName("Test replacing a forced conversion preserves shared dependencies")
	void testReplacingForcedConversionPreservesSharedDependencies() {
		mappingCollector.setValueBefore("shared", 4L);
		mappingCollector.setValueBefore("old", 10L);
		mappingCollector.setValueBefore("replacement", 30L);
		mappingCollector.setValueFromConversion(1, "output", EMCHelper.intMapOf("shared", 1, "old", 1));
		mappingCollector.setValueFromConversion(1, "output", EMCHelper.intMapOf("shared", 1, "replacement", 1));

		Object2LongMap<String> values = valueGenerator.generateValues();
		Assertions.assertEquals(34, values.getLong("output"));
	}

	@Test
	@DisplayName("Test invalid negative fixed values before inheritance cannot subsidize conversions")
	void testInvalidNegativeFixedValueBeforeIsIgnored() {
		mappingCollector.setValueBefore("invalid", -5L);
		mappingCollector.setValueBefore("valid", 10L);
		mappingCollector.addConversion(1, "output", List.of("invalid", "valid"));

		Object2LongMap<String> values = valueGenerator.generateValues();
		Assertions.assertFalse(values.containsKey("invalid"));
		Assertions.assertEquals(10, values.getLong("valid"));
		Assertions.assertFalse(values.containsKey("output"));
	}

	@Test
	@DisplayName("Test invalid negative fixed values after inheritance are ignored")
	void testInvalidNegativeFixedValueAfterIsIgnored() {
		mappingCollector.setValueAfter("invalid", -5L);

		Object2LongMap<String> values = valueGenerator.generateValues();
		Assertions.assertFalse(values.containsKey("invalid"));
	}

	@Test
	@DisplayName("Test conversion ingredient maps are defensively copied")
	void testConversionIngredientMapIsDefensivelyCopied() {
		mappingCollector.setValueBefore("ingredient", 10L);
		Object2IntOpenHashMap<String> ingredients = new Object2IntOpenHashMap<>();
		ingredients.put("ingredient", 1);
		mappingCollector.addConversion(1, "output", ingredients);
		ingredients.clear();
		ingredients.put("replacement", 1);

		Object2LongMap<String> values = valueGenerator.generateValues();
		Assertions.assertEquals(10, values.getLong("output"));
	}

	@Test
	@DisplayName("Test invalid conversion output counts are rejected")
	void testInvalidConversionOutputCountsAreRejected() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> mappingCollector.addConversion(0, "output", List.of("ingredient")));
		Assertions.assertThrows(IllegalArgumentException.class, () -> mappingCollector.addConversion(-1, "output", List.of("ingredient")));
		Assertions.assertThrows(IllegalArgumentException.class, () -> mappingCollector.setValueFromConversion(0, "output", List.of("ingredient")));
		Assertions.assertThrows(IllegalArgumentException.class, () -> mappingCollector.setValueFromConversion(-1, "output", List.of("ingredient")));
	}

	@Test
	@DisplayName("Test zero-amount ingredients are ignored")
	void testZeroAmountIngredientsAreIgnored() {
		mappingCollector.setValueBefore("ingredient", 10L);
		mappingCollector.addConversion(1, "normal", EMCHelper.intMapOf("ingredient", 1, "ignored", 0));
		mappingCollector.setValueFromConversion(1, "forced", EMCHelper.intMapOf("ingredient", 1, "ignored", 0));

		Object2LongMap<String> values = valueGenerator.generateValues();
		Assertions.assertEquals(10, values.getLong("normal"));
		Assertions.assertEquals(10, values.getLong("forced"));
	}

}
