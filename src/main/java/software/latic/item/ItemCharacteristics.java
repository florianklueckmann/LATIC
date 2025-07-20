package software.latic.item;

import software.latic.task.TaskLevel;

import java.util.Optional;

public interface ItemCharacteristics {

    String getId();
    TaskLevel getLevel();
    Optional<Class<?>> getValueClass();
}
