package software.latic.item;

import software.latic.task.TaskLevel;

public enum BrelixItemCharacteristics implements ItemCharacteristics {
    PAGES_COUNT("pagesCount", TaskLevel.TEXT),
    FONT_SIZE_MM("fontSizeMm", TaskLevel.TEXT),
    LIX_PLUS_SCORE("lixPlusScore", TaskLevel.TEXT_READABILITY),
    BRELIX0_SCORE("brelix0Score", TaskLevel.TEXT_READABILITY),
    BRELIX1_SCORE("brelix1Score", TaskLevel.TEXT_READABILITY),
    BRELIX2_SCORE("brelix2Score", TaskLevel.TEXT_READABILITY),
    BRELIX3_SCORE("brelix3Score", TaskLevel.TEXT_READABILITY),
    BRELIX4_SCORE("brelix4Score", TaskLevel.TEXT_READABILITY),
    BRELIX5_SCORE("brelix5Score", TaskLevel.TEXT_READABILITY),
    LIX_PLUS_LEVEL("lixPlusLevel", TaskLevel.TEXT_READABILITY),
    BRELIX0_LEVEL("brelix0Level", TaskLevel.TEXT_READABILITY),
    BRELIX1_LEVEL("brelix1Level", TaskLevel.TEXT_READABILITY),
    BRELIX2_LEVEL("brelix2Level", TaskLevel.TEXT_READABILITY),
    BRELIX3_LEVEL("brelix3Level", TaskLevel.TEXT_READABILITY),
    BRELIX4_LEVEL("brelix4Level", TaskLevel.TEXT_READABILITY),
    BRELIX5_LEVEL("brelix5Level", TaskLevel.TEXT_READABILITY);

    private final TaskLevel level;
    private final String id;

    @Override
    public TaskLevel getLevel() {
        return level;
    }

    @Override
    public String getId() {
        return id;
    }

    BrelixItemCharacteristics(String id, TaskLevel level) {
        this.id = id;
        this.level = level;
    }
}
