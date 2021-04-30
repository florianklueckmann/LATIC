package dev.florianklueckmann.latic.task;

import java.util.Objects;

public enum TaskLevel {
    ROOT(0),
    WORD(1),
    WORD_CLASS,
    WORD_CLASS_VERBS,
    SENTENCE(2),
    SENTENCE_LENGTH,
    TEXT(3),
    TEXT_READABILITY;

    private final int priority;

    public int getPriority() {
        return priority;
    }

    private TaskLevel parent = null;

    public TaskLevel getParent() {
        return Objects.requireNonNullElse(parent, TaskLevel.ROOT);
    }

    public boolean hasParent() {
        return this.parent != null;
//        if (this.parent == null) {
//            return false;
//        } else {
//            return true;
//        }
    }

    public void setParent(TaskLevel parent) {
        this.parent = parent;
    }

    TaskLevel(int priority) {
        this.priority = priority;
    }

    TaskLevel() {
        this.priority = 101;
    }

}