package software.latic.brelix;

import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.simple.Document;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Regression test for German subordinate-clause detection (BRELIX4/5 Nebensatz term).
 *
 * <p>The detector previously matched only the English PTB label {@code SBAR}, which the
 * German parser never emits, so every German text scored 0 subordinate clauses and the
 * Nebensatz term silently vanished from BRELIX4/5. The fix counts embedded {@code S}
 * clause nodes (UD/NEGRA-style). These tests pin that German clauses are now detected;
 * if the SBAR-only behaviour ever returns, the German counts collapse to 0 and fail.
 *
 * <p>Uses the German CoreNLP pipeline explicitly (the constituency labels are
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
    void detectsGermanConjunctionClauses() {
        // Three subordinate clauses: wenn-, dass-, wie-clause + one clause-free sentence.
        String text = "Hanna ist traurig, wenn sie an die Scheidung denkt. "
                + "Marie will nicht, dass Hanna mit Tim spielt. "
                + "Du weißt nicht, wie man mit einem Baby spielt. "
                + "Sie geht extra langsam hinter den Mädchen her.";
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
}
