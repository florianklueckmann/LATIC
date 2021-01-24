package dev.florianklueckmann.latic;

import javafx.beans.property.SimpleDoubleProperty;
import java.math.BigDecimal;
import java.math.RoundingMode;

class RoundedDoubleProperty extends SimpleDoubleProperty {

    @Override
    public double get() {
        return BigDecimal.valueOf(super.get()).setScale(3, RoundingMode.HALF_UP).doubleValue();
    }

    public double getNonRounded() {
        return super.get();
    }
}
