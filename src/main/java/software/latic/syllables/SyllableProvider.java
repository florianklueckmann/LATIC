package software.latic.syllables;

import software.latic.translation.Translation;

import java.util.Locale;

public class SyllableProvider {
    public static Syllables getInstance() {
        if (Translation.getInstance().getLocale().equals(Locale.GERMAN)) {
            return GermanSyllables.getInstance();
        } else {
            return EnglishSyllables.getInstance();
        }
    }
}
