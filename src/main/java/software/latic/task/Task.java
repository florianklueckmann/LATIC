package software.latic.task;

import software.latic.translation.Translation;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleBooleanProperty;

public class Task {
    private ReadOnlyStringWrapper name = new ReadOnlyStringWrapper();
    private ReadOnlyStringWrapper id = new ReadOnlyStringWrapper();
    private BooleanProperty selected = new SimpleBooleanProperty(false);
    private TaskLevel level;
    private boolean isBeta = false;

    public Task(TaskLevel taskLevel) {
        this.name.set(Translation.getInstance().getTranslation(taskLevel.name()));
        this.id.set(taskLevel.name());
        this.level = taskLevel;

        setSelected(true);
    }

    public Task(String name, String id, TaskLevel taskLevel) {
        this.name.set(name);
        this.id.set(id);
        this.level = taskLevel;

        setSelected(true);
    }

    public Task(String name, String id, TaskLevel taskLevel, boolean isBeta) {
        this.name.set(name);
        this.id.set(id);
        this.level = taskLevel;
        this.isBeta = isBeta;

        setSelected(true);
    }

    public String getId() {
        return id.get();
    }
    public ReadOnlyStringProperty idProperty() {
        return id.getReadOnlyProperty();
    }

    @Override
    public String toString(){
        return getName();
    }


    public String getName() {
        return name.get();
    }
    public ReadOnlyStringProperty nameProperty() {
        return name.getReadOnlyProperty();
    }

    public TaskLevel getLevel() {return this.level;}

    public boolean getIsBeta() {return this.isBeta;}
    public void setIsBeta(boolean isBeta) {this.isBeta = isBeta;}

    public BooleanProperty selectedProperty() {
        return selected;
    }
    public boolean isSelected() {
        return selected.get();
    }
    public void setSelected(boolean selected) {
        this.selected.set(selected);
    }

    public boolean equals(Task task) {
        return this.id.equals(task.id);
    }
}
