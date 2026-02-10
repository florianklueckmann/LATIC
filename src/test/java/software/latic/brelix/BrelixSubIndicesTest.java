package software.latic.brelix;

import edu.stanford.nlp.simple.Document;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BrelixSubIndicesTest {

    private final BrelixAnalyzer analyzer = BrelixAnalyzer.getInstance();

    @Test
    void testCalculateWortschwMinusC() {
        assertEquals(10, analyzer.calculateWortschwMinusC(6, 2, 2, 4));
        assertEquals(0, analyzer.calculateWortschwMinusC(0, 0, 0, 0));
        assertEquals(5, analyzer.calculateWortschwMinusC(5, 5, 2, 3));
    }

    @Test
    void testCalculateWortschwAdditiv() {
        assertEquals(12, analyzer.calculateWortschwAdditiv(6, 2, 4));
        assertEquals(0, analyzer.calculateWortschwAdditiv(0, 0, 0));
    }

    @Test
    void testCalculateProzMehrsilber() {
        assertEquals(50.0, analyzer.calculateProzMehrsilber(5, 10), 0.001);
        assertEquals(0.0, analyzer.calculateProzMehrsilber(0, 10), 0.001);
        assertEquals(100.0, analyzer.calculateProzMehrsilber(10, 10), 0.001);
    }

    @Test
    void testCalculateProzWortschwMinusC() {
        assertEquals(25.0, analyzer.calculateProzWortschwMinusC(5, 20), 0.001);
        assertEquals(0.0, analyzer.calculateProzWortschwMinusC(0, 20), 0.001);
    }

    @Test
    void testCalculateAnteilLangeWoerter() {
        assertEquals(20.0, analyzer.calculateAnteilLangeWoerter(4, 20), 0.001);
        assertEquals(0.0, analyzer.calculateAnteilLangeWoerter(0, 20), 0.001);
    }

    @Test
    void testCalculateWoerterSeite() {
        assertEquals(100.0, analyzer.calculateWoerterSeite(100, 1), 0.001);
        assertEquals(50.0, analyzer.calculateWoerterSeite(100, 2), 0.001);
        assertEquals(100.0, analyzer.calculateWoerterSeite(100, 0), 0.001, "Should handle 0 pages by using 1");
    }

    @Test
    void testCalculateSchriftgroesseDiff() {
        assertEquals(0.0, analyzer.calculateSchriftgroesseDiff(6.0), 0.001);
        assertEquals(2.0, analyzer.calculateSchriftgroesseDiff(4.0), 0.001);
        assertEquals(-2.0, analyzer.calculateSchriftgroesseDiff(8.0), 0.001);
    }

    @Test
    void testCalculateLix() {
        assertEquals(40.0, analyzer.calculateLix(20.0, 20.0), 0.001);
    }

    @Test
    void testCalculateLixPlus() {
        // LIX + (schriftgröße_diff*20) + (Wörter/Seiten)*3
        // 40 + (0 * 20) + (100 * 3) = 340
        assertEquals(340.0, analyzer.calculateLixPlus(40.0, 0.0, 100.0), 0.001);
        // 40 + (1 * 20) + (50 * 3) = 40 + 20 + 150 = 210
        assertEquals(210.0, analyzer.calculateLixPlus(40.0, 1.0, 50.0), 0.001);
    }

    @Test
    void testCalculateBrelix0() {
        // LIX + proz_wortschw_minus_c/5
        // 40 + 25/5 = 45
        assertEquals(45.0, analyzer.calculateBrelix0(40.0, 25.0), 0.001);
    }

    @Test
    void testCalculateBrelix1() {
        // satzlaenge * 5 + woerter_seite * 3 + (proz_mehrsilber + proz_wortschw_minus_c) / 100.0 * 50
        // 10 * 5 + 50 * 3 + (10 + 20) / 100 * 50 = 50 + 150 + 0.3 * 50 = 200 + 15 = 215
        assertEquals(215.0, analyzer.calculateBrelix1(10.0, 50.0, 10.0, 20.0), 0.001);
    }

    @Test
    void testCalculateBrelix2() {
        // satzlaenge * 5 + woerter_seite * 3 + (proz_mehrsilber + proz_wortschw_minus_c) / 100.0 * 100
        // 10 * 5 + 50 * 3 + (10 + 20) / 100 * 100 = 50 + 150 + 30 = 230
        assertEquals(230.0, analyzer.calculateBrelix2(10.0, 50.0, 10.0, 20.0), 0.001);
    }

    @Test
    void testCalculateBrelix3() {
        // (schriftgroesse_diff * 20.0) + brelix2
        // (1 * 20) + 230 = 250
        assertEquals(250.0, analyzer.calculateBrelix3(1.0, 230.0), 0.001);
    }

    @Test
    void testCalculateBrelix3Neu() {
        // (schriftgroesse_diff * 20.0) + satzlaenge * 5.0 + woerter_seite * 3.0 + ((proz_mehrsilber + proz_wortschw_additiv) / 100.0 * 100.0)
        // (1 * 20) + 10 * 5 + 50 * 3 + (10 + 30) = 20 + 50 + 150 + 40 = 260
        assertEquals(260.0, analyzer.calculateBrelix3Neu(1.0, 10.0, 50.0, 10.0, 30.0), 0.001);
    }

    @Test
    void testCalculateBrelix4() {
        // brelix3 + subordinateClauses * 5.0
        // 250 + 2 * 5 = 260
        assertEquals(260.0, analyzer.calculateBrelix4(250.0, 2), 0.001);
    }

    @Test
    void testCalculateBrelix5() {
        // brelix4 + typeTokenRatio * 100.0
        // 260 + 0.8 * 100 = 260 + 80 = 340
        assertEquals(340.0, analyzer.calculateBrelix5(260.0, 0.8), 0.001);
    }

    @Test
    void testCalculateLevel() {
        double[] thresholds = {10.0, 20.0, 30.0, 40.0, 50.0};
        assertEquals(1, analyzer.calculateLevel(5.0, thresholds));
        assertEquals(1, analyzer.calculateLevel(10.0, thresholds));
        assertEquals(2, analyzer.calculateLevel(15.0, thresholds));
        assertEquals(2, analyzer.calculateLevel(20.0, thresholds));
        assertEquals(5, analyzer.calculateLevel(45.0, thresholds));
        assertEquals(5, analyzer.calculateLevel(50.0, thresholds));
        assertEquals(6, analyzer.calculateLevel(55.0, thresholds));
    }

    @Test
    void testCountLongWords() {
        String text = "Dies ist ein sehr langes Wort und ein kurzes.";
        // langes (6), Wort (4), kurzes (6) -> 0 long words (>6)
        Document doc = new Document(text);
        assertEquals(0, analyzer.countLongWords(doc));

        text = "Donaudampfschifffahrtsgesellschaftskapitän ist ein sehr langes Wort.";
        doc = new Document(text);
        assertEquals(1, analyzer.countLongWords(doc));
        
        text = "Eins zwei drei vier fünf sechs sieben acht.";
        // sieben (6), sechs (5)... none > 6
        doc = new Document(text);
        assertEquals(0, analyzer.countLongWords(doc));

        text = "Siebenundsiebzig Jahre alt.";
        // Siebenundsiebzig (17)
        doc = new Document(text);
        assertEquals(1, analyzer.countLongWords(doc));
    }

    @Test
    void testCountSubordinateClauses() {
        String text = "Ich weiß, dass du hier bist.";
        Document doc = new Document(text);
        // German might need specific models, but Stanford CoreNLP usually detects SBAR in many languages if configured.
        // In the existing BrelixAnalyzerTest.java, it uses English text for testing this.
        text = "I know that you are here.";
        doc = new Document(text);
        int clauses = analyzer.countSubordinateClauses(doc);
        assertTrue(clauses >= 1);
    }
}
