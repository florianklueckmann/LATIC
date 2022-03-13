package software.latic.syllables;

import org.junit.jupiter.api.*;
import software.latic.helper.CsvReader;

import java.util.Map;

class EnglishSyllablesTest {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";

    Syllables syllables = EnglishSyllables.getInstance();

    static Map<String, Integer> words = Map.of(
//            "algorithms", 3,
//            "announcement", 3,
//            "apple", 2,
//            "appliance", 3,
//            "arrangement", 3,
//            "appreciate", 4,
//            "bundle", 2,
//            "buy", 1,
//            "buyer", 2,
//            "cakes", 1
            "amperage", 3
    );

    private final Map<String, String> testWords = CsvReader.getInstance()
            .convertCsvToMap("syllables/syllables_test_en.csv", ",");

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void syllablesPerWord() {
        var success = true;
        var counter = 0;
        System.out.format("%-15s%-15s%-15s%-15s%n", "Word", "detected", "actual", "false");
        for (var word : testWords.keySet()) {
            var result = syllables.syllablesPerWord(word);
            if (Integer.parseInt(testWords.get(word).trim()) != result) {
                counter++;
                System.out.format(ANSI_RED + "%-15s%-15s%-15s%-15s%n" + ANSI_RESET,
                        word, result, testWords.get(word), "false");
            }
        }
        success = counter <= 22;
        if (success) {
            System.out.format(ANSI_GREEN + "This test is considered a success if 22 or fewer words" +
                    " were given a divergent syllable count." + ANSI_RESET);
        }
        Assertions.assertTrue(success);
    }
}