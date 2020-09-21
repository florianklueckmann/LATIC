package dev.florianklueckmann.latic;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class StringLinguisticFeature extends LinguisticFeatureBase {
    StringProperty value = new SimpleStringProperty();

    public StringLinguisticFeature(String name, String id, String value) {
        super(name, id);
        setValue(value);
    }

    public String getValue() {
        return value.get();
    }

    public void setValue(String value) {
        this.value.set(value);
    }

    @Override
    public StringProperty valueProperty() {
        return value;
    }
}
