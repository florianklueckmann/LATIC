package dev.florianklueckmann.latic;

public enum GeneralLanguageItemCharacteristics implements ItemCharacteristics {


    ADJECTIVES("Adjectives", "adjectives"),
    ADVERBS("Adverbs", "adverbs"),
    DETERMINER("Determiner", "determiner"),
    INTERJECTIONS("Interjections", "interjections"),
    MODAL("Modals", "modals"),
    NOUNS("Nouns", "nouns"),
    NUMBERS("Numbers", "numbers"),
    PARTICLES("Particles", "particles"),
    PRONOUNS("Pronouns", "pronouns"),
    PROPER_NOUNS("Proper nouns", "properNouns"),
    SYMBOLS("Symbols", "symbols"),
    VERBS("Verbs", "verbs"),
    PUNCTUATION("Punctuation", "punctuation"),
    UNKNOWN("Unknown", "unknown");


    private final String name;

    public String getName() {
        return name;
    }

    private final String id;

    public String getId() {
        return id;
    }

    GeneralLanguageItemCharacteristics(String name, String id) {
        this.name = name;
        this.id = id;
    }
}
