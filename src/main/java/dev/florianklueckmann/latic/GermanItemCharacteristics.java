package dev.florianklueckmann.latic;

public enum GermanItemCharacteristics {
//    ADJECTIVES("Adjectives", "adjectives"),
    ADPOSITIONS("Adpositions", "adpositions"),
//    ADVERBS("Adverbs", "adverbs"),
    COORDINATING_CONJUNCTIONS("Coordinating conjunctions", "coordinatingConjunctions"),
    SUBORDINATING_CONJUNCTIONS("Subordinating conjunctions", "subordinatingConjunctions");
//    DETERMINER("Determiner", "determiner"),
//    PROPER_NOUNS("Proper nouns", "properNouns");
//    INTERJECTIONS("Interjections", "interjections"),
//    MODAL("Modals", "modals"),
//    NOUNS("Nouns", "nouns"),
//    NUMBERS("Numbers", "numbers"),
//    PARTICLES("Particles", "particles"),
//    PRONOUNS("Pronouns", "pronouns"),
//    SYMBOLS("Symbols", "symbols"),
//    VERBS("Verbs", "verbs"),
//    PUNCTUATION("Punctuation", "punctuation"),
//    UNKNOWN("Unknown", "unknown");

    private final String name;

    public String getName() {
        return name;
    }

    private final String id;

    public String getId() {
        return id;
    }

    GermanItemCharacteristics(String name, String id) {
        this.name = name;
        this.id = id;
    }
}
