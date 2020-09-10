package dev.florianklueckmann.latic;

import java.io.IOException;
import java.net.URL;
import java.util.*;

import dev.florianklueckmann.latic.services.DocumentKeeper;
import dev.florianklueckmann.latic.services.NlpTextAnalyzer;
import dev.florianklueckmann.latic.services.SimpleTextAnalyzer;
import dev.florianklueckmann.latic.services.TextFormattingService;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.fxmisc.richtext.InlineCssTextArea;
import org.fxmisc.richtext.StyleClassedTextArea;
import org.fxmisc.richtext.model.StyledDocument;

public class PrimaryViewModel implements Initializable {

    private PrimaryModel primaryModel;

    @FXML
    protected TextArea textAreaInput;

    @FXML
    protected TextFlow textFlowOutput;

    @FXML
    protected InlineCssTextArea textAreaOutput;

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

    public ListProperty<String> result = new SimpleListProperty<>();

    public void setResults(String results) {
        textAreaOutput.clear();
//        textAreaOutput.append(results, "-fx-fill: black");
        textAreaOutput.appendText(results);
        textAreaOutput.appendText("\n");
    }

    public void addResults(String results) {
        textAreaOutput.appendText(results);
        textAreaOutput.appendText("\n");
    }

    public List<String> getResults() {
        return result.get();
    }

    // Define a getter for the property itself
    ListProperty<String> resultProperty() {
        return result;
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        var documentKeeper = new DocumentKeeper();
        var textFormatter = new TextFormattingService();
        var simpleTextAnalyzer = new SimpleTextAnalyzer(textFormatter);
        var nlp = new NlpTextAnalyzer();

        primaryModel = new PrimaryModel(simpleTextAnalyzer, textFormatter, nlp);

        Bindings.bindBidirectional(inputProperty(), textAreaInput.textProperty());
    }

    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("SecondaryView");
    }

    public void AnalyzeText(ActionEvent actionEvent) {
        System.out.println("Input getParagraphs: ");
        System.out.println(textAreaInput.getParagraphs());

        System.out.println();

        primaryModel.setParagraphs(textAreaInput.getParagraphs());
        primaryModel.initializeDocument();

        setResults(primaryModel.analyze());

    }
}
