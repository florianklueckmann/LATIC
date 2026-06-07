package software.latic.brelix;

import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;
import software.latic.Logging;
import software.latic.helper.SentenceSegmentation;
import software.latic.item.TextItemData;
import software.latic.syllables.SyllableProvider;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BrelixAnalyzer {

    private static final BrelixAnalyzer instance = new BrelixAnalyzer();

    private static final double[] LIX_THRESHOLDS = {10, 15, 20, 25, 30};
    private static final double[] LIX_PLUS_THRESHOLDS = {50, 70, 90, 110, 130};
    private static final double[] BRELIX0_THRESHOLDS = {30, 40, 50, 60, 70};
    private static final double[] BRELIX1_THRESHOLDS = {50, 65, 80, 95, 110};
    private static final double[] BRELIX2_THRESHOLDS = {70, 90, 110, 130, 150};
    private static final double[] BRELIX3_THRESHOLDS = {90, 120, 150, 180, 210};
    private static final double[] BRELIX3_NEU_THRESHOLDS = {90, 120, 150, 180, 210};
    private static final double[] BRELIX4_THRESHOLDS = {90, 125, 160, 200, 240};
    private static final double[] BRELIX5_THRESHOLDS = {100, 150, 200, 250, 300};

    public static BrelixAnalyzer getInstance() {
        return instance;
    }

    private BrelixAnalyzer() {}

    public void analyze(TextItemData data, Document doc) {
        if (data == null || doc == null) return;

        int wordCount = data.getWordCount();
        if (wordCount == 0) return;

        long multiGraphems = 0;
        long multiGraphemsBinary = 0;
        long rareLetters = 0;
        long rareLettersBinary = 0;
        long rareLettersWithoutC = 0;
        long consonantClusters = 0;
        long cInMultiGraphems = 0;
        int syllablesGe3 = 0;

        for (Sentence sent : doc.sentences()) {
            for (String word : sent.words()) {
                if (word.matches("\\W+")) continue;

                String lowerWord = word.toLowerCase();
                multiGraphems += countMultiGraphems(lowerWord);
                multiGraphemsBinary += containsMultiGrapheme(lowerWord) ? 1 : 0;
                rareLetters += countRareLetters(lowerWord);
                rareLettersBinary += containsRareLetter(lowerWord) ? 1 : 0;
                rareLettersWithoutC += countRareLettersWithoutC(lowerWord);
                consonantClusters += countConsonantClusters(lowerWord);
                cInMultiGraphems += countCInMultiGraphems(lowerWord);

                if (SyllableProvider.getInstance().syllablesPerWord(lowerWord) >= 3) {
                    syllablesGe3++;
                }
            }
        }

        Logging.getInstance().debug("BrelixAnalyzer", String.format(
            "Word counts: multiGraphems=%d, multiGraphemsBinary=%d, rareLetters=%d, rareLettersBinary=%d, rareLettersWithoutC=%d, consonantClusters=%d, cInMultiGraphems=%d, syllablesGe3=%d",
            multiGraphems, multiGraphemsBinary, rareLetters, rareLettersBinary, rareLettersWithoutC, consonantClusters, cInMultiGraphems, syllablesGe3));

        long wortschwierig = calculateWortschwierig(multiGraphems, rareLetters, consonantClusters);
        long wortschw_minus_c = calculateWortschwMinusC(multiGraphems, cInMultiGraphems, rareLetters, consonantClusters);
        long wortschw_additiv = calculateWortschwAdditiv(multiGraphems, rareLetters, consonantClusters);
        // Diagnostic-only candidate matching the paper's WortSchw column. Per Brügelmann's SPSS
        // (wortschwierig = mehrgliedrig + Buchst_selten + Konshfg), all three components are
        // counted per word (binary). On Sonntagshose this reproduces the paper's 59.1% almost
        // exactly (57/96 = 59.38%). Not yet wired into the BRELIX score formulas.
        long wortschw_paper_candidate = calculateWortschwPaperCandidate(multiGraphemsBinary, rareLettersBinary, consonantClusters);
        
        Logging.getInstance().debug("BrelixAnalyzer", String.format(
            "Base metrics: wordCount=%d, multiGraphems=%d, multiGraphemsBinary=%d, rareLetters=%d, rareLettersWithoutC=%d, consonantClusters=%d, cInMultiGraphems=%d, syllablesGe3=%d, wortschwierig=%d, wortschw_minus_c=%d",
            wordCount, multiGraphems, multiGraphemsBinary, rareLetters, rareLettersWithoutC, consonantClusters, cInMultiGraphems, syllablesGe3, wortschwierig, wortschw_minus_c));

        // Prozentsätze
        double proz_mehrsilber = calculateProzMehrsilber(syllablesGe3, wordCount);
        double proz_wortschwierig = calculateProzWortschwierigkeit(wortschwierig, wordCount);
        double proz_wortschw_minus_c = calculateProzWortschwMinusC(wortschw_minus_c, wordCount);
        double proz_wortschw_paper_candidate = calculateProzWortschwMinusC(wortschw_paper_candidate, wordCount);
        
        Logging.getInstance().debug("BrelixAnalyzer", String.format(
            "Calculated rates: wortschwierig=%d, wortschw_minus_c=%d, proz_mehrsilber=%.2f%%, proz_wortschwierig=%.2f%%, proz_wortschw_minus_c=%.2f%%",
            wortschwierig, wortschw_minus_c, proz_mehrsilber, proz_wortschwierig, proz_wortschw_minus_c));

        // Nebensätze
        int subordinateClauses = countSubordinateClauses(doc);
        data.setSubordinateClausesCount(subordinateClauses);
        // Share of subordinate clauses (per sentence) — the metric reported in the
        // BRELIX reference tables ("% Nebensätze"); diagnostic only, the BRELIX4/5
        // formulas use the absolute count per the verbatim SPSS. The denominator uses
        // the same continuation-fragment correction as SimpleTextAnalyzer.sentenceCount.
        int sentenceCountForClauses = (int) doc.sentences().stream()
                .filter(s -> s.length() > 1)
                .filter(s -> !SentenceSegmentation.isContinuationFragment(s))
                .count();
        double proz_nebensaetze = sentenceCountForClauses == 0 ? 0.0
                : (double) subordinateClauses / sentenceCountForClauses * 100.0;

        // Lange Wörter (> 6 Buchstaben)
        int longWords = countLongWords(doc);
        var listLongWords = new ArrayList<String>();
        for (Sentence sent : doc.sentences()) {
            for (String word : sent.words()) {
                if (word.length() > 6 && !word.matches("\\W+")) {
                    listLongWords.add(word);
                }
            }
        }

        Logging.getInstance().debug("BrelixAnalyzer", String.format("Long words (more than 6 Characters): %s", listLongWords));

        double anteil_lange_woerter = calculateAnteilLangeWoerter(longWords, wordCount);
        
        double satzlaenge = data.getAverageSentenceLengthWords();
        double woerter_seite = calculateWoerterSeite(wordCount, data.getPagesCount());
        double schriftgroesse_diff = calculateSchriftgroesseDiff(data.getFontSizeMm());
        
        // Indizes berechnen
//        var liiix = SimpleTextAnalyzer.getInstance().setDoc(doc);
//        double lix = calculateLix(satzlaenge, anteil_lange_woerter);
        var lix = data.getLixReadabilityScore();
        double lixPlus = calculateLixPlus(lix, schriftgroesse_diff, woerter_seite);
        
        double brelix0 = calculateBrelix0(lix, proz_wortschw_minus_c);
        double brelix1 = calculateBrelix1(satzlaenge, woerter_seite, proz_mehrsilber, proz_wortschwierig);
        double brelix2 = calculateBrelix2(satzlaenge, woerter_seite, proz_mehrsilber, proz_wortschwierig);
        double brelix3 = calculateBrelix3(schriftgroesse_diff, satzlaenge, woerter_seite, proz_mehrsilber, proz_wortschwierig);
        double proz_wortschw_additiv = calculateProzWortschwMinusC(wortschw_additiv, wordCount);
        double brelix3Neu = calculateBrelix3Neu(schriftgroesse_diff, satzlaenge, woerter_seite, proz_mehrsilber, proz_wortschw_additiv);
        double brelix4 = calculateBrelix4(schriftgroesse_diff, satzlaenge, subordinateClauses, woerter_seite, proz_mehrsilber, proz_wortschwierig);
        double proz_wortversch = data.getTypeTokenRatio() * 100.0;
        double brelix5 = calculateBrelix5(schriftgroesse_diff, satzlaenge, subordinateClauses, woerter_seite, proz_mehrsilber, proz_wortschwierig, proz_wortversch);

        Logging.getInstance().debug("BrelixAnalyzer", String.format(
                "Text properties: satzlaenge=%.2f, woerter_seite=%.2f, schriftgroesse_diff=%.2f, longWords=%d, " +
                        "anteil_lange_woerter=%.2f%%, subordinateClauses=%d, proz_wortschwierig=%.2f%%, proz_wortschw_minus_c=%.2f%%, " +
                        "proz_wortschw_additiv=%.2f%%, proz_wortversch=%.2f%%, proz_mehrsilber=%.2f%%, wortschw_additiv=%d, " +
                        "wordCount=%d",
                satzlaenge, woerter_seite, schriftgroesse_diff, longWords, 
                anteil_lange_woerter, subordinateClauses, proz_wortschwierig, proz_wortschw_minus_c, 
                proz_wortschw_additiv, proz_wortversch, proz_mehrsilber, wortschw_additiv, wordCount));
        
        Logging.getInstance().debug("BrelixAnalyzer", String.format(
            "Final Scores: LIX=%.2f, LIX+=%.2f, BRELIX0=%.2f, BRELIX1=%.2f, BRELIX2=%.2f, BRELIX3=%.2f, BRELIX3_NEU=%.2f, BRELIX4=%.2f, BRELIX5=%.2f",
            lix, lixPlus, brelix0, brelix1, brelix2, brelix3, brelix3Neu, brelix4, brelix5));

        data.setLixPlusScore(lixPlus);
        data.setBrelix0Score(brelix0);
        data.setBrelix1Score(brelix1);
        data.setBrelix2Score(brelix2);
        data.setBrelix3Score(brelix3);
        data.setBrelix3NeuScore(brelix3Neu);
        data.setBrelix4Score(brelix4);
        data.setBrelix5Score(brelix5);

        data.setLixReadabilityLevel(String.valueOf(calculateLevel(lix, LIX_THRESHOLDS)));
        data.setLixPlusLevel(calculateLevel(lixPlus, LIX_PLUS_THRESHOLDS));
        data.setBrelix0Level(calculateLevel(brelix0, BRELIX0_THRESHOLDS));
        data.setBrelix1Level(calculateLevel(brelix1, BRELIX1_THRESHOLDS));
        data.setBrelix2Level(calculateLevel(brelix2, BRELIX2_THRESHOLDS));
        data.setBrelix3Level(calculateLevel(brelix3, BRELIX3_THRESHOLDS));
        data.setBrelix3NeuLevel(calculateLevel(brelix3Neu, BRELIX3_NEU_THRESHOLDS));
        data.setBrelix4Level(calculateLevel(brelix4, BRELIX4_THRESHOLDS));
        data.setBrelix5Level(calculateLevel(brelix5, BRELIX5_THRESHOLDS));

        data.setBrelixDebugInfo(String.format(
            "wordCount=%d, multiGraphems=%d, multiGraphemsBinary=%d, rareLetters=%d, rareLettersBinary=%d, rareLettersWithoutC=%d, consonantClusters=%d, cInMultiGraphems=%d, syllablesGe3=%d%n" +
            "wortschwierig=%d, wortschw_minus_c=%d, wortschw_additiv=%d, wortschw_paper_candidate=%d%n" +
            "proz_mehrsilber=%.2f%%, proz_wortschwierig=%.2f%%, proz_wortschw_minus_c=%.2f%%, proz_wortschw_additiv=%.2f%%, proz_wortschw_paper_candidate=%.2f%%, proz_wortversch=%.2f%%%n" +
            "longWords=%d, anteil_lange_woerter=%.2f%%, subordinateClauses=%d%n" +
            "satzlaenge=%.2f, woerter_seite=%.2f, schriftgroesse_diff=%.2f%n" +
            "LIX=%.2f, LIX+=%.2f%n" +
            "BRELIX0=%.2f, BRELIX1=%.2f, BRELIX2=%.2f, BRELIX3=%.2f, BRELIX3_NEU=%.2f, BRELIX4=%.2f, BRELIX5=%.2f%n" +
            "longWordsList=%s",
            wordCount, multiGraphems, multiGraphemsBinary, rareLetters, rareLettersBinary, rareLettersWithoutC, consonantClusters, cInMultiGraphems, syllablesGe3,
            wortschwierig, wortschw_minus_c, wortschw_additiv, wortschw_paper_candidate,
            proz_mehrsilber, proz_wortschwierig, proz_wortschw_minus_c, proz_wortschw_additiv, proz_wortschw_paper_candidate, proz_wortversch,
            longWords, anteil_lange_woerter, subordinateClauses,
            satzlaenge, woerter_seite, schriftgroesse_diff,
            lix, lixPlus,
            brelix0, brelix1, brelix2, brelix3, brelix3Neu, brelix4, brelix5,
            listLongWords));

        LinkedHashMap<String, String> map = data.getBrelixDebugMap();
        map.put("wordCount", String.valueOf(wordCount));
        map.put("multiGraphems", String.valueOf(multiGraphems));
        map.put("multiGraphemsBinary", String.valueOf(multiGraphemsBinary));
        map.put("rareLetters", String.valueOf(rareLetters));
        map.put("rareLettersBinary", String.valueOf(rareLettersBinary));
        map.put("rareLettersWithoutC", String.valueOf(rareLettersWithoutC));
        map.put("consonantClusters", String.valueOf(consonantClusters));
        map.put("cInMultiGraphems", String.valueOf(cInMultiGraphems));
        map.put("syllablesGe3", String.valueOf(syllablesGe3));
        map.put("wortschwierig", String.valueOf(wortschwierig));
        map.put("wortschw_minus_c", String.valueOf(wortschw_minus_c));
        map.put("wortschw_additiv", String.valueOf(wortschw_additiv));
        map.put("wortschw_paper_candidate", String.valueOf(wortschw_paper_candidate));
        map.put("proz_mehrsilber", String.format(Locale.ROOT, "%.4f", proz_mehrsilber));
        map.put("proz_wortschwierig", String.format(Locale.ROOT, "%.4f", proz_wortschwierig));
        map.put("proz_wortschw_minus_c", String.format(Locale.ROOT, "%.4f", proz_wortschw_minus_c));
        map.put("proz_wortschw_additiv", String.format(Locale.ROOT, "%.4f", proz_wortschw_additiv));
        map.put("proz_wortschw_paper_candidate", String.format(Locale.ROOT, "%.4f", proz_wortschw_paper_candidate));
        map.put("proz_wortversch", String.format(Locale.ROOT, "%.4f", proz_wortversch));
        map.put("longWords", String.valueOf(longWords));
        map.put("anteil_lange_woerter", String.format(Locale.ROOT, "%.4f", anteil_lange_woerter));
        map.put("subordinateClauses", String.valueOf(subordinateClauses));
        map.put("proz_nebensaetze", String.format(Locale.ROOT, "%.4f", proz_nebensaetze));
        map.put("satzlaenge", String.format(Locale.ROOT, "%.4f", satzlaenge));
        map.put("woerter_seite", String.format(Locale.ROOT, "%.4f", woerter_seite));
        map.put("schriftgroesse_diff", String.format(Locale.ROOT, "%.4f", schriftgroesse_diff));
        map.put("LIX", String.format(Locale.ROOT, "%.4f", lix));
        map.put("LIX+", String.format(Locale.ROOT, "%.4f", lixPlus));
        map.put("BRELIX0", String.format(Locale.ROOT, "%.4f", brelix0));
        map.put("BRELIX1", String.format(Locale.ROOT, "%.4f", brelix1));
        map.put("BRELIX2", String.format(Locale.ROOT, "%.4f", brelix2));
        map.put("BRELIX3", String.format(Locale.ROOT, "%.4f", brelix3));
        map.put("BRELIX3_NEU", String.format(Locale.ROOT, "%.4f", brelix3Neu));
        map.put("BRELIX4", String.format(Locale.ROOT, "%.4f", brelix4));
        map.put("BRELIX5", String.format(Locale.ROOT, "%.4f", brelix5));
        map.put("longWordsList", listLongWords.toString());
    }

    // --- Extracted calculation functions ---

    /**
     * Calculates Brügelmann's raw word difficulty score.
     * wortschwierig = mehrgliedrig + Buchst_selten + Konshfg
     */
    long calculateWortschwierig(long multiGraphems, long rareLetters, long consonantClusters) {
        return multiGraphems + rareLetters + consonantClusters;
    }

    /**
     * Calculates the word difficulty score excluding C-related multi-graphemes.
     * wortschw_minus_c = mehrgliedrig - c_mehr + Buchst_selten + Konshfg
     */
    long calculateWortschwMinusC(long multiGraphems, long cInMultiGraphems, long rareLetters, long consonantClusters) {
        return multiGraphems - cInMultiGraphems + rareLetters + consonantClusters;
    }

    /**
     * Calculates the additive word difficulty score (without C correction).
     * wortschw_additiv = mehrgliedrig + Buchst_selten + Konshfg
     */
    long calculateWortschwAdditiv(long multiGraphems, long rareLetters, long consonantClusters) {
        return multiGraphems + rareLetters + consonantClusters;
    }

    /**
     * Diagnostic candidate reproducing the BRELIX paper's "WortSchw" column, matching
     * Brügelmann's SPSS {@code wortschwierig = mehrgliedrig + Buchst_selten + Konshfg}
     * with all three components counted per word (binary). On the Sonntagshose reference
     * this hits the paper's 59.1% almost exactly (57/96 = 59.38%), better than
     * wortschw_minus_c or wortschw_additiv. Not yet wired into the BRELIX score formulas.
     */
    long calculateWortschwPaperCandidate(long multiGraphemsBinary, long rareLettersBinary, long consonantClusters) {
        return multiGraphemsBinary + rareLettersBinary + consonantClusters;
    }

    /**
     * Calculates the percentage of polysyllabic words (words with 3+ syllables).
     */
    double calculateProzMehrsilber(int syllablesGe3, int wordCount) {
        return (double) syllablesGe3 / wordCount * 100;
    }

    /**
     * Calculates the percentage of word difficulty minus C.
     */
    double calculateProzWortschwMinusC(long wortschw_minus_c, int wordCount) {
        return (double) wortschw_minus_c / wordCount * 100;
    }

    /**
     * Calculates the percentage of Brügelmann's raw word difficulty score.
     */
    double calculateProzWortschwierigkeit(long wortschwierig, int wordCount) {
        return (double) wortschwierig / wordCount * 100;
    }

    /**
     * Counts the number of long words (more than 6 characters) in the document.
     */
    int countLongWords(Document doc) {
        int longWords = 0;
        for (Sentence sent : doc.sentences()) {
            for (String word : sent.words()) {
                if (word.length() > 6 && !word.matches("\\W+")) {
                    longWords++;
                }
            }
        }
        return longWords;
    }

    /**
     * Calculates the percentage of long words (more than 6 characters).
     */
    double calculateAnteilLangeWoerter(int longWords, int wordCount) {
        return (double) longWords / wordCount * 100;
    }

    /**
     * Calculates the number of words per page.
     */
    double calculateWoerterSeite(int wordCount, int pagesCount) {
        return (double) wordCount / Math.max(1, pagesCount);
    }

    /**
     * Calculates the font size difference from the baseline (6.0 mm).
     */
    double calculateSchriftgroesseDiff(double fontSizeMm) {
        return 6.0 - fontSizeMm;
    }

    /**
     * Calculates the LIX (Läsbarhetsindex) score.
     * LIX = average sentence length + percentage of long words
     */
    double calculateLix(double satzlaenge, double anteil_lange_woerter) {
//        return SimpleTextAnalyzer.getInstance().lixReadabilityScore();
        return satzlaenge + anteil_lange_woerter;
    }

    /**
     * Calculates the LIX+ score.
     * SPSS Code:
     * compute LIX_Plus = LIX + (schriftgröße_diff*20) + (Wörter/Seiten)*3.
     */
    double calculateLixPlus(double lix, double schriftgroesse_diff, double woerter_seite) {
        return lix + (schriftgroesse_diff * 20) + (woerter_seite * 3);
    }

    /**
     * Calculates the BRELIX0 score.
     * SPSS Code:
     * compute brelix0 = LIX + proz_wortschw_minus_c/5.
     */
    double calculateBrelix0(double lix, double proz_wortschw_minus_c) {
        return lix + proz_wortschw_minus_c / 5.0;
    }

    /**
     * Calculates the BRELIX1 score.
     * SPSS Code:
     * compute brelix1 = (Wörter/Sätze)*5 + (Wörter/Seiten)*3 + (Mehrsilber+wortschwierig)/Wörter*50.
     */
    double calculateBrelix1(double satzlaenge, double woerter_seite, double proz_mehrsilber, double proz_wortschwierig) {
        return satzlaenge * 5 + woerter_seite * 3 + (proz_mehrsilber + proz_wortschwierig) / 100.0 * 50;
    }

    /**
     * Calculates the BRELIX2 score.
     * SPSS Code:
     * compute brelix2 = (Wörter/Sätze)*5 + (Wörter/Seiten)*3 + (Mehrsilber+wortschwierig)/Wörter*100.
     */
    double calculateBrelix2(double satzlaenge, double woerter_seite, double proz_mehrsilber, double proz_wortschwierig) {
        return satzlaenge * 5 + woerter_seite * 3 + (proz_mehrsilber + proz_wortschwierig) / 100.0 * 100;
    }

    /**
     * Calculates the BRELIX3 score.
     * SPSS Code:
     * compute brelix3 = (schriftgröße_diff*20)+ (Wörter/Sätze)*5 + (Wörter/Seiten)*3 + (Mehrsilber+wortschwierig)/Wörter*100.
     */
    double calculateBrelix3(double schriftgroesse_diff, double satzlaenge, double woerter_seite, double proz_mehrsilber, double proz_wortschwierig) {
        return (schriftgroesse_diff * 20) + satzlaenge * 5 + woerter_seite * 3 + (proz_mehrsilber + proz_wortschwierig) / 100.0 * 100;
    }

    /**
     * Calculates the BRELIX3 Neu score.
     * SPSS Code:
     * compute brelix3_neu = (schriftgröße_diff*20)+ (Wörter/Sätze)*5  +  (Wörter/Seiten)*3 + (Mehrsilber+wortschw_additiv)/Wörter*100.
     * Same as BRELIX3 but uses wortschw_additiv (without C correction) instead of wortschw_minus_c.
     */
    double calculateBrelix3Neu(double schriftgroesse_diff, double satzlaenge, double woerter_seite, double proz_mehrsilber, double proz_wortschw_additiv) {
        return (schriftgroesse_diff * 20) + satzlaenge * 5 + woerter_seite * 3 + (proz_mehrsilber + proz_wortschw_additiv) / 100.0 * 100;
    }

    /**
     * Calculates the BRELIX4 score.
     * SPSS Code:
     * compute brelix4 = (schriftgröße_diff*20)+ (Wörter/Sätze+Nebensätze)*5 + (Wörter/Seiten)*3 + (Mehrsilber+wortschwierig)/Wörter*100.
     * In SPSS, division has higher precedence than addition, so Wörter/Sätze+Nebensätze = (Wörter/Sätze)+Nebensätze.
     */
    double calculateBrelix4(double schriftgroesse_diff, double satzlaenge, int subordinateClauses, double woerter_seite, double proz_mehrsilber, double proz_wortschwierig) {
        return (schriftgroesse_diff * 20) + (satzlaenge + subordinateClauses) * 5 + woerter_seite * 3 + (proz_mehrsilber + proz_wortschwierig) / 100.0 * 100;
    }

    /**
     * Calculates the BRELIX5 score.
     * SPSS Code:
     * compute brelix5 = (schriftgröße_diff*20)+ (Wörter/Sätze+Nebensätze)*5 + (Wörter/Seiten)*3 + (Mehrsilber+wortschwierig)/Wörter*100 + proz_wortversch.
     */
    double calculateBrelix5(double schriftgroesse_diff, double satzlaenge, int subordinateClauses, double woerter_seite, double proz_mehrsilber, double proz_wortschwierig, double proz_wortversch) {
        return (schriftgroesse_diff * 20) + (satzlaenge + subordinateClauses) * 5 + woerter_seite * 3 + (proz_mehrsilber + proz_wortschwierig) / 100.0 * 100 + proz_wortversch;
    }

    int calculateLevel(double score, double[] thresholds) {
        for (int i = 0; i < thresholds.length; i++) {
            if (score <= thresholds[i]) {
                return i + 1;
            }
        }
        return 6;
    }

    final String[] CLUSTERS = {"sch", "ch", "ck", "ph", "rh", "th", "sp", "st", "ng", "ie", "ei", "eu", "äu"};

    // st/sp only produce their combined sound [ʃt]/[ʃp] at word-initial position
    private static final java.util.Set<String> WORD_INITIAL_CLUSTERS = java.util.Set.of("st", "sp");

    boolean containsMultiGrapheme(String word) {
        // <ch>, <ck>, <ph>, <rh>, <th>, <sch>, <sp>, <st>, <ng>, <ie>, <ei>, <eu>, <äu>
        for (String c : CLUSTERS) {
            if (WORD_INITIAL_CLUSTERS.contains(c)) {
                if (word.startsWith(c)) return true;
            } else {
                if (word.contains(c)) return true;
            }
        }
        return false;
    }

    int countMultiGraphems(String word) {
        int count = 0;
        String temp = word.toLowerCase();
        for (String c : CLUSTERS) {
            boolean wordInitialOnly = WORD_INITIAL_CLUSTERS.contains(c);
            int index = temp.indexOf(c);
            while (index != -1) {
                if (wordInitialOnly && index != 0) break;
                Logging.getInstance().debug("BrelixAnalyzer", String.format("Found %s at index %d in word %s", c, index, word));
                count++;
                temp = temp.substring(0, index) + " ".repeat(c.length()) + temp.substring(index + c.length());
                index = temp.indexOf(c);
            }
        }
        return count;
    }

    int countCInMultiGraphems(String word) {
        int c_mehr = 0;
        String temp = word.toLowerCase();
        String[] cClusters = {"sch", "ch", "ck"};
        for (String c : cClusters) {
            int index = temp.indexOf(c);
            while (index != -1) {
                c_mehr++;
                temp = temp.substring(0, index) + " ".repeat(c.length()) + temp.substring(index + c.length());
                index = temp.indexOf(c);
            }
        }
        return c_mehr;
    }

    boolean containsRareLetter(String word) {
        String rareLetters = "cqßxyäöü";
        for (char c : word.toCharArray()) {
            if (rareLetters.indexOf(c) != -1) {
                return true;
            }
        }
        return false;
    }

    int countRareLetters(String word) {
        int count = 0;
        // <c>, <q>, <ß>, <x>, <y>, <ä>, <ö>, <ü>
        String rare = "cqßxyäöü";
        for (char c : word.toCharArray()) {
            if (rare.indexOf(c) != -1) {
                count++;
            }
        }
        return count;
    }

    int countRareLettersWithoutC(String word) {
        int count = 0;
        // <q>, <ß>, <x>, <y>, <ä>, <ö>, <ü> (excluding <c>)
        String rare = "qßxyäöü";
        for (char c : word.toCharArray()) {
            if (rare.indexOf(c) != -1) {
                count++;
            }
        }
        return count;
    }

    boolean containsConsonantCluster(String word) {
        return countConsonantClusters(word) > 0;
    }

    int countConsonantClusters(String word) {
        if (word == null || word.length() < 2) return 0;

        String processedWord = normalizeConsonantSounds(word);
        Pattern p = Pattern.compile("[bcdfghjklmnpqrstvwxzß§]+");
        Matcher m = p.matcher(processedWord);

        while (m.find()) {
            String cluster = m.group();
            int start = m.start();
            int end = m.end();

            if (start == 0) {
                // Wortanfang = Silbenanfang der 1. Silbe
                if (cluster.length() >= 2) return 1;
            } else if (end == processedWord.length()) {
                // Wortende = Silbenende der letzten Silbe
                if (cluster.length() >= 3) return 1;
            } else {
                // Wortmitte: Trennung nach einer Heuristik:
                // Der letzte Konsonant geht nach rechts zum nächsten Silbenanfang.
                int splitPoint = cluster.length() - 1;
                String left = cluster.substring(0, splitPoint);
                String right = cluster.substring(splitPoint);

                if (left.length() >= 3) return 1; // Silbenende >= 3
                if (right.length() >= 2) return 1; // Silbenanfang >= 2
            }
        }

        return 0;
    }

    private String normalizeConsonantSounds(String word) {
        String processedWord = word.toLowerCase();

        processedWord = processedWord
                .replaceAll("(st|sp)(?=[bcdfghjklmnpqrstvwxzß])", "§")
                .replaceAll("(st|sp)(?=[aeiouäöüéàáy])", " ");

        return processedWord
                .replace("sch", "§")
                .replace("ch", "§")
                .replace("ck", "§")
                .replace("ph", "§")
                .replace("rh", "§")
                .replace("th", "§")
                .replace("ng", "§")
                .replace("tz", "§")
                .replaceAll("([bcdfghjklmnpqrstvwxzß§])\\1+", "$1");
    }

    /**
     * Universal-Dependencies relations whose dependent token heads a subordinate
     * clause (Nebensatz). This mirrors the linguistic definition (a dependent,
     * verb-final clause): {@code advcl} = adverbial clause (weil/wenn/als…),
     * {@code acl}/{@code acl:relcl} = adnominal/relative clause, {@code ccomp} =
     * clausal complement (dass-clause, indirect question), {@code csubj} = clausal
     * subject, {@code xcomp} = open clausal complement (zu-infinitive). Coordination
     * ({@code conj}/{@code cc}) is deliberately excluded — those are Hauptsätze.
     * Relations are matched on their base label, since the German model subtypes
     * them (e.g. {@code advcl:wenn}).
     */
    private static final Set<String> SUBORDINATE_CLAUSE_RELATIONS =
            Set.of("advcl", "acl", "ccomp", "csubj", "xcomp");

    /**
     * Counts subordinate clauses (Nebensätze) via dependency relations, consistent
     * with the rest of the NLP analysis (e.g. passive detection in NlpTextAnalyzer
     * uses the same {@code incomingDependencyLabels}).
     *
     * <p>Replaces the earlier constituency checks: {@code SBAR} (English-only, scored
     * 0 for every German text) and nested {@code S} nodes (parser-dependent). The
     * dependency relations above cover the German Nebensatz types directly and also
     * work on the English parse (same UD relations).
     *
     * <p>Known limitation: still undercounts vs. Brügelmann's manual count (Hanna:
     * 10 vs. ~16 → 14.5 % vs. 27 %). The dominant residual cause is sentence
     * over-segmentation around dialogue/quotes (which also lowers Wörter/Satz), plus
     * occasional parser errors — not the relation set. BRELIX4/5 stay "experimental".
     * See docs/BRELIX-Kalibrierung-Befunde.md §4.7.
     */
    int countSubordinateClauses(Document doc) {
        int count = 0;
        for (Sentence sent : doc.sentences()) {
            for (Optional<String> label : sent.incomingDependencyLabels()) {
                if (label.isEmpty()) {
                    continue;
                }
                String base = label.get().split(":", 2)[0];
                if (SUBORDINATE_CLAUSE_RELATIONS.contains(base)) {
                    count++;
                }
            }
        }
        return count;
    }
}
