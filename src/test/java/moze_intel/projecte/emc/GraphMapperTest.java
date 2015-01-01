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

    private static <T,V extends Number> int getValue(Map<T,V> map, T key) {
        V val = map.get(key);
        if (val == null) return 0;
        return val.intValue();
    }
}