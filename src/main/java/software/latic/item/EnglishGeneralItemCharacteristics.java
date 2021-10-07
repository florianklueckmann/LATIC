package software.latic.item;

import software.latic.task.TaskLevel;

public enum EnglishGeneralItemCharacteristics implements ItemCharacteristics {
    FLESCH_INDEX( "fleschIndexEnglish", TaskLevel.TEXT_READABILITY),
    FLESCH_KINDCAID( "fleschKincaid", TaskLevel.TEXT_READABILITY),
    GUNNING_FOG( "gunningFog", TaskLevel.TEXT_READABILITY),
    AUTOMATED_READABILITY_INDEX( "automatedReadabilityIndex", TaskLevel.TEXT_READABILITY),
    COLEMAN_LIAU( "colemanLiau", TaskLevel.TEXT_READABILITY),
    SMOG( "SMOG", TaskLevel.TEXT_READABILITY);

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

    EnglishGeneralItemCharacteristics(String id, TaskLevel level) {
        this.id = id;
        this.level = level;
    }
}