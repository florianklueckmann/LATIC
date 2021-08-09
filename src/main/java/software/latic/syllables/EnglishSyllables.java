package software.latic.syllables;

import java.util.Locale;

public class EnglishSyllables implements Syllables {

    private static final EnglishSyllables englishSyllables = new EnglishSyllables();
    public static Syllables getInstance() {
        return englishSyllables;
    }

//    String vocals = "[aeiouyäöüéáà]";
//    String pseudoVocals = "ei|au|ie|eu|äu|aa|oo";
//    String pseudoConsonants = "qu";

    public int syllablesPerWord(String word) {
    //TODO: Implement
        return 0;
    }
}
