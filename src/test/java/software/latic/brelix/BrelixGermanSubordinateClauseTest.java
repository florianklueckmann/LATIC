package software.latic.brelix;

import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.simple.Document;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Regression test for German subordinate-clause detection (BRELIX4/5 Nebensatz term).
 *
 * <p>Detection is dependency-based (UD relations advcl/acl/ccomp/csubj/xcomp), the same
 * machinery the rest of the NLP analysis uses. It replaced an SBAR-only check that the
 * German parser never matched, so every German text scored 0 subordinate clauses and the
 * Nebensatz term silently vanished from BRELIX4/5. These tests pin that German clauses
 * are detected across the main types (adverbial, indirect question, relative); if the
 * detector regresses to 0 they fail.
 *
 * <p>Uses the German CoreNLP pipeline explicitly (dependency relations are
 * language-specific). Does not touch the Translation locale (the count is parse-only).
 */
public class BrelixGermanSubordinateClauseTest {

    private Document germanDoc(String text) {
        Properties props = new Properties();
        try {
            props.load(IOUtils.readerFromString("StanfordCoreNLP-german.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new Document(props, text);
    }

    @Test
    void detectsGermanSubordinateClauseTypes() {
        // advcl (wenn), ccomp (indirect question wie), acl (relative der), + a clause-free sentence.
        String text = "Hanna ist traurig, wenn sie an die Scheidung denkt. "
                + "Du weißt nicht, wie man mit einem Baby spielt. "
                + "Der Mann, der dort steht, ist mein Vater. "
                + "Sie geht langsam nach Hause.";
        int clauses = BrelixAnalyzer.getInstance().countSubordinateClauses(germanDoc(text));
        assertTrue(clauses >= 3,
                "German subordinate clauses must be detected (SBAR-only returned 0); was " + clauses);
    }

    @Test
    void mainClauseOnlyHasNoSubordinate() {
        int clauses = BrelixAnalyzer.getInstance()
                .countSubordinateClauses(germanDoc("Hanna fährt nach Berlin."));
        assertEquals(0, clauses, "A single main clause has no subordinate clause");
    }

    @Test
    void countsReferenceCorpusSubordinateClausesWithGermanPipeline() {
        assertAll(
                () -> assertReferenceCorpusCounts("Hanna fährt nach Berlin", "nebensaetze_hanna.txt", 11, 14),
                () -> assertReferenceCorpusCounts("Wanda will weg", "nebensaetze_wanda.txt", 2, 3)
        );
    }

    /** Loads a reference text from the test classpath (package-relative), joined to one line. */
    private String loadFixture(String name) {
        try (InputStream in = getClass().getResourceAsStream(name)) {
            if (in == null) {
                throw new IllegalStateException("fixture not found on classpath: " + name);
            }
            return new String(in.readAllBytes(), StandardCharsets.UTF_8).replaceAll("\\R+", " ").trim();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void assertReferenceCorpusCounts(String title, String fileName, int expectedDefault,
                                             int minimumReportedSpeechCount) {
        String text = loadFixture(fileName);

        BrelixAnalyzer analyzer = BrelixAnalyzer.getInstance();
        analyzer.setCountReportedSpeechAsSubordinate(false);
        int englishClauses = analyzer.countSubordinateClauses(new Document(text));
        int germanClauses = analyzer.countSubordinateClauses(germanDoc(text));
        System.out.printf("%s subordinateClauses default: EN=%d, DE=%d, expected=%d%n",
                title, englishClauses, germanClauses, expectedDefault);

        assertEquals(expectedDefault, germanClauses, title + " German pipeline subordinateClauses");

        try {
            analyzer.setCountReportedSpeechAsSubordinate(true);
            int englishReportedSpeechClauses = analyzer.countSubordinateClauses(new Document(text));
            int germanReportedSpeechClauses = analyzer.countSubordinateClauses(germanDoc(text));
            System.out.printf("%s subordinateClauses direct speech: EN=%d, DE=%d, default=%d%n",
                    title, englishReportedSpeechClauses, germanReportedSpeechClauses, germanClauses);

            assertTrue(germanReportedSpeechClauses > germanClauses,
                    title + " direct-speech mode should count more subordinate clauses");
            assertTrue(germanReportedSpeechClauses >= minimumReportedSpeechCount,
                    title + " direct-speech mode should stay in a plausible range");
        } finally {
            analyzer.setCountReportedSpeechAsSubordinate(false);
        }
    }
}
