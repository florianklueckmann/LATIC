package software.latic.helper;

import software.latic.translation.SupportedLocales;
import software.latic.translation.Translation;

import java.util.AbstractMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class KnownTags {
    private static final KnownTags knownTags = new KnownTags();
    public static HashSet<String> get() {
        return knownTags.getTagSet();
    }
    public static Boolean isKnown(String tag) {
        return knownTags.getTagSet().contains(tag);
    }

    private final HashSet<String> knownTagsDE = Stream.of(
            "ADJ",
            "ADP",
            "ADV",
            "AUX",
            "CCONJ",
            "DET",
            "INTJ",
            "NOUN",
            "NUM",
            "PART",
            "PRON",
            "PROPN",
            "PUNCT",
            "SCONJ",
            "SYM",
            "VERB",
            "X"
    ).collect(Collectors.toCollection(HashSet::new));

    private final HashSet<String> knownTagsEN = Stream.of(
            "JJ", "JJR", "JJS",
            "RB", "RBR", "RBS", "WRB",
            "CD",
            "CC",
            "IN",
            "$",
            "DT", "PDT", "WDT",
            "EX",
            "UH",
            "LS",
            "MD",
            "NN", "NNS",
            "RP",
            "POS",
            "PRP", "PRP$",
            "WP$", "WP",
            "NNP", "NNPS",
            "SYM",
            "TO",
            "VB", "VBG", "VBN", "VBD", "VBZ", "VBP",
            "PUNCT"
    ).collect(Collectors.toCollection(HashSet::new));

    private final Map<Locale, HashSet<String>> tagsMap = Map.ofEntries(
            new AbstractMap.SimpleEntry<>(SupportedLocales.ENGLISH.getLocale(), knownTagsEN),
            new AbstractMap.SimpleEntry<>(SupportedLocales.GERMAN.getLocale(), knownTagsDE),
            new AbstractMap.SimpleEntry<>(SupportedLocales.SPANISH.getLocale(), knownTagsDE),
            new AbstractMap.SimpleEntry<>(SupportedLocales.FRENCH.getLocale(), knownTagsDE)
    );

    private HashSet<String> getTagSet() {
        return tagsMap.get(Translation.getInstance().getLocale());
    }
}
