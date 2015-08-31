package moze_intel.projecte.emc;

import moze_intel.projecte.emc.arithmetics.HiddenFractionArithmetic;
import moze_intel.projecte.emc.valuetranslators.FractionToIntegerTranslator;

import com.google.common.collect.ImmutableMap;
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

	public GraphMapperTest(GraphMapper<String, Integer> graphMapper) {
		this.graphMapper = graphMapper;
	}*/
	@Before
	public void setup() {
		//graphMapper = new SimpleGraphMapper<String, Integer>(new IntArithmetic());
		graphMapper = new FractionToIntegerTranslator<String>(new SimpleGraphMapper<String, Fraction>(new HiddenFractionArithmetic()));
	}

	@Rule
	public Timeout timeout = new Timeout(3000);

	public IValueGenerator<String, Integer> graphMapper;

	@org.junit.Test
	public void testGetOrCreateList() throws Exception {
		Map<String, List<Integer>> map = new HashMap<String, List<Integer>>();
		List<Integer> l1 = GraphMapper.getOrCreateList(map, "abc");
		assertNotNull(l1);
		assertTrue(map.containsKey("abc"));
		List<Integer> l2 = GraphMapper.getOrCreateList(map, "abc");
		assertSame(l1, l2);
	}

	@org.junit.Test
	public void testGenerateValuesSimple() throws Exception {
		graphMapper.setValueBefore("a1", 1);
		graphMapper.addConversion(1, "c4", Arrays.asList("a1", "a1", "a1", "a1"));
		graphMapper.addConversion(1, "b2", Arrays.asList("a1", "a1"));

		Map<String, Integer> values = graphMapper.generateValues();
		assertEquals(1, getValue(values, "a1"));
		assertEquals(2, getValue(values, "b2"));
		assertEquals(4, getValue(values, "c4"));

	}

	@org.junit.Test
	public void testGenerateValuesSimpleMultiRecipe() throws Exception {
		graphMapper.setValueBefore("a1", 1);
		//2 Recipes for c4
		graphMapper.addConversion(1, "c4", Arrays.asList("a1", "a1", "a1", "a1"));
		graphMapper.addConversion(2, "c4", Arrays.asList("b2", "b2"));
		graphMapper.addConversion(1, "b2", Arrays.asList("a1", "a1"));

		Map<String, Integer> values = graphMapper.generateValues();
		assertEquals(1, getValue(values, "a1"));
		assertEquals(2, getValue(values, "b2"));
		assertEquals(2, getValue(values, "c4")); //2 * c4 = 2 * b2 => 2 * (2) = 2 * (2)
	}

	@org.junit.Test
	public void testGenerateValuesSimpleMultiRecipeWithEmptyAlternative() throws Exception {
		graphMapper.setValueBefore("a1", 1);
		//2 Recipes for c4
		graphMapper.addConversion(1, "c4", Arrays.asList("a1", "a1", "a1", "a1"));
		graphMapper.addConversion(1, "c4", new LinkedList<String>());
		graphMapper.addConversion(1, "b2", Arrays.asList("a1", "a1"));

		Map<String, Integer> values = graphMapper.generateValues();
		assertEquals(1, getValue(values, "a1"));
		assertEquals(2, getValue(values, "b2"));
		assertEquals(4, getValue(values, "c4")); //2 * c4 = 2 * b2 => 2 * (2) = 2 * (2)
	}

	@org.junit.Test
	public void testGenerateValuesSimpleFixedAfterInherit() throws Exception {
		graphMapper.setValueBefore("a1", 1);
		graphMapper.addConversion(1, "c4", Arrays.asList("a1", "a1", "a1", "a1"));
		graphMapper.addConversion(1, "b2", Arrays.asList("a1", "a1"));
		graphMapper.setValueAfter("b2", 20);

		Map<String, Integer> values = graphMapper.generateValues();
		assertEquals(1, getValue(values, "a1"));
		assertEquals(20, getValue(values, "b2"));
		assertEquals(4, getValue(values, "c4"));
	}

	@org.junit.Test
	public void testGenerateValuesSimpleFixedDoNotInherit() throws Exception {
		graphMapper.setValueBefore("a1", 1);
		graphMapper.addConversion(1, "b2", Arrays.asList("a1", "a1"));
		graphMapper.addConversion(1, "c4", Arrays.asList("b2", "b2"));
		graphMapper.setValueBefore("b2", 0);
		graphMapper.setValueAfter("b2", 20);

		Map<String, Integer> values = graphMapper.generateValues();
		assertEquals(1, getValue(values, "a1"));
		assertEquals(20, getValue(values, "b2"));
		assertEquals(0, getValue(values, "c4"));
	}

	@org.junit.Test
	public void testGenerateValuesSimpleFixedDoNotInheritMultiRecipes() throws Exception
	{
		graphMapper.setValueBefore("a1", 1);
		graphMapper.addConversion(1, "c", Arrays.asList("a1", "a1"));
		graphMapper.addConversion(1, "c", Arrays.asList("a1", "b"));
		graphMapper.setValueBefore("b", 0);
		graphMapper.setValueAfter("b", 20);

		Map<String, Integer> values = graphMapper.generateValues();
		assertEquals(1, getValue(values, "a1"));
		assertEquals(20, getValue(values, "b"));
		assertEquals(2, getValue(values, "c"));
	}

	@org.junit.Test
	public void testGenerateValuesSimpleSelectMinValue() throws Exception
	{
		graphMapper.setValueBefore("a1", 1);
		graphMapper.setValueBefore("b2", 2);
		graphMapper.addConversion(1, "c", Arrays.asList("a1", "a1"));
		graphMapper.addConversion(1, "c", Arrays.asList("b2", "b2"));

		Map<String, Integer> values = graphMapper.generateValues();
		assertEquals(1, getValue(values, "a1"));
		assertEquals(2, getValue(values, "b2"));
		assertEquals(2, getValue(values, "c"));
	}

	@org.junit.Test
	public void testGenerateValuesSimpleSelectMinValueWithDependency() throws Exception
	{
		graphMapper.setValueBefore("a1", 1);
		graphMapper.setValueBefore("b2", 2);
		graphMapper.addConversion(1, "c", Arrays.asList("a1", "a1"));
		graphMapper.addConversion(1, "c", Arrays.asList("b2", "b2"));
		graphMapper.addConversion(1, "d", Arrays.asList("c", "c"));

		Map<String, Integer> values = graphMapper.generateValues();
		assertEquals(1, getValue(values, "a1"));
		assertEquals(2, getValue(values, "b2"));
		assertEquals(2, getValue(values, "c"));
		assertEquals(4, getValue(values, "d"));
	}

	@org.junit.Test
	public void testGenerateValuesSimpleWoodToWorkBench() throws Exception
	{
		graphMapper.setValueBefore("planks", 1);
		graphMapper.addConversion(4, "planks", Arrays.asList("wood"));
		graphMapper.addConversion(1, "workbench", Arrays.asList("planks", "planks", "planks", "planks"));

		Map<String, Integer> values = graphMapper.generateValues();
		assertEquals(0, getValue(values, "wood"));
		assertEquals(1, getValue(values, "planks"));
		assertEquals(4, getValue(values, "workbench"));
	}

	@org.junit.Test
	public void testGenerateValuesWood() throws Exception {
		for (char i : "ABCD".toCharArray())
		{
			graphMapper.setValueBefore("wood" + i, 32);
			graphMapper.addConversion(4, "planks" + i, Arrays.asList("wood" + i));
		}

		for (char i : "ABCD".toCharArray()) {
			graphMapper.addConversion(4, "planks" + i, Arrays.asList("wood"));
		}

		for (char i : "ABCD".toCharArray())
			for (char j : "ABCD".toCharArray())
				graphMapper.addConversion(4, "stick", Arrays.asList("planks" + i, "planks" + j));
		graphMapper.addConversion(1, "crafting_table", Arrays.asList("planksA", "planksA", "planksA", "planksA"));
		for (char i : "ABCD".toCharArray())
			for (char j : "ABCD".toCharArray())
				graphMapper.addConversion(1, "wooden_hoe", Arrays.asList("stick", "stick", "planks" + i, "planks" + j));

		Map<String, Integer> values = graphMapper.generateValues();
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
		graphMapper.setValueBefore("a1", 1);
		graphMapper.addConversion(1, "b1", Arrays.asList("a1"));
		graphMapper.addConversion(1, "c1", Arrays.asList("b1"));

		Map<String, Integer> values = graphMapper.generateValues();
		assertEquals(1, getValue(values, "a1"));
		assertEquals(1, getValue(values, "b1"));
		assertEquals(1, getValue(values, "c1"));
	}

	@org.junit.Test
	public void testGenerateValuesDeepInvalidConversion() throws Exception
	{
		graphMapper.setValueBefore("a1", 1);
		graphMapper.addConversion(1, "b", Arrays.asList("a1", "invalid1"));
		graphMapper.addConversion(1, "invalid1", Arrays.asList("a1", "invalid2"));
		graphMapper.addConversion(1, "invalid2", Arrays.asList("a1", "invalid3"));


		Map<String, Integer> values = graphMapper.generateValues();
		assertEquals(1, getValue(values, "a1"));
		assertEquals(0, getValue(values, "b"));
		assertEquals(0, getValue(values, "invalid1"));
		assertEquals(0, getValue(values, "invalid2"));
		assertEquals(0, getValue(values, "invalid3"));
	}

	@org.junit.Test
	public void testGenerateValuesMultiRecipeDeepInvalid() throws Exception
	{
		graphMapper.setValueBefore("a1", 1);
		graphMapper.addConversion(1, "b2", Arrays.asList("a1", "a1"));
		graphMapper.addConversion(1, "b2", Arrays.asList("invalid1"));
		graphMapper.addConversion(1, "invalid1", Arrays.asList("a1", "invalid2"));


		Map<String, Integer> values = graphMapper.generateValues();
		assertEquals(1, getValue(values, "a1"));
		assertEquals(2, getValue(values, "b2"));
		assertEquals(0, getValue(values, "invalid1"));
		assertEquals(0, getValue(values, "invalid2"));
	}

	@org.junit.Test
	public void testGenerateValuesMultiRecipesInvalidIngredient() throws Exception
	{
		graphMapper.setValueBefore("a1", 1);
		graphMapper.addConversion(1, "b2", Arrays.asList("a1", "a1"));
		graphMapper.addConversion(1, "b2", Arrays.asList("invalid"));


		Map<String, Integer> values = graphMapper.generateValues();
		assertEquals(1, getValue(values, "a1"));
		assertEquals(2, getValue(values, "b2"));
		assertEquals(0, getValue(values, "invalid"));
	}

	@org.junit.Test
	public void testGenerateValuesCycleRecipe() throws Exception
	{
		graphMapper.setValueBefore("a1", 1);
		graphMapper.addConversion(1, "cycle-1", Arrays.asList("a1"));
		graphMapper.addConversion(1, "cycle-2", Arrays.asList("cycle-1"));
		graphMapper.addConversion(1, "cycle-1", Arrays.asList("cycle-2"));


		Map<String, Integer> values = graphMapper.generateValues();
		assertEquals(1, getValue(values, "a1"));
		assertEquals(1, getValue(values, "cycle-1"));
		assertEquals(1, getValue(values, "cycle-2"));
	}

	@org.junit.Test
	public void testGenerateValuesBigCycleRecipe() throws Exception
	{
		graphMapper.setValueBefore("a1", 1);
		graphMapper.addConversion(1, "cycle-1", Arrays.asList("a1"));
		graphMapper.addConversion(1, "cycle-2", Arrays.asList("cycle-1"));
		graphMapper.addConversion(1, "cycle-3", Arrays.asList("cycle-2"));
		graphMapper.addConversion(1, "cycle-4", Arrays.asList("cycle-3"));
		graphMapper.addConversion(1, "cycle-5", Arrays.asList("cycle-4"));
		graphMapper.addConversion(1, "cycle-1", Arrays.asList("cycle-5"));


		Map<String, Integer> values = graphMapper.generateValues();
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

		graphMapper.setValueBefore(coal, 128);

		graphMapper.addConversion(1, aCoal, Arrays.asList(coal, coal, coal, coal));
		graphMapper.addConversion(4, aCoal, Arrays.asList(mFuel));
		graphMapper.addConversion(9, aCoal, Arrays.asList(aCoalBlock));
		repeat = aCoal;
		graphMapper.addConversion(1, aCoalBlock, Arrays.asList(repeat, repeat, repeat, repeat, repeat, repeat, repeat, repeat, repeat));

		graphMapper.addConversion(1, mFuel, Arrays.asList(aCoal, aCoal, aCoal, aCoal));
		graphMapper.addConversion(4, mFuel, Arrays.asList(aFuel));
		graphMapper.addConversion(9, mFuel, Arrays.asList(mFuelBlock));
		repeat = mFuel;
		graphMapper.addConversion(1, mFuelBlock, Arrays.asList(repeat, repeat, repeat, repeat, repeat, repeat, repeat, repeat, repeat));

		graphMapper.addConversion(1, aFuel, Arrays.asList(mFuel, mFuel, mFuel, mFuel));
		graphMapper.addConversion(9, aFuel, Arrays.asList(aFuelBlock));
		repeat = aFuel;
		graphMapper.addConversion(1, aFuelBlock, Arrays.asList(repeat, repeat, repeat, repeat, repeat, repeat, repeat, repeat, repeat));

		graphMapper.setValueBefore("diamondBlock", 73728);
		final String dMatter = "darkMatter";
		final String dMatterBlock = "darkMatterBlock";

		graphMapper.addConversion(1, dMatter, Arrays.asList(aFuel, aFuel, aFuel, aFuel, aFuel, aFuel, aFuel, aFuel, "diamondBlock"));
		graphMapper.addConversion(1, dMatter, Arrays.asList(dMatterBlock));
		graphMapper.addConversion(4, dMatterBlock, Arrays.asList(dMatter, dMatter, dMatter, dMatter));

		final String rMatter = "redMatter";
		final String rMatterBlock = "redMatterBlock";
		graphMapper.addConversion(1, rMatter, Arrays.asList(aFuel, aFuel, aFuel, dMatter, dMatter, dMatter, aFuel, aFuel, aFuel));
		graphMapper.addConversion(1, rMatter, Arrays.asList(rMatterBlock));
		graphMapper.addConversion(4, rMatterBlock, Arrays.asList(rMatter, rMatter, rMatter, rMatter));


		Map<String, Integer> values = graphMapper.generateValues();
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
			graphMapper.setValueBefore("dye" + dyes[i], dyeValue[i]);
			graphMapper.addConversion(1, "wool" + dyes[i], Arrays.asList("woolWhite", "dye" + dyes[i]));
		}
		graphMapper.setValueBefore("string", 12);
		graphMapper.addConversion(1, "woolWhite", Arrays.asList("string", "string", "string", "string"));

		graphMapper.setValueBefore("stick", 4);
		graphMapper.setValueBefore("plank", 8);
		for (String dye : dyes) {
			graphMapper.addConversion(1, "bed", Arrays.asList("plank", "plank", "plank", "wool" + dye, "wool" + dye, "wool" + dye));
			graphMapper.addConversion(3, "carpet" + dye, Arrays.asList("wool" + dye, "wool" + dye));
			graphMapper.addConversion(1, "painting", Arrays.asList("wool" + dye, "stick", "stick", "stick", "stick", "stick", "stick", "stick", "stick"));
		}

		Map<String, Integer> values = graphMapper.generateValues();
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
		graphMapper.setValueBefore("somethingElse", 9);
		graphMapper.setValueBefore("container", 23);
		graphMapper.setValueBefore("fluid", 17);
		graphMapper.addConversion(1, "filledContainer", Arrays.asList("container", "fluid"));

		//Recipe that only consumes fluid:
		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("container", -1);
		map.put("filledContainer", 1);
		map.put("somethingElse", 2);
		graphMapper.addConversion(1, "fluidCraft", map);

		Map<String, Integer> values = graphMapper.generateValues();
		assertEquals(9, getValue(values, "somethingElse"));
		assertEquals(23, getValue(values, "container"));
		assertEquals(17, getValue(values, "fluid"));
		assertEquals(17 + 23, getValue(values, "filledContainer"));
		assertEquals(17 + 2 * 9, getValue(values, "fluidCraft"));

	}

	@org.junit.Test
	public void testGenerateValuesWaterBucketRecipe() throws Exception
	{
		graphMapper.setValueBefore("somethingElse", 9);
		graphMapper.setValueBefore("container", 23);
		graphMapper.setValueBefore("fluid", Integer.MIN_VALUE);
		graphMapper.addConversion(1, "filledContainer", Arrays.asList("container", "fluid"));

		//Recipe that only consumes fluid:
		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("container", -1);
		map.put("filledContainer", 1);
		map.put("somethingElse", 2);
		graphMapper.addConversion(1, "fluidCraft", map);

		Map<String, Integer> values = graphMapper.generateValues();
		assertEquals(9, getValue(values, "somethingElse"));
		assertEquals(23, getValue(values, "container"));
		assertEquals(0, getValue(values, "fluid"));
		assertEquals(23, getValue(values, "filledContainer"));
		assertEquals(2 * 9, getValue(values, "fluidCraft"));

	}

	@org.junit.Test
	public void testGenerateValuesCycleRecipeExploit() throws Exception
	{
		graphMapper.setValueBefore("a1", 1);
		//Exploitable Cycle Recype
		graphMapper.addConversion(1, "exploitable", Arrays.asList("a1"));
		graphMapper.addConversion(2, "exploitable", Arrays.asList("exploitable"));

		//Not-exploitable Cycle Recype
		graphMapper.addConversion(1, "notExploitable", Arrays.asList("a1"));
		graphMapper.addConversion(2, "notExploitable", Arrays.asList("notExploitable", "notExploitable"));

		Map<String, Integer> values = graphMapper.generateValues();
		assertEquals(1, getValue(values, "a1"));
		assertEquals(0, getValue(values, "exploitable"));
		assertEquals(1, getValue(values, "notExploitable"));
	}

	@org.junit.Test
	public void testGenerateValuesDelayedCycleRecipeExploit() throws Exception
	{
		graphMapper.setValueBefore("a1", 1);
		//Exploitable Cycle Recype
		graphMapper.addConversion(1, "exploitable1", Arrays.asList("a1"));
		graphMapper.addConversion(2, "exploitable2", Arrays.asList("exploitable1"));
		graphMapper.addConversion(1, "exploitable1", Arrays.asList("exploitable2"));

		Map<String, Integer> values = graphMapper.generateValues();
		assertEquals(1, getValue(values, "a1"));
		assertEquals(0, getValue(values, "exploitable1"));
		assertEquals(0, getValue(values, "exploitable2"));
	}

	@org.junit.Test
	public void testGenerateValuesCycleRecipeExploit2() throws Exception
	{
		graphMapper.setValueBefore("a1", 1);
		//Exploitable Cycle Recype
		graphMapper.addConversion(1, "exploitable", Arrays.asList("a1"));
		graphMapper.addConversion(2, "exploitable", Arrays.asList("exploitable"));
		graphMapper.addConversion(1, "b", Arrays.asList("exploitable"));

		//Not-exploitable Cycle Recype
		graphMapper.addConversion(1, "notExploitable", Arrays.asList("a1"));
		graphMapper.addConversion(2, "notExploitable", Arrays.asList("notExploitable", "notExploitable"));

		Map<String, Integer> values = graphMapper.generateValues();
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

		graphMapper.setValueBefore("coalore", 0);
		graphMapper.setValueBefore("coal0", 128);
		graphMapper.setValueBefore("gunpowder", 192);
		graphMapper.setValueBefore("blazepowder", 768);

		for (String logType : logTypes)
		{
			graphMapper.setValueBefore(logType, 32);
			graphMapper.addConversion(1, "log*", Arrays.asList(logType));
		}
		for (String log2Type : log2Types)
		{
			graphMapper.setValueBefore(log2Type, 32);
			graphMapper.addConversion(1, "log2*", Arrays.asList(log2Type));
		}
		graphMapper.addConversion(1, "coal1", Arrays.asList("log*"));
		for (String coalType : coalTypes) {
			graphMapper.addConversion(1, "coal*", Arrays.asList(coalType));
			graphMapper.addConversion(3, "firecharge", Arrays.asList(coalType, "gunpowder", "blazepowder"));
		}
		graphMapper.addConversion(1, "firecharge*", Arrays.asList("firecharge"));
		Map<String, Integer> m = new HashMap<String, Integer>();
		m.put("coal0", 9);
		graphMapper.addConversion(1, "coalblock", m);

		m.clear();
		//Philosophers stone smelting 7xCoalOre -> 7xCoal
		m.put("coalore", 7);
		m.put("coal*", 1);
		graphMapper.addConversion(7, "coal0", m);

		m.clear();
		//Philosophers stone smelting logs
		m.put("log*", 7);
		m.put("coal*", 1);
		graphMapper.addConversion(7, "coal1", m);

		m.clear();
		//Philosophers stone smelting log2s
		m.put("log2*", 7);
		m.put("coal*", 1);
		graphMapper.addConversion(7, "coal1", m);


		//Smelting single coal ore
		graphMapper.addConversion(1, "coal0", Arrays.asList("coalore"));
		//Coal Block
		graphMapper.addConversion(9, "coal0", Arrays.asList("coalblock"));

		Map<String, Integer> values = graphMapper.generateValues();
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
			graphMapper.setValueBefore("dye" + dyes[i], dyeValue[i]);
			graphMapper.addConversion(8, "antiblock" + dyes[i], Arrays.asList(
					"antiblock_all", "antiblock_all", "antiblock_all",
					"antiblock_all", "dye" + dyes[i], "antiblock_all",
					"antiblock_all", "antiblock_all", "antiblock_all"
			));
			graphMapper.addConversion(1, "antiblock_all", Arrays.asList("antiblock" + dyes[i]));
		}

		graphMapper.setValueBefore(gDust, 384);
		graphMapper.setValueBefore(stone, 1);
		graphMapper.addConversion(8, "antiblockWhite", Arrays.asList(
				stone, stone, stone,
				stone, gDust, stone,
				stone, stone, stone));

		Map<String, Integer> values = graphMapper.generateValues();
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
		graphMapper.setValueBefore("a", 2);
		graphMapper.setValueBefore("b", 3);
		graphMapper.setValueBefore("notConsume1", 1);
		HashMap<String, Integer> ingredients = new HashMap<String, Integer>();
		ingredients.put("a", 1);
		ingredients.put("b", 1);
		ingredients.put("notConsume1", 0);
		graphMapper.addConversion(1, "c1", ingredients);
		ingredients.remove("notConsume1");
		ingredients.put("notConsume2", 0);
		graphMapper.addConversion(1, "c2", ingredients);


		Map<String, Integer> values = graphMapper.generateValues();
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
		graphMapper.setValueBefore("freeWater", Integer.MIN_VALUE/* = 'Free' */);
		graphMapper.setValueBefore("waterBottle", 0);
		graphMapper.addConversion(1, "waterGroup", Arrays.asList("freeWater"));
		graphMapper.addConversion(1, "waterGroup", Arrays.asList("waterBottle"));
		graphMapper.setValueBefore("a", 3);
		graphMapper.addConversion(1, "result", Arrays.asList("a", "waterGroup"));

		Map<String, Integer> values = graphMapper.generateValues();
		assertEquals(3, getValue(values, "a"));
		assertEquals(0, getValue(values, "freeWater"));
		assertEquals(0, getValue(values, "waterBottle"));
		assertEquals(0, getValue(values, "waterGroup"));
		assertEquals(3, getValue(values, "result"));
	}

	@org.junit.Test
	public void testGenerateValuesFreeAlternativesWithNegativeIngredients() throws Exception
	{
		graphMapper.setValueBefore("bucket", 768);
		graphMapper.setValueBefore("waterBucket", 768);
		graphMapper.setValueBefore("waterBottle", 0);
		Map<String, Integer> m = new HashMap<String, Integer>();
		m.put("waterBucket", 1);
		m.put("bucket", -1);
		graphMapper.addConversion(1, "waterGroup", m);
		graphMapper.addConversion(1, "waterGroup", Arrays.asList("waterBottle"));
		graphMapper.setValueBefore("a", 3);
		graphMapper.addConversion(1, "result", Arrays.asList("a", "waterGroup"));

		Map<String, Integer> values = graphMapper.generateValues();
		assertEquals(3, getValue(values, "a"));
		assertEquals(768, getValue(values, "bucket"));
		assertEquals(768, getValue(values, "waterBucket"));
		assertEquals(0, getValue(values, "waterGroup"));
		assertEquals(3, getValue(values, "result"));
	}


	@org.junit.Test
	public void testOverflowWithIngredients() throws Exception
	{
		graphMapper.setValueBefore("a", Integer.MAX_VALUE / 2 + 1);
		graphMapper.setValueBefore("b", Integer.MAX_VALUE / 2 + 1);
		graphMapper.addConversion(1, "c", Arrays.asList("a", "b"));

		Map<String, Integer> values = graphMapper.generateValues();
		assertEquals(Integer.MAX_VALUE / 2 + 1, getValue(values, "a"));
		assertEquals(Integer.MAX_VALUE/2+1, getValue(values, "b"));
		assertEquals(0, getValue(values, "c"));
	}

	@org.junit.Test
	public void testOverflowWithAmount() throws Exception
	{
		graphMapper.setValueBefore("a", Integer.MAX_VALUE / 2);
		graphMapper.addConversion(3, "a", Arrays.asList("something"));

		Map<String, Integer> values = graphMapper.generateValues();
		assertEquals(Integer.MAX_VALUE/2, getValue(values, "a"));
	}

	@org.junit.Test
	public void testOverwriteConversions()
	{
		graphMapper.setValueBefore("a", 1);
		graphMapper.setValueFromConversion(1, "b", Arrays.asList("a", "a", "a"));
		graphMapper.addConversion(1, "b", Arrays.asList("a"));
		graphMapper.addConversion(1, "c", Arrays.asList("b", "b"));
		Map<String, Integer> values = graphMapper.generateValues();
		assertEquals(1, getValue(values, "a"));
		assertEquals(3, getValue(values, "b"));
		assertEquals(6, getValue(values, "c"));

	}

	@org.junit.Test
	public void testNegativeIngredientProblem()
	{
		//negativeIngredient has 10 emc at first - this is ued for "r".
		//Later the negativeIngredient EMC goes down to 5, but "r" is not updated
		graphMapper.setValueBefore("a", 10);
		graphMapper.addConversion(1, "negativeIngredient", Arrays.asList("a"));
		graphMapper.addConversion(2, "b", Arrays.asList("a"));
		graphMapper.addConversion(1, "negativeIngredient", Arrays.asList("b"));
		graphMapper.addConversion(1, "r", ImmutableMap.of("a", 2, "negativeIngredient", -1));

		Map<String, Integer> values = graphMapper.generateValues();
		assertEquals(10, getValue(values, "a"));
		assertEquals(5, getValue(values, "b"));
		assertEquals(5, getValue(values, "negativeIngredient"));
		assertEquals(2*10 - 5, getValue(values, "r"));
	}

	private static <T, V extends Number> int getValue(Map<T, V> map, T key) {
		V val = map.get(key);
		if (val == null) return 0;
		return val.intValue();
	}
}
