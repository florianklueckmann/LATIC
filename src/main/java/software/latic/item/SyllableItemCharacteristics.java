package software.latic.item;

import software.latic.task.TaskLevel;

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

    SyllableItemCharacteristics(String id, TaskLevel level) {
        this.id = id;
        this.level = level;
    }

}
