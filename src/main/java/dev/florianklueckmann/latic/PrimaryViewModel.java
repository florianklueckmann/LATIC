package dev.florianklueckmann.latic;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

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
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.util.StringConverter;
import org.fxmisc.richtext.InlineCssTextArea;

public class PrimaryViewModel implements Initializable {

    private PrimaryModel primaryModel;

    @FXML
    protected TextArea textAreaInput;

    @FXML
    protected InlineCssTextArea textAreaOutput;

    @FXML
    ChoiceBox<String> choiceBoxLanguage;

    @FXML
    ListView<Task> checkList;

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

    ObservableList<Task> tasks;


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
        System.out.println("GUI");
        setLanguages();

//        ObservableList<Task> tasks = FXCollections.observableArrayList(
//                Arrays.stream(GeneralItemCharacteristics.values())
//                        .map(generalItemCharacteristics -> new Task(generalItemCharacteristics.getName(), generalItemCharacteristics.getId()))
//                        .collect(Collectors.toList())
//        );

//        ObservableList<Task> tasks = FXCollections.observableArrayList();
//        Arrays.stream(GeneralItemCharacteristics.values())
//                .forEach(generalItemCharacteristics -> tasks.add(new Task(generalItemCharacteristics.getName(), generalItemCharacteristics.getId())));

        tasks = FXCollections.observableArrayList(
                Arrays.stream(GeneralItemCharacteristics.values()).map(generalItemCharacteristics -> new Task(generalItemCharacteristics.getName(), generalItemCharacteristics.getId())).collect(Collectors.toList())
        );

//        tasks.forEach(task -> task.selectedProperty().get());
//
//        tasks.forEach(task -> task.selectedProperty().addListener((observable, wasSelected, isSelected) -> {
//            if (isSelected) {
//                reactionLog.getItems().add(reactionStrings.get(task.getName()));
//                reactionLog.scrollTo(reactionLog.getItems().size() - 1);
//            }
//        }));

        checkList.setItems(tasks);
        checkList.setCellFactory(CheckBoxListCell.forListView(Task::selectedProperty, new StringConverter<Task>() {
            @Override
            public String toString(Task object) {
                return object.getName();
            }

            @Override
            public Task fromString(String string) {
                return null;
            }
        }));


//            checkList = new ListView<>(tasks);

//        checkList.setCellFactory(CheckBoxListCell.forListView(task -> task.selectedProperty(), new StringConverter<>() {
//            @Override
//            public String toString(Task object) {
//                System.out.println(object.getName());
//                return object.getName();
//            }
//
//            @Override
//            public Task fromString(String string) {
//                return null;
//            }
//        }));
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
//    public void AnalyzeText(ObservableList<Task> tasks) {
        primaryModel.setParagraphs(textAreaInput.getParagraphs());
        primaryModel.initializeDocument();

        addResults("Item:");
        textAreaInput.getParagraphs().forEach(charSequence -> addResults(charSequence.toString()));

        primaryModel.analyzeGeneralItemCharacteristics(tasks)
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
