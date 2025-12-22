package software.latic.brelix;

import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.trees.Tree;
import software.latic.item.TextItemData;
import software.latic.syllables.SyllableProvider;

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
        long rareLetters = 0;
        long consonantClusters = 0;
        long multiGraphemsMinusC = 0;
        int syllablesGe3 = 0;

        for (Sentence sent : doc.sentences()) {
            for (String word : sent.words()) {
                if (word.matches("\\W+")) continue;
                
                String lowerWord = word.toLowerCase();
                multiGraphems += countMultiGraphems(lowerWord);
                rareLetters += countRareLetters(lowerWord);
                consonantClusters += countConsonantClusters(lowerWord);
                
                multiGraphemsMinusC += countMultiGraphemsMinusC(lowerWord);
                
                if (SyllableProvider.getInstance().syllablesPerWord(lowerWord) >= 3) {
                    syllablesGe3++;
                }
            }
        }

        double wortschw_minus_c = (double) (multiGraphemsMinusC + rareLetters + consonantClusters) / wordCount;
        
        // Prozentsätze
        double proz_mehrsilber = (double) syllablesGe3 / wordCount * 100;
        double proz_wortschw_minus_c = wortschw_minus_c * 100; // Laut Formel ist es oft ein Prozentsatz oder gewichteter Wert
        
        // Nebensätze
        int subordinateClauses = countSubordinateClauses(doc);
        data.setSubordinateClausesCount(subordinateClauses);

        // Lange Wörter (> 6 Buchstaben)
        int longWords = 0;
        for (Sentence sent : doc.sentences()) {
            for (String word : sent.words()) {
                if (word.length() > 6 && !word.matches("\\W+")) {
                    longWords++;
                }
            }
        }
        double anteil_lange_woerter = (double) longWords / wordCount * 100;
        
        double satzlaenge = data.getAverageSentenceLengthWords();
        double woerter_seite = (double) wordCount / Math.max(1, data.getPagesCount());
        double schriftgroesse_diff = 6.0 - data.getFontSizeMm();
        
        // Indizes berechnen
        double lix = satzlaenge + anteil_lange_woerter;
        double lixPlus = lix + (schriftgroesse_diff * 20) + (woerter_seite * 3);
        
        double brelix0 = lix + (proz_wortschw_minus_c / 5.0);
        double brelix1 = (satzlaenge * 5) + (woerter_seite * 3) + ((proz_mehrsilber + proz_wortschw_minus_c) / 100.0 * 50);
        double brelix2 = (satzlaenge * 5) + (woerter_seite * 3) + ((proz_mehrsilber + proz_wortschw_minus_c) / 100.0 * 100);
        double brelix3 = (schriftgroesse_diff * 20) + brelix2;
        double brelix4 = brelix3 + (data.getSentenceCount() + subordinateClauses) * 5;
        double brelix5 = brelix4 + (data.getTypeTokenRatio() * 100.0);

        data.setLixPlusScore(lixPlus);
        data.setBrelix0Score(brelix0);
        data.setBrelix1Score(brelix1);
        data.setBrelix2Score(brelix2);
        data.setBrelix3Score(brelix3);
        data.setBrelix4Score(brelix4);
        data.setBrelix5Score(brelix5);

        data.setLixReadabilityLevel(String.valueOf(calculateLevel(lix, LIX_THRESHOLDS)));
        data.setLixPlusLevel(calculateLevel(lixPlus, LIX_PLUS_THRESHOLDS));
        data.setBrelix0Level(calculateLevel(brelix0, BRELIX0_THRESHOLDS));
        data.setBrelix1Level(calculateLevel(brelix1, BRELIX1_THRESHOLDS));
        data.setBrelix2Level(calculateLevel(brelix2, BRELIX2_THRESHOLDS));
        data.setBrelix3Level(calculateLevel(brelix3, BRELIX3_THRESHOLDS));
        data.setBrelix4Level(calculateLevel(brelix4, BRELIX4_THRESHOLDS));
        data.setBrelix5Level(calculateLevel(brelix5, BRELIX5_THRESHOLDS));
    }

    private int calculateLevel(double score, double[] thresholds) {
        for (int i = 0; i < thresholds.length; i++) {
            if (score <= thresholds[i]) {
                return i + 1;
            }
        }
        return 6;
    }

    int countMultiGraphems(String word) {
        int count = 0;
        // <ch>, <ck>, <sch>, <sp>, <st>, <ng>, <ie>, <ei>, <eu>, <äu>, Dehnungs-h
        String[] clusters = {"sch", "ch", "ck", "sp", "st", "ng", "ie", "ei", "eu", "äu"};
        String temp = word;
        for (String c : clusters) {
            int index = temp.indexOf(c);
            while (index != -1) {
                count++;
                temp = temp.substring(0, index) + " " + temp.substring(index + c.length());
                index = temp.indexOf(c);
            }
        }
        // Dehnungs-h: h nach Vokal (a, e, i, o, u, ä, ö, ü) und vor Konsonant oder am Ende
        Pattern hPattern = Pattern.compile("[aeiouäöü]h([bcdfghjklmnpqrstvwxyzß]|$)");
        Matcher hMatcher = hPattern.matcher(word);
        while (hMatcher.find()) {
            count++;
        }
        return count;
    }

    int countMultiGraphemsMinusC(String word) {
        int count = countMultiGraphems(word);
        // c_mehr: c in ck, ch, sch
        int c_mehr = 0;
        String temp = word;
        String[] cClusters = {"sch", "ch", "ck"};
        for (String c : cClusters) {
             int index = temp.indexOf(c);
             while (index != -1) {
                 c_mehr++;
                 temp = temp.substring(0, index) + " " + temp.substring(index + c.length());
                 index = temp.indexOf(c);
             }
        }
        return count - c_mehr;
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

    int countConsonantClusters(String word) {
        if (word == null || word.length() < 2) return 0;

        int count = 0;
        String vocalsRegex = "[aeiouäöüéàáy]";

        // Finde alle Konsonanten-Sequenzen
        Pattern p = Pattern.compile("[^aeiouäöüéàáy]+");
        Matcher m = p.matcher(word.toLowerCase());

        while (m.find()) {
            String cluster = m.group();
            int start = m.start();
            int end = m.end();

            if (start == 0) {
                // Wortanfang = Silbenanfang der 1. Silbe
                if (cluster.length() >= 2) count++;
            } else if (end == word.length()) {
                // Wortende = Silbenende der letzten Silbe
                if (cluster.length() >= 3) count++;
            } else {
                // Wortmitte: Trennung nach einer Heuristik:
                // Wenn 'st' oder 'sp' enthalten, gehen sie nach rechts (Silbenanfang).
                // Ansonsten geht der letzte Konsonant nach rechts.
                int splitPoint;
                if (cluster.contains("st")) {
                    splitPoint = cluster.indexOf("st");
                } else if (cluster.contains("sp")) {
                    splitPoint = cluster.indexOf("sp");
                } else {
                    splitPoint = cluster.length() - 1;
                }

                String left = cluster.substring(0, splitPoint);
                String right = cluster.substring(splitPoint);

                if (left.length() >= 3) count++; // Silbenende >= 3
                if (right.length() >= 2) count++; // Silbenanfang >= 2
            }
        }

        return count;
    }

    int countSubordinateClauses(Document doc) {
        int count = 0;
        for (Sentence sent : doc.sentences()) {
            Tree tree = sent.parse();
            count += countSbar(tree);
        }
        return count;
    }

    int countSbar(Tree tree) {
        int count = 0;
        if (tree.label().value().equals("SBAR")) {
            count++;
        }
        for (Tree child : tree.children()) {
            count += countSbar(child);
        }
        return count;
    }
}
