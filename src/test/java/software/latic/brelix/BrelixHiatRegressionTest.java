package software.latic.brelix;

import edu.stanford.nlp.simple.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import software.latic.item.GermanTextItemData;
import software.latic.item.TextItemData;
import software.latic.translation.Translation;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Regression test for the syllable-counter fixes as seen through BRELIX1.
 *
 * <p>BRELIX1 is the only BRELIX index that depends on the syllable counter: it
 * uses {@code proz_mehrsilber}, the share of words with 3+ syllables
 * ({@code BrelixAnalyzer.syllablesGe3}). BRELIX0 does not use syllables at all.
 *
 * <p>{@code Museum} (Mu-se-um) and {@code Ideen} (I-de-en) are 3-syllable hiat
 * words that the pre-fix counter undercounted to 2, so neither crossed the 3+
 * threshold. This test pins the now-correct behaviour: both count, lifting
 * BRELIX1 from ~81.33 (syllablesGe3=0, pre-fix) to 98.0 (syllablesGe3=2). If the
 * hiat fix ever regresses, syllablesGe3 drops back to 0 and this test fails.
 *
 * <p>The six calibrated reference books (see {@code BrelixReferenceTest}) are
 * unaffected by the fixes, so their BRELIX values stay frozen there; this test
 * guards the complementary case of a text that DOES contain hiat words.
 *
 * <p>The locale must be German so the German syllable counter is selected, but
 * it is captured and restored afterwards: the Translation locale is a shared
 * singleton and BrelixReferenceTest relies on the ambient (non-German) default,
 * so leaking German here would break it via TagMapper.
 */
public class BrelixHiatRegressionTest {

    private Locale originalLocale;

    @AfterEach
    void restoreLocale() {
        Translation.getInstance().setLocale(originalLocale);
    }

    @Test
    void hiatWordsCountAsPolysyllabicAndLiftBrelix1() {
        originalLocale = Translation.getInstance().getLocale();
        Translation.getInstance().setLocale(Locale.GERMAN);

        // 6 content words: Das(1) Museum(3) zeigt(1) viele(2) neue(2) Ideen(3).
        String text = "Das Museum zeigt viele neue Ideen.";

        TextItemData data = new GermanTextItemData(text);
        data.setPagesCount(1);
        data.setFontSizeMm(6.0);              // font-size diff = 0
        data.setWordCount(6);
        data.setSentenceCount(1);
        data.setAverageSentenceLengthWords(6.0);
        data.setTypeTokenRatio(1.0);

        Document doc = new Document(text);
        BrelixAnalyzer.getInstance().analyze(data, doc);

        // Core guard: Museum and Ideen are each counted as 3+ syllables.
        assertEquals("2", data.getBrelixDebugMap().get("syllablesGe3"),
                "Museum (Mu-se-um) and Ideen (I-de-en) must each count as 3+ syllables");
        assertEquals("33.3333", data.getBrelixDebugMap().get("proz_mehrsilber"),
                "share of polysyllabic words = 2/6");

        // End-to-end freeze: 6*5 + 6*3 + (33.3333 + 66.6667)/100 * 50 = 98.0.
        // Without the hiat fix syllablesGe3 would be 0 and BRELIX1 ~81.33.
        assertEquals(98.0, data.getBrelix1Score(), 0.01, "BRELIX1 with hiat words counted");
    }
}
