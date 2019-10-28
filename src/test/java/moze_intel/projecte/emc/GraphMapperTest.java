package moze_intel.projecte.emc;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import moze_intel.projecte.api.mapper.arithmetic.IValueArithmetic;
import moze_intel.projecte.api.mapper.collector.IExtendedMappingCollector;
import moze_intel.projecte.api.mapper.generator.IValueGenerator;
import moze_intel.projecte.emc.arithmetic.HiddenBigFractionArithmetic;
import moze_intel.projecte.emc.collector.LongToBigFractionCollector;
import moze_intel.projecte.emc.generator.BigFractionToLongGenerator;
import moze_intel.projecte.utils.Constants;
import org.apache.commons.math3.fraction.BigFraction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

//@RunWith(value = Parameterized.class)
@Timeout(3000)
@DisplayName("Test graph mapper")
class GraphMapperTest {

	/*@Parameterized.Parameters
	public static Collection  parameters() {
		Object[][] data = new Object[][] { { new ComplexGraphMapper<String, Integer>(new IntArithmetic())  }};
		return Arrays.asList(data);
	}

	public GraphMapperTest(GraphMapper<String, Integer> mappingCollector) {
		this.mappingCollector = mappingCollector;
	}*/

	@BeforeEach
	void setup() {
		//mappingCollector = new SimpleGraphMapper<String, Integer>(new IntArithmetic());
		SimpleGraphMapper<String, BigFraction, IValueArithmetic<BigFraction>> mapper = new SimpleGraphMapper<>(new HiddenBigFractionArithmetic());
		valueGenerator = new BigFractionToLongGenerator<>(mapper);
		mappingCollector = new LongToBigFractionCollector<>(mapper);
	}

	private IValueGenerator<String, Long> valueGenerator;
	private IExtendedMappingCollector<String, Long, IValueArithmetic<BigFraction>> mappingCollector;

	@Test
	@DisplayName("Test generating simple values")
	void testGenerateValuesSimple() {
		mappingCollector.setValueBefore("a1", 1L);
		mappingCollector.addConversion(1, "c4", Arrays.asList("a1", "a1", "a1", "a1"));
		mappingCollector.addConversion(1, "b2", Arrays.asList("a1", "a1"));

		Map<String, Long> values = valueGenerator.generateValues();
		Assertions.assertEquals(1, getValue(values, "a1"));
		Assertions.assertEquals(2, getValue(values, "b2"));
		Assertions.assertEquals(4, getValue(values, "c4"));

	}

	@Test
	@DisplayName("Test generating simple values from multiple recipes")
	void testGenerateValuesSimpleMultiRecipe() {
		mappingCollector.setValueBefore("a1", 1L);
		//2 Recipes for c4
		mappingCollector.addConversion(1, "c4", Arrays.asList("a1", "a1", "a1", "a1"));
		mappingCollector.addConversion(2, "c4", Arrays.asList("b2", "b2"));
		mappingCollector.addConversion(1, "b2", Arrays.asList("a1", "a1"));

		Map<String, Long> values = valueGenerator.generateValues();
		Assertions.assertEquals(1, getValue(values, "a1"));
		Assertions.assertEquals(2, getValue(values, "b2"));
		Assertions.assertEquals(2, getValue(values, "c4")); //2 * c4 = 2 * b2 => 2 * (2) = 2 * (2)
	}

	@Test
	@DisplayName("Test generating simple values from multiple recipes, with an empty alternate")
	void testGenerateValuesSimpleMultiRecipeWithEmptyAlternative() {
		mappingCollector.setValueBefore("a1", 1L);
		//2 Recipes for c4
		mappingCollector.addConversion(1, "c4", Arrays.asList("a1", "a1", "a1", "a1"));
		mappingCollector.addConversion(1, "c4", new LinkedList<>());
		mappingCollector.addConversion(1, "b2", Arrays.asList("a1", "a1"));

		Map<String, Long> values = valueGenerator.generateValues();
		Assertions.assertEquals(1, getValue(values, "a1"));
		Assertions.assertEquals(2, getValue(values, "b2"));
		Assertions.assertEquals(4, getValue(values, "c4")); //2 * c4 = 2 * b2 => 2 * (2) = 2 * (2)
	}

	@Test
	@DisplayName("Test generating and fixing simple values after inheriting")
	void testGenerateValuesSimpleFixedAfterInherit() {
		mappingCollector.setValueBefore("a1", 1L);
		mappingCollector.addConversion(1, "c4", Arrays.asList("a1", "a1", "a1", "a1"));
		mappingCollector.addConversion(1, "b2", Arrays.asList("a1", "a1"));
		mappingCollector.setValueAfter("b2", 20L);

		Map<String, Long> values = valueGenerator.generateValues();
		Assertions.assertEquals(1, getValue(values, "a1"));
		Assertions.assertEquals(20, getValue(values, "b2"));
		Assertions.assertEquals(4, getValue(values, "c4"));
	}

	@Test
	@DisplayName("Test generating and fixing simple values without inheriting")
	void testGenerateValuesSimpleFixedDoNotInherit() {
		mappingCollector.setValueBefore("a1", 1L);
		mappingCollector.addConversion(1, "b2", Arrays.asList("a1", "a1"));
		mappingCollector.addConversion(1, "c4", Arrays.asList("b2", "b2"));
		mappingCollector.setValueBefore("b2", 0L);
		mappingCollector.setValueAfter("b2", 20L);

		Map<String, Long> values = valueGenerator.generateValues();
		Assertions.assertEquals(1, getValue(values, "a1"));
		Assertions.assertEquals(20, getValue(values, "b2"));
		Assertions.assertEquals(0, getValue(values, "c4"));
	}

	@Test
	@DisplayName("Test generating and fixing simple values multiple recipes without inheriting")
	void testGenerateValuesSimpleFixedDoNotInheritMultiRecipes() {
		mappingCollector.setValueBefore("a1", 1L);
		mappingCollector.addConversion(1, "c", Arrays.asList("a1", "a1"));
		mappingCollector.addConversion(1, "c", Arrays.asList("a1", "b"));
		mappingCollector.setValueBefore("b", 0L);
		mappingCollector.setValueAfter("b", 20L);

		Map<String, Long> values = valueGenerator.generateValues();
		Assertions.assertEquals(1, getValue(values, "a1"));
		Assertions.assertEquals(20, getValue(values, "b"));
		Assertions.assertEquals(2, getValue(values, "c"));
	}

	@Test
	@DisplayName("Test generating simple values with a given min value")
	void testGenerateValuesSimpleSelectMinValue() {
		mappingCollector.setValueBefore("a1", 1L);
		mappingCollector.setValueBefore("b2", 2L);
		mappingCollector.addConversion(1, "c", Arrays.asList("a1", "a1"));
		mappingCollector.addConversion(1, "c", Arrays.asList("b2", "b2"));

		Map<String, Long> values = valueGenerator.generateValues();
		Assertions.assertEquals(1, getValue(values, "a1"));
		Assertions.assertEquals(2, getValue(values, "b2"));
		Assertions.assertEquals(2, getValue(values, "c"));
	}

	@Test
	@DisplayName("Test generating simple values with a given min value, with dependency")
	void testGenerateValuesSimpleSelectMinValueWithDependency() {
		mappingCollector.setValueBefore("a1", 1L);
		mappingCollector.setValueBefore("b2", 2L);
		mappingCollector.addConversion(1, "c", Arrays.asList("a1", "a1"));
		mappingCollector.addConversion(1, "c", Arrays.asList("b2", "b2"));
		mappingCollector.addConversion(1, "d", Arrays.asList("c", "c"));

		Map<String, Long> values = valueGenerator.generateValues();
		Assertions.assertEquals(1, getValue(values, "a1"));
		Assertions.assertEquals(2, getValue(values, "b2"));
		Assertions.assertEquals(2, getValue(values, "c"));
		Assertions.assertEquals(4, getValue(values, "d"));
	}

	@Test
	@DisplayName("Test generating EMC values for a crafting table")
	void testGenerateValuesSimpleWoodToWorkBench() {
		mappingCollector.setValueBefore("planks", 1L);
		mappingCollector.addConversion(4, "planks", Collections.singletonList("wood"));
		mappingCollector.addConversion(1, "workbench", Arrays.asList("planks", "planks", "planks", "planks"));

		Map<String, Long> values = valueGenerator.generateValues();
		Assertions.assertEquals(0, getValue(values, "wood"));
		Assertions.assertEquals(1, getValue(values, "planks"));
		Assertions.assertEquals(4, getValue(values, "workbench"));
	}

	@Test
	@DisplayName("Test generating EMC values for wood")
	void testGenerateValuesWood() {
		for (char i : "ABCD".toCharArray()) {
			mappingCollector.setValueBefore("wood" + i, 32L);
			mappingCollector.addConversion(4, "planks" + i, Collections.singletonList("wood" + i));
		}

		for (char i : "ABCD".toCharArray()) {
			mappingCollector.addConversion(4, "planks" + i, Collections.singletonList("wood"));
		}

		for (char i : "ABCD".toCharArray()) {
			for (char j : "ABCD".toCharArray()) {
				mappingCollector.addConversion(4, "stick", Arrays.asList("planks" + i, "planks" + j));
			}
		}
		mappingCollector.addConversion(1, "crafting_table", Arrays.asList("planksA", "planksA", "planksA", "planksA"));
		for (char i : "ABCD".toCharArray()) {
			for (char j : "ABCD".toCharArray()) {
				mappingCollector.addConversion(1, "wooden_hoe", Arrays.asList("stick", "stick", "planks" + i, "planks" + j));
			}
		}

		Map<String, Long> values = valueGenerator.generateValues();
		for (char i : "ABCD".toCharArray()) {
			Assertions.assertEquals(32, getValue(values, "wood" + i));
		}
		for (char i : "ABCD".toCharArray()) {
			Assertions.assertEquals(8, getValue(values, "planks" + i));
		}
		Assertions.assertEquals(4, getValue(values, "stick"));
		Assertions.assertEquals(32, getValue(values, "crafting_table"));
		Assertions.assertEquals(24, getValue(values, "wooden_hoe"));

	}

	@Test
	@DisplayName("Test generating values with deep conversion")
	void testGenerateValuesDeepConversions() {
		mappingCollector.setValueBefore("a1", 1L);
		mappingCollector.addConversion(1, "b1", Collections.singletonList("a1"));
		mappingCollector.addConversion(1, "c1", Collections.singletonList("b1"));

		Map<String, Long> values = valueGenerator.generateValues();
		Assertions.assertEquals(1, getValue(values, "a1"));
		Assertions.assertEquals(1, getValue(values, "b1"));
		Assertions.assertEquals(1, getValue(values, "c1"));
	}

	@Test
	@DisplayName("Test generating values with deep invalid conversion")
	void testGenerateValuesDeepInvalidConversion() {
		mappingCollector.setValueBefore("a1", 1L);
		mappingCollector.addConversion(1, "b", Arrays.asList("a1", "invalid1"));
		mappingCollector.addConversion(1, "invalid1", Arrays.asList("a1", "invalid2"));
		mappingCollector.addConversion(1, "invalid2", Arrays.asList("a1", "invalid3"));

		Map<String, Long> values = valueGenerator.generateValues();
		Assertions.assertEquals(1, getValue(values, "a1"));
		Assertions.assertEquals(0, getValue(values, "b"));
		Assertions.assertEquals(0, getValue(values, "invalid1"));
		Assertions.assertEquals(0, getValue(values, "invalid2"));
		Assertions.assertEquals(0, getValue(values, "invalid3"));
	}

	@Test
	@DisplayName("Test generating values from multiple deep recipes with invalid conversion")
	void testGenerateValuesMultiRecipeDeepInvalid() {
		mappingCollector.setValueBefore("a1", 1L);
		mappingCollector.addConversion(1, "b2", Arrays.asList("a1", "a1"));
		mappingCollector.addConversion(1, "b2", Collections.singletonList("invalid1"));
		mappingCollector.addConversion(1, "invalid1", Arrays.asList("a1", "invalid2"));

		Map<String, Long> values = valueGenerator.generateValues();
		Assertions.assertEquals(1, getValue(values, "a1"));
		Assertions.assertEquals(2, getValue(values, "b2"));
		Assertions.assertEquals(0, getValue(values, "invalid1"));
		Assertions.assertEquals(0, getValue(values, "invalid2"));
	}

	@Test
	@DisplayName("Test generating values from multiple deep recipes with invalid ingredient")
	void testGenerateValuesMultiRecipesInvalidIngredient() {
		mappingCollector.setValueBefore("a1", 1L);
		mappingCollector.addConversion(1, "b2", Arrays.asList("a1", "a1"));
		mappingCollector.addConversion(1, "b2", Collections.singletonList("invalid"));

		Map<String, Long> values = valueGenerator.generateValues();
		Assertions.assertEquals(1, getValue(values, "a1"));
		Assertions.assertEquals(2, getValue(values, "b2"));
		Assertions.assertEquals(0, getValue(values, "invalid"));
	}

	@Test
	@DisplayName("Test generating values from recipes that have a cycle")
	void testGenerateValuesCycleRecipe() {
		mappingCollector.setValueBefore("a1", 1L);
		mappingCollector.addConversion(1, "cycle-1", Collections.singletonList("a1"));
		mappingCollector.addConversion(1, "cycle-2", Collections.singletonList("cycle-1"));
		mappingCollector.addConversion(1, "cycle-1", Collections.singletonList("cycle-2"));

		Map<String, Long> values = valueGenerator.generateValues();
		Assertions.assertEquals(1, getValue(values, "a1"));
		Assertions.assertEquals(1, getValue(values, "cycle-1"));
		Assertions.assertEquals(1, getValue(values, "cycle-2"));
	}

	@Test
	@DisplayName("Test generating values from recipes that have a large cycle")
	void testGenerateValuesBigCycleRecipe() {
		mappingCollector.setValueBefore("a1", 1L);
		mappingCollector.addConversion(1, "cycle-1", Collections.singletonList("a1"));
		mappingCollector.addConversion(1, "cycle-2", Collections.singletonList("cycle-1"));
		mappingCollector.addConversion(1, "cycle-3", Collections.singletonList("cycle-2"));
		mappingCollector.addConversion(1, "cycle-4", Collections.singletonList("cycle-3"));
		mappingCollector.addConversion(1, "cycle-5", Collections.singletonList("cycle-4"));
		mappingCollector.addConversion(1, "cycle-1", Collections.singletonList("cycle-5"));

		Map<String, Long> values = valueGenerator.generateValues();
		Assertions.assertEquals(1, getValue(values, "a1"));
		Assertions.assertEquals(1, getValue(values, "cycle-1"));
		Assertions.assertEquals(1, getValue(values, "cycle-2"));
		Assertions.assertEquals(1, getValue(values, "cycle-3"));
		Assertions.assertEquals(1, getValue(values, "cycle-4"));
		Assertions.assertEquals(1, getValue(values, "cycle-5"));
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

		mappingCollector.addConversion(1, aCoal, Arrays.asList(coal, coal, coal, coal));
		mappingCollector.addConversion(4, aCoal, Collections.singletonList(mFuel));
		mappingCollector.addConversion(9, aCoal, Collections.singletonList(aCoalBlock));
		repeat = aCoal;
		mappingCollector.addConversion(1, aCoalBlock, Arrays.asList(repeat, repeat, repeat, repeat, repeat, repeat, repeat, repeat, repeat));

		mappingCollector.addConversion(1, mFuel, Arrays.asList(aCoal, aCoal, aCoal, aCoal));
		mappingCollector.addConversion(4, mFuel, Collections.singletonList(aFuel));
		mappingCollector.addConversion(9, mFuel, Collections.singletonList(mFuelBlock));
		repeat = mFuel;
		mappingCollector.addConversion(1, mFuelBlock, Arrays.asList(repeat, repeat, repeat, repeat, repeat, repeat, repeat, repeat, repeat));

		mappingCollector.addConversion(1, aFuel, Arrays.asList(mFuel, mFuel, mFuel, mFuel));
		mappingCollector.addConversion(9, aFuel, Collections.singletonList(aFuelBlock));
		repeat = aFuel;
		mappingCollector.addConversion(1, aFuelBlock, Arrays.asList(repeat, repeat, repeat, repeat, repeat, repeat, repeat, repeat, repeat));

		mappingCollector.setValueBefore("diamondBlock", 73728L);
		final String dMatter = "darkMatter";
		final String dMatterBlock = "darkMatterBlock";

		mappingCollector.addConversion(1, dMatter, Arrays.asList(aFuel, aFuel, aFuel, aFuel, aFuel, aFuel, aFuel, aFuel, "diamondBlock"));
		mappingCollector.addConversion(1, dMatter, Collections.singletonList(dMatterBlock));
		mappingCollector.addConversion(4, dMatterBlock, Arrays.asList(dMatter, dMatter, dMatter, dMatter));

		final String rMatter = "redMatter";
		final String rMatterBlock = "redMatterBlock";
		mappingCollector.addConversion(1, rMatter, Arrays.asList(aFuel, aFuel, aFuel, dMatter, dMatter, dMatter, aFuel, aFuel, aFuel));
		mappingCollector.addConversion(1, rMatter, Collections.singletonList(rMatterBlock));
		mappingCollector.addConversion(4, rMatterBlock, Arrays.asList(rMatter, rMatter, rMatter, rMatter));

		Map<String, Long> values = valueGenerator.generateValues();
		Assertions.assertEquals(128, getValue(values, coal));
		Assertions.assertEquals(512, getValue(values, aCoal));
		Assertions.assertEquals(4608, getValue(values, aCoalBlock));
		Assertions.assertEquals(2048, getValue(values, mFuel));
		Assertions.assertEquals(18432, getValue(values, mFuelBlock));
		Assertions.assertEquals(8192, getValue(values, aFuel));
		Assertions.assertEquals(73728, getValue(values, aFuelBlock));
		Assertions.assertEquals(73728, getValue(values, "diamondBlock"));
		Assertions.assertEquals(139264, getValue(values, dMatter));
		Assertions.assertEquals(139264, getValue(values, dMatterBlock));
		Assertions.assertEquals(466944, getValue(values, rMatter));
		Assertions.assertEquals(466944, getValue(values, rMatterBlock));
	}

	@Test
	@DisplayName("Test generating EMC values for things made from wools")
	void testGenerateValuesWool() {
		final String[] dyes = new String[]{"Blue", "Brown", "White", "Other"};
		final int[] dyeValue = new int[]{864, 176, 48, 16};
		for (int i = 0; i < dyes.length; i++) {
			mappingCollector.setValueBefore("dye" + dyes[i], (long) dyeValue[i]);
			mappingCollector.addConversion(1, "wool" + dyes[i], Arrays.asList("woolWhite", "dye" + dyes[i]));
		}
		mappingCollector.setValueBefore("string", 12L);
		mappingCollector.addConversion(1, "woolWhite", Arrays.asList("string", "string", "string", "string"));

		mappingCollector.setValueBefore("stick", 4L);
		mappingCollector.setValueBefore("plank", 8L);
		for (String dye : dyes) {
			mappingCollector.addConversion(1, "bed", Arrays.asList("plank", "plank", "plank", "wool" + dye, "wool" + dye, "wool" + dye));
			mappingCollector.addConversion(3, "carpet" + dye, Arrays.asList("wool" + dye, "wool" + dye));
			mappingCollector.addConversion(1, "painting", Arrays.asList("wool" + dye, "stick", "stick", "stick", "stick", "stick", "stick", "stick", "stick"));
		}

		Map<String, Long> values = valueGenerator.generateValues();
		for (int i = 0; i < dyes.length; i++) {
			Assertions.assertEquals(dyeValue[i], getValue(values, "dye" + dyes[i]));
		}
		Assertions.assertEquals(12, getValue(values, "string"));
		Assertions.assertEquals(48, getValue(values, "woolWhite"));
		Assertions.assertEquals(224, getValue(values, "woolBrown"));
		Assertions.assertEquals(912, getValue(values, "woolBlue"));
		Assertions.assertEquals(64, getValue(values, "woolOther"));

		Assertions.assertEquals(32, getValue(values, "carpetWhite"));
		Assertions.assertEquals(149, getValue(values, "carpetBrown"));
		Assertions.assertEquals(608, getValue(values, "carpetBlue"));
		Assertions.assertEquals(42, getValue(values, "carpetOther"));

		Assertions.assertEquals(168, getValue(values, "bed"));
		Assertions.assertEquals(80, getValue(values, "painting"));
	}

	@Test
	@DisplayName("Test generating values for arbitrary bucket")
	void testGenerateValuesBucketRecipe() {
		mappingCollector.setValueBefore("somethingElse", 9L);
		mappingCollector.setValueBefore("container", 23L);
		mappingCollector.setValueBefore("fluid", 17L);
		mappingCollector.addConversion(1, "filledContainer", Arrays.asList("container", "fluid"));

		//Recipe that only consumes fluid:
		Map<String, Integer> map = new HashMap<>();
		map.put("container", -1);
		map.put("filledContainer", 1);
		map.put("somethingElse", 2);
		mappingCollector.addConversion(1, "fluidCraft", map);

		Map<String, Long> values = valueGenerator.generateValues();
		Assertions.assertEquals(9, getValue(values, "somethingElse"));
		Assertions.assertEquals(23, getValue(values, "container"));
		Assertions.assertEquals(17, getValue(values, "fluid"));
		Assertions.assertEquals(17 + 23, getValue(values, "filledContainer"));
		Assertions.assertEquals(17 + 2 * 9, getValue(values, "fluidCraft"));

	}

	@Test
	@DisplayName("Test generating EMC value for water bucket")
	void testGenerateValuesWaterBucketRecipe() {
		mappingCollector.setValueBefore("somethingElse", 9L);
		mappingCollector.setValueBefore("container", 23L);
		mappingCollector.setValueBefore("fluid", Constants.FREE_ARITHMETIC_VALUE);
		mappingCollector.addConversion(1, "filledContainer", Arrays.asList("container", "fluid"));

		//Recipe that only consumes fluid:
		Map<String, Integer> map = new HashMap<>();
		map.put("container", -1);
		map.put("filledContainer", 1);
		map.put("somethingElse", 2);
		mappingCollector.addConversion(1, "fluidCraft", map);

		Map<String, Long> values = valueGenerator.generateValues();
		Assertions.assertEquals(9, getValue(values, "somethingElse"));
		Assertions.assertEquals(23, getValue(values, "container"));
		Assertions.assertEquals(0, getValue(values, "fluid"));
		Assertions.assertEquals(23, getValue(values, "filledContainer"));
		Assertions.assertEquals(2 * 9, getValue(values, "fluidCraft"));

	}

	@Test
	@DisplayName("Test generating EMC from cyclic recipes with exploits")
	void testGenerateValuesCycleRecipeExploit() {
		mappingCollector.setValueBefore("a1", 1L);
		//Exploitable Cycle Recipe
		mappingCollector.addConversion(1, "exploitable", Collections.singletonList("a1"));
		mappingCollector.addConversion(2, "exploitable", Collections.singletonList("exploitable"));

		//Not-exploitable Cycle Recipe
		mappingCollector.addConversion(1, "notExploitable", Collections.singletonList("a1"));
		mappingCollector.addConversion(2, "notExploitable", Arrays.asList("notExploitable", "notExploitable"));

		Map<String, Long> values = valueGenerator.generateValues();
		Assertions.assertEquals(1, getValue(values, "a1"));
		Assertions.assertEquals(0, getValue(values, "exploitable"));
		Assertions.assertEquals(1, getValue(values, "notExploitable"));
	}

	@Test
	@DisplayName("Test generating EMC from cyclic recipes with delayed exploit")
	void testGenerateValuesDelayedCycleRecipeExploit() {
		mappingCollector.setValueBefore("a1", 1L);
		//Exploitable Cycle Recipe
		mappingCollector.addConversion(1, "exploitable1", Collections.singletonList("a1"));
		mappingCollector.addConversion(2, "exploitable2", Collections.singletonList("exploitable1"));
		mappingCollector.addConversion(1, "exploitable1", Collections.singletonList("exploitable2"));

		Map<String, Long> values = valueGenerator.generateValues();
		Assertions.assertEquals(1, getValue(values, "a1"));
		Assertions.assertEquals(0, getValue(values, "exploitable1"));
		Assertions.assertEquals(0, getValue(values, "exploitable2"));
	}

	@Test
	@DisplayName("Test generating EMC from cyclic recipes with exploits 2")
	void testGenerateValuesCycleRecipeExploit2() {
		mappingCollector.setValueBefore("a1", 1L);
		//Exploitable Cycle Recipe
		mappingCollector.addConversion(1, "exploitable", Collections.singletonList("a1"));
		mappingCollector.addConversion(2, "exploitable", Collections.singletonList("exploitable"));
		mappingCollector.addConversion(1, "b", Collections.singletonList("exploitable"));

		//Not-exploitable Cycle Recipe
		mappingCollector.addConversion(1, "notExploitable", Collections.singletonList("a1"));
		mappingCollector.addConversion(2, "notExploitable", Arrays.asList("notExploitable", "notExploitable"));

		Map<String, Long> values = valueGenerator.generateValues();
		Assertions.assertEquals(1, getValue(values, "a1"));
		Assertions.assertEquals(0, getValue(values, "exploitable"));
		Assertions.assertEquals(1, getValue(values, "notExploitable"));
		Assertions.assertEquals(0, getValue(values, "b"));
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
			mappingCollector.addConversion(1, "log*", Collections.singletonList(logType));
		}
		for (String log2Type : log2Types) {
			mappingCollector.setValueBefore(log2Type, 32L);
			mappingCollector.addConversion(1, "log2*", Collections.singletonList(log2Type));
		}
		mappingCollector.addConversion(1, "coal1", Collections.singletonList("log*"));
		for (String coalType : coalTypes) {
			mappingCollector.addConversion(1, "coal*", Collections.singletonList(coalType));
			mappingCollector.addConversion(3, "firecharge", Arrays.asList(coalType, "gunpowder", "blazepowder"));
		}
		mappingCollector.addConversion(1, "firecharge*", Collections.singletonList("firecharge"));
		Map<String, Integer> m = new HashMap<>();
		m.put("coal0", 9);
		mappingCollector.addConversion(1, "coalblock", m);

		m.clear();
		//Philosophers stone smelting 7xCoalOre -> 7xCoal
		m.put("coalore", 7);
		m.put("coal*", 1);
		mappingCollector.addConversion(7, "coal0", m);

		m.clear();
		//Philosophers stone smelting logs
		m.put("log*", 7);
		m.put("coal*", 1);
		mappingCollector.addConversion(7, "coal1", m);

		m.clear();
		//Philosophers stone smelting log2s
		m.put("log2*", 7);
		m.put("coal*", 1);
		mappingCollector.addConversion(7, "coal1", m);

		//Smelting single coal ore
		mappingCollector.addConversion(1, "coal0", Collections.singletonList("coalore"));
		//Coal Block
		mappingCollector.addConversion(9, "coal0", Collections.singletonList("coalblock"));

		Map<String, Long> values = valueGenerator.generateValues();
		for (String logType : logTypes) {
			Assertions.assertEquals(32, getValue(values, logType));
		}
		Assertions.assertEquals(32, getValue(values, "log*"));
		Assertions.assertEquals(128, getValue(values, "coal0"));
		Assertions.assertEquals(32, getValue(values, "coal1"));
		Assertions.assertEquals(32, getValue(values, "coal*"));
		Assertions.assertEquals(330, getValue(values, "firecharge"));
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
			mappingCollector.addConversion(8, "antiblock" + dyes[i], Arrays.asList(
					"antiblock_all", "antiblock_all", "antiblock_all",
					"antiblock_all", "dye" + dyes[i], "antiblock_all",
					"antiblock_all", "antiblock_all", "antiblock_all"
			));
			mappingCollector.addConversion(1, "antiblock_all", Collections.singletonList("antiblock" + dyes[i]));
		}

		mappingCollector.setValueBefore(gDust, 384L);
		mappingCollector.setValueBefore(stone, 1L);
		mappingCollector.addConversion(8, "antiblockWhite", Arrays.asList(
				stone, stone, stone,
				stone, gDust, stone,
				stone, stone, stone));

		Map<String, Long> values = valueGenerator.generateValues();
		Assertions.assertEquals((8 + 384) / 8, getValue(values, "antiblockWhite"));
		for (int i = 0; i < dyes.length; i++) {
			Assertions.assertEquals(dyeValue[i], getValue(values, "dye" + dyes[i]));
			if (!dyes[i].equals("White")) {
				Assertions.assertEquals((dyeValue[i] + ((8 + 384) / 8) * 8) / 8, getValue(values, "antiblock" + dyes[i]));
			}
		}
	}

	@Test
	@DisplayName("Test generating values from a zero count ingredient")
	void testGenerateValuesZeroCountIngredientDependency() {
		mappingCollector.setValueBefore("a", 2L);
		mappingCollector.setValueBefore("b", 3L);
		mappingCollector.setValueBefore("notConsume1", 1L);
		HashMap<String, Integer> ingredients = new HashMap<>();
		ingredients.put("a", 1);
		ingredients.put("b", 1);
		ingredients.put("notConsume1", 0);
		mappingCollector.addConversion(1, "c1", ingredients);
		ingredients.remove("notConsume1");
		ingredients.put("notConsume2", 0);
		mappingCollector.addConversion(1, "c2", ingredients);

		Map<String, Long> values = valueGenerator.generateValues();
		Assertions.assertEquals(2, getValue(values, "a"));
		Assertions.assertEquals(3, getValue(values, "b"));
		Assertions.assertEquals(1, getValue(values, "notConsume1"));
		Assertions.assertEquals(0, getValue(values, "notConsume2"));
		Assertions.assertEquals(5, getValue(values, "c1"));
		Assertions.assertEquals(0, getValue(values, "c2"));
	}


	@Test
	@DisplayName("Test generating values with a free alternative recipe")
	void testGenerateValuesFreeAlternatives() {
		mappingCollector.setValueBefore("freeWater", Constants.FREE_ARITHMETIC_VALUE);
		mappingCollector.setValueBefore("waterBottle", 0L);
		mappingCollector.addConversion(1, "waterGroup", Collections.singletonList("freeWater"));
		mappingCollector.addConversion(1, "waterGroup", Collections.singletonList("waterBottle"));
		mappingCollector.setValueBefore("a", 3L);
		mappingCollector.addConversion(1, "result", Arrays.asList("a", "waterGroup"));

		Map<String, Long> values = valueGenerator.generateValues();
		Assertions.assertEquals(3, getValue(values, "a"));
		Assertions.assertEquals(0, getValue(values, "freeWater"));
		Assertions.assertEquals(0, getValue(values, "waterBottle"));
		Assertions.assertEquals(0, getValue(values, "waterGroup"));
		Assertions.assertEquals(3, getValue(values, "result"));
	}

	@Test
	@DisplayName("Test generating values with a free alternative recipe, when we have a negative ingredient")
	void testGenerateValuesFreeAlternativesWithNegativeIngredients() {
		mappingCollector.setValueBefore("bucket", 768L);
		mappingCollector.setValueBefore("waterBucket", 768L);
		mappingCollector.setValueBefore("waterBottle", 0L);
		Map<String, Integer> m = new HashMap<>();
		m.put("waterBucket", 1);
		m.put("bucket", -1);
		mappingCollector.addConversion(1, "waterGroup", m);
		mappingCollector.addConversion(1, "waterGroup", Collections.singletonList("waterBottle"));
		mappingCollector.setValueBefore("a", 3L);
		mappingCollector.addConversion(1, "result", Arrays.asList("a", "waterGroup"));

		Map<String, Long> values = valueGenerator.generateValues();
		Assertions.assertEquals(3, getValue(values, "a"));
		Assertions.assertEquals(768, getValue(values, "bucket"));
		Assertions.assertEquals(768, getValue(values, "waterBucket"));
		Assertions.assertEquals(0, getValue(values, "waterGroup"));
		Assertions.assertEquals(3, getValue(values, "result"));
	}


	@Test
	@DisplayName("Test overflow from recipe")
	void testOverflowWithIngredients() {
		mappingCollector.setValueBefore("a", Long.MAX_VALUE / 2 + 1);
		mappingCollector.setValueBefore("b", Long.MAX_VALUE / 2 + 1);
		mappingCollector.addConversion(1, "c", Arrays.asList("a", "b"));

		Map<String, Long> values = valueGenerator.generateValues();
		Assertions.assertEquals(Long.MAX_VALUE / 2 + 1, getValue(values, "a"));
		Assertions.assertEquals(Long.MAX_VALUE / 2 + 1, getValue(values, "b"));
		Assertions.assertEquals(0, getValue(values, "c"));
	}

	@Test
	@DisplayName("Test overflow with an already set in range amount")
	void testOverflowWithAmount() {
		mappingCollector.setValueBefore("a", Long.MAX_VALUE / 2);
		mappingCollector.addConversion(3, "a", Collections.singletonList("something"));

		Map<String, Long> values = valueGenerator.generateValues();
		Assertions.assertEquals(Long.MAX_VALUE / 2, getValue(values, "a"));
	}

	@Test
	@DisplayName("Test extreme overflow")
	void testLargeOverflow() {
		mappingCollector.setValueBefore("a", Long.MAX_VALUE);
		mappingCollector.addConversion(1, "b", Arrays.asList("a", "a", "a", "a", "a", "a", "a", "a", "a"));

		Map<String, Long> values = valueGenerator.generateValues();
		Assertions.assertEquals(0, getValue(values, "b"));
	}

	@Test
	@DisplayName("Test simple overflow hidden by result being within bounds")
	void testHiddenOverflow() {
		mappingCollector.setValueBefore("a", Long.MAX_VALUE);
		mappingCollector.addConversion(2, "b", Arrays.asList("a", "a"));
		mappingCollector.addConversion(5, "c", Arrays.asList("a", "a", "a", "a", "a"));

		Map<String, Long> values = valueGenerator.generateValues();
		Assertions.assertEquals(Long.MAX_VALUE, getValue(values, "b"));
		Assertions.assertEquals(Long.MAX_VALUE, getValue(values, "c"));
	}

	@Test
	@DisplayName("Test overwriting conversion with smaller value")
	void testOverwriteConversions() {
		mappingCollector.setValueBefore("a", 1L);
		mappingCollector.setValueFromConversion(1, "b", Arrays.asList("a", "a", "a"));
		mappingCollector.addConversion(1, "b", Collections.singletonList("a"));
		mappingCollector.addConversion(1, "c", Arrays.asList("b", "b"));
		Map<String, Long> values = valueGenerator.generateValues();
		Assertions.assertEquals(1, getValue(values, "a"));
		Assertions.assertEquals(3, getValue(values, "b"));
		Assertions.assertEquals(6, getValue(values, "c"));

	}

	private static <T, V extends Number> long getValue(Map<T, V> map, T key) {
		V val = map.get(key);
		return val == null ? 0 : val.longValue();
	}
}