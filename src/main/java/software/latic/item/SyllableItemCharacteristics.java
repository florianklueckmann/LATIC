package software.latic.item;

import software.latic.task.TaskLevel;

import java.util.Optional;

public enum SyllableItemCharacteristics implements ItemCharacteristics {

    SYLLABLE_COUNT("syllableCount", TaskLevel.TEXT),
    AVERAGE_WORD_LENGTH_SYLLABLES("averageWordLengthSyllables", TaskLevel.WORD_LENGTH),
    AVERAGE_SENTENCE_LENGTH_SYLLABLES("averageSentenceLengthSyllables", TaskLevel.SENTENCE_LENGTH),
    WORDS_WITH_MORE_THAN_TWO_SYLLABLES_COUNT("wordsWithMoreThanTwoSyllablesCount", TaskLevel.WORD_LENGTH);

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

    SyllableItemCharacteristics(String id, TaskLevel level, Class<?> valueClass) {
        this.id = id;
        this.level = level;
        this.valueClass = valueClass;
    }
    
    SyllableItemCharacteristics(String id, TaskLevel level) {
        this(id, level, null);
    }

}
