package software.latic.item;

import software.latic.task.TaskLevel;

public enum GermanGeneralItemCharacteristics implements ItemCharacteristics {
    FLESCH_INDEX( "fleschIndexGerman", TaskLevel.TEXT_READABILITY),
    WIENER_SACHTEXTFORMEL( "wienerSachtextformel", TaskLevel.TEXT_READABILITY),
    gSMOG( "gSMOG", TaskLevel.TEXT_READABILITY);

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

    GermanGeneralItemCharacteristics(String id, TaskLevel level) {
        this.id = id;
        this.level = level;
    }
}
