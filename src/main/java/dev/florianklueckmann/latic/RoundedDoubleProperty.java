package dev.florianklueckmann.latic;

import javafx.beans.property.SimpleDoubleProperty;
import java.math.BigDecimal;
import java.math.RoundingMode;

class RoundedDoubleProperty extends SimpleDoubleProperty {

    @Override
    public double get() {
        var raw = super.get();
        if(raw == Double.POSITIVE_INFINITY || raw == Double.NEGATIVE_INFINITY || Double.isNaN(raw))
            return 0;
        else
            return BigDecimal.valueOf(super.get()).setScale(3, RoundingMode.HALF_UP).doubleValue();
    }

    public double getNonRounded() {
        return super.get();
    }
}
