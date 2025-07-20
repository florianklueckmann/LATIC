package software.latic.item;

import software.latic.task.TaskLevel;

import java.util.Optional;

public enum GeneralLanguageItemCharacteristics implements ItemCharacteristics {


    ADJECTIVES("adjectives", TaskLevel.WORD_CLASS),
    ADVERBS("adverbs", TaskLevel.WORD_CLASS),
    DETERMINER("determiner", TaskLevel.WORD_CLASS),
    INTERJECTIONS("interjections", TaskLevel.WORD_CLASS),
    MODAL("modals", TaskLevel.WORD_CLASS_VERBS),
    NOUNS("nouns", TaskLevel.WORD_CLASS),
    NUMBERS("numbers", TaskLevel.WORD),
    PARTICLES("particles", TaskLevel.WORD_CLASS),
    PRONOUNS("pronouns", TaskLevel.WORD_CLASS),
    PROPER_NOUNS("properNouns", TaskLevel.WORD_CLASS),
    SYMBOLS("symbols", TaskLevel.WORD),
    VERBS("verbs", TaskLevel.WORD_CLASS_VERBS),
    PUNCTUATION("punctuation", TaskLevel.WORD),
    UNKNOWN("unknown", TaskLevel.WORD_CLASS);

    private final TaskLevel level;

    public TaskLevel getLevel() {
        return level;
    }

    private final String id;

    public String getId() {
        return id;
    }
    
    private final Class<?> valueClass;
    
    @Override
    public Optional<Class<?>> getValueClass() {
        return Optional.ofNullable(valueClass);
    }

    GeneralLanguageItemCharacteristics(String id, TaskLevel level, Class<?> valueClass) {
        this.id = id;
        this.level = level;
        this.valueClass = valueClass;
    }

    GeneralLanguageItemCharacteristics(String id, TaskLevel level) {
        this(id, level, null);
    }

    GeneralLanguageItemCharacteristics(String id) {
        this(id, TaskLevel.WORD);
    }
}
