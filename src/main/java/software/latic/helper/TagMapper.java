package software.latic.helper;

import software.latic.Logging;
import software.latic.translation.SupportedLocales;
import software.latic.translation.Translation;
import edu.stanford.nlp.simple.Token;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TagMapper {
    private static final TagMapper tagMapper = new TagMapper();
    private final Map<String, String> tagsDE = new HashMap<>();
    //TODO Load Mappings from file
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
            new AbstractMap.SimpleEntry<>(SupportedLocales.GERMAN.getLocale(), tagsDE),
            new AbstractMap.SimpleEntry<>(SupportedLocales.SPANISH.getLocale(), tagsDE),
            new AbstractMap.SimpleEntry<>(SupportedLocales.FRENCH.getLocale(), tagsDE)
    );
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
            new AbstractMap.SimpleEntry<>("&", "SYM"),
            new AbstractMap.SimpleEntry<>("|", "SYM"),
            new AbstractMap.SimpleEntry<>("=", "SYM"),
            new AbstractMap.SimpleEntry<>("+", "SYM"),
            new AbstractMap.SimpleEntry<>("/", "SYM"),
            new AbstractMap.SimpleEntry<>("\\", "SYM"),
            new AbstractMap.SimpleEntry<>("~", "SYM"),
            new AbstractMap.SimpleEntry<>("#", "SYM"),
            new AbstractMap.SimpleEntry<>(">", "SYM"),
            new AbstractMap.SimpleEntry<>("^", "SYM"),
            new AbstractMap.SimpleEntry<>("„", "PUNCT"),
            new AbstractMap.SimpleEntry<>("“", "PUNCT")
    );

    private final Map<String, String> additionalTagsEs = Map.ofEntries(
            new AbstractMap.SimpleEntry<>("(", "PUNCT"),
            new AbstractMap.SimpleEntry<>(")", "PUNCT"),
            new AbstractMap.SimpleEntry<>("{", "PUNCT"),
            new AbstractMap.SimpleEntry<>("}", "PUNCT"),
            new AbstractMap.SimpleEntry<>("[", "PUNCT"),
            new AbstractMap.SimpleEntry<>("]", "PUNCT"),
            new AbstractMap.SimpleEntry<>("@", "SYM")
    );

    private final Map<String, String> fixedTagsEs = Stream.concat(fixedTagsDe.entrySet().stream(), additionalTagsEs
            .entrySet().stream()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    private final Map<Locale, Map<String, String>> localeFixedTagMaps = Map.ofEntries(
            new AbstractMap.SimpleEntry<>(SupportedLocales.ENGLISH.getLocale(), fixedTagsEn),
            new AbstractMap.SimpleEntry<>(SupportedLocales.GERMAN.getLocale(), fixedTagsDe),
            new AbstractMap.SimpleEntry<>(SupportedLocales.SPANISH.getLocale(), fixedTagsEs),
            new AbstractMap.SimpleEntry<>(SupportedLocales.FRENCH.getLocale(), fixedTagsDe)
    );
    private Map<String, String> tagMap;
    private Map<String, String> fixedTagMap;
    private List<String> interjections = Collections.emptyList();

    public static TagMapper getInstance() {
        return tagMapper;
    }

    private Stream<SimpleToken> prepareTokenStream(List<Token> tokens) {
        tagMap = localeTags.get(Translation.getInstance().getLocale());
        fixedTagMap = localeFixedTagMaps.get(Translation.getInstance().getLocale());

        return tokens.stream().map(SimpleToken::new)
                .map(this::fixedTag)
                .map(token -> {
                    if (interjections.size() > 0) return replaceInterjection(token);
                    else return token;
                })
                .map(token -> {
                    if (!tagMap.isEmpty()) return mapPunctuation(token);
                    else return token;
                })
                .map(this::mapKnownTags);
    }

    public List<String> replaceTags(List<Token> tokens) {
        return prepareTokenStream(tokens).map(this::getTag).collect(Collectors.toList());
    }

    public List<SimpleToken> replaceTagsInTokenList(List<Token> tokens) {
        return prepareTokenStream(tokens).collect(Collectors.toList());
    }

    private String getTag(SimpleToken token) {
        return token.tag();
    }

    private SimpleToken mapKnownTags(SimpleToken token) {
        token.setTag(KnownTags.isKnown(token.tag()) ? token.tag() : "X");
        return token;
    }

    private SimpleToken mapPunctuation(SimpleToken token) {
        token.setTag(tagMap.getOrDefault(token.tag(), token.tag()));
        return token;
    }

    private SimpleToken fixedTag(SimpleToken token) {
        token.setTag(fixedTagMap.getOrDefault(token.word(), token.tag()));
        return token;
    }

    private SimpleToken replaceInterjection(SimpleToken token) {
        if (interjections.stream().anyMatch(s -> s.equalsIgnoreCase(token.word()))) {
            token.setTag(Translation.getInstance().getLocale().equals(SupportedLocales.ENGLISH.getLocale()) ? "UH" : "INTJ");
        }
        return token;
    }

    public void loadInterjections() {
        if (!Translation.getInstance().getLocale().equals(Locale.ENGLISH)) {
            interjections = CsvReader.getInstance()
                    .readFile(String.format("interjections_%s.txt", Translation.getInstance().getLanguageTag()));
        } else interjections = Collections.emptyList();

        Logging.getInstance().debug("TagMapper", String.format("Locale: %s - Interjections: %d", Translation.getInstance().getLocale(), interjections.size()));
    }

}
