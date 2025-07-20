package software.latic.item;

import software.latic.task.TaskLevel;

import java.util.Optional;

public enum GermanGeneralItemCharacteristics implements ItemCharacteristics {
    FLESCH_INDEX( "fleschIndexGerman", TaskLevel.TEXT_READABILITY),
    FLESCH_INDEX_LEVEL( "fleschIndexGermanLevel", TaskLevel.TEXT_READABILITY),
    WIENER_SACHTEXTFORMEL( "wienerSachtextformel", TaskLevel.TEXT_READABILITY),
    WIENER_SACHTEXTFORMEL_LEVEL( "wienerSachtextformelLevel", TaskLevel.TEXT_READABILITY),
    gSMOG( "gSMOG", TaskLevel.TEXT_READABILITY),
    gSMOG_LEVEL( "gSMOGLevel", TaskLevel.TEXT_READABILITY),
    CONNECTIVES_COUNT("connectivesCount", TaskLevel.TEXT),
    AVERAGE_WORD_FREQUENCY("averageWordFrequencyClass", TaskLevel.TEXT);

    private final TaskLevel level;

    @Override
    public TaskLevel getLevel() {
        return level;
    }

    private final String id;

    @Override
    public String getId() {
        return id;
    }

    public final boolean isBeta;

    public boolean getIsBeta() {return isBeta;}
    
    private final Class<?> valueClass;
    
    @Override
    public Optional<Class<?>> getValueClass() {
        return Optional.ofNullable(valueClass);
    }

    GermanGeneralItemCharacteristics(String id, TaskLevel level, boolean isBeta, Class<?> valueClass) {
        this.id = id;
        this.level = level;
        this.isBeta = isBeta;
        this.valueClass = valueClass;
    }

    GermanGeneralItemCharacteristics(String id, TaskLevel level, boolean isBeta) {
        this(id, level, isBeta, null);
    }

    GermanGeneralItemCharacteristics(String id, TaskLevel level) {
        this(id, level, false);
    }
}
