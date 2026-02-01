package software.latic.brelix;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for grapheme analysis, specifically for German language features.
 */
public class GraphemeUtils {

    /**
     * Pattern for identifying a "Dehnungs-h" in German.
     * A "Dehnungs-h" is an 'h' after a vowel (a, e, i, o, u, ä, ö, ü) and before a consonant or at the end of a word,
     * but not after 'c' (to avoid 'ch').
     */
    private static final Pattern DEHNUNGS_H_PATTERN = Pattern.compile("(?<!c)[aeiouäöü]h([bcdfghjklmnpqrstvwxyzß]|$)");

    /**
     * Checks if the given word contains a "Dehnungs-h".
     *
     * @param word the word to check (should be lowercase)
     * @return true if it contains a Dehnungs-h, false otherwise
     */
    public static boolean containsDehnungsH(String word) {
        if (word == null) return false;
        Matcher hMatcher = DEHNUNGS_H_PATTERN.matcher(word);
        return hMatcher.find();
    }

    /**
     * Counts the number of "Dehnungs-h" occurrences in the given word.
     *
     * @param word the word to check (should be lowercase)
     * @return the number of Dehnungs-h occurrences
     */
    public static int countDehnungsH(String word) {
        if (word == null) return 0;
        int count = 0;
        Matcher hMatcher = DEHNUNGS_H_PATTERN.matcher(word);
        while (hMatcher.find()) {
            count++;
        }
        return count;
    }
}
