package software.latic.linguistic_feature;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class IntegerLinguisticFeature extends LinguisticFeatureBase implements LinguisticFeature {
    IntegerProperty value = new SimpleIntegerProperty();

    public IntegerLinguisticFeature(String name, String id, int value) {
        super(name, id);
        setValue(value);
    }

    public Integer getValue() {
        return value.get();
    }

    public void setValue(int value) {
        this.value.set(value);
    }

    @Override
    public IntegerProperty valueProperty() {
        return value;
    }

}
