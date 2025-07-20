package software.latic.item;

import software.latic.task.TaskLevel;

import java.util.Optional;

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
    
    private final Class<?> valueClass;
    
    @Override
    public Optional<Class<?>> getValueClass() {
        return Optional.ofNullable(valueClass);
    }

    GermanItemCharacteristics(String id, TaskLevel level, Class<?> valueClass) {
        this.id = id;
        this.level = level;
        this.valueClass = valueClass;
    }

    GermanItemCharacteristics(String id, TaskLevel level) {
        this(id, level, null);
    }

    GermanItemCharacteristics(String id) {
        this(id, TaskLevel.WORD);
    }
}
