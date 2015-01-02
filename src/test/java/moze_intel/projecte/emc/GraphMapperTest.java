package moze_intel.projecte.emc;

import scala.Int;

import java.util.*;

import static org.junit.Assert.*;

public class GraphMapperTest {

    @org.junit.Test
    public void testGetOrCreateList() throws Exception {
        Map<String,List<Integer>> map = new HashMap<String, List<Integer>>();
        List<Integer> l1 = GraphMapper.getOrCreateList(map, "abc");
        assertNotNull(l1);
        assertTrue(map.containsKey("abc"));
        List<Integer> l2 = GraphMapper.getOrCreateList(map, "abc");
        assertSame(l1, l2);

    }

    @org.junit.Test
    public void testGenerateValuesSimple() throws Exception {
        GraphMapper<String> graphMapper = new GraphMapper<String>();

        graphMapper.setValue("a1",1, GraphMapper.FixedValue.FixAndInherit);
        graphMapper.addConversion(1, "c4", Arrays.asList("a1", "a1", "a1", "a1"));
        graphMapper.addConversion(1, "b2", Arrays.asList("a1", "a1"));

        Map<String,Double> values = graphMapper.generateValues();
        assertEquals(1, getValue(values,"a1"));
        assertEquals(2, getValue(values,"b2"));
        assertEquals(4, getValue(values, "c4"));

    }

    @org.junit.Test
    public void testGenerateValuesSimpleMultiRecipe() throws Exception {
        GraphMapper<String> graphMapper = new GraphMapper<String>();

        graphMapper.setValue("a1",1, GraphMapper.FixedValue.FixAndInherit);
        //2 Recipes for c4
        graphMapper.addConversion(1, "c4", Arrays.asList("a1", "a1", "a1", "a1"));
        graphMapper.addConversion(2, "c4", Arrays.asList("b2","b2"));
        graphMapper.addConversion(1, "b2", Arrays.asList("a1", "a1"));

        Map<String,Double> values = graphMapper.generateValues();
        assertEquals(1, getValue(values,"a1"));
        assertEquals(2, getValue(values,"b2"));
        assertEquals(2, getValue(values,"c4")); //2 * c4 = 2 * b2 => 2 * (2) = 2 * (2)
    }

    @org.junit.Test
    public void testGenerateValuesSimpleMultiRecipeWithEmptyAlternative() throws Exception {
        GraphMapper<String> graphMapper = new GraphMapper<String>();

        graphMapper.setValue("a1",1, GraphMapper.FixedValue.FixAndInherit);
        //2 Recipes for c4
        graphMapper.addConversion(1, "c4", Arrays.asList("a1", "a1", "a1", "a1"));
        graphMapper.addConversion(1, "c4", new LinkedList<String>());
        graphMapper.addConversion(1, "b2", Arrays.asList("a1", "a1"));

        Map<String,Double> values = graphMapper.generateValues();
        assertEquals(1, getValue(values,"a1"));
        assertEquals(2, getValue(values,"b2"));
        assertEquals(4, getValue(values,"c4")); //2 * c4 = 2 * b2 => 2 * (2) = 2 * (2)
    }

    @org.junit.Test
    public void testGenerateValuesSimpleFixedAfterInherit() throws Exception {
        GraphMapper<String> graphMapper = new GraphMapper<String>();

        graphMapper.setValue("a1",1, GraphMapper.FixedValue.FixAndInherit);
        graphMapper.addConversion(1, "c4", Arrays.asList("a1", "a1", "a1", "a1"));
        graphMapper.addConversion(1, "b2", Arrays.asList("a1","a1"));
        graphMapper.setValue("b2", 20, GraphMapper.FixedValue.FixAfterInherit);

        Map<String,Double> values = graphMapper.generateValues();
        assertEquals(1, getValue(values,"a1"));
        assertEquals(20, getValue(values,"b2"));
        assertEquals(4, getValue(values,"c4"));
    }

    @org.junit.Test
    public void testGenerateValuesSimpleFixedDoNotInherit() throws Exception {
        GraphMapper<String> graphMapper = new GraphMapper<String>();

        graphMapper.setValue("a1",1, GraphMapper.FixedValue.FixAndInherit);
        graphMapper.addConversion(1, "b2", Arrays.asList("a1","a1"));
        graphMapper.addConversion(1, "c4", Arrays.asList("b2","b2"));
        graphMapper.setValue("b2", 20, GraphMapper.FixedValue.FixAndDoNotInherit);

        Map<String,Double> values = graphMapper.generateValues();
        assertEquals(1, getValue(values,"a1"));
        assertEquals(20, getValue(values,"b2"));
        assertEquals(0, getValue(values,"c4"));
    }

    @org.junit.Test
    public void testGenerateValuesSimpleFixedDoNotInheritMultiRecipes() throws Exception {
        GraphMapper<String> graphMapper = new GraphMapper<String>();

        graphMapper.setValue("a1",1, GraphMapper.FixedValue.FixAndInherit);
        graphMapper.addConversion(1, "c",  Arrays.asList("a1", "a1"));
        graphMapper.addConversion(1, "c",  Arrays.asList("a1", "b"));
        graphMapper.setValue("b", 20, GraphMapper.FixedValue.FixAndDoNotInherit);

        Map<String,Double> values = graphMapper.generateValues();
        assertEquals(1, getValue(values,"a1"));
        assertEquals(20, getValue(values,"b"));
        assertEquals(2, getValue(values,"c"));
    }

    @org.junit.Test
    public void testGenerateValuesSimpleSelectMinValue() throws Exception {
        GraphMapper<String> graphMapper = new GraphMapper<String>();

        graphMapper.setValue("a1",1, GraphMapper.FixedValue.FixAndInherit);
        graphMapper.setValue("b2", 2, GraphMapper.FixedValue.FixAndInherit);
        graphMapper.addConversion(1, "c", Arrays.asList("a1","a1"));
        graphMapper.addConversion(1, "c", Arrays.asList("b2", "b2"));

        Map<String,Double> values = graphMapper.generateValues();
        assertEquals(1, getValue(values,"a1"));
        assertEquals(2, getValue(values,"b2"));
        assertEquals(2, getValue(values,"c"));
    }

    @org.junit.Test
    public void testGenerateValuesSimpleSelectMinValueWithDependency() throws Exception {
        GraphMapper<String> graphMapper = new GraphMapper<String>();

        graphMapper.setValue("a1",1, GraphMapper.FixedValue.FixAndInherit);
        graphMapper.setValue("b2",2, GraphMapper.FixedValue.FixAndInherit);
        graphMapper.addConversion(1, "c", Arrays.asList("a1","a1"));
        graphMapper.addConversion(1, "c", Arrays.asList("b2","b2"));
        graphMapper.addConversion(1, "d", Arrays.asList("c","c"));

        Map<String,Double> values = graphMapper.generateValues();
        assertEquals(1, getValue(values,"a1"));
        assertEquals(2, getValue(values,"b2"));
        assertEquals(2, getValue(values,"c"));
        assertEquals(4, getValue(values,"d"));
    }

    @org.junit.Test
    public void testGenerateValuesSimpleWoodToWorkBench() throws Exception {
        GraphMapper<String> graphMapper = new GraphMapper<String>();

        graphMapper.setValue("planks",1, GraphMapper.FixedValue.FixAndInherit);
        graphMapper.addConversion(4, "planks", Arrays.asList("wood"));
        graphMapper.addConversion(1, "workbench", Arrays.asList("planks","planks","planks","planks"));

        Map<String,Double> values = graphMapper.generateValues();
        assertEquals(0,getValue(values,"wood"));
        assertEquals(1, getValue(values,"planks"));
        assertEquals(4, getValue(values,"workbench"));
    }

    @org.junit.Test
    public void testGenerateValuesWood() throws Exception {
        GraphMapper<String> graphMapper = new GraphMapper<String>();

        for (char i: "ABCD".toCharArray()) {
            graphMapper.setValue("wood" + i, 32, GraphMapper.FixedValue.FixAndInherit);
            graphMapper.addConversion(4, "planks"+i, Arrays.asList("wood"+i));
        }

        for (char i: "ABCD".toCharArray()) {
            graphMapper.addConversion(4, "planks"+i, Arrays.asList("wood"));
        }

        for (char i: "ABCD".toCharArray())
            for (char j: "ABCD".toCharArray())
                graphMapper.addConversion(4,"stick",Arrays.asList("planks"+i,"planks"+j));
        graphMapper.addConversion(1, "crafting_table", Arrays.asList("planksA","planksA","planksA","planksA"));
        for (char i: "ABCD".toCharArray())
            for (char j: "ABCD".toCharArray())
                    graphMapper.addConversion(1,"wooden_hoe",Arrays.asList("stick","stick","planks"+i,"planks"+j));

        Map<String,Double> values = graphMapper.generateValues();
        for (char i: "ABCD".toCharArray())
            assertEquals(32,getValue(values,"wood"+i));
        for (char i: "ABCD".toCharArray())
            assertEquals(8, getValue(values,"planks"+i));
        assertEquals(4, getValue(values,"stick"));
        assertEquals(32, getValue(values,"crafting_table"));
        assertEquals(24, getValue(values,"wooden_hoe"));

    }

    @org.junit.Test
    public void testGenerateValuesDeepConversions() throws Exception {
        GraphMapper<String> graphMapper = new GraphMapper<String>();

        graphMapper.setValue("a1",1, GraphMapper.FixedValue.FixAndInherit);
        graphMapper.addConversion(1, "b1", Arrays.asList("a1"));
        graphMapper.addConversion(1, "c1", Arrays.asList("b1"));

        Map<String,Double> values = graphMapper.generateValues();
        assertEquals(1, getValue(values,"a1"));
        assertEquals(1, getValue(values,"b1"));
        assertEquals(1, getValue(values,"c1"));
    }

    @org.junit.Test
    public void testGenerateValuesDeepInvalidConversion() throws Exception {
        GraphMapper<String> graphMapper = new GraphMapper<String>();

        graphMapper.setValue("a1", 1, GraphMapper.FixedValue.FixAndInherit);
        graphMapper.addConversion(1, "b", Arrays.asList("a1", "invalid1"));
        graphMapper.addConversion(1, "invalid1", Arrays.asList("a1", "invalid2"));
        graphMapper.addConversion(1, "invalid2", Arrays.asList("a1", "invalid3"));


        Map<String,Double> values = graphMapper.generateValues();
        assertEquals(1, getValue(values,"a1"));
        assertEquals(0, getValue(values,"b"));
        assertEquals(0, getValue(values,"invalid1"));
        assertEquals(0, getValue(values,"invalid2"));
        assertEquals(0, getValue(values,"invalid3"));
    }

    @org.junit.Test
    public void testGenerateValuesMultiRecipeDeepInvalid() throws Exception {
        GraphMapper<String> graphMapper = new GraphMapper<String>();

        graphMapper.setValue("a1", 1, GraphMapper.FixedValue.FixAndInherit);
        graphMapper.addConversion(1, "b2", Arrays.asList("a1", "a1"));
        graphMapper.addConversion(1, "b2", Arrays.asList("invalid1"));
        graphMapper.addConversion(1, "invalid1", Arrays.asList("a1", "invalid2"));


        Map<String,Double> values = graphMapper.generateValues();
        assertEquals(1, getValue(values,"a1"));
        assertEquals(2, getValue(values,"b2"));
        assertEquals(0, getValue(values,"invalid1"));
        assertEquals(0, getValue(values,"invalid2"));
    }

    @org.junit.Test
    public void testGenerateValuesMultiRecipesInvalidIngredient() throws Exception {
        GraphMapper<String> graphMapper = new GraphMapper<String>();

        graphMapper.setValue("a1", 1, GraphMapper.FixedValue.FixAndInherit);
        graphMapper.addConversion(1, "b2", Arrays.asList("a1", "a1"));
        graphMapper.addConversion(1, "b2", Arrays.asList("invalid"));


        Map<String,Double> values = graphMapper.generateValues();
        assertEquals(1, getValue(values,"a1"));
        assertEquals(2, getValue(values,"b2"));
        assertEquals(0, getValue(values,"invalid"));
    }

    @org.junit.Test
    public void testGenerateValuesCycleRecipe() throws Exception {
        GraphMapper<String> graphMapper = new GraphMapper<String>();

        graphMapper.setValue("a1", 1, GraphMapper.FixedValue.FixAndInherit);
        graphMapper.addConversion(1, "cycle-1", Arrays.asList("a1"));
        graphMapper.addConversion(1, "cycle-2", Arrays.asList("cycle-1"));
        graphMapper.addConversion(1, "cycle-1", Arrays.asList("cycle-2"));


        Map<String,Double> values = graphMapper.generateValues();
        assertEquals(1, getValue(values,"a1"));
        assertEquals(1, getValue(values,"cycle-1"));
        assertEquals(1, getValue(values,"cycle-2"));
    }

    @org.junit.Test
    public void testGenerateValuesBigCycleRecipe() throws Exception {
        GraphMapper<String> graphMapper = new GraphMapper<String>();

        graphMapper.setValue("a1", 1, GraphMapper.FixedValue.FixAndInherit);
        graphMapper.addConversion(1, "cycle-1", Arrays.asList("a1"));
        graphMapper.addConversion(1, "cycle-2", Arrays.asList("cycle-1"));
        graphMapper.addConversion(1, "cycle-3", Arrays.asList("cycle-2"));
        graphMapper.addConversion(1, "cycle-4", Arrays.asList("cycle-3"));
        graphMapper.addConversion(1, "cycle-5", Arrays.asList("cycle-4"));
        graphMapper.addConversion(1, "cycle-1", Arrays.asList("cycle-5"));


        Map<String,Double> values = graphMapper.generateValues();
        assertEquals(1, getValue(values,"a1"));
        assertEquals(1, getValue(values,"cycle-1"));
        assertEquals(1, getValue(values,"cycle-2"));
        assertEquals(1, getValue(values,"cycle-3"));
        assertEquals(1, getValue(values,"cycle-4"));
        assertEquals(1, getValue(values,"cycle-5"));
    }

    @org.junit.Test
    public void testGenerateValuesFuelAndMatter() throws Exception {
        GraphMapper<String> graphMapper = new GraphMapper<String>();
        final String coal = "coal";
        final String aCoal = "alchemicalCoal";
        final String aCoalBlock = "alchemicalCoalBlock";
        final String mFuel = "mobiusFuel";
        final String mFuelBlock = "mobiusFuelBlock";
        final String aFuel = "aeternalisFuel";
        final String aFuelBlock = "aeternalisFuelBlock";
        String repeat;

        graphMapper.setValue(coal, 128, GraphMapper.FixedValue.FixAndInherit);

        graphMapper.addConversion(1, aCoal, Arrays.asList(coal, coal, coal, coal));
        graphMapper.addConversion(4, aCoal , Arrays.asList(mFuel));
        graphMapper.addConversion(9, aCoal, Arrays.asList(aCoalBlock));
         repeat=aCoal;
        graphMapper.addConversion(1, aCoalBlock, Arrays.asList(repeat, repeat, repeat, repeat, repeat, repeat, repeat, repeat, repeat));

        graphMapper.addConversion(1, mFuel, Arrays.asList(aCoal, aCoal, aCoal, aCoal));
        graphMapper.addConversion(4, mFuel , Arrays.asList(aFuel));
        graphMapper.addConversion(9, mFuel, Arrays.asList(mFuelBlock));
        repeat=mFuel;
        graphMapper.addConversion(1, mFuelBlock, Arrays.asList(repeat, repeat, repeat, repeat, repeat, repeat, repeat, repeat, repeat));

        graphMapper.addConversion(1, aFuel, Arrays.asList(mFuel, mFuel, mFuel, mFuel));
        graphMapper.addConversion(9, aFuel, Arrays.asList(aFuelBlock));
        repeat=aFuel;
        graphMapper.addConversion(1, aFuelBlock, Arrays.asList(repeat, repeat, repeat, repeat, repeat, repeat, repeat, repeat, repeat));

        graphMapper.setValue("diamondBlock", 73728, GraphMapper.FixedValue.FixAndInherit);
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


        Map<String,Double> values = graphMapper.generateValues();
        assertEquals(128, getValue(values,coal));
        assertEquals(512, getValue(values,aCoal));
        assertEquals(4608, getValue(values,aCoalBlock));
        assertEquals(2048, getValue(values,mFuel));
        assertEquals(18432, getValue(values,mFuelBlock));
        assertEquals(8192, getValue(values,aFuel));
        assertEquals(73728, getValue(values,aFuelBlock));
        assertEquals(73728, getValue(values,"diamondBlock"));
        assertEquals(139264, getValue(values, dMatter));
        assertEquals(139264, getValue(values, dMatterBlock));
        assertEquals(466944, getValue(values, rMatter));
        assertEquals(466944, getValue(values, rMatterBlock));
    }

    @org.junit.Test
    public void testGenerateValuesWool() throws Exception {
        GraphMapper<String> graphMapper = new GraphMapper<String>();

        final String[] dyes = new String[]{"Blue", "Brown", "White", "Other"};
        final int[] dyeValue = new int[] {864, 176, 48, 16};
        for (int i = 0; i < dyes.length; i++) {
            graphMapper.setValue("dye"+dyes[i], dyeValue[i], GraphMapper.FixedValue.FixAndInherit);
            graphMapper.addConversion(1, "wool" + dyes[i], Arrays.asList("woolWhite", "dye"+dyes[i]));
        }
        graphMapper.setValue("string", 12, GraphMapper.FixedValue.FixAndInherit);
        graphMapper.addConversion(1, "woolWhite", Arrays.asList("string", "string", "string", "string"));

        graphMapper.setValue("stick", 4, GraphMapper.FixedValue.FixAndInherit);
        graphMapper.setValue("plank", 8, GraphMapper.FixedValue.FixAndInherit);
        for (String dye: dyes) {
            graphMapper.addConversion(1,"bed", Arrays.asList("plank","plank","plank", "wool"+dye,"wool"+dye,"wool"+dye));
            graphMapper.addConversion(3,"carpet"+dye, Arrays.asList("wool"+dye,"wool"+dye));
            graphMapper.addConversion(1,"painting", Arrays.asList("wool"+dye, "stick","stick","stick","stick","stick","stick","stick","stick"));
        }

        Map<String,Double> values = graphMapper.generateValues();
        for (int i = 0; i < dyes.length; i++) {
            assertEquals(dyeValue[i], getValue(values,"dye"+dyes[i]));
        }
        assertEquals(12, getValue(values,"string"));
        assertEquals(48, getValue(values,"woolWhite"));
        assertEquals(224, getValue(values,"woolBrown"));
        assertEquals(912, getValue(values,"woolBlue"));
        assertEquals(64, getValue(values,"woolOther"));

        assertEquals(32, getValue(values,"carpetWhite"));
        assertEquals(149, getValue(values,"carpetBrown"));
        assertEquals(608, getValue(values,"carpetBlue"));
        assertEquals(42, getValue(values,"carpetOther"));

        assertEquals(168, getValue(values,"bed"));
        assertEquals(80, getValue(values,"painting"));
    }

    private static <T,V extends Number> int getValue(Map<T,V> map, T key) {
        V val = map.get(key);
        if (val == null) return 0;
        return val.intValue();
    }
}