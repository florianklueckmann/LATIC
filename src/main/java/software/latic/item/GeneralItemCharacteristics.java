package software.latic.item;

import software.latic.task.TaskLevel;

import java.util.Optional;

public enum GeneralItemCharacteristics implements ItemCharacteristics {

    WORD_COUNT("wordCount", TaskLevel.TEXT),
    SENTENCE_COUNT("sentenceCount", TaskLevel.SENTENCE),
    AVERAGE_WORD_LENGTH_CHAR("averageWordLengthCharacters", TaskLevel.WORD_LENGTH),
    AVERAGE_SENTENCE_LENGTH_CHARACTERS("averageSentenceLengthCharacters", TaskLevel.SENTENCE_LENGTH),
    AVERAGE_SENTENCE_LENGTH_CHARACTERS_WITHOUT_WHITESPACES("averageSentenceLengthCharactersWithoutWhitespaces", TaskLevel.SENTENCE_LENGTH),
    AVERAGE_SENTENCE_LENGTH_WORDS("averageSentenceLengthWords", TaskLevel.SENTENCE_LENGTH),
    LEXICAL_DIVERSITY("lexicalDiversity", TaskLevel.TEXT),
    LIX_SCORE("lixReadabilityScore", TaskLevel.TEXT_READABILITY),
    LIX_SCORE_LEVEL("lixReadabilityLevel", TaskLevel.TEXT_READABILITY);

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

    GeneralItemCharacteristics(String id, TaskLevel level, Class<?> valueClass) {
        this.id = id;
        this.level = level;
        this.valueClass = valueClass;
    }
    
    GeneralItemCharacteristics(String id, TaskLevel level) {
        this(id, level, null);
    }

}
