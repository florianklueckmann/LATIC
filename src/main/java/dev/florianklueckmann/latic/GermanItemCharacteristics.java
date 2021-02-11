package dev.florianklueckmann.latic;

public enum GermanItemCharacteristics implements ItemCharacteristics {
//    ADJECTIVES("Adjectives", "adjectives"),
    ADPOSITIONS( "adpositions", TaskLevel.WORD_CLASS),
//    ADVERBS("Adverbs", "adverbs"),
    COORDINATING_CONJUNCTIONS("coordinatingConjunctions", TaskLevel.WORD_CLASS),
    SUBORDINATING_CONJUNCTIONS( "subordinatingConjunctions", TaskLevel.WORD_CLASS);
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

    private final TaskLevel level;

    public TaskLevel getLevel() {
        return level;
    }

    private final String id;

    public String getId() {
        return id;
    }


    GermanItemCharacteristics(String id, TaskLevel level) {
        this.id = id;
        this.level = level;
    }

    GermanItemCharacteristics(String id) {
        this.id = id;
        this.level = TaskLevel.WORD;
    }
}
