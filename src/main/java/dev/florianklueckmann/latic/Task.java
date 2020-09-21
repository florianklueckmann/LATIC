package dev.florianklueckmann.latic;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleBooleanProperty;

public class Task {
    private ReadOnlyStringWrapper name = new ReadOnlyStringWrapper();
    private ReadOnlyStringWrapper id = new ReadOnlyStringWrapper();
    private BooleanProperty selected = new SimpleBooleanProperty(false);

    public Task(String name, String id) {
        this.name.set(name);
        this.id.set(id);
    }

    public String getId() {
        return id.get();
    }
    public ReadOnlyStringProperty idProperty() {
        return id.getReadOnlyProperty();
    }



    public String getName() {
        return name.get();
    }
    public ReadOnlyStringProperty nameProperty() {
        return name.getReadOnlyProperty();
    }

    public BooleanProperty selectedProperty() {
        return selected;
    }
    public boolean isSelected() {
        return selected.get();
    }
    public void setSelected(boolean selected) {
        this.selected.set(selected);
    }
}
