package software.latic.helper;

import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Regression test for the reported-speech sentence-merge correction.
 *
 * <p>CoreNLP splits {@code „Reist du allein?", fragt er.} at the {@code ?} inside the
 * quote, leaving the attribution {@code ", fragt er.} as its own pseudo-sentence. That
 * fragment must not be counted, otherwise the sentence count is inflated and Wörter/Satz
 * (and the % Nebensätze denominator) are deflated.
 */
public class SentenceSegmentationTest {

    private Document germanDoc(String text) {
        Properties props = new Properties();
        try {
            props.load(IOUtils.readerFromString("StanfordCoreNLP-german.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new Document(props, text);
    }

    private long count(Document doc, boolean corrected) {
        return doc.sentences().stream()
                .filter(s -> s.length() > 1)
                .filter(s -> !corrected || !SentenceSegmentation.isContinuationFragment(s))
                .count();
    }

    @Test
    void reportedSpeechAttributionIsMerged() {
        Document doc = germanDoc("„Reist du ganz allein?“, fragt er erstaunt. Sie nickt langsam.");
        long raw = count(doc, false);
        long corrected = count(doc, true);
        assertTrue(corrected < raw,
                "the „...?\", fragt er. attribution fragment must be merged (raw=" + raw + ", corrected=" + corrected + ")");
    }

    @Test
    void plainProseIsUnchanged() {
        Document doc = germanDoc("Hanna fährt nach Berlin. Mama bleibt zu Hause. Der Zug ist pünktlich.");
        for (Sentence s : doc.sentences()) {
            assertTrue(!SentenceSegmentation.isContinuationFragment(s),
                    "plain sentence wrongly flagged as continuation: " + s.text());
        }
        assertTrue(count(doc, true) == count(doc, false), "plain prose count must be unchanged");
    }
}
