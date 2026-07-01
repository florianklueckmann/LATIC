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
        assertEquals(1, analyzer.countMultiGraphems("schaukelstuhl"), "sch; st at index 7 not counted");
        assertEquals(3, analyzer.countMultiGraphems("schachspiel"), "sch + ch + ie; sp at index 6 not counted");
        assertEquals(2, analyzer.countMultiGraphems("spiel"), "sp at index 0 + ie");
        assertEquals(1, analyzer.countMultiGraphems("steht"), "st at index 0");
        assertEquals(1, analyzer.countMultiGraphems("phase"), "ph is listed by Brügelmann");
        assertEquals(1, analyzer.countMultiGraphems("rhesus"), "rh is listed by Brügelmann");
        assertEquals(1, analyzer.countMultiGraphems("theater"), "th is listed by Brügelmann");
        assertEquals(0, analyzer.countMultiGraphems("ist"), "st at index 1 not counted (word-medial/final)");
        assertEquals(0, analyzer.countMultiGraphems("monster"), "st at index 3 not counted");
        assertEquals(0, analyzer.countMultiGraphems("bahn"), "Dehnungs-h is not part of BRELIX mehrgliedrig");
        assertEquals(0, analyzer.countMultiGraphems("halt"), "h at start is not Dehnungs-h");
    }

    @Test
    void testMultiGraphemsBinary() {
        BrelixAnalyzer analyzer = BrelixAnalyzer.getInstance();
        assertTrue(analyzer.containsMultiGrapheme("schach"));
        assertTrue(analyzer.containsMultiGrapheme("schaukelstuhl"));
        assertTrue(analyzer.containsMultiGrapheme("phase"));
        assertTrue(analyzer.containsMultiGrapheme("rhesus"));
        assertTrue(analyzer.containsMultiGrapheme("theater"));
        assertTrue(analyzer.containsMultiGrapheme("spiel"), "sp at index 0");
        assertTrue(analyzer.containsMultiGrapheme("steht"), "st at index 0");
        assertFalse(analyzer.containsMultiGrapheme("ist"), "st at index 1 doesn't qualify");
        assertFalse(analyzer.containsMultiGrapheme("monster"), "st at index 3 doesn't qualify");
        assertFalse(analyzer.containsMultiGrapheme("durstig"), "st at index 3 doesn't qualify");
        assertFalse(analyzer.containsMultiGrapheme("bahn"), "Dehnungs-h is not part of BRELIX mehrgliedrig");
        assertFalse(analyzer.containsMultiGrapheme("test"), "only st at index 1");
    }

    @Test
    void testMultiGraphemsMinusC() {
        BrelixAnalyzer analyzer = BrelixAnalyzer.getInstance();
        // schach: sch(1), ch(1). c_mehr: sch(1), ch(1). 2 - 2 = 0.
        int count = analyzer.countMultiGraphems("schach");
        int c_mehr = analyzer.countCInMultiGraphems("schach");
        assertEquals(0, count - c_mehr);

        // bahn: Dehnungs-h is not part of BRELIX mehrgliedrig.
        count = analyzer.countMultiGraphems("bahn");
        c_mehr = analyzer.countCInMultiGraphems("bahn");
        assertEquals(0, count - c_mehr);

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
        // "strandtest": start "str" (3 sounds after st+r normalization) -> count++, end "st" (2) -> no. Total 1.
        assertEquals(1, analyzer.countConsonantClusters("strandtest"));
        // "sprichst": spr at start (3 sounds after sp+r normalization) -> count++; final st has length 2 -> no extra. Total 1.
        assertEquals(1, analyzer.countConsonantClusters("sprichst"));

        // Multi-graphemes and affricates are counted as one sound, not as letter piles.
        assertEquals(0, analyzer.countConsonantClusters("fenster"), "st before a vowel is tracked as a multi-grapheme");
        assertEquals(0, analyzer.countConsonantClusters("garten"), "no clusters in middle");
        assertEquals(1, analyzer.countConsonantClusters("wurst"), "rst at end (>=3)");
        assertEquals(0, analyzer.countConsonantClusters("sitzt"), "tz is one consonant sound");
        assertEquals(0, analyzer.countConsonantClusters("phase"), "ph is one consonant sound");
        assertEquals(0, analyzer.countConsonantClusters("rhesus"), "rh is one consonant sound");
        assertEquals(0, analyzer.countConsonantClusters("theater"), "th is one consonant sound");
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

        // Word difficulty per word (binary), the count feeding BRELIX1–5:
        // multiGraphemsBinary=3 (spielt, schaukelstuhl, lacht), rareLettersBinary=2
        // (schaukelstuhl: c; lacht: c), consonantClusters=1 (flora: Fl)
        // wortschw (binär) = 3 + 2 + 1 = 6  → 6/11 = 54.55%
        // (additive count 7 → 63.64% is used only by BRELIX3_NEU.)
        // LIX = 5.5 + 9.09 = 14.59

        // BRELIX 0: 14.59 + 45.45/5 = 23.68 (uses wortschw_minus_c, unchanged)
        assertEquals(23.68, data.getBrelix0Score(), 0.1, "BRELIX 0 mismatch");

        // BRELIX 1: 27.5 + 33 + ((9.09+54.55)/100*50) = 92.32
        assertEquals(92.32, data.getBrelix1Score(), 0.1, "BRELIX 1 mismatch");

        // BRELIX 2: 27.5 + 33 + ((9.09+54.55)/100*100) = 124.14
        assertEquals(124.14, data.getBrelix2Score(), 0.1, "BRELIX 2 mismatch");

        // BRELIX 3: (0*20) + 124.14 = 124.14
        assertEquals(124.14, data.getBrelix3Score(), 0.1, "BRELIX 3 mismatch");

        // BRELIX 3 Neu: (0*20) + 5.5*5 + 11*3 + ((9.09 + 63.64)/100*100) = 0 + 27.5 + 33 + 72.73 = 133.23
        // wortschw_additiv = multiGraphems(4) + rareLetters(2) + consonantClusters(1) = 7
        // proz_wortschw_additiv = 7/11*100 = 63.64%
        assertEquals(133.23, data.getBrelix3NeuScore(), 0.1, "BRELIX 3 Neu mismatch");

        // BRELIX 4: 124.14 + 0*5 = 124.14 (SPSS: brelix4 = brelix3 + Nebensätze*5)
        assertEquals(124.14, data.getBrelix4Score(), 0.1, "BRELIX 4 mismatch");

        // BRELIX 5: 124.14 + 100 (TTR*100) = 224.14
        assertEquals(224.14, data.getBrelix5Score(), 0.1, "BRELIX 5 mismatch");

        // Verifikation der Niveaus (Levels)
        assertEquals("2", data.getLixReadabilityLevel(), "LIX Level mismatch");
        assertEquals(1, data.getBrelix0Level(), "BRELIX 0 Level mismatch");
        assertEquals(4, data.getBrelix1Level(), "BRELIX 1 Level mismatch");
        assertEquals(4, data.getBrelix2Level(), "BRELIX 2 Level mismatch");
        assertEquals(3, data.getBrelix3Level(), "BRELIX 3 Level mismatch");
        assertEquals(2, data.getBrelix4Level(), "BRELIX 4 Level mismatch");
        assertEquals(4, data.getBrelix5Level(), "BRELIX 5 Level mismatch");
    }
}
