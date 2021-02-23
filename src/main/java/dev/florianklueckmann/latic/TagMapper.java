package dev.florianklueckmann.latic;

import dev.florianklueckmann.latic.Translation.SupportedLocales;
import dev.florianklueckmann.latic.Translation.Translation;
import edu.stanford.nlp.simple.Token;

import java.util.*;
import java.util.stream.Collectors;

public class TagMapper {
    private static final TagMapper tagMapper = new TagMapper();
    public static TagMapper getInstance() {
        return tagMapper;
    }

    private Map<String, String> tagMap;
    private Map<String, String> fixedTagMap;

    private final Map<String, String> tagsDE = new HashMap<>();

    private final Map<String, String> tagsEN = Map.ofEntries(
            new AbstractMap.SimpleEntry<>(".", "PUNCT"),
            new AbstractMap.SimpleEntry<>(",", "PUNCT"),
            new AbstractMap.SimpleEntry<>(":", "PUNCT"),
            new AbstractMap.SimpleEntry<>("\"", "PUNCT"),
            new AbstractMap.SimpleEntry<>("-RRB-", "PUNCT"),
            new AbstractMap.SimpleEntry<>("-LRB-", "PUNCT"),
            new AbstractMap.SimpleEntry<>("``", "PUNCT"),
            new AbstractMap.SimpleEntry<>("''", "PUNCT"),
            new AbstractMap.SimpleEntry<>("HYPH", "PUNCT"),
            new AbstractMap.SimpleEntry<>("NFP", "PUNCT")
    );

    private final Map<Locale, Map<String, String>> localeTags = Map.ofEntries(
            new AbstractMap.SimpleEntry<>(SupportedLocales.ENGLISH.getLocale(), tagsEN),
            new AbstractMap.SimpleEntry<>(SupportedLocales.GERMAN.getLocale(), tagsDE)
    );

    public List<String> replaceTags(List<Token> tokens) {
        tagMap = localeTags.get(Translation.getInstance().getLocale());
        fixedTagMap = localeFixedTagMaps.get(Translation.getInstance().getLocale());
        if(!tagMap.isEmpty()) {
            return tokens.stream()
                    .map(this::fixedTag)
                    .map(tag -> tagMap.getOrDefault(tag, tag))
                    .map(tag -> KnownTags.isKnown(tag) ? tag : "X")
                    .collect(Collectors.toList());
        } else {
            return tokens.stream()
                    .map(this::fixedTag)
                    .map(tag -> KnownTags.isKnown(tag) ? tag : "X")
                    .collect(Collectors.toList());
        }
    }

    public String fixedTag(Token token) {
        return fixedTagMap.getOrDefault(token.word(), token.tag());
    }

    private final Map<String, String> fixedTagsEn = Map.ofEntries(
            new AbstractMap.SimpleEntry<>("%", "SYM"),
            new AbstractMap.SimpleEntry<>("°", "SYM"),
            new AbstractMap.SimpleEntry<>("&", "SYM")
    );

    private final Map<String, String> fixedTagsDe = Map.ofEntries(
            new AbstractMap.SimpleEntry<>("%", "SYM"),
            new AbstractMap.SimpleEntry<>("°", "SYM"),
            new AbstractMap.SimpleEntry<>("§", "SYM"),
            new AbstractMap.SimpleEntry<>("$", "SYM"),
            new AbstractMap.SimpleEntry<>("€", "SYM"),
            new AbstractMap.SimpleEntry<>("¥", "SYM"),
            new AbstractMap.SimpleEntry<>("&", "SYM")
    );

    private final Map<Locale, Map<String, String>> localeFixedTagMaps = Map.ofEntries(
            new AbstractMap.SimpleEntry<>(SupportedLocales.ENGLISH.getLocale(), fixedTagsEn),
            new AbstractMap.SimpleEntry<>(SupportedLocales.GERMAN.getLocale(), fixedTagsDe)
    );

}
