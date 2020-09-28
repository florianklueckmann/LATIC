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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;
import org.fxmisc.richtext.InlineCssTextArea;

public class PrimaryViewModel implements Initializable {

    private PrimaryModel primaryModel;

    @FXML
    public TableView<TextItemData> tableViewResults;

    @FXML
    protected TextArea textAreaInput;

    @FXML
    protected InlineCssTextArea textAreaOutput;

    @FXML
    ChoiceBox<String> choiceBoxLanguage;

    @FXML
    ListView<Task> checkList;

    ObservableList<TextItemData> textItemDataResults;

    public ListProperty<String> languages = new SimpleListProperty<>();

    public void setLanguages() {
        languages.add("English");
        languages.add("German");

        choiceBoxLanguage.setValue("German");
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

    ObservableList<Task> textTasks;
    ObservableList<Task> generalTasks;
    ObservableList<Task> languageSpecificTasks;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        var documentKeeper = new DocumentKeeper();
        var textFormatter = new TextFormattingService();
        var simpleTextAnalyzer = new SimpleTextAnalyzer(textFormatter);
        var nlp = new NlpTextAnalyzer(textFormatter);

        primaryModel = new PrimaryModel(simpleTextAnalyzer, textFormatter, nlp);

        textItemDataResults = FXCollections.observableArrayList();

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
        createCheckboxes();

        setItem("I'm a cookie.");

    }

    private void createCheckboxes() {
        generalTasks = FXCollections.observableArrayList(
                Arrays.stream(GeneralItemCharacteristics.values())
                        .map(generalItemCharacteristics -> new Task(generalItemCharacteristics.getName(), generalItemCharacteristics.getId()))
                        .collect(Collectors.toList())
        );

        textTasks = FXCollections.observableArrayList(
                Arrays.stream(TextInformation.values())
                .map(textInformation -> new Task(textInformation.getName(), textInformation.getId()))
                .collect(Collectors.toList()));

        languageSpecificTasks = FXCollections.observableArrayList(
                Arrays.stream(GeneralLanguageItemCharacteristics.values())
                        .map(generalLanguageItemCharacteristics -> new Task(generalLanguageItemCharacteristics.getName(), generalLanguageItemCharacteristics.getId()))
                        .collect(Collectors.toList()));

        if (choiceBoxLanguage.getValue().equals("English")) {
            languageSpecificTasks.addAll(FXCollections.observableArrayList(
                    Arrays.stream(EnglishItemCharacteristics.values())
                            .map(englishItemCharacteristic -> new Task(englishItemCharacteristic.getName(), englishItemCharacteristic.getId()))
                            .collect(Collectors.toList())
            ));
        }

        if (choiceBoxLanguage.getValue().equals("German")) {
            languageSpecificTasks.addAll(FXCollections.observableArrayList(
                    Arrays.stream(GermanItemCharacteristics.values())
                            .map(germanItemCharacteristic -> new Task(germanItemCharacteristic.getName(), germanItemCharacteristic.getId()))
                            .collect(Collectors.toList())
            ));
        }

        checkList.setItems(FXCollections.concat(generalTasks, textTasks, languageSpecificTasks));
//        checkList.setItems(languageSpecificTasks);
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
    }

    public void changeLanguage() {
        System.out.println(choiceBoxLanguage.getValue());
        primaryModel.setLanguage(choiceBoxLanguage.getValue().toLowerCase());
        createCheckboxes();
//        createColumns();
    }

    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("SecondaryView");
    }

    public void AnalyzeText(ActionEvent actionEvent) {
        createColumns();

        primaryModel.setParagraphs(textAreaInput.getParagraphs());
        primaryModel.initializeDocument();

        textItemDataResults.add(primaryModel.analyzeItem(textTasks, generalTasks, languageSpecificTasks));

        addResults("Item:");
        textAreaInput.getParagraphs().forEach(charSequence -> addResults(charSequence.toString()));

        primaryModel.analyzeGeneralItemCharacteristics(generalTasks)
                .forEach(linguisticFeature -> addResults(
                        //linguisticFeature.getId() +  " : " +
                        linguisticFeature.getName() + " : " +
                        linguisticFeature.getValue()));

//        log("---- selected Tasks ----");
//        languageSpecificTasks.stream().forEach(task -> log(task.getId() + " : " + task.isSelected()));
//        log("---- end log-----");
        for (var linguisticFeature : primaryModel.wordClassesAsList(languageSpecificTasks))
        {
            if (languageSpecificTasks.stream().anyMatch(task -> task.getId().equals(linguisticFeature.getId()) && task.isSelected()))
            {
                addResults(
                        //linguisticFeature.getId() +  " : " +
                        linguisticFeature.getName() + " : " +
                                linguisticFeature.getValue());
            }
            log(linguisticFeature.getId() + " : " + linguisticFeature.getName() + " : " +
                    linguisticFeature.getValue());
        }

        addResults("\n" + "----------------Text and Tags----------------" + "\n");

        for (var linguisticFeature : primaryModel.analyzeTextInformation(textTasks))
        {
            if (textTasks.stream().anyMatch(task -> task.getId().equals(linguisticFeature.getId()) && task.isSelected()))
            {
                addResults(
                        //linguisticFeature.getId() +  " : " +
                        linguisticFeature.getName() + " : \n" +
                                linguisticFeature.getValue());
            }
            log(linguisticFeature.getId() + " : " + linguisticFeature.getName() + " : " +
                    linguisticFeature.getValue());
        }

        addResults("\n" + "---------------------------------------------" + "\n");


        tableViewResults.setItems(textItemDataResults);
    }

    private void appendSelectedTextInformation() {

    }

    private void log(Object o){
        System.out.println(o);
    }

    private void createColumns() {
        tableViewResults.getColumns().clear();
        ObservableList<Task> tasks = FXCollections.observableArrayList();
        tasks.addAll(textTasks);
        tasks.addAll(generalTasks);
        tasks.addAll(languageSpecificTasks);
        for (var task : tasks) {
            if (task.selectedProperty().get())
            {
                TableColumn<TextItemData, ?> column = new TableColumn<>(task.getName());
                column.setId(task.getId());
                System.out.println("ID: " + task.getId() + " Name: " + task.getName());
//            column.setCellValueFactory(new LinguisticFeatureCallback((linguisticFeature) -> { return linguisticFeature.getId();}));
                column.setCellValueFactory(new PropertyValueFactory<>(task.getId()));
                tableViewResults.getColumns().add(column);
            }
        }
//        if (choiceBoxLanguage.getValue().equals("English")) {
//            for (var itemCharacteristic : EnglishItemCharacteristics.values()) {
//                TableColumn<TextItem, ?> column = new TableColumn<>(itemCharacteristic.getName());
//                column.setId(itemCharacteristic.getId());
//                System.out.println("ID: " + itemCharacteristic.getId() + " Name: " + itemCharacteristic.getName());
//                column.setCellValueFactory(new PropertyValueFactory<>(itemCharacteristic.getId()));
//                tableViewResults.getColumns().add(column);
//            }
//        } else if(choiceBoxLanguage.getValue().equals("German")){
//            for (var itemCharacteristic : GermanItemCharacteristics.values()) {
//                TableColumn<TextItem, ?> column = new TableColumn<>(itemCharacteristic.getName());
//                column.setId(itemCharacteristic.getId());
//                System.out.println("ID: " + itemCharacteristic.getId() + " Name: " + itemCharacteristic.getName());
//                column.setCellValueFactory(new PropertyValueFactory<>(itemCharacteristic.getId()));
//                tableViewResults.getColumns().add(column);
//            }
//        }
    }

}
