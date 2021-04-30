package dev.florianklueckmann.latic.linguistic_feature;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class DoubleLinguisticFeature extends LinguisticFeatureBase{
    public Double getValue() {
        return value.get();
    }

    public DoubleProperty valueProperty() {
        return value;
    }

    public void setValue(double value) {
        this.value.set(value);
    }

    DoubleProperty value = new SimpleDoubleProperty();

    public DoubleLinguisticFeature(String name, String id, Double value) {
        super(name, id);
        setValue(value);
    }


}
