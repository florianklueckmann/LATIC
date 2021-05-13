package software.latic.item;

import software.latic.task.TaskLevel;

public enum GermanItemCharacteristics implements ItemCharacteristics {
    ADPOSITIONS( "adpositions", TaskLevel.WORD_CLASS),
    COORDINATING_CONJUNCTIONS("coordinatingConjunctions", TaskLevel.WORD_CLASS),
    SUBORDINATING_CONJUNCTIONS( "subordinatingConjunctions", TaskLevel.WORD_CLASS);

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
