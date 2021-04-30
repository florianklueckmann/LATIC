package dev.florianklueckmann.latic.item;

import dev.florianklueckmann.latic.task.TaskLevel;

public enum TextInformationItemCharacteristics implements ItemCharacteristics {
    TEXT_AND_POS_TAGS("textAndPosTags", TaskLevel.TEXT);
//    POS_TAGS_PER_SENTENCE("posTagsPerSentence", TaskLevel.TEXT);


    private final String id;

    public String getId() { return id; }

    private final TaskLevel level;

    public TaskLevel getLevel() { return level; }

    TextInformationItemCharacteristics(String id, TaskLevel level) {
        this.id = id;
        this.level = level;
    }

    TextInformationItemCharacteristics(String id) {
        this.id = id;
        this.level = TaskLevel.WORD;
    }
}
