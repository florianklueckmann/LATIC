package dev.florianklueckmann.latic;

import javafx.beans.property.Property;

public interface LinguisticFeature {
    String getName();
    String getId();
    Property<?> valueProperty();
    Object getValue();
}
