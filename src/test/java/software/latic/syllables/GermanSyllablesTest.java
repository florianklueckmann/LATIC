package software.latic.syllables;

import org.junit.jupiter.api.*;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class GermanSyllablesTest {

    Syllables syllables = GermanSyllables.getInstance();

    // ---------------------------------------------------------------------
    // Original corpus (kept verbatim). Previously only wordsWithSyllableCount4
    // was asserted; the other three maps were dead code. All four are asserted
    // now (see assertAllCorrect / the @Test methods below).
    // ---------------------------------------------------------------------
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

    /**
     * Hiat (vowel-vowel across a morpheme boundary) cases where two adjacent
     * vowels form separate syllable nuclei. The Vokal-Digraphe {@code eu/ee}
     * were collapsed unconditionally before the fix, undercounting these by one.
     * Duden-Worttrennung is the source of truth. Fixed in Phase 3.
     */
    static Map<String, Integer> hiatCases = mapOf(
            "Museum", 3,        // Mu-se-um
            "Museums", 3,       // Mu-se-ums
            "Museen", 3,        // Mu-se-en
            "Ideen", 3,         // I-de-en
            "Petroleum", 4,     // Pe-tro-le-um
            "Linoleum", 4,      // Li-no-le-um
            "Mausoleum", 4,     // Mau-so-le-um
            "Lyzeum", 3,        // Ly-ze-um
            "Kolosseum", 4,     // Ko-los-se-um
            "Seen", 2,          // Se-en (plural of See)
            "Armeen", 3,        // Ar-me-en
            "Alleen", 3,        // Al-le-en
            "Moscheen", 3       // Mo-sche-en
    );

    /**
     * CSV-Substring-Bleed cases: short unanchored rules in syllables_de.csv
     * (arie, linie) leaked into larger words, splitting a diphthong "ie" that
     * is in fact a single nucleus before the -iert suffix. Fixed in Phase 2.
     */
    static Map<String, Integer> csvBleedCases = mapOf(
            "karierte", 3,      // ka-rier-te
            "kariert", 2,       // ka-riert
            "kariertem", 3,     // ka-rier-tem
            "diszipliniert", 4, // dis-zi-pli-niert
            "disziplinierte", 5,// dis-zi-pli-nier-te
            "liniert", 2        // li-niert
    );

    /**
     * Protection corpus: Duden-correct words that already count correctly and
     * must NOT change when the bleed/hiat fixes land. Includes the true
     * diphthong digraphs (sieht, Baum, Idee, See, Beere ...) that must stay
     * collapsed, and the -ien words handled correctly by the CSV.
     */
    static Map<String, Integer> protectionCases = mapOf(
            // CSV -ie / -ien words that already work
            "Familie", 4, "Familien", 4, "Linie", 3, "Serie", 3, "Pinie", 3,
            "Linien", 3, "Serien", 3, "Pinien", 3, "Kastanien", 4, "Ferien", 3,
            "Spanien", 3, "Theater", 3, "Ideal", 3, "Beamter", 3,
            // arie-bleed must not regress these (arie is genuinely hiat here)
            "Karies", 3, "Vegetarier", 5, "Bibliothekarin", 6,
            // true diphthong digraphs - must stay collapsed
            "Idee", 2, "Beere", 2, "Beeren", 2, "Seele", 2, "Seelen", 2,
            "sieht", 1, "Baum", 1, "Bäume", 2, "Häuser", 2, "Leute", 2,
            "Eier", 2, "See", 1, "Tee", 1, "Boot", 1, "Paar", 1,
            // -eu/-äu that is NOT word-final hiat - must stay collapsed
            "Neumann", 2, "Heumarkt", 2, "räumen", 2, "Raum", 1, "Leumund", 2,
            "leeren", 2,
            // -iert verb family - diphthong, must stay collapsed
            "studiert", 2, "riskiert", 2, "kopiert", 2,
            // iu/uu/au hiat already handled correctly (no digraph collapse)
            "Aquarium", 4, "Individuum", 5, "Jubiläum", 4, "Vakuum", 3,
            "Kontinuum", 4
    );

    /**
     * Pre-existing tool behaviour for a few loanwords (Arie/Aktie) where the
     * count is debatable vs. Duden but is intentionally OUT OF SCOPE here. These
     * are locked at the current value purely so the arie-bleed fix is proven not
     * to disturb them.
     */
    static Map<String, Integer> lockedPreExisting = mapOf(
            "Arie", 3,   // tool: A-ri-e (Duden A-rie=2; not in scope)
            "Arien", 3,  // tool: A-ri-en
            "Aktie", 3,  // tool: Ak-ti-e (Duden Ak-tie=2; not in scope)
            "Aktien", 3
    );

    @Test
    void originalCorpusGroup1() {
        assertAllCorrect(words);
    }

    @Test
    void originalCorpusGroup2() {
        assertAllCorrect(wordsWithSyllableCount2);
    }

    @Test
    void originalCorpusGroup3() {
        assertAllCorrect(wordsWithSyllableCount3);
    }

    @Test
    void originalCorpusGroup4() {
        assertAllCorrect(wordsWithSyllableCount4);
    }

    @Test
    void protection() {
        assertAllCorrect(protectionCases);
    }

    @Test
    void lockedPreExistingBehaviour() {
        assertAllCorrect(lockedPreExisting);
    }

    @Test
    @Disabled("Known hiat-collapse bug captured in Phase 1; enabled by the Phase 3 fix.")
    void hiat() {
        assertAllCorrect(hiatCases);
    }

    @Test
    @Disabled("Known CSV-substring-bleed bug captured in Phase 1; enabled by the Phase 2 fix.")
    void csvBleed() {
        assertAllCorrect(csvBleedCases);
    }

    /**
     * Asserts every word in the map at once and reports ALL mismatches in one
     * failure, so a single run shows the full picture instead of stopping at the
     * first wrong word.
     */
    private void assertAllCorrect(Map<String, Integer> corpus) {
        String mismatches = corpus.entrySet().stream()
                .map(e -> Map.entry(e.getKey(), Map.entry(e.getValue(), syllables.syllablesPerWord(e.getKey()))))
                .filter(e -> !e.getValue().getKey().equals(e.getValue().getValue()))
                .map(e -> String.format("%s: expected %d but was %d",
                        e.getKey(), e.getValue().getKey(), e.getValue().getValue()))
                .collect(Collectors.joining("\n"));
        assertTrue(mismatches.isEmpty(), () -> "Syllable count mismatches:\n" + mismatches);
    }

    private static Map<String, Integer> mapOf(Object... kv) {
        Map<String, Integer> m = new LinkedHashMap<>();
        for (int i = 0; i < kv.length; i += 2) {
            m.put((String) kv[i], (Integer) kv[i + 1]);
        }
        return m;
    }
}
