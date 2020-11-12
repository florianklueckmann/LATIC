package dev.florianklueckmann.latic;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

import java.util.function.Function;

public abstract class LinguisticFeatureBase implements LinguisticFeature {

    StringProperty name = new SimpleStringProperty();
    StringProperty id = new SimpleStringProperty();

    public LinguisticFeatureBase(String name, String id) {
        setName(name);
        setId(id);
    }

    @Override
    public String getName()
    {
        return name.get();
    }

    public StringProperty nameProperty()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name.set(name);
    }

    @Override
    public String getId()
    {
        return id.get();
    }

    public StringProperty idProperty()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id.set(id);
    }
}

class LinguisticFeatureCallback implements Callback<TableColumn.CellDataFeatures<LinguisticFeature, String>, ObservableValue<String>>
{

    private Function<LinguisticFeature,Object> extractor;

    public LinguisticFeatureCallback(Function<LinguisticFeature,Object> extractorFunction) {
        extractor = extractorFunction;
    }

    @Override
    public ObservableValue<String> call(TableColumn.CellDataFeatures<LinguisticFeature, String> cellData) {
        return new SimpleObjectProperty(extractor.apply(cellData.getValue()));
    }
}