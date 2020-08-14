package dev.florianklueckmann.latic;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;

public class PrimaryViewModel implements Initializable {

    private PrimaryModel primaryModel;

    @FXML
    protected ListView<CheckBox> optionsListView;

    @FXML
    protected TextArea itemTextArea;

    @FXML
    protected Label testLabel;

    final StringProperty item = new SimpleStringProperty("");

    public final String getItem() {
        return item.get();
    }

    public final void setItem(String input) {
        item.set(input);
    }

    public final StringProperty itemProperty() {
        return item;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        primaryModel = new PrimaryModel();
        optionsListView.setItems(createOptionsList());

        Bindings.bindBidirectional(itemProperty(), itemTextArea.textProperty());
        Bindings.bindBidirectional(itemProperty(), testLabel.textProperty());
    }

    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("SecondaryView");
    }

    @FXML
    protected ObservableList<CheckBox> createOptionsList() {
        return FXCollections.observableArrayList(
                new CheckBox("TextIsGerman"),
                new CheckBox("WordCount"),
                new CheckBox("SentenceLength"),
                new CheckBox("ParseAll"));
    }

    public void CalculateStuffCommand(ActionEvent actionEvent) {
        primaryModel.calculateStuff();
    }
}
