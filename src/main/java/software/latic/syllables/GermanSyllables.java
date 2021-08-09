package software.latic.syllables;

import java.util.Locale;

public class GermanSyllables implements Syllables {

    private static final GermanSyllables germanSyllables = new GermanSyllables();
    public static Syllables getInstance() {
        return germanSyllables;
    }

    String vocals = "[aeiouyäöüéáà]";
    String pseudoVocals = "ei|au|ie|eu|äu|aa|oo";
    String pseudoConsonants = "qu";
    String specialCases = "ien$";

    //TODO syllablesPerToken to adjust syllables based on wordClass
    public int syllablesPerWord(String word) {
        var syllableCount = 0;

        var codedChars = word.toLowerCase(Locale.ROOT)
                .replaceAll("ien$", "001")
                .replaceAll("sch[bcdfghjklmnpqrstvwxyz]", "1")
                .replaceAll(pseudoConsonants,"1")
                .replaceAll(pseudoVocals,"0")
                .replaceAll( vocals, "0")
                .replaceAll("[bcdfghjklmnpqrstvwxyzß]", "1")
                .toCharArray();

        for (var character : codedChars) {
            if (character == '0') {
                syllableCount++;
            }
        }
        return syllableCount;
    }
}
