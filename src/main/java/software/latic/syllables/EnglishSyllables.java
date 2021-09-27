package software.latic.syllables;

import software.latic.helper.CsvReader;

import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

public class EnglishSyllables implements Syllables {

    private static final EnglishSyllables englishSyllables = new EnglishSyllables();
    public static Syllables getInstance() {
        return englishSyllables;
    }

//    String vocals = "[aeiouyäöüéáà]";
//    String pseudoVocals = "ei|au|ie|eu|äu|aa|oo";
//    String pseudoConsonants = "qu";

// Two expressions of occurrences which normally would be counted as two syllables, but should be counted as one.
String EXPRESSION_MONOSYLLABIC_ONE = "(?i)awe($|d|so)|cia(?:l|$)|tia|cius|cious|[^aeiou]giu|[aeiouy][^aeiouy]ion|iou|sia$|eous$|[oa]gue$|.[^aeiuoycgltdb]{2,}ed$|.ely$|^jua|uai|eau|^busi$|(?:[aeiouy](?:[bcfgklmnprsvwxyz]|ch|dg|g[hn]|lch|l[lv]|mm|nch|n[cgn]|r[bcnsv]|squ|s[chkls]|th)ed$)|(?:[aeiouy](?:[bdfklmnprstvy]|ch|g[hn]|lch|l[lv]|mm|nch|nn|r[nsv]|squ|s[cklst]|th)es$)"; // /g
Pattern EXPRESSION_MONOSYLLABIC_ONE_PATTERN = Pattern.compile(EXPRESSION_MONOSYLLABIC_ONE);


String EXPRESSION_MONOSYLLABIC_TWO = "(?i)[aeiouy](?:[bcdfgklmnprstvyz]|ch|dg|g[hn]|l[lv]|mm|n[cgns]|r[cnsv]|squ|s[cklst]|th)e$";// / ... /g
Pattern EXPRESSION_MONOSYLLABIC_TWO_PATTERN = Pattern.compile(EXPRESSION_MONOSYLLABIC_TWO);

// Four expression of occurrences which normally would be counted as one syllable, but should be counted as two.
String EXPRESSION_DOUBLE_SYLLABIC_ONE = "(?i)(?:([^aeiouy])\1l|[^aeiouy]ie(?:r|s?t)|[aeiouym]bl|eo|ism|asm|thm|dnt|snt|uity|dea|gean|oa|ua|react?|orbed|shred|eings?|[aeiouy]sh?e[rs])$";  // /g
Pattern EXPRESSION_DOUBLE_SYLLABIC_ONE_PATTERN = Pattern.compile(EXPRESSION_DOUBLE_SYLLABIC_ONE);  // /g

String EXPRESSION_DOUBLE_SYLLABIC_TWO = "(?i)creat(?!u)|[^gq]ua[^auieo]|[aeiou]{3}|^(?:ia|mc|coa[dglx].)|^re(app|es|im|us)|(th|d)eist";  // /g
Pattern EXPRESSION_DOUBLE_SYLLABIC_TWO_PATTERN = Pattern.compile(EXPRESSION_DOUBLE_SYLLABIC_TWO);

String EXPRESSION_DOUBLE_SYLLABIC_THREE = "(?i)[^aeiou]y[ae]|[^l]lien|riet|dien|iu|io|ii|uen|[aeilotu]real|real[aeilotu]|iell|eo[^aeiou]|[aeiou]y[aeiou]";  // /g
Pattern EXPRESSION_DOUBLE_SYLLABIC_THREE_PATTERN = Pattern.compile(EXPRESSION_DOUBLE_SYLLABIC_THREE);

String EXPRESSION_DOUBLE_SYLLABIC_FOUR = "(?i)[^s]ia";
Pattern EXPRESSION_DOUBLE_SYLLABIC_FOUR_PATTERN = Pattern.compile(EXPRESSION_DOUBLE_SYLLABIC_FOUR);

// Expression to match single syllable pre- and suffixes.
String EXPRESSION_SINGLE = "(?i)^(?:un|fore|ware|none?|out|post|sub|pre|pro|dis|side|some)|(?:ly|less|some|ful|ers?|ness|cians?|ments?|ettes?|villes?|ships?|sides?|ports?|shires?|[gnst]ion(?:ed|s)?)$"; // /g
Pattern EXPRESSION_SINGLE_PATTERN = Pattern.compile(EXPRESSION_SINGLE);

// Expression to match double syllable pre- and suffixes.
String EXPRESSION_DOUBLE = "(?i)^(?:above|anti|ante|counter|hyper|afore|agri|infra|intra|inter|over|semi|ultra|under|extra|dia|micro|mega|kilo|pico|nano|macro|somer)|(?:fully|berry|woman|women|edly|union|((?:[bcdfghjklmnpqrstvwxz])|[aeiou])ye?ing)$";  // /g
Pattern EXPRESSION_DOUBLE_PATTERN = Pattern.compile(EXPRESSION_DOUBLE);

// Expression to match triple syllable suffixes.
String EXPRESSION_TRIPLE = "(?i)(creations?|ology|ologist|onomy|onomist)$"; // /g
Pattern EXPRESSION_TRIPLE_PATTERN = Pattern.compile(EXPRESSION_TRIPLE);

private final Map<String, String> specialWords = CsvReader.getInstance()
        .convertCsvToMap("syllables/syllables_en.csv", ",");
    
    public int syllablesPerWord(String word) {
        var count = 0;

        if (word.length() == 0) {
            return 0;
        }

        if (word.length() < 3) {
            return 1;
        }

        if (specialWords.containsKey(word)) {
            return Integer.parseInt(specialWords.get(word));
        }
        //Should test singular...

        //addOne = returnFactory(1)
        //subtractOne = returnFactory(-1)

        while (EXPRESSION_TRIPLE_PATTERN.matcher(word).find()) {
//            System.out.println("EXPRESSION_TRIPLE before: " + word);
            word = word.replaceFirst(EXPRESSION_TRIPLE, "");
//            System.out.println("EXPRESSION_TRIPLE after: " + word);
            count += 3;
        }
        while (EXPRESSION_DOUBLE_PATTERN.matcher(word).find()) {
//            System.out.println("EXPRESSION_DOUBLE before: " + word);
            word = word.replaceFirst(EXPRESSION_DOUBLE, "");
//            System.out.println("EXPRESSION_DOUBLE after: " + word);
            count += 2;
        }
        while (EXPRESSION_SINGLE_PATTERN.matcher(word).find()) {
//            System.out.println("EXPRESSION_SINGLE before: " + word);
            word = word.replaceFirst(EXPRESSION_SINGLE, "");
//            System.out.println("EXPRESSION_SINGLE after: " + word);
            count += 1;
        }

        var parts = word.split("[^aeiouy]+");

        for (String part : parts) {
            if (!part.isEmpty()) {
                count++;
            }
        }

        if (EXPRESSION_MONOSYLLABIC_ONE_PATTERN.matcher(word).find()) {
//            System.out.println("DIIIIIING - EXPRESSION_MONOSYLLABIC_ONE");
            count -= 1;
        }
        if (EXPRESSION_MONOSYLLABIC_TWO_PATTERN.matcher(word).find()) {
//            System.out.println("DIIIIIING - EXPRESSION_MONOSYLLABIC_TWO");
            count -= 1;
        }

        if (EXPRESSION_DOUBLE_SYLLABIC_ONE_PATTERN.matcher(word).find()) {
//            System.out.println("DIIIIIING - EXPRESSION_DOUBLE_SYLLABIC_ONE");
            count += 1;
        }
        if (EXPRESSION_DOUBLE_SYLLABIC_TWO_PATTERN.matcher(word).find()) {
//            System.out.println("DIIIIIING - EXPRESSION_DOUBLE_SYLLABIC_TWO");
            count += 1;
        }
        if (EXPRESSION_DOUBLE_SYLLABIC_THREE_PATTERN.matcher(word).find()) {
//            System.out.println("DIIIIIING - EXPRESSION_DOUBLE_SYLLABIC_THREE");
            count += 1;
        }
        if (EXPRESSION_DOUBLE_SYLLABIC_FOUR_PATTERN.matcher(word).find()) {
//            System.out.println("DIIIIIING - EXPRESSION_DOUBLE_SYLLABIC_FOUR");
            count += 1;
        }

        //TODO: Implement
        return count > 0 ? count : 1;
    }
}
