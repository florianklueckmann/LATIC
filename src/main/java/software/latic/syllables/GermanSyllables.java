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

    // Hiat exceptions: the digraphs eu/ee are normally one nucleus (Leute, Tee),
    // but at these word-final morpheme boundaries the two vowels are a hiat and
    // form two nuclei. Anchored tightly so true diphthongs stay collapsed:
    //   eu before a final m  -> Latinate -eum (Mu-se-um, Pe-tro-le-um, Ly-ze-um)
    //   ee before a final n  -> -een plural  (I-de-en, Se-en, Ar-me-en, Al-le-en)
    // Counter-examples kept intact: Leute/Bäume/Idee/Beere/Seele, and non-final
    // eu/ee like Neu-mann, Heu-markt, lee-ren. These run BEFORE pseudoVocals so
    // they pre-empt the unconditional digraph collapse. Known limitation: rare
    // monosyllabic -een loanwords (Spleen, Queen) are over-counted by the -een
    // rule; accepted as a small trade-off for the common plurals.
    String hiatEumFinal = "eu(?=ms?$)";
    String hiatEenFinal = "ee(?=ns?$)";
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
                .replaceAll(hiatEumFinal,"00")
                .replaceAll(hiatEenFinal,"00")
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
