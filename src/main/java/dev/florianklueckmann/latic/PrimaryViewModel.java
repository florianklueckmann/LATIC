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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.fxmisc.richtext.InlineCssTextArea;

public class PrimaryViewModel implements Initializable {

    private PrimaryModel primaryModel;

    @FXML
    protected TextArea textAreaInput;

    @FXML
    protected InlineCssTextArea textAreaOutput;

    @FXML
    ChoiceBox<String> choiceBoxLanguage;

    public ListProperty<String> languages = new SimpleListProperty<>();

    public void setLanguages() {
        languages.add("English");
        languages.add("German");

        choiceBoxLanguage.setValue("English");
    }

    public List<String> getLanguages() {
        return languages.get();
    }

    public ListProperty<String> languageProperty() {
        return languages;
    }

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

    ListProperty<String> resultProperty() {
        return result;
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        var documentKeeper = new DocumentKeeper();
        var textFormatter = new TextFormattingService();
        var simpleTextAnalyzer = new SimpleTextAnalyzer(textFormatter);
        var nlp = new NlpTextAnalyzer(textFormatter);

        primaryModel = new PrimaryModel(simpleTextAnalyzer, textFormatter, nlp);

        initializeBindings();

        initializeGui();
    }

    private void initializeBindings() {
        Bindings.bindBidirectional(inputProperty(), textAreaInput.textProperty());
        Bindings.bindBidirectional(languageProperty(), choiceBoxLanguage.itemsProperty());
    }

    private void initializeGui() {
        setLanguages();
    }

    public void changeLanguage() {
        System.out.println(choiceBoxLanguage.getValue());
        primaryModel.setLanguage(choiceBoxLanguage.getValue().toLowerCase());
    }

    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("SecondaryView");
    }

    public void AnalyzeText(ActionEvent actionEvent) {
        primaryModel.setParagraphs(textAreaInput.getParagraphs());
        primaryModel.initializeDocument();

        addResults("Item:");
        textAreaInput.getParagraphs().forEach(charSequence -> addResults(charSequence.toString()));

        primaryModel.analyzeGeneralItemCharacteristics()
                .forEach(linguisticFeature -> addResults(
                        //linguisticFeature.getId() +  " : " +
                        linguisticFeature.getName() + " : " +
                        linguisticFeature.getValue()));

        primaryModel.wordClassesAsList()
                .forEach(linguisticFeature -> addResults(
                        //linguisticFeature.getId() +  " : " +
                        linguisticFeature.getName() + " : " +
                        linguisticFeature.getValue()));

        addResults("\n" + "----------------Text and Tags----------------" + "\n");

        primaryModel.sentencesAndPosTags()
                .forEach(result -> addResults(result));

        addResults("\n" + "---------------------------------------------" + "\n");
    }
}
