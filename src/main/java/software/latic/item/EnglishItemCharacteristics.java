package software.latic.item;

import software.latic.task.TaskLevel;

import java.util.Optional;

public enum EnglishItemCharacteristics implements ItemCharacteristics {
    CONJUNCTIONS("conjunctions", TaskLevel.WORD_CLASS),
    EXISTENTIAL_THERE("existentialThere", TaskLevel.WORD_CLASS),
    LIST_ITEM_MARKERS("listItemMarkers"),
    POSSESSIVE_ENDINGS("possessiveEndings", TaskLevel.WORD_CLASS),
    PREPOSITION_OR_SUBORDINATING_CONJUNCTION("prepositionOrSubordinatingConjunction", TaskLevel.WORD_CLASS),
    TO("to", TaskLevel.WORD_CLASS),
    VERBS_BASE_FORM("verbsBaseForm", TaskLevel.WORD_CLASS_VERBS),
    VERBS_GERUND_PRESENT_PARTICIPLE("verbsGerundOrPresentParticiple", TaskLevel.WORD_CLASS_VERBS),
    VERBS_PAST("verbsPastTense", TaskLevel.WORD_CLASS_VERBS),
    VERBS_PAST_PARTICIPLE("verbsPastParticiple", TaskLevel.WORD_CLASS_VERBS),
    VERBS_NON_THIRD_SINGULAR_PRESENT("verbsNonThirdSingularPresent", TaskLevel.WORD_CLASS_VERBS),
    VERBS_THIRD_SINGULAR_PRESENT("verbsThirdSingularPresent", TaskLevel.WORD_CLASS_VERBS);

    private final String id;

    public String getId() { return id; }

    private final TaskLevel level;

    public TaskLevel getLevel() { return level; }
    
    private final Class<?> valueClass;
    
    @Override
    public Optional<Class<?>> getValueClass() {
        return Optional.ofNullable(valueClass);
    }

    EnglishItemCharacteristics(String id, TaskLevel level, Class<?> valueClass) {
        this.id = id;
        this.level = level;
        this.valueClass = valueClass;
    }

    EnglishItemCharacteristics(String id, TaskLevel level) {
        this(id, level, null);
    }

    EnglishItemCharacteristics(String id) {
        this(id, TaskLevel.WORD);
    }

}
