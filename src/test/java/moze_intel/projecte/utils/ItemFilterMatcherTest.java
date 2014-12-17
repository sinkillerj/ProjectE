package moze_intel.projecte.utils;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import org.junit.Before;
import org.junit.Test;


public class ItemFilterMatcherTest {
    ItemFilterMatcher matcher;

    @Test
    public void testMatchesAll() throws Exception {
        assertTrue(matcher.matchesAll("minecraft:iron_pickaxe"));
        assertFalse(matcher.matchesAll("minecraft:stone_pickaxe"));
        assertFalse(matcher.matchesAll("minecraft:stone_axe"));
        assertFalse(matcher.matchesAll("somethingelse"));
    }

    @Test
    public void testMatchesAny() throws Exception {
        assertTrue(matcher.matchesAny("minecraft:iron_pickaxe"));
        assertFalse(matcher.matchesAny("minecraft:stone_axe"));
        assertFalse(matcher.matchesAny("somethingelse"));
    }

    @Before
    public void setUp() throws Exception {

        matcher = new ItemFilterMatcher(new String[]{"minecraft:*_pickaxe", "minecraft:iron_*"});
    }

}