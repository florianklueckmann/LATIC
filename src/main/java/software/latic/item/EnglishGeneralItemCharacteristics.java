package software.latic.item;

import software.latic.task.TaskLevel;

import java.util.Optional;

public enum EnglishGeneralItemCharacteristics implements ItemCharacteristics {
    FLESCH_INDEX( "fleschIndexEnglish", TaskLevel.TEXT_READABILITY),
    FLESCH_INDEX_LEVEL( "fleschIndexEnglishLevel", TaskLevel.TEXT_READABILITY),
    FLESCH_KINCAID( "fleschKincaid", TaskLevel.TEXT_READABILITY),
    FLESCH_KINCAID_LEVEL( "fleschKincaidLevel", TaskLevel.TEXT_READABILITY),
    GUNNING_FOG( "gunningFog", TaskLevel.TEXT_READABILITY),
    GUNNING_FOG_LEVEL( "gunningFogLevel", TaskLevel.TEXT_READABILITY),
    AUTOMATED_READABILITY_INDEX( "automatedReadabilityIndex", TaskLevel.TEXT_READABILITY),
    AUTOMATED_READABILITY_INDEX_LEVEL( "automatedReadabilityIndexLevel", TaskLevel.TEXT_READABILITY),
    COLEMAN_LIAU( "colemanLiau", TaskLevel.TEXT_READABILITY),
    COLEMAN_LIAU_LEVEL( "colemanLiauLevel", TaskLevel.TEXT_READABILITY),
    SMOG( "SMOG", TaskLevel.TEXT_READABILITY),
    SMOG_LEVEL( "SMOGLevel", TaskLevel.TEXT_READABILITY),
    CONNECTIVES_COUNT("connectivesCount", TaskLevel.TEXT);

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

    EnglishGeneralItemCharacteristics(String id, TaskLevel level, boolean isBeta, Class<?> valueClass) {
        this.id = id;
        this.level = level;
        this.isBeta = isBeta;
        this.valueClass = valueClass;
    }

    EnglishGeneralItemCharacteristics(String id, TaskLevel level, boolean isBeta) {
        this( id, level, isBeta, null);
    }

    EnglishGeneralItemCharacteristics(String id, TaskLevel level) {
        this( id, level, false);
    }
}