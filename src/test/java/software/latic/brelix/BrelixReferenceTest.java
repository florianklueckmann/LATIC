package software.latic.brelix;

import edu.stanford.nlp.simple.Document;
import org.junit.jupiter.api.Test;
import software.latic.item.GermanTextItemData;
import software.latic.item.TextItemData;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BrelixReferenceTest {

    @Test
    void testBrelixReferenceExample() {
        // Text from brelix-beispiel.md
        String text = "Flora spielt mit Mama Ball. Opa sitzt im Schaukelstuhl und lacht.";
        TextItemData data = new GermanTextItemData(text);
        data.setPagesCount(1);
        data.setFontSizeMm(6.0); // diff = 0
        data.setWordCount(11);
        data.setSentenceCount(2);
        data.setAverageSentenceLengthWords(5.5);
        data.setTypeTokenRatio(1.0); // All 11 words are unique

        Document doc = new Document(text);
        BrelixAnalyzer analyzer = BrelixAnalyzer.getInstance();
        analyzer.analyze(data, doc);

        // Reference values from brelix-beispiel.md
        assertEquals(11, data.getWordCount(), "Word count should be 11");
        assertEquals(2, data.getSentenceCount(), "Sentence count should be 2");
        assertEquals(5.5, data.getAverageSentenceLengthWords(), 0.01, "Average sentence length should be 5.5");

        // Our implementation counts occurrence-based:
        // Multigraphemes: spielt(sp,ie=2), schaukelstuhl(sch,st,Dehnungs-h=3), lacht(ch=1) = 6
        // c_mehr: schaukelstuhl(sch=1), lacht(ch=1) = 2
        // Rare letters: schaukelstuhl(c=1), lacht(c=1) = 2
        // Consonant clusters: flora(Fl=1), spielt(sp=1), sitzt(tzt=1), schaukelstuhl(st=1) = 4
        // wortschw_minus_c = 6 - 2 + 2 + 4 = 10
        // proz_wortschw_minus_c = 10/11 * 100 = 90.91%

        // LIX = 5.5 + 9.09 = 14.59
        assertEquals(14.59, data.getLixReadabilityScore(), 0.1, "LIX should be 14.59");

        // BRELIX 0 = 14.59 + 90.91 / 5 = 32.77
        // (Reference shows 30.95 because it misses the Dehnungs-h in "Schaukelstuhl")
        assertEquals(32.77, data.getBrelix0Score(), 0.1, "BRELIX 0");

        // BRELIX 1 = (5.5*5) + (11*3) + ((9.09+90.91)/100*50) = 27.5 + 33 + 50 = 110.5
        assertEquals(110.5, data.getBrelix1Score(), 0.1, "BRELIX 1");

        // BRELIX 2 = (5.5*5) + (11*3) + ((9.09+90.91)/100*100) = 27.5 + 33 + 100 = 160.5
        assertEquals(160.5, data.getBrelix2Score(), 0.1, "BRELIX 2");

        // BRELIX 3 = (0*20) + 160.5 = 160.5 (font size diff = 0)
        assertEquals(160.5, data.getBrelix3Score(), 0.1, "BRELIX 3");

        // BRELIX 4 = 160.5 + (2+0)*5 = 170.5
        assertEquals(170.5, data.getBrelix4Score(), 0.1, "BRELIX 4");

        // BRELIX 5 = 170.5 + (1.0 * 100) = 270.5
        assertEquals(270.5, data.getBrelix5Score(), 0.1, "BRELIX 5");
    }
}
