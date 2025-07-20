package software.latic.item;

import software.latic.task.TaskLevel;

import java.util.Optional;

public enum TextInformationItemCharacteristics implements ItemCharacteristics {
    TEXT_AND_POS_TAGS("textAndPosTags", TaskLevel.TEXT, String.class),
    PASSIVE_CONSTRUCTIONS_COUNT("passiveConstructionsCount", TaskLevel.TEXT, Integer.class),;
//    POS_TAGS_PER_SENTENCE("posTagsPerSentence", TaskLevel.TEXT);


    private final String id;

    public String getId() { return id; }

    private final TaskLevel level;

    public TaskLevel getLevel() { return level; }
    
    private final Class<?> valueClass;
    
    @Override
    public Optional<Class<?>> getValueClass() {
        return Optional.ofNullable(valueClass);
    }

    TextInformationItemCharacteristics(String id, TaskLevel level, Class<?> valueClass) {
        this.id = id;
        this.level = level;
        this.valueClass = valueClass;
    }

    TextInformationItemCharacteristics(String id, TaskLevel level) {
        this(id, level, null);
    }

    TextInformationItemCharacteristics(String id) {
        this(id, TaskLevel.WORD);
    }
}
