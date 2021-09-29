package software.latic.syllables;

import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GermanSyllablesTest {

    Syllables syllables = GermanSyllables.getInstance();

    static Map<String, Integer> words = Map.of(
            "Ananas", 3,
            "Fluss", 1,
            "Situation", 5,
            "Kaulquappe", 3,
            "Baum", 1,
            "Olympia", 4,
            "Nukleotid", 4,
            "Hund", 1,
            "Esel", 2,
            "Ehe", 2
    );

    static Map<String, Integer> wordsWithSyllableCount2 = Map.of(
            "Limosine", 4,
            "Aufgabe", 3,
            "Spektakel", 3,
            "Regal", 2,
            "Katzen", 2,
            "Rehe", 2,
            "Schnabeltier", 3,
            "Schnabeltiere", 4,
            "Krankenschwester", 4,
            "Krankenschwestern", 4
    );
    static Map<String, Integer> wordsWithSyllableCount3 = Map.of(
            "Schlinge", 2,
            "Schleifenrettich", 4,
            "Schreiben", 2,
            "Abschneiden", 3,
            "Schmal", 1,
            "Verschreiben", 3,
            "Schmeißen", 2,
            "Ameise", 3,
            "Elefant", 3,
            "Durchbrechen", 3
    );
    static Map<String, Integer> wordsWithSyllableCount4 = Map.of(
            "Bearbeiten", 4,
            "vergrub", 2,
            "brechen", 2,
            "Durchfahrt", 2,
            "dranbleiben", 3,
            "bleiben", 2,
            "Routinemäßig", 5,
            "Beamter", 3,
            "Ideal", 3,
            "Schafe", 2
    );

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void syllablesPerWord() {
        for (var word : wordsWithSyllableCount4.keySet()) {
            Assertions.assertEquals(wordsWithSyllableCount4.get(word), syllables.syllablesPerWord(word), word);
        }
    }
}