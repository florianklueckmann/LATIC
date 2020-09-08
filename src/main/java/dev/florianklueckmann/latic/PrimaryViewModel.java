package dev.florianklueckmann.latic;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import dev.florianklueckmann.latic.services.DocumentKeeper;
import dev.florianklueckmann.latic.services.NlpTextAnalyzer;
import dev.florianklueckmann.latic.services.SimpleTextAnalyzer;
import dev.florianklueckmann.latic.services.TextFormattingService;
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
import javafx.scene.text.TextFlow;

public class PrimaryViewModel implements Initializable {

    private PrimaryModel primaryModel;

//    @FXML
//    protected ListView<CheckBox> optionsListView;

    @FXML
    protected TextArea textAreaInput;

//    @FXML
//    protected TextFlow textFlowOutput;

//    @FXML
//    protected Label testLabel;

    final StringProperty item = new SimpleStringProperty("");

    public final String getItem() {
        return item.get();
    }

    public final void setItem(String input) {
        item.set(input);
    }

    public final StringProperty inputProperty() {
        return item;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        var documentKeeper = new DocumentKeeper();
        var textFormatter = new TextFormattingService();
        var simpleTextAnalyzer = new SimpleTextAnalyzer(textFormatter);
        var nlp = new NlpTextAnalyzer();

        primaryModel = new PrimaryModel(simpleTextAnalyzer, textFormatter, nlp);
//        optionsListView.setItems(createOptionsList());

        Bindings.bindBidirectional(inputProperty(), textAreaInput.textProperty());
//        Bindings.bindBidirectional(outputProperty(), textFlowOutput.text);
    }

    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("SecondaryView");
    }

//    @FXML
//    protected ObservableList<CheckBox> createOptionsList() {
//        return FXCollections.observableArrayList(
//                new CheckBox("TextIsGerman"),
//                new CheckBox("WordCount"),
//                new CheckBox("SentenceLength"),
//                new CheckBox("ParseAll"));
//    }

    public void AnalyzeText(ActionEvent actionEvent) {
        System.out.println("Input getParagraphs: ");
        System.out.println(textAreaInput.getParagraphs());
        System.out.println();

        primaryModel.setParagraphs(textAreaInput.getParagraphs());
        primaryModel.initializeDocument();
        primaryModel.analyzeAndPrintToConsole();
    }
}
