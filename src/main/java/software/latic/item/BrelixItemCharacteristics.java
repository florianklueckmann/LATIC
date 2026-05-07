package software.latic.item;

import software.latic.task.TaskLevel;

public enum BrelixItemCharacteristics implements ItemCharacteristics {
    PAGES_COUNT("pagesCount", TaskLevel.BRELIX),
    FONT_SIZE_MM("fontSizeMm", TaskLevel.BRELIX),
    LIX_PLUS_SCORE("lixPlusScore", TaskLevel.BRELIX),
    BRELIX0_SCORE("brelix0Score", TaskLevel.BRELIX),
    BRELIX1_SCORE("brelix1Score", TaskLevel.BRELIX),
    BRELIX2_SCORE("brelix2Score", TaskLevel.BRELIX),
    BRELIX3_SCORE("brelix3Score", TaskLevel.BRELIX),
    BRELIX3_NEU_SCORE("brelix3NeuScore", TaskLevel.BRELIX),
    BRELIX4_SCORE("brelix4Score", TaskLevel.BRELIX),
    BRELIX5_SCORE("brelix5Score", TaskLevel.BRELIX),
    LIX_PLUS_LEVEL("lixPlusLevel", TaskLevel.BRELIX),
    BRELIX0_LEVEL("brelix0Level", TaskLevel.BRELIX),
    BRELIX1_LEVEL("brelix1Level", TaskLevel.BRELIX),
    BRELIX2_LEVEL("brelix2Level", TaskLevel.BRELIX),
    BRELIX3_LEVEL("brelix3Level", TaskLevel.BRELIX),
    BRELIX3_NEU_LEVEL("brelix3NeuLevel", TaskLevel.BRELIX),
    BRELIX4_LEVEL("brelix4Level", TaskLevel.BRELIX),
    BRELIX5_LEVEL("brelix5Level", TaskLevel.BRELIX),
    BRELIX_DEBUG_INFO("brelixDebugInfo", TaskLevel.BRELIX);

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
