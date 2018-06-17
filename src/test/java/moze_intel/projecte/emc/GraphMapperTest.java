package moze_intel.projecte.emc;

import moze_intel.projecte.emc.arithmetics.DoubleArithmetic;
import moze_intel.projecte.emc.arithmetics.IValueArithmetic;
import moze_intel.projecte.emc.collector.IExtendedMappingCollector;
import moze_intel.projecte.emc.collector.LongToDoubleCollector;
import moze_intel.projecte.emc.collector.MappingCollector;
import moze_intel.projecte.emc.generators.DoubleToLongGenerator;
import moze_intel.projecte.emc.generators.IValueGenerator;

import org.apache.commons.lang3.math.Fraction;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.Timeout;

import java.util.*;

import static org.junit.Assert.*;

//@RunWith(value = Parameterized.class)
public class GraphMapperTest {

/*	@Parameterized.Parameters
	public static Collection  parameters() {
		Object[][] data = new Object[][] { { new ComplexGraphMapper<String, Integer>(new IntArithmetic())  }};
		return Arrays.asList(data);
	}

	public GraphMapperTest(GraphMapper<String, Integer> mappingCollector) {
		this.mappingCollector = mappingCollector;
	}*/
	@Before
	public void setup() {
		//mappingCollector = new SimpleGraphMapper<String, Integer>(new IntArithmetic());
		SimpleGraphMapper<String, Double, IValueArithmetic<Double>> mapper = new SimpleGraphMapper<>(new DoubleArithmetic());
		valueGenerator = new DoubleToLongGenerator<>(mapper);
		mappingCollector = new LongToDoubleCollector<>(mapper);
	}

	@Rule
	public Timeout timeout = new Timeout(3000);
	private IValueGenerator<String, Long> valueGenerator;
	private IExtendedMappingCollector<String, Long, IValueArithmetic<Double>> mappingCollector;

	@org.junit.Test
	public void testGenerateValuesSimple() throws Exception {
		mappingCollector.setValueBefore("a1", 1l);
		mappingCollector.addConversion(1, "c4", Arrays.asList("a1", "a1", "a1", "a1"));
		mappingCollector.addConversion(1, "b2", Arrays.asList("a1", "a1"));

		Map<String, Long> values = valueGenerator.generateValues();
		assertEquals(1, getValue(values, "a1"));
		assertEquals(2, getValue(values, "b2"));
		assertEquals(4, getValue(values, "c4"));

	}

	@org.junit.Test
	public void testGenerateValuesSimpleMultiRecipe() throws Exception {
		mappingCollector.setValueBefore("a1", 1l);
		//2 Recipes for c4
		mappingCollector.addConversion(1, "c4", Arrays.asList("a1", "a1", "a1", "a1"));
		mappingCollector.addConversion(2, "c4", Arrays.asList("b2", "b2"));
		mappingCollector.addConversion(1, "b2", Arrays.asList("a1", "a1"));

		Map<String, Long> values = valueGenerator.generateValues();
		assertEquals(1, getValue(values, "a1"));
		assertEquals(2, getValue(values, "b2"));
		assertEquals(2, getValue(values, "c4")); //2 * c4 = 2 * b2 => 2 * (2) = 2 * (2)
	}

	@org.junit.Test
	public void testGenerateValuesSimpleMultiRecipeWithEmptyAlternative() throws Exception {
		mappingCollector.setValueBefore("a1", 1l);
		//2 Recipes for c4
		mappingCollector.addConversion(1, "c4", Arrays.asList("a1", "a1", "a1", "a1"));
		mappingCollector.addConversion(1, "c4", new LinkedList<>());
		mappingCollector.addConversion(1, "b2", Arrays.asList("a1", "a1"));

		Map<String, Long> values = valueGenerator.generateValues();
		assertEquals(1, getValue(values, "a1"));
		assertEquals(2, getValue(values, "b2"));
		assertEquals(4, getValue(values, "c4")); //2 * c4 = 2 * b2 => 2 * (2) = 2 * (2)
	}

	@org.junit.Test
	public void testGenerateValuesSimpleFixedAfterInherit() throws Exception {
		mappingCollector.setValueBefore("a1", 1l);
		mappingCollector.addConversion(1, "c4", Arrays.asList("a1", "a1", "a1", "a1"));
		mappingCollector.addConversion(1, "b2", Arrays.asList("a1", "a1"));
		mappingCollector.setValueAfter("b2", 20l);

		Map<String, Long> values = valueGenerator.generateValues();
		assertEquals(1, getValue(values, "a1"));
		assertEquals(20, getValue(values, "b2"));
		assertEquals(4, getValue(values, "c4"));
	}

	@org.junit.Test
	public void testGenerateValuesSimpleFixedDoNotInherit() throws Exception {
		mappingCollector.setValueBefore("a1", 1l);
		mappingCollector.addConversion(1, "b2", Arrays.asList("a1", "a1"));
		mappingCollector.addConversion(1, "c4", Arrays.asList("b2", "b2"));
		mappingCollector.setValueBefore("b2", 0l);
		mappingCollector.setValueAfter("b2", 20l);

		Map<String, Long> values = valueGenerator.generateValues();
		assertEquals(1, getValue(values, "a1"));
		assertEquals(20, getValue(values, "b2"));
		assertEquals(0, getValue(values, "c4"));
	}

	@org.junit.Test
	public void testGenerateValuesSimpleFixedDoNotInheritMultiRecipes() throws Exception
	{
		mappingCollector.setValueBefore("a1", 1l);
		mappingCollector.addConversion(1, "c", Arrays.asList("a1", "a1"));
		mappingCollector.addConversion(1, "c", Arrays.asList("a1", "b"));
		mappingCollector.setValueBefore("b", 0l);
		mappingCollector.setValueAfter("b", 20l);

		Map<String, Long> values = valueGenerator.generateValues();
		assertEquals(1, getValue(values, "a1"));
		assertEquals(20, getValue(values, "b"));
		assertEquals(2, getValue(values, "c"));
	}

	@org.junit.Test
	public void testGenerateValuesSimpleSelectMinValue() throws Exception
	{
		mappingCollector.setValueBefore("a1", 1l);
		mappingCollector.setValueBefore("b2", 2l);
		mappingCollector.addConversion(1, "c", Arrays.asList("a1", "a1"));
		mappingCollector.addConversion(1, "c", Arrays.asList("b2", "b2"));

		Map<String, Long> values = valueGenerator.generateValues();
		assertEquals(1, getValue(values, "a1"));
		assertEquals(2, getValue(values, "b2"));
		assertEquals(2, getValue(values, "c"));
	}

	@org.junit.Test
	public void testGenerateValuesSimpleSelectMinValueWithDependency() throws Exception
	{
		mappingCollector.setValueBefore("a1", 1l);
		mappingCollector.setValueBefore("b2", 2l);
		mappingCollector.addConversion(1, "c", Arrays.asList("a1", "a1"));
		mappingCollector.addConversion(1, "c", Arrays.asList("b2", "b2"));
		mappingCollector.addConversion(1, "d", Arrays.asList("c", "c"));

		Map<String, Long> values = valueGenerator.generateValues();
		assertEquals(1, getValue(values, "a1"));
		assertEquals(2, getValue(values, "b2"));
		assertEquals(2, getValue(values, "c"));
		assertEquals(4, getValue(values, "d"));
	}

	@org.junit.Test
	public void testGenerateValuesSimpleWoodToWorkBench() throws Exception
	{
		mappingCollector.setValueBefore("planks", 1l);
		mappingCollector.addConversion(4, "planks", Collections.singletonList("wood"));
		mappingCollector.addConversion(1, "workbench", Arrays.asList("planks", "planks", "planks", "planks"));

		Map<String, Long> values = valueGenerator.generateValues();
		assertEquals(0, getValue(values, "wood"));
		assertEquals(1, getValue(values, "planks"));
		assertEquals(4, getValue(values, "workbench"));
	}

	@org.junit.Test
	public void testGenerateValuesWood() throws Exception {
		for (char i : "ABCD".toCharArray())
		{
			mappingCollector.setValueBefore("wood" + i, 32l);
			mappingCollector.addConversion(4, "planks" + i, Collections.singletonList("wood" + i));
		}

		for (char i : "ABCD".toCharArray()) {
			mappingCollector.addConversion(4, "planks" + i, Collections.singletonList("wood"));
		}

		for (char i : "ABCD".toCharArray())
			for (char j : "ABCD".toCharArray())
				mappingCollector.addConversion(4, "stick", Arrays.asList("planks" + i, "planks" + j));
		mappingCollector.addConversion(1, "crafting_table", Arrays.asList("planksA", "planksA", "planksA", "planksA"));
		for (char i : "ABCD".toCharArray())
			for (char j : "ABCD".toCharArray())
				mappingCollector.addConversion(1, "wooden_hoe", Arrays.asList("stick", "stick", "planks" + i, "planks" + j));

		Map<String, Long> values = valueGenerator.generateValues();
		for (char i : "ABCD".toCharArray())
			assertEquals(32, getValue(values, "wood" + i));
		for (char i : "ABCD".toCharArray())
			assertEquals(8, getValue(values, "planks" + i));
		assertEquals(4, getValue(values, "stick"));
		assertEquals(32, getValue(values, "crafting_table"));
		assertEquals(24, getValue(values, "wooden_hoe"));

	}

	@org.junit.Test
	public void testGenerateValuesDeepConversions() throws Exception
	{
		mappingCollector.setValueBefore("a1", 1l);
		mappingCollector.addConversion(1, "b1", Collections.singletonList("a1"));
		mappingCollector.addConversion(1, "c1", Collections.singletonList("b1"));

		Map<String, Long> values = valueGenerator.generateValues();
		assertEquals(1, getValue(values, "a1"));
		assertEquals(1, getValue(values, "b1"));
		assertEquals(1, getValue(values, "c1"));
	}

	@org.junit.Test
	public void testGenerateValuesDeepInvalidConversion() throws Exception
	{
		mappingCollector.setValueBefore("a1", 1l);
		mappingCollector.addConversion(1, "b", Arrays.asList("a1", "invalid1"));
		mappingCollector.addConversion(1, "invalid1", Arrays.asList("a1", "invalid2"));
		mappingCollector.addConversion(1, "invalid2", Arrays.asList("a1", "invalid3"));


		Map<String, Long> values = valueGenerator.generateValues();
		assertEquals(1, getValue(values, "a1"));
		assertEquals(0, getValue(values, "b"));
		assertEquals(0, getValue(values, "invalid1"));
		assertEquals(0, getValue(values, "invalid2"));
		assertEquals(0, getValue(values, "invalid3"));
	}

	@org.junit.Test
	public void testGenerateValuesMultiRecipeDeepInvalid() throws Exception
	{
		mappingCollector.setValueBefore("a1", 1l);
		mappingCollector.addConversion(1, "b2", Arrays.asList("a1", "a1"));
		mappingCollector.addConversion(1, "b2", Collections.singletonList("invalid1"));
		mappingCollector.addConversion(1, "invalid1", Arrays.asList("a1", "invalid2"));


		Map<String, Long> values = valueGenerator.generateValues();
		assertEquals(1, getValue(values, "a1"));
		assertEquals(2, getValue(values, "b2"));
		assertEquals(0, getValue(values, "invalid1"));
		assertEquals(0, getValue(values, "invalid2"));
	}

	@org.junit.Test
	public void testGenerateValuesMultiRecipesInvalidIngredient() throws Exception
	{
		mappingCollector.setValueBefore("a1", 1l);
		mappingCollector.addConversion(1, "b2", Arrays.asList("a1", "a1"));
		mappingCollector.addConversion(1, "b2", Collections.singletonList("invalid"));


		Map<String, Long> values = valueGenerator.generateValues();
		assertEquals(1, getValue(values, "a1"));
		assertEquals(2, getValue(values, "b2"));
		assertEquals(0, getValue(values, "invalid"));
	}

	@org.junit.Test
	public void testGenerateValuesCycleRecipe() throws Exception
	{
		mappingCollector.setValueBefore("a1", 1l);
		mappingCollector.addConversion(1, "cycle-1", Collections.singletonList("a1"));
		mappingCollector.addConversion(1, "cycle-2", Collections.singletonList("cycle-1"));
		mappingCollector.addConversion(1, "cycle-1", Collections.singletonList("cycle-2"));


		Map<String, Long> values = valueGenerator.generateValues();
		assertEquals(1, getValue(values, "a1"));
		assertEquals(1, getValue(values, "cycle-1"));
		assertEquals(1, getValue(values, "cycle-2"));
	}

	@org.junit.Test
	public void testGenerateValuesBigCycleRecipe() throws Exception
	{
		mappingCollector.setValueBefore("a1", 1l);
		mappingCollector.addConversion(1, "cycle-1", Collections.singletonList("a1"));
		mappingCollector.addConversion(1, "cycle-2", Collections.singletonList("cycle-1"));
		mappingCollector.addConversion(1, "cycle-3", Collections.singletonList("cycle-2"));
		mappingCollector.addConversion(1, "cycle-4", Collections.singletonList("cycle-3"));
		mappingCollector.addConversion(1, "cycle-5", Collections.singletonList("cycle-4"));
		mappingCollector.addConversion(1, "cycle-1", Collections.singletonList("cycle-5"));


		Map<String, Long> values = valueGenerator.generateValues();
		assertEquals(1, getValue(values, "a1"));
		assertEquals(1, getValue(values, "cycle-1"));
		assertEquals(1, getValue(values, "cycle-2"));
		assertEquals(1, getValue(values, "cycle-3"));
		assertEquals(1, getValue(values, "cycle-4"));
		assertEquals(1, getValue(values, "cycle-5"));
	}

	@org.junit.Test
	public void testGenerateValuesFuelAndMatter() throws Exception {
		final String coal = "coal";
		final String aCoal = "alchemicalCoal";
		final String aCoalBlock = "alchemicalCoalBlock";
		final String mFuel = "mobiusFuel";
		final String mFuelBlock = "mobiusFuelBlock";
		final String aFuel = "aeternalisFuel";
		final String aFuelBlock = "aeternalisFuelBlock";
		String repeat;

		mappingCollector.setValueBefore(coal, 128l);

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

		mappingCollector.setValueBefore("diamondBlock", 73728l);
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
		assertEquals(128, getValue(values, coal));
		assertEquals(512, getValue(values, aCoal));
		assertEquals(4608, getValue(values, aCoalBlock));
		assertEquals(2048, getValue(values, mFuel));
		assertEquals(18432, getValue(values, mFuelBlock));
		assertEquals(8192, getValue(values, aFuel));
		assertEquals(73728, getValue(values, aFuelBlock));
		assertEquals(73728, getValue(values, "diamondBlock"));
		assertEquals(139264, getValue(values, dMatter));
		assertEquals(139264, getValue(values, dMatterBlock));
		assertEquals(466944, getValue(values, rMatter));
		assertEquals(466944, getValue(values, rMatterBlock));
	}

	@org.junit.Test
	public void testGenerateValuesWool() throws Exception {
		final String[] dyes = new String[]{"Blue", "Brown", "White", "Other"};
		final int[] dyeValue = new int[]{864, 176, 48, 16};
		for (int i = 0; i < dyes.length; i++)
		{
			mappingCollector.setValueBefore("dye" + dyes[i], (long) dyeValue[i]);
			mappingCollector.addConversion(1, "wool" + dyes[i], Arrays.asList("woolWhite", "dye" + dyes[i]));
		}
		mappingCollector.setValueBefore("string", 12l);
		mappingCollector.addConversion(1, "woolWhite", Arrays.asList("string", "string", "string", "string"));

		mappingCollector.setValueBefore("stick", 4l);
		mappingCollector.setValueBefore("plank", 8l);
		for (String dye : dyes) {
			mappingCollector.addConversion(1, "bed", Arrays.asList("plank", "plank", "plank", "wool" + dye, "wool" + dye, "wool" + dye));
			mappingCollector.addConversion(3, "carpet" + dye, Arrays.asList("wool" + dye, "wool" + dye));
			mappingCollector.addConversion(1, "painting", Arrays.asList("wool" + dye, "stick", "stick", "stick", "stick", "stick", "stick", "stick", "stick"));
		}

		Map<String, Long> values = valueGenerator.generateValues();
		for (int i = 0; i < dyes.length; i++) {
			assertEquals(dyeValue[i], getValue(values, "dye" + dyes[i]));
		}
		assertEquals(12, getValue(values, "string"));
		assertEquals(48, getValue(values, "woolWhite"));
		assertEquals(224, getValue(values, "woolBrown"));
		assertEquals(912, getValue(values, "woolBlue"));
		assertEquals(64, getValue(values, "woolOther"));

		assertEquals(32, getValue(values, "carpetWhite"));
		assertEquals(149, getValue(values, "carpetBrown"));
		assertEquals(608, getValue(values, "carpetBlue"));
		assertEquals(42, getValue(values, "carpetOther"));

		assertEquals(168, getValue(values, "bed"));
		assertEquals(80, getValue(values, "painting"));
	}

	@org.junit.Test
	public void testGenerateValuesBucketRecipe() throws Exception
	{
		mappingCollector.setValueBefore("somethingElse", 9l);
		mappingCollector.setValueBefore("container", 23l);
		mappingCollector.setValueBefore("fluid", 17l);
		mappingCollector.addConversion(1, "filledContainer", Arrays.asList("container", "fluid"));

		//Recipe that only consumes fluid:
		Map<String, Integer> map = new HashMap<>();
		map.put("container", -1);
		map.put("filledContainer", 1);
		map.put("somethingElse", 2);
		mappingCollector.addConversion(1, "fluidCraft", map);

		Map<String, Long> values = valueGenerator.generateValues();
		assertEquals(9, getValue(values, "somethingElse"));
		assertEquals(23, getValue(values, "container"));
		assertEquals(17, getValue(values, "fluid"));
		assertEquals(17 + 23, getValue(values, "filledContainer"));
		assertEquals(17 + 2 * 9, getValue(values, "fluidCraft"));

	}

	@org.junit.Test
	public void testGenerateValuesWaterBucketRecipe() throws Exception
	{
		mappingCollector.setValueBefore("somethingElse", 9l);
		mappingCollector.setValueBefore("container", 23l);
		mappingCollector.setValueBefore("fluid", Long.MIN_VALUE);
		mappingCollector.addConversion(1, "filledContainer", Arrays.asList("container", "fluid"));

		//Recipe that only consumes fluid:
		Map<String, Integer> map = new HashMap<>();
		map.put("container", -1);
		map.put("filledContainer", 1);
		map.put("somethingElse", 2);
		mappingCollector.addConversion(1, "fluidCraft", map);

		Map<String, Long> values = valueGenerator.generateValues();
		assertEquals(9, getValue(values, "somethingElse"));
		assertEquals(23, getValue(values, "container"));
		assertEquals(0, getValue(values, "fluid"));
		assertEquals(23, getValue(values, "filledContainer"));
		assertEquals(2 * 9, getValue(values, "fluidCraft"));

	}

	@org.junit.Test
	public void testGenerateValuesCycleRecipeExploit() throws Exception
	{
		mappingCollector.setValueBefore("a1", 1l);
		//Exploitable Cycle Recype
		mappingCollector.addConversion(1, "exploitable", Collections.singletonList("a1"));
		mappingCollector.addConversion(2, "exploitable", Collections.singletonList("exploitable"));

		//Not-exploitable Cycle Recype
		mappingCollector.addConversion(1, "notExploitable", Collections.singletonList("a1"));
		mappingCollector.addConversion(2, "notExploitable", Arrays.asList("notExploitable", "notExploitable"));

		Map<String, Long> values = valueGenerator.generateValues();
		assertEquals(1, getValue(values, "a1"));
		assertEquals(0, getValue(values, "exploitable"));
		assertEquals(1, getValue(values, "notExploitable"));
	}

	@org.junit.Test
	public void testGenerateValuesDelayedCycleRecipeExploit() throws Exception
	{
		mappingCollector.setValueBefore("a1", 1l);
		//Exploitable Cycle Recype
		mappingCollector.addConversion(1, "exploitable1", Collections.singletonList("a1"));
		mappingCollector.addConversion(2, "exploitable2", Collections.singletonList("exploitable1"));
		mappingCollector.addConversion(1, "exploitable1", Collections.singletonList("exploitable2"));

		Map<String, Long> values = valueGenerator.generateValues();
		assertEquals(1, getValue(values, "a1"));
		assertEquals(0, getValue(values, "exploitable1"));
		assertEquals(0, getValue(values, "exploitable2"));
	}

	@org.junit.Test
	public void testGenerateValuesCycleRecipeExploit2() throws Exception
	{
		mappingCollector.setValueBefore("a1", 1l);
		//Exploitable Cycle Recype
		mappingCollector.addConversion(1, "exploitable", Collections.singletonList("a1"));
		mappingCollector.addConversion(2, "exploitable", Collections.singletonList("exploitable"));
		mappingCollector.addConversion(1, "b", Collections.singletonList("exploitable"));

		//Not-exploitable Cycle Recype
		mappingCollector.addConversion(1, "notExploitable", Collections.singletonList("a1"));
		mappingCollector.addConversion(2, "notExploitable", Arrays.asList("notExploitable", "notExploitable"));

		Map<String, Long> values = valueGenerator.generateValues();
		assertEquals(1, getValue(values, "a1"));
		assertEquals(0, getValue(values, "exploitable"));
		assertEquals(1, getValue(values, "notExploitable"));
		assertEquals(0, getValue(values, "b"));
	}

	@org.junit.Test
	public void testGenerateValuesCoalToFireChargeWithWildcard() throws Exception {
		String[] logTypes = new String[]{"logA", "logB", "logC"};
		String[] log2Types = new String[]{"log2A", "log2B", "log2C"};
		String[] coalTypes = new String[]{"coal0", "coal1"};

		mappingCollector.setValueBefore("coalore", 0l);
		mappingCollector.setValueBefore("coal0", 128l);
		mappingCollector.setValueBefore("gunpowder", 192l);
		mappingCollector.setValueBefore("blazepowder", 768l);

		for (String logType : logTypes)
		{
			mappingCollector.setValueBefore(logType, 32l);
			mappingCollector.addConversion(1, "log*", Collections.singletonList(logType));
		}
		for (String log2Type : log2Types)
		{
			mappingCollector.setValueBefore(log2Type, 32l);
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
			assertEquals(32, getValue(values, logType));
		}
		assertEquals(32, getValue(values, "log*"));
		assertEquals(128, getValue(values, "coal0"));
		assertEquals(32, getValue(values, "coal1"));
		assertEquals(32, getValue(values, "coal*"));
		assertEquals(330, getValue(values, "firecharge"));
	}

	@org.junit.Test
	public void testGenerateValuesChisel2AntiBlock() throws Exception {
		final String gDust = "glowstone dust";
		final String stone = "stone";

		final String[] dyes = new String[]{"Blue", "Brown", "White", "Other"};
		final int[] dyeValue = new int[]{864, 176, 48, 16};
		for (int i = 0; i < dyes.length; i++)
		{
			mappingCollector.setValueBefore("dye" + dyes[i], (long) dyeValue[i]);
			mappingCollector.addConversion(8, "antiblock" + dyes[i], Arrays.asList(
					"antiblock_all", "antiblock_all", "antiblock_all",
					"antiblock_all", "dye" + dyes[i], "antiblock_all",
					"antiblock_all", "antiblock_all", "antiblock_all"
			));
			mappingCollector.addConversion(1, "antiblock_all", Collections.singletonList("antiblock" + dyes[i]));
		}

		mappingCollector.setValueBefore(gDust, 384l);
		mappingCollector.setValueBefore(stone, 1l);
		mappingCollector.addConversion(8, "antiblockWhite", Arrays.asList(
				stone, stone, stone,
				stone, gDust, stone,
				stone, stone, stone));

		Map<String, Long> values = valueGenerator.generateValues();
		assertEquals((8 + 384)/8, getValue(values, "antiblockWhite"));
		for (int i = 0; i < dyes.length; i++) {
			assertEquals(dyeValue[i], getValue(values, "dye" + dyes[i]));
			if (!dyes[i].equals("White"))
				assertEquals((dyeValue[i] + ((8 + 384)/8)*8)/8, getValue(values, "antiblock" + dyes[i]));
		}
	}

	@org.junit.Test
	public void testGenerateValuesZeroCountIngredientDependency() throws Exception
	{
		mappingCollector.setValueBefore("a", 2l);
		mappingCollector.setValueBefore("b", 3l);
		mappingCollector.setValueBefore("notConsume1", 1l);
		HashMap<String, Integer> ingredients = new HashMap<>();
		ingredients.put("a", 1);
		ingredients.put("b", 1);
		ingredients.put("notConsume1", 0);
		mappingCollector.addConversion(1, "c1", ingredients);
		ingredients.remove("notConsume1");
		ingredients.put("notConsume2", 0);
		mappingCollector.addConversion(1, "c2", ingredients);


		Map<String, Long> values = valueGenerator.generateValues();
		assertEquals(2, getValue(values, "a"));
		assertEquals(3, getValue(values, "b"));
		assertEquals(1, getValue(values, "notConsume1"));
		assertEquals(0, getValue(values, "notConsume2"));
		assertEquals(5, getValue(values, "c1"));
		assertEquals(0, getValue(values, "c2"));
	}


	@org.junit.Test
	public void testGenerateValuesFreeAlternatives() throws Exception
	{
		mappingCollector.setValueBefore("freeWater", Long.MIN_VALUE/* = 'Free' */);
		mappingCollector.setValueBefore("waterBottle", 0l);
		mappingCollector.addConversion(1, "waterGroup", Collections.singletonList("freeWater"));
		mappingCollector.addConversion(1, "waterGroup", Collections.singletonList("waterBottle"));
		mappingCollector.setValueBefore("a", 3l);
		mappingCollector.addConversion(1, "result", Arrays.asList("a", "waterGroup"));

		Map<String, Long> values = valueGenerator.generateValues();
		assertEquals(3, getValue(values, "a"));
		assertEquals(0, getValue(values, "freeWater"));
		assertEquals(0, getValue(values, "waterBottle"));
		assertEquals(0, getValue(values, "waterGroup"));
		assertEquals(3, getValue(values, "result"));
	}

	@org.junit.Test
	public void testGenerateValuesFreeAlternativesWithNegativeIngredients() throws Exception
	{
		mappingCollector.setValueBefore("bucket", 768l);
		mappingCollector.setValueBefore("waterBucket", 768l);
		mappingCollector.setValueBefore("waterBottle", 0l);
		Map<String, Integer> m = new HashMap<>();
		m.put("waterBucket", 1);
		m.put("bucket", -1);
		mappingCollector.addConversion(1, "waterGroup", m);
		mappingCollector.addConversion(1, "waterGroup", Collections.singletonList("waterBottle"));
		mappingCollector.setValueBefore("a", 3l);
		mappingCollector.addConversion(1, "result", Arrays.asList("a", "waterGroup"));

		Map<String, Long> values = valueGenerator.generateValues();
		assertEquals(3, getValue(values, "a"));
		assertEquals(768, getValue(values, "bucket"));
		assertEquals(768, getValue(values, "waterBucket"));
		assertEquals(0, getValue(values, "waterGroup"));
		assertEquals(3, getValue(values, "result"));
	}


	@org.junit.Test
	public void testOverflowWithIngredients() throws Exception
	{
		mappingCollector.setValueBefore("a", Long.MAX_VALUE / 2 + 1);
		mappingCollector.setValueBefore("b", Long.MAX_VALUE / 2 + 1);
		mappingCollector.addConversion(1, "c", Arrays.asList("a", "b"));

		Map<String, Long> values = valueGenerator.generateValues();
		assertEquals(Long.MAX_VALUE / 2 + 1, getValue(values, "a"));
		assertEquals(Long.MAX_VALUE/2+1, getValue(values, "b"));
		assertEquals(0, getValue(values, "c"));
	}

	@org.junit.Test
	public void testOverflowWithAmount() throws Exception
	{
		mappingCollector.setValueBefore("a", Long.MAX_VALUE / 2);
		mappingCollector.addConversion(3, "a", Collections.singletonList("something"));

		Map<String, Long> values = valueGenerator.generateValues();
		assertEquals(Long.MAX_VALUE/2, getValue(values, "a"));
	}

	@org.junit.Test
	public void testOverwriteConversions()
	{
		mappingCollector.setValueBefore("a", 1l);
		mappingCollector.setValueFromConversion(1, "b", Arrays.asList("a", "a", "a"));
		mappingCollector.addConversion(1, "b", Collections.singletonList("a"));
		mappingCollector.addConversion(1, "c", Arrays.asList("b", "b"));
		Map<String, Long> values = valueGenerator.generateValues();
		assertEquals(1, getValue(values, "a"));
		assertEquals(3, getValue(values, "b"));
		assertEquals(6, getValue(values, "c"));

	}

	private static <T, V extends Number> long getValue(Map<T, V> map, T key) {
		V val = map.get(key);
		if (val == null) return 0;
		return val.longValue();
	}
}
