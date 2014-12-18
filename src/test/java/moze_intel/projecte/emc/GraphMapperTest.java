package moze_intel.projecte.emc;

import scala.Int;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        graphMapper.addConversion(1, "b2", Arrays.asList("a1","a1"));

        Map<String,Double> values = graphMapper.generateValues();
        assertEquals(1, getValue(values,"a1"));
        assertEquals(2, getValue(values,"b2"));
        assertEquals(4, getValue(values,"c4"));

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
        assertEquals(4, getValue(values,"c4"));
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

    private static <T,V extends Number> int getValue(Map<T,V> map, T key) {
        V val = map.get(key);
        assertNotNull(val);
        return val.intValue();
    }
}