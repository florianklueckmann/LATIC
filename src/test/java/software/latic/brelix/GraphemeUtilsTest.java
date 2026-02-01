package software.latic.brelix;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GraphemeUtilsTest {

    @Test
    void testContainsDehnungsH() {
        assertTrue(GraphemeUtils.containsDehnungsH("bahn"));
        assertTrue(GraphemeUtils.containsDehnungsH("uhr"));
        assertTrue(GraphemeUtils.containsDehnungsH("ähre"));
        assertTrue(GraphemeUtils.containsDehnungsH("ohne"));
        assertTrue(GraphemeUtils.containsDehnungsH("ihm"));
        assertTrue(GraphemeUtils.containsDehnungsH("reh"), "h at end of word");

        assertFalse(GraphemeUtils.containsDehnungsH("halt"), "h at start is not Dehnungs-h");
        assertFalse(GraphemeUtils.containsDehnungsH("bach"), "ch is not Dehnungs-h");
        assertFalse(GraphemeUtils.containsDehnungsH("ich"), "ch is not Dehnungs-h");
        assertFalse(GraphemeUtils.containsDehnungsH("haus"), "h at start is not Dehnungs-h");
        assertFalse(GraphemeUtils.containsDehnungsH("sehen"), "h between vowels is silbentrennendes h, not Dehnungs-h in this logic");
        assertFalse(GraphemeUtils.containsDehnungsH("gehen"), "h between vowels is silbentrennendes h, not Dehnungs-h in this logic");
    }

    @Test
    void testCountDehnungsH() {
        assertEquals(1, GraphemeUtils.countDehnungsH("bahn"));
        assertEquals(0, GraphemeUtils.countDehnungsH("sehen"));
        assertEquals(0, GraphemeUtils.countDehnungsH("bach"));
        assertEquals(0, GraphemeUtils.countDehnungsH("schach"));
        
        // Multiple Dehnungs-h (rare but for testing)
        assertEquals(2, GraphemeUtils.countDehnungsH("bahn-uhr")); 
    }

    @Test
    void testEdgeCases() {
        assertFalse(GraphemeUtils.containsDehnungsH(null));
        assertFalse(GraphemeUtils.containsDehnungsH(""));
        assertEquals(0, GraphemeUtils.countDehnungsH(null));
        assertEquals(0, GraphemeUtils.countDehnungsH(""));
    }
}
