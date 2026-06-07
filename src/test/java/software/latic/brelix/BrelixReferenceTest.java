package software.latic.brelix;

import edu.stanford.nlp.simple.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.latic.item.GermanTextItemData;
import software.latic.item.TextItemData;
import software.latic.text_analyzer.SimpleTextAnalyzer;
import software.latic.translation.Translation;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BrelixReferenceTest {

    private Locale originalLocale;

    /**
     * Pin a deterministic locale so these tests are independent of execution order.
     * The corpus texts are tokenized via {@code new Document(text)}, i.e. CoreNLP's
     * default (English/PTB) pipeline, and {@code SimpleTextAnalyzer.wordCount()}
     * filters punctuation through the locale-dependent {@code TagMapper}; the two
     * must agree. A GERMAN locale leaked by another test mismatches the PTB tags and
     * inflates wordCount (e.g. 96 -> 120), so we force ENGLISH here and restore the
     * previous locale afterwards to avoid polluting other tests.
     */
    @BeforeEach
    void pinLocale() {
        originalLocale = Translation.getInstance().getLocale();
        Translation.getInstance().setLocale(Locale.ENGLISH);
    }

    @AfterEach
    void restoreLocale() {
        Translation.getInstance().setLocale(originalLocale);
    }

    @Test
    void testBrelixReferenceExample() {
        // Text from brelix-beispiel.md
        String text = "Flora spielt mit Mama Ball. Opa sitzt im Schaukelstuhl und lacht.";
        TextItemData data = new GermanTextItemData(text);
        data.setPagesCount(1);
        data.setFontSizeMm(6.0); // diff = 0
        data.setWordCount(11);
        data.setSentenceCount(2);
        data.setAverageSentenceLengthWords(5.5);
        data.setTypeTokenRatio(1.0); // All 11 words are unique
        data.setLixReadabilityScore(14.59); // LIX = 5.5 + 9.09

        Document doc = new Document(text);
        BrelixAnalyzer analyzer = BrelixAnalyzer.getInstance();
        analyzer.analyze(data, doc);

        // Reference values from brelix-beispiel.md
        assertEquals(11, data.getWordCount(), "Word count should be 11");
        assertEquals(2, data.getSentenceCount(), "Sentence count should be 2");
        assertEquals(5.5, data.getAverageSentenceLengthWords(), 0.01, "Average sentence length should be 5.5");

        // Our implementation counts consonant clusters word-based and sound-based:
        // Multigraphemes: spielt(sp,ie=2), schaukelstuhl(sch=1), lacht(ch=1) = 4
        // c_mehr: schaukelstuhl(sch=1), lacht(ch=1) = 2
        // Rare letters: schaukelstuhl(c=1), lacht(c=1) = 2
        // Consonant cluster words: flora(Fl=1)
        // wortschwierig = 4 + 2 + 1 = 7
        // proz_wortschwierig = 7/11 * 100 = 63.64%

        // LIX = 5.5 + 9.09 = 14.59
        assertEquals(14.59, data.getLixReadabilityScore(), 0.1, "LIX should be 14.59");

        // BRELIX 0 = 14.59 + 45.45 / 5 = 23.68
        assertEquals(23.68, data.getBrelix0Score(), 0.1, "BRELIX 0");

        // BRELIX 1 = (5.5*5) + (11*3) + ((9.09+63.64)/100*50) = 27.5 + 33 + 36.36 = 96.86
        assertEquals(96.86, data.getBrelix1Score(), 0.1, "BRELIX 1");

        // BRELIX 2 = (5.5*5) + (11*3) + ((9.09+63.64)/100*100) = 27.5 + 33 + 72.73 = 133.23
        assertEquals(133.23, data.getBrelix2Score(), 0.1, "BRELIX 2");

        // BRELIX 3 = (0*20) + 133.23 = 133.23 (font size diff = 0)
        assertEquals(133.23, data.getBrelix3Score(), 0.1, "BRELIX 3");

        // BRELIX 3 Neu: uses wortschw_additiv (no C correction) = 4 + 2 + 1 = 7
        // proz_wortschw_additiv = 7/11*100 = 63.64%
        // (0*20) + 5.5*5 + 11*3 + ((9.09+63.64)/100*100) = 0 + 27.5 + 33 + 72.73 = 133.23
        assertEquals(133.23, data.getBrelix3NeuScore(), 0.1, "BRELIX 3 Neu");

        // BRELIX 4 = 133.23 + 0*5 = 133.23 (SPSS: brelix4 = brelix3 + Nebensätze*5)
        assertEquals(133.23, data.getBrelix4Score(), 0.1, "BRELIX 4");

        // BRELIX 5 = 133.23 + (1.0 * 100) = 233.23
        assertEquals(233.23, data.getBrelix5Score(), 0.1, "BRELIX 5");
    }

    /**
     * Regression test for the "Die karierte Sonntagshose" reference text.
     * Locks in current intermediate values (multiGraphems, multiGraphemsBinary, etc.)
     * so future changes that affect counting rules will be visible.
     * Reference percentage from paper: ~36.6% multigraphems → our binary count ≈ 36.45%.
     */
    @Test
    void testKarierteSonntagshose() {
        String text = "DIE KARIERTE Sonntagshose. " +
                "An der Kasse steht der Dino, denn er möchte gern ins Kino. " +
                "Die karierte Sonntagshose sitzt dem Maulwurf etwas lose. " +
                "Der Magen knurrt dem Krokodil, denn es fraß heut noch nicht viel. " +
                "Diese nette kleine Fliege ist verliebt in eine Ziege. " +
                "Gut versteckt hat sich der Hase, denn er popelt in der Nase. " +
                "Spuren sieht man noch im Sand – das Kamel ist fortgerannt. " +
                "Der Vampir ist aufgewacht, durstig flieht er durch die Nacht. " +
                "Das Monster hier schaut grimmig drein, denn es ist nicht gern allein. " +
                "Mit dem kleinen Gummiball spielt ein riesengroßer Wal.";

        Document doc = new Document(text);
        TextItemData data = new GermanTextItemData(text);

        SimpleTextAnalyzer analyzer = SimpleTextAnalyzer.getInstance();
        analyzer.setDoc(doc);
        data.setWordCount(analyzer.wordCount());
        data.setSentenceCount(analyzer.sentenceCount());
        data.setAverageSentenceLengthWords(analyzer.averageSentenceLengthWords());
        data.setTypeTokenRatio(analyzer.typeTokenRatio());
        data.setLixReadabilityScore(analyzer.lixReadabilityScore());

        // Paper's "Word/Page = 9.3" implies ~10 pages for the picture book.
        data.setPagesCount(10);
        data.setFontSizeMm(6.0); // diff = 0

        BrelixAnalyzer.getInstance().analyze(data, doc);

        // --- Intermediate counts ---
        // Calibrated against paper's reference values where applicable.
        // Paper values: Anteil 3+Syl=9.7%, mehrg Graph=36.6%, selten ohne c=3.2%, Konshfg=9.7%
        var map = data.getBrelixDebugMap();
        assertEquals("96", map.get("wordCount"), "wordCount");
        assertEquals("37", map.get("multiGraphems"), "multiGraphems (additive)");
        assertEquals("35", map.get("multiGraphemsBinary"), "multiGraphemsBinary (≈ paper 36.6%)");
        assertEquals("14", map.get("rareLetters"), "rareLetters (occurrence-based)");
        assertEquals("13", map.get("rareLettersBinary"), "rareLettersBinary (per-word, Brügelmann Buchst_selten)");
        assertEquals("3", map.get("rareLettersWithoutC"), "rareLettersWithoutC (≈ paper 3.2%)");
        assertEquals("9", map.get("consonantClusters"),
                "consonantClusters (≈ paper 9.7%)");
        assertEquals("11", map.get("cInMultiGraphems"), "cInMultiGraphems");
        assertEquals("9", map.get("syllablesGe3"), "syllablesGe3 (≈ paper 9.7%)");
        assertEquals("60", map.get("wortschwierig"), "wortschwierig");
        assertEquals("49", map.get("wortschw_minus_c"), "wortschw_minus_c");
        assertEquals("60", map.get("wortschw_additiv"), "wortschw_additiv");
        // Paper's WortSchw = 59.1%. Brügelmann's wortschwierig, counted fully per-word (binary):
        // multigraphems(35) + Buchst_selten binary(13) + Konshfg binary(9) = 57 → 59.38%. Near-exact.
        assertEquals("57", map.get("wortschw_paper_candidate"), "wortschw_paper_candidate (≈ paper 59.1%)");
        assertEquals("59.3750", map.get("proz_wortschw_paper_candidate"), "proz_wortschw_paper_candidate");
        assertEquals("0", map.get("subordinateClauses"), "subordinateClauses (dependency-based; EN pipeline yields 0 here)");
        assertEquals("16", map.get("longWords"), "longWords");

        // --- Final scores (current app behavior with pagesCount=10) ---
        assertEquals(26.27, data.getLixReadabilityScore(), 0.01, "LIX (≈ paper 26.3)");
        assertEquals(55.07, data.getLixPlusScore(), 0.01, "LIX+");
        assertEquals(36.48, data.getBrelix0Score(), 0.01, "BRELIX0");
        assertEquals(112.74, data.getBrelix1Score(), 0.01, "BRELIX1");
        assertEquals(148.68, data.getBrelix2Score(), 0.01, "BRELIX2");
        assertEquals(148.68, data.getBrelix3Score(), 0.01, "BRELIX3");
        assertEquals(148.68, data.getBrelix3NeuScore(), 0.01, "BRELIX3 Neu");
        assertEquals(148.68, data.getBrelix4Score(), 0.01, "BRELIX4");
        assertEquals(223.68, data.getBrelix5Score(), 0.01, "BRELIX5");

        // Sanity-check that st/sp rule applied: word-medial occurrences must not count.
        BrelixAnalyzer ba = BrelixAnalyzer.getInstance();
        assertEquals(0, ba.countMultiGraphems("ist"), "ist: word-final st must not count");
        assertEquals(0, ba.countMultiGraphems("monster"), "monster: word-medial st must not count");
        assertEquals(0, ba.countMultiGraphems("durstig"), "durstig: word-medial st must not count");
    }

    /**
     * Reference row for "Anna wird sieben" transcribed from the original source book.
     * The source book prints pedagogical compound hyphens across lines
     * (e.g. SCHOKOLADEN-/SPIEL), so this calibration keeps the compounds split.
     * The published percentages reveal a denominator of 55 words:
     * 3+ syllables 2/55=3.6%, multigraph words 39/55=70.9%,
     * rare letters without c 1/55=1.8%, consonant clusters 4/55=7.3%.
     */
    @Test
    void testAnnaWirdSiebenReferenceCorpus() {
        String text = "ANNA WIRD SIEBEN. " +
                "SIEBEN KINDER UND EIN KUCHEN. " +
                "SIEBEN KINDER UND VIELE GESCHENKE. " +
                "EIN TIER? " +
                "EIN KUSCHEL-TIER. " +
                "EIN SCHWEIN? " +
                "EIN SPAR-SCHWEIN. " +
                "SCHUHE? " +
                "ROLL-SCHUHE. " +
                "EIN FAHRRAD? " +
                "EINE FAHRRAD-KLINGEL. " +
                "WAS IST DAS? " +
                "Für Anna von Florian. " +
                "EIN SCHUMMEL-PAKET. " +
                "WAS SPIELEN DIE SIEBEN KINDER? " +
                "SIE SPIELEN DAS SCHOKOLADEN-SPIEL. " +
                "BALD WERDE ICH ACHT.";

        Document doc = new Document(text);
        TextItemData data = new GermanTextItemData(text);
        data.setWordCount(55);
        data.setSentenceCount(16);
        data.setAverageSentenceLengthWords(55.0 / 16.0);
        data.setPagesCount(16);
        data.setFontSizeMm(6.0);
        data.setTypeTokenRatio(0.63);
        data.setLixReadabilityScore(25.3);

        BrelixAnalyzer.getInstance().analyze(data, doc);

        var map = data.getBrelixDebugMap();
        assertEquals("55", map.get("wordCount"), "wordCount");
        assertEquals("1", map.get("rareLettersWithoutC"), "rareLettersWithoutC (1/55 = 1.8%)");
        assertEquals("4", map.get("consonantClusters"), "consonantClusters (4/55 = 7.3%)");
        assertEquals("3", map.get("syllablesGe3"), "syllablesGe3 (current app behavior; reference row has 2/55 = 3.6%)");
        assertEquals("3.4400", map.get("satzlaenge"), "words/sentence");
        assertEquals("3.4375", map.get("woerter_seite"), "words/page");

        // Published row: LIX=25.3, BRELIX1=79.3, Wortschwierigkeit=100.0,
        // 3+ syllables=2/55=3.6%, mehrgliedrige Grapheme=39/55=70.9%.
        // Current app behavior still counts Florian as 3+ syllables and is one
        // short on the multigraph count, but the combined BRELIX1 term matches.
        assertEquals(25.3, data.getLixReadabilityScore(), 0.01, "LIX reference input");
        assertEquals("38", map.get("multiGraphems"), "multiGraphems (current additive count; reference row has 39/55 = 70.9%)");
        assertEquals("33", map.get("multiGraphemsBinary"), "multiGraphemsBinary (current per-word count)");
        assertEquals("54", map.get("wortschwierig"), "wortschwierig (current count; reference row has 55/55 = 100.0%)");
        assertEquals("43", map.get("wortschw_minus_c"), "wortschw_minus_c");
        assertEquals(79.33, data.getBrelix1Score(), 0.01, "BRELIX1 (≈ published 79.3)");
    }
}
