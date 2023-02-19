package software.latic.item;

import software.latic.task.TaskLevel;

public enum GermanGeneralItemCharacteristics implements ItemCharacteristics {
    FLESCH_INDEX( "fleschIndexGerman", TaskLevel.TEXT_READABILITY),
    WIENER_SACHTEXTFORMEL( "wienerSachtextformel", TaskLevel.TEXT_READABILITY),
    gSMOG( "gSMOG", TaskLevel.TEXT_READABILITY),
    CONNECTIVES_COUNT("connectivesCount", TaskLevel.TEXT, true);

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

    GermanGeneralItemCharacteristics(String id, TaskLevel level, boolean isBeta) {
        this.id = id;
        this.level = level;
        this.isBeta = isBeta;
    }

    GermanGeneralItemCharacteristics(String id, TaskLevel level) {
        this.id = id;
        this.level = level;
        this.isBeta = false;
    }
}
