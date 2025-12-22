package software.latic.brelix;

import edu.stanford.nlp.simple.Document;
import org.junit.jupiter.api.Test;
import software.latic.item.GermanTextItemData;
import software.latic.item.TextItemData;

import static org.junit.jupiter.api.Assertions.*;

public class BrelixAnalyzerTest {

    @Test
    void testBrelixAnalysis() {
        String text = "Dies ist ein Test. Der Herbst ist da. Wir gehen auf der Strasse.";
        Document doc = new Document(text);
        TextItemData data = new GermanTextItemData(text);
        
        // Simuliere einige Daten
        data.setWordCount(13); 
        data.setSentenceCount(3);
        data.setAverageSentenceLengthWords(4.33);
        data.setPagesCount(1);
        data.setFontSizeMm(6.0);
        data.setTypeTokenRatio(0.8);
        
        BrelixAnalyzer.getInstance().analyze(data, doc);
        
        assertTrue(data.getLixPlusScore() > 0, "LIX Plus should be > 0");
        assertTrue(data.getBrelix0Score() > 0, "BRELIX 0 should be > 0");
        assertTrue(data.getBrelix1Score() > 0, "BRELIX 1 should be > 0");
        assertTrue(data.getBrelix2Score() > 0, "BRELIX 2 should be > 0");
        assertTrue(data.getBrelix3Score() > 0, "BRELIX 3 should be > 0");
        assertTrue(data.getBrelix4Score() > 0, "BRELIX 4 should be > 0");
        assertTrue(data.getBrelix5Score() > 0, "BRELIX 5 should be > 0");
        
        System.out.println("BRELIX 0: " + data.getBrelix0Score());
        System.out.println("BRELIX 1: " + data.getBrelix1Score());
        System.out.println("BRELIX 2: " + data.getBrelix2Score());
        System.out.println("BRELIX 3: " + data.getBrelix3Score());
        System.out.println("BRELIX 4: " + data.getBrelix4Score());
        System.out.println("BRELIX 5: " + data.getBrelix5Score());
    }

    @Test
    void testMultiGraphems() {
        BrelixAnalyzer analyzer = BrelixAnalyzer.getInstance();
        assertEquals(2, analyzer.countMultiGraphems("schach"));
        assertEquals(4, analyzer.countMultiGraphems("schachspiel")); // sch, ch, sp, ie
        assertEquals(1, analyzer.countMultiGraphems("bahn"), "Dehnungs-h should count as 1");
        assertEquals(0, analyzer.countMultiGraphems("halt"), "h at start is not Dehnungs-h");
    }

    @Test
    void testSubordinateClauses() {
        // Use English for the test document to ensure the default parser works as expected
        String text = "I know that you are here.";
        Document doc = new Document(text);
        BrelixAnalyzer analyzer = BrelixAnalyzer.getInstance();
        int clauses = analyzer.countSubordinateClauses(doc);
        // "that you are here" should be identified as SBAR
        assertTrue(clauses >= 1, "Should detect at least one subordinate clause in English test sentence");
    }

    @Test
    void testMultiGraphemsMinusC() {
        BrelixAnalyzer analyzer = BrelixAnalyzer.getInstance();
        // schach: sch(1), ch(1). c_mehr: sch(1), ch(1). 2 - 2 = 0.
        assertEquals(0, analyzer.countMultiGraphemsMinusC("schach"));
        // bahn: 1 - 0 = 1.
        assertEquals(1, analyzer.countMultiGraphemsMinusC("bahn"));
        // spiel: sp(1), ie(1). c_mehr: 0. 2 - 0 = 2.
        assertEquals(2, analyzer.countMultiGraphemsMinusC("spiel"));
    }

    @Test
    void testRareLetters() {
        BrelixAnalyzer analyzer = BrelixAnalyzer.getInstance();
        assertEquals(1, analyzer.countRareLetters("taxi")); // x
        assertEquals(2, analyzer.countRareLetters("äußerst")); // ä, ß
        assertEquals(0, analyzer.countRareLetters("haus"));
    }

    @Test
    void testConsonantClusters() {
        BrelixAnalyzer analyzer = BrelixAnalyzer.getInstance();
        assertEquals(1, analyzer.countConsonantClusters("straße"), "str at start (>=2)");
        assertEquals(1, analyzer.countConsonantClusters("herbst"), "rbst at end (>=3)");
        // "strandtest": start "str" (3) -> count++, end "test" -> end "st" (2) -> no. Total 1.
        assertEquals(1, analyzer.countConsonantClusters("strandtest"));
        assertEquals(2, analyzer.countConsonantClusters("sprichst")); // spr(3) start, chst(4) end. Total 2.
    }
}
