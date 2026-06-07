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
}
