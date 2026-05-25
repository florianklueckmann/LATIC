package software.latic.brelix;

import edu.stanford.nlp.simple.Document;
import org.junit.jupiter.api.Test;
import software.latic.item.GermanTextItemData;
import software.latic.item.TextItemData;
import software.latic.text_analyzer.SimpleTextAnalyzer;

import static org.junit.jupiter.api.Assertions.*;

public class BrelixAnalyzerTest {

    @Test
    void testBrelixAnalysis() {
        String text = "Dies ist ein Test. Der Herbst ist da. Wir gehen auf der Strasse.";
        Document doc = new Document(text);
        TextItemData data = new GermanTextItemData(text);

        // Use SimpleTextAnalyzer to prefill textItemData like the main application does
        SimpleTextAnalyzer analyzer = SimpleTextAnalyzer.getInstance();
        analyzer.setDoc(doc);

        data.setWordCount(analyzer.wordCount());
        data.setSentenceCount(analyzer.sentenceCount());
        data.setAverageSentenceLengthWords(analyzer.averageSentenceLengthWords());
        data.setTypeTokenRatio(analyzer.typeTokenRatio());
        data.setLixReadabilityScore(analyzer.lixReadabilityScore());

        // These are document-level properties not computed by SimpleTextAnalyzer
        data.setPagesCount(1);
        data.setFontSizeMm(6.0);

        BrelixAnalyzer.getInstance().analyze(data, doc);

        assertTrue(data.getLixPlusScore() > 0, "LIX Plus should be > 0");
        assertTrue(data.getBrelix0Score() > 0, "BRELIX 0 should be > 0");
        assertTrue(data.getBrelix1Score() > 0, "BRELIX 1 should be > 0");
        assertTrue(data.getBrelix2Score() > 0, "BRELIX 2 should be > 0");
        assertTrue(data.getBrelix3Score() > 0, "BRELIX 3 should be > 0");
        assertTrue(data.getBrelix3NeuScore() > 0, "BRELIX 3 Neu should be > 0");
        assertTrue(data.getBrelix4Score() > 0, "BRELIX 4 should be > 0");
        assertTrue(data.getBrelix5Score() > 0, "BRELIX 5 should be > 0");

        System.out.println("LIX: " + data.getLixReadabilityScore());
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
        // st/sp only count at word-initial position ([ʃt]/[ʃp] sound)
        assertEquals(2, analyzer.countMultiGraphems("schaukelstuhl"), "sch + Dehnungs-h; st at index 7 not counted");
        assertEquals(3, analyzer.countMultiGraphems("schachspiel"), "sch + ch + ie; sp at index 6 not counted");
        assertEquals(2, analyzer.countMultiGraphems("spiel"), "sp at index 0 + ie");
        assertEquals(2, analyzer.countMultiGraphems("steht"), "st at index 0 + Dehnungs-h");
        assertEquals(0, analyzer.countMultiGraphems("ist"), "st at index 1 not counted (word-medial/final)");
        assertEquals(0, analyzer.countMultiGraphems("monster"), "st at index 3 not counted");
        assertEquals(1, analyzer.countMultiGraphems("bahn"), "Dehnungs-h should count as 1");
        assertEquals(0, analyzer.countMultiGraphems("halt"), "h at start is not Dehnungs-h");
    }

    @Test
    void testMultiGraphemsBinary() {
        BrelixAnalyzer analyzer = BrelixAnalyzer.getInstance();
        assertTrue(analyzer.containsMultiGrapheme("schach"));
        assertTrue(analyzer.containsMultiGrapheme("schaukelstuhl"));
        assertTrue(analyzer.containsMultiGrapheme("spiel"), "sp at index 0");
        assertTrue(analyzer.containsMultiGrapheme("steht"), "st at index 0");
        assertFalse(analyzer.containsMultiGrapheme("ist"), "st at index 1 doesn't qualify");
        assertFalse(analyzer.containsMultiGrapheme("monster"), "st at index 3 doesn't qualify");
        assertFalse(analyzer.containsMultiGrapheme("durstig"), "st at index 3 doesn't qualify");
        assertTrue(analyzer.containsMultiGrapheme("bahn"), "Dehnungs-h");
        assertFalse(analyzer.containsMultiGrapheme("test"), "only st at index 1");
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

        // Occurrence-based counts (st/sp only count at word-initial position):
        // multiGraphems=5 (spielt: sp,ie; schaukelstuhl: sch,Dehnungs-h; lacht: ch)
        // cInMultiGraphems=2 (schaukelstuhl: sch; lacht: ch)
        // rareLetters=2 (schaukelstuhl: c; lacht: c)
        // consonantClusters=4 (flora: Fl; spielt: sp; sitzt: tzt; schaukelstuhl: st)
        // wortschw_minus_c = 5 - 2 + 2 + 4 = 9
        // proz_wortschw_minus_c = 9/11 * 100 = 81.82%
        // LIX = 5.5 + 9.09 = 14.59

        // BRELIX 0: 14.59 + 81.82/5 = 30.95
        assertEquals(30.95, data.getBrelix0Score(), 0.1, "BRELIX 0 mismatch");

        // BRELIX 1: 27.5 + 33 + ((9.09+81.82)/100*50) = 105.96
        assertEquals(105.96, data.getBrelix1Score(), 0.1, "BRELIX 1 mismatch");

        // BRELIX 2: 27.5 + 33 + ((9.09+81.82)/100*100) = 151.41
        assertEquals(151.41, data.getBrelix2Score(), 0.1, "BRELIX 2 mismatch");

        // BRELIX 3: (0*20) + 151.41 = 151.41
        assertEquals(151.41, data.getBrelix3Score(), 0.1, "BRELIX 3 mismatch");

        // BRELIX 3 Neu: (0*20) + 5.5*5 + 11*3 + ((9.09 + 100)/100*100) = 0 + 27.5 + 33 + 109.09 = 169.59
        // wortschw_additiv = multiGraphems(5) + rareLetters(2) + consonantClusters(4) = 11
        // proz_wortschw_additiv = 11/11*100 = 100%
        assertEquals(169.59, data.getBrelix3NeuScore(), 0.1, "BRELIX 3 Neu mismatch");

        // BRELIX 4: 151.41 + 0*5 = 151.41 (SPSS: brelix4 = brelix3 + Nebensätze*5)
        assertEquals(151.41, data.getBrelix4Score(), 0.1, "BRELIX 4 mismatch");

        // BRELIX 5: 151.41 + 100 (TTR*100) = 251.41
        assertEquals(251.41, data.getBrelix5Score(), 0.1, "BRELIX 5 mismatch");

        // Verifikation der Niveaus (Levels)
        assertEquals("2", data.getLixReadabilityLevel(), "LIX Level mismatch");
        assertEquals(2, data.getBrelix0Level(), "BRELIX 0 Level mismatch");
        assertEquals(5, data.getBrelix1Level(), "BRELIX 1 Level mismatch");
        assertEquals(6, data.getBrelix2Level(), "BRELIX 2 Level mismatch");
        assertEquals(4, data.getBrelix3Level(), "BRELIX 3 Level mismatch");
        assertEquals(3, data.getBrelix4Level(), "BRELIX 4 Level mismatch");
        assertEquals(5, data.getBrelix5Level(), "BRELIX 5 Level mismatch");
    }
}
