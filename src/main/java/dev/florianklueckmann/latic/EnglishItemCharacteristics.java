package dev.florianklueckmann.latic;

public enum EnglishItemCharacteristics implements ItemCharacteristics {
//    ADJECTIVES("Adjectives", "adjectives"),
//    ADVERBS("Adverbs", "adverbs"),
    CONJUNCTIONS("Conjunctions", "conjunctions"),
//    DETERMINER("Determiners", "determiner"),
    EXISTENTIAL_THERE("Existential there", "existentialThere"),
//    FOREIGN_WORDS("Foreign words", "foreignWords"),
//    INTERJECTIONS("Interjections", "interjections"),
    LIST_ITEM_MARKERS("List item markers", "listItemMarkers"),
//    MODAL("Modals", "modal"),
//    NOUNS("Nouns", "nouns"),
//    NUMBERS("Cardinal numbers", "numbers"),
//    PARTICLES("Particles", "particles"),
    POSSESSIVE_ENDINGS("Possessive endings", "possessiveEndings"),
    PREPOSITION_OR_SUBORDINATING_CONJUNCTION("Preposition or subordinating conjunction", "prepositionOrSubordinatingConjunction"),
//    PROPER_NOUNS("Proper nouns", "properNouns"),
//    PRONOUNS("Pronouns", "pronouns"),
//    SYMBOLS("Symbols", "symbols"),
//    VERBS("Verbs", "verbs"),
    TO("to", "to");
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

    EnglishItemCharacteristics(String name, String id) {
        this.name = name;
        this.id = id;
    }

}
