package software.latic.syllables;

import software.latic.helper.CsvReader;
import java.util.Locale;
import java.util.Map;

public class GermanSyllables extends BaseSyllables implements Syllables {

    private static final GermanSyllables germanSyllables = new GermanSyllables();
    public static Syllables getInstance() {
        return germanSyllables;
    }

    private final Map<String, String> specialWords = CsvReader.getInstance()
            .convertCsvToMap("syllables/syllables_de.csv", ",");

    String vocals = "[aeiouyäöüéáà]";
    String pseudoVocals = "ei|au|ie|eu|äu|aa|oo|ee|ée";
    String pseudoConsonants = "qu";
    String specialOneSyllable = "eau|oire|sance";
    String specialTwoSyllables = "ouille|tiell|ziell";
    String bePrefixTwoSyllables = "(be)(eng|end|eid|ehr|eil|erb|erd|ein)";

    //TODO syllablesPerToken to adjust syllables based on wordClass
    //TODO function for each replacment for better readability?
    public int syllablesPerWord(String word) {
        var syllableCount = 0;

        var codedChars = replaceSpecialWords(word.toLowerCase(Locale.ROOT))
                .replaceAll(bePrefixTwoSyllables, "00")
                .replaceAll(specialTwoSyllables, "00")
                .replaceAll(specialOneSyllable, "0")
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

    private String replaceSpecialWords(String word) {
        for (var regex : specialWords.keySet()) {
            word = word.replaceAll(regex, specialWords.get(regex));
            if (word.matches("[10]+")) {
                return word;
            }
        }
        return word;
    }
}
