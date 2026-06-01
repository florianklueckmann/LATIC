package software.latic.syllables;

import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.simple.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.latic.text_analyzer.SimpleTextAnalyzer;
import software.latic.translation.Translation;

import java.io.IOException;
import java.util.Locale;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Golden / characterization test for the readability metrics that are fed by the
 * German syllable counter (Flesch, SMOG, Wiener Sachtextformel, gSMOG).
 *
 * <p>The reference text is deliberately loaded with words affected by the two
 * syllable-counter fixes (hiat: Museum, Ideen, Petroleum, Linoleum; CSV-bleed:
 * karierte) so that any change in syllable counting moves these numbers visibly.
 *
 * <p>The frozen values are signed off CONSCIOUSLY after each phase, not blindly:
 * when a fix changes them, inspect the delta, confirm it reflects a real Duden
 * correction, then update the constants below in the same commit.
 */
class SyllableDownstreamGoldenTest {

    private static final String REFERENCE_TEXT = """
            Im Museum hingen viele karierte Bilder an den Wänden.
            Die Ideen der jungen Künstler waren grenzenlos und mutig.
            Wir besuchten das alte Aquarium und bewunderten die Pinien im Garten.
            Serien über Familien und ihre Ferien waren damals sehr beliebt.
            Das Petroleum roch streng, doch das Linoleum glänzte im Licht.
            """;

    private SimpleTextAnalyzer analyzer;

    @BeforeEach
    void setUp() {
        Properties props = new Properties();
        try {
            props.load(IOUtils.readerFromString("StanfordCoreNLP-german.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Translation.getInstance().setLocale(Locale.GERMAN);
        Document doc = new Document(props, REFERENCE_TEXT);
        analyzer = SimpleTextAnalyzer.getInstance();
        analyzer.setDoc(doc);
        // Populate the stateful >2/>3 syllable counters in the syllable provider.
        SyllableProvider.getInstance().syllablesInDocument(doc);
    }

    @Test
    void structuralCountsAreStable() {
        assertEquals(49, analyzer.wordCount(), "wordCount");
        assertEquals(5, analyzer.sentenceCount(), "sentenceCount");
    }

    @Test
    void syllableDrivenMetricsAreFrozen() {
        // --- Signed off after Phase 3 (hiat-collapse fix) ---
        // Phase 2 (CSV bleed): karierte 4->3.
        // Phase 3 (hiat): Museum 2->3, Ideen 2->3, Petroleum 3->4, Linoleum 3->4.
        // Net syllableCount vs. Phase 1 baseline (93): -1 (karierte) +4 (hiat) = 96.
        // words>2 rises 11->13 (Museum, Ideen cross the 2-syllable threshold), so
        // SMOG/Wiener/gSMOG move up too; Flesch lands between the two phases.
        // All deltas trace to documented Duden corrections - consciously accepted.
        assertEquals(96, analyzer.syllableCount(), "syllableCount");
        assertEquals(13, analyzer.wordsWithMoreThanTwoSyllablesCount(), "words>2syllables");
        assertEquals(1.95918, analyzer.averageWordLengthSyllables(), 1e-5, "avgWordLengthSyllables");
        assertEquals(55.58776, analyzer.fleschIndexGerman(), 1e-5, "fleschIndexGerman");
        assertEquals(9.39448, analyzer.SMOG(), 1e-5, "SMOG");
        assertEquals(8.18988, analyzer.wienerSachtextformel(), 1e-5, "wienerSachtextformel");
        assertEquals(6.83176, analyzer.gSMOG(), 1e-5, "gSMOG");
    }
}
