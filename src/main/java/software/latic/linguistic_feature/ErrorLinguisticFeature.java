package software.latic.linguistic_feature;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ErrorLinguisticFeature extends LinguisticFeatureBase implements LinguisticFeature {
    StringProperty value = new SimpleStringProperty();

    public ErrorLinguisticFeature(String name, String id, String value) {
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