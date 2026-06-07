package software.latic.helper;

import edu.stanford.nlp.simple.Sentence;

import java.util.List;

/**
 * Heuristics that correct CoreNLP's sentence over-segmentation around German
 * reported speech.
 *
 * <p>CoreNLP places a sentence boundary at the {@code . ? !} that ends a quotation,
 * even when the closing quotation mark is followed by an attribution clause
 * („Reist du allein?", fragt er.). The attribution is then split into its own
 * pseudo-sentence that begins with the closing quote and a comma. That fragment is
 * never a real sentence start, so counting it inflates the sentence count and
 * deflates Wörter/Satz (and the % Nebensätze denominator).
 */
public final class SentenceSegmentation {

    private SentenceSegmentation() {
    }

    // Quotation-mark tokens (initial/final punctuation + ASCII quotes). The German
    // closing double quote is U+201C, category Pi, hence \p{Pi} is required.
    private static final String QUOTE_TOKEN = "[\\p{Pi}\\p{Pf}\"']";

    /**
     * True if this CoreNLP sentence is a reported-speech attribution fragment that the
     * splitter broke off the preceding sentence: after any leading quotation mark(s)
     * its first token is a comma (e.g. {@code “ , fragt er erstaunt .}). Such a
     * fragment should not be counted as a sentence of its own.
     */
    public static boolean isContinuationFragment(Sentence sentence) {
        List<String> words = sentence.words();
        int i = 0;
        while (i < words.size() && words.get(i).matches(QUOTE_TOKEN)) {
            i++;
        }
        return i < words.size() && words.get(i).equals(",");
    }
}
