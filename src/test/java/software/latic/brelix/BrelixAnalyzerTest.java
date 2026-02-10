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
        assertTrue(data.getBrelix3NeuScore() > 0, "BRELIX 3 Neu should be > 0");
        assertTrue(data.getBrelix4Score() > 0, "BRELIX 4 should be > 0");
        assertTrue(data.getBrelix5Score() > 0, "BRELIX 5 should be > 0");

        System.out.println("BRELIX 0: " + data.getBrelix0Score());
        System.out.println("BRELIX 1: " + data.getBrelix1Score());
        System.out.println("BRELIX 2: " + data.getBrelix2Score());
        System.out.println("BRELIX 3: " + data.getBrelix3Score());
        System.out.println("BRELIX 3 Neu: " + data.getBrelix3NeuScore());
        System.out.println("BRELIX 4: " + data.getBrelix4Score());
        System.out.println("BRELIX 5: " + data.getBrelix5Score());
    }

    @Test
    void testMultiGraphems() {
        BrelixAnalyzer analyzer = BrelixAnalyzer.getInstance();
        assertEquals(2, analyzer.countMultiGraphems("schach")); // sch, ch
        assertEquals(3, analyzer.countMultiGraphems("schaukelstuhl")); //sch, st, uh (Dehnungs-h)
        assertEquals(4, analyzer.countMultiGraphems("schachspiel")); // sch, ch, sp, ie
        assertEquals(1, analyzer.countMultiGraphems("bahn"), "Dehnungs-h should count as 1");
        assertEquals(0, analyzer.countMultiGraphems("halt"), "h at start is not Dehnungs-h");
    }

    @Test
    void testMultiGraphemsMinusC() {
        BrelixAnalyzer analyzer = BrelixAnalyzer.getInstance();
        // schach: sch(1), ch(1). c_mehr: sch(1), ch(1). 2 - 2 = 0.
        int count = analyzer.countMultiGraphems("schach");
        int c_mehr = analyzer.countCInMultiGraphems("schach");
        assertEquals(0, count - c_mehr);

        // bahn: 1 - 0 = 1.
        count = analyzer.countMultiGraphems("bahn");
        c_mehr = analyzer.countCInMultiGraphems("bahn");
        assertEquals(1, count - c_mehr);

        // spiel: sp(1), ie(1). c_mehr: 0. 2 - 0 = 2.
        count = analyzer.countMultiGraphems("spiel");
        c_mehr = analyzer.countCInMultiGraphems("spiel");
        assertEquals(2, count - c_mehr);
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
    void testRareLetters() {
        BrelixAnalyzer analyzer = BrelixAnalyzer.getInstance();
        assertEquals(1, analyzer.countRareLetters("taxi")); // x
        assertEquals(2, analyzer.countRareLetters("äußerst")); // ä, ß
        assertEquals(0, analyzer.countRareLetters("haus"));
        assertEquals(1, analyzer.countRareLetters("lacht")); // c
    }

    @Test
    void testConsonantClusters() {
        BrelixAnalyzer analyzer = BrelixAnalyzer.getInstance();
        assertEquals(1, analyzer.countConsonantClusters("straße"), "str at start (>=2)");
        assertEquals(1, analyzer.countConsonantClusters("herbst"), "rbst at end (>=3)");
        // "strandtest": start "str" (3) -> count++, end "st" (2) -> no. Total 1.
        assertEquals(1, analyzer.countConsonantClusters("strandtest"));
        // "sprichst": spr at start (3, >=2) -> count++; after ch->§: "§  i§ t" end cluster "t" length 1 < 3 -> no extra. Total 1.
        assertEquals(1, analyzer.countConsonantClusters("sprichst"));

        // Multiple clusters per word
        assertEquals(1, analyzer.countConsonantClusters("fenster"), "st at syllable start in middle");
        assertEquals(0, analyzer.countConsonantClusters("garten"), "no clusters in middle");
        assertEquals(1, analyzer.countConsonantClusters("wurst"), "rst at end (>=3)");
        assertEquals(1, analyzer.countConsonantClusters("sitzt"), "tzt at end (>=3)");
    }

    @Test
    void testLevelAssignment() {
        String text = "Dies ist ein Test.";
        Document doc = new Document(text);
        TextItemData data = new GermanTextItemData(text);

        data.setAverageSentenceLengthWords(4.0);
        data.setWordCount(4);
        data.setSentenceCount(1);
        data.setAverageWordLengthCharacters(4.0);
        data.setPagesCount(1);
        data.setFontSizeMm(6.0);
        data.setTypeTokenRatio(0.4);

        BrelixAnalyzer.getInstance().analyze(data, doc);

        // Prüfe ob Level gesetzt wurden (Stufe 1-6)
        assertTrue(data.getBrelix0Level() >= 1 && data.getBrelix0Level() <= 6);
        assertTrue(data.getBrelix1Level() >= 1 && data.getBrelix1Level() <= 6);
        assertTrue(data.getBrelix2Level() >= 1 && data.getBrelix2Level() <= 6);
        assertNotNull(data.getLixReadabilityLevel());
    }

    @Test
    void testManualBrelixExample() {
        // Beispieltext aus brelix-beispiel.md
        String text = "Flora spielt mit Mama Ball. Opa sitzt im Schaukelstuhl und lacht.";
        Document doc = new Document(text);
        TextItemData data = new GermanTextItemData(text);

        // Manuelle Setzung der Basisdaten wie im Beispiel
        data.setWordCount(11);
        data.setSentenceCount(2);
        data.setAverageSentenceLengthWords(5.5);
        data.setPagesCount(1);
        data.setFontSizeMm(6.0); // diff = 0
        data.setTypeTokenRatio(1.0); // Alle 11 Wörter verschieden
        data.setLixReadabilityScore(14.59); // LIX = 5.5 + 9.09

        BrelixAnalyzer.getInstance().analyze(data, doc);

        // Occurrence-based counts:
        // multiGraphems=6 (spielt: sp,ie; schaukelstuhl: sch,st,Dehnungs-h; lacht: ch)
        // cInMultiGraphems=2 (schaukelstuhl: sch; lacht: ch)
        // rareLetters=2 (schaukelstuhl: c; lacht: c)
        // consonantClusters=4 (flora: Fl; spielt: sp; sitzt: tzt; schaukelstuhl: st)
        // wortschw_minus_c = 6 - 2 + 2 + 4 = 10
        // proz_wortschw_minus_c = 10/11 * 100 = 90.91%
        // LIX = 5.5 + 9.09 = 14.59

        // BRELIX 0: 14.59 + 90.91/5 = 32.77
        assertEquals(32.77, data.getBrelix0Score(), 0.1, "BRELIX 0 mismatch");

        // BRELIX 1: 27.5 + 33 + ((9.09+90.91)/100*50) = 110.5
        assertEquals(110.5, data.getBrelix1Score(), 0.1, "BRELIX 1 mismatch");

        // BRELIX 2: 27.5 + 33 + ((9.09+90.91)/100*100) = 160.5
        assertEquals(160.5, data.getBrelix2Score(), 0.1, "BRELIX 2 mismatch");

        // BRELIX 3: (0*20) + 160.5 = 160.5
        assertEquals(160.5, data.getBrelix3Score(), 0.1, "BRELIX 3 mismatch");

        // BRELIX 3 Neu: (0*20) + 5.5*5 + 11*3 + ((9.09 + 109.09)/100*100) = 0 + 27.5 + 33 + 118.18 = 178.68
        // wortschw_additiv = multiGraphems(6) + rareLetters(2) + consonantClusters(4) = 12
        // proz_wortschw_additiv = 12/11*100 = 109.09%
        assertEquals(178.68, data.getBrelix3NeuScore(), 0.1, "BRELIX 3 Neu mismatch");

        // BRELIX 4: 160.5 + 0*5 = 160.5 (SPSS: brelix4 = brelix3 + Nebensätze*5)
        assertEquals(160.5, data.getBrelix4Score(), 0.1, "BRELIX 4 mismatch");

        // BRELIX 5: 160.5 + 100 (TTR*100) = 260.5
        assertEquals(260.5, data.getBrelix5Score(), 0.1, "BRELIX 5 mismatch");

        // Verifikation der Niveaus (Levels)
        assertEquals("2", data.getLixReadabilityLevel(), "LIX Level mismatch");
        assertEquals(2, data.getBrelix0Level(), "BRELIX 0 Level mismatch");
        assertEquals(6, data.getBrelix1Level(), "BRELIX 1 Level mismatch");
        assertEquals(6, data.getBrelix2Level(), "BRELIX 2 Level mismatch");
        assertEquals(4, data.getBrelix3Level(), "BRELIX 3 Level mismatch");
        assertEquals(4, data.getBrelix4Level(), "BRELIX 4 Level mismatch");
        assertEquals(5, data.getBrelix5Level(), "BRELIX 5 Level mismatch");
    }
}
