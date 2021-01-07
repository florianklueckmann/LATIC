package dev.florianklueckmann.latic;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import dev.florianklueckmann.latic.Translation.Translation;
import dev.florianklueckmann.latic.services.*;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.util.StringConverter;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.fxmisc.richtext.InlineCssTextArea;

public class PrimaryViewModel implements Initializable {

    @FXML
    public BorderPane mainPane;

    @FXML
    public Label labelLanguage;
    public Menu menuFile;
    public Menu menuHelp;
    public MenuItem menuItemDocumentation;
    public MenuItem menuItemOpen;
    public MenuItem menuItemSave;
    public MenuItem menuItemClose;

    private PrimaryModel primaryModel;

    @FXML
    public TableView<TextItemData> tableViewResults;

    @FXML
    protected TextArea textAreaInput;

    @FXML
    protected InlineCssTextArea textAreaOutput;

    @FXML
    ChoiceBox<Locale> choiceBoxLanguage;

    @FXML
    TreeView<Task> treeView;

    @FXML
    Button buttonAnalyze;
    @FXML
    Button buttonSaveFile;

    ObservableList<TextItemData> textItemDataResults;

    public ListProperty<Locale> languages = new SimpleListProperty<>();

//    ObservableList<CheckBoxTreeItem<Task>> allTaskCheckBoxItems;

    public void bindGuiElements() {
        menuFile.textProperty().bind(Translation.getInstance().createStringBinding("file"));
        menuHelp.textProperty().bind(Translation.getInstance().createStringBinding("help"));
        menuItemDocumentation.textProperty().bind(Translation.getInstance().createStringBinding("documentation"));
        menuItemOpen.textProperty().bind(Translation.getInstance().createStringBinding("open"));
        menuItemSave.textProperty().bind(Translation.getInstance().createStringBinding("save"));
        menuItemClose.textProperty().bind(Translation.getInstance().createStringBinding("close"));
        labelLanguage.textProperty().bind(Translation.getInstance().createStringBinding("language"));
        buttonAnalyze.textProperty().bind(Translation.getInstance().createStringBinding("analyze"));
        buttonSaveFile.textProperty().bind(Translation.getInstance().createStringBinding("saveFile"));

        Label resultPlaceholder = new Label();
        resultPlaceholder.textProperty().bind(Translation.getInstance().createStringBinding("resultPlaceholder"));
        tableViewResults.setPlaceholder(resultPlaceholder);
    }

    public void setLanguages() {
        languages.addAll(Translation.getInstance().getSupportedLocales());

        choiceBoxLanguage.setValue(Translation.getInstance().getLocale());
        choiceBoxLanguage.setConverter(new StringConverter<Locale>() {
            @Override
            public String toString(Locale locale) {
                if (locale != null) {
                    return locale.getDisplayLanguage(locale);
                }
                return "";
            }

            @Override
            public Locale fromString(String string) {
                var locale = Translation.getInstance().getSupportedLocales().stream().filter(l -> l.getLanguage().equals(string)).findFirst();
                return locale.orElse(null);
            }
        });
    }

    public List<Locale> getLanguages() {
        return languages.get();
    }

    public ListProperty<Locale> languageProperty() {
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

    ObservableList<CheckBoxTreeItem<Task>> textTaskCheckBoxItems;
    ObservableList<CheckBoxTreeItem<Task>> generalTaskCheckBoxItems;
    ObservableList<CheckBoxTreeItem<Task>> languageSpecificTaskCheckBoxItems;

    FileChooser fileChooser = new FileChooser();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        var textFormatter = new TextFormattingService();
        var simpleTextAnalyzer = new SimpleTextAnalyzer(textFormatter);
        var nlp = new NlpTextAnalyzer(textFormatter);

        primaryModel = new PrimaryModel(simpleTextAnalyzer, textFormatter, nlp);

        textItemDataResults = FXCollections.observableArrayList();


        initializeBindings();
        initialzeFileChooser();
        initializeGui();
    }

    private void initialzeFileChooser() {

        boolean isMac = System.getProperty("os.name").contains("Mac");
                //.equals("Mac OS X");
        boolean isWin = System.getProperty("os.name").contains("Win");
        boolean isOther = !isMac && !isWin;

        String initialFilePath;
        if (isMac)
            initialFilePath = System.getProperty("user.home")+File.separator+"Documents";
        else if(isWin)
            initialFilePath = System.getProperty("user.home");
        else
            initialFilePath = System.getProperty("user.home");




        fileChooser.setInitialDirectory(new File(initialFilePath));
        fileChooser.setTitle("Save File");
        fileChooser.setInitialFileName("table");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text file", "*.txt"),
                new FileChooser.ExtensionFilter("CSV table file", "*.csv"));

    }

    private List<String[]> getTableData(){
        var outList = new ArrayList<String[]>();

        for (var textItemDataResult : textItemDataResults) {
            outList.add(textItemDataResult.getValues());
        }

        return outList;

    }

    @FXML
    private void handleSaveClicked(ActionEvent event){
        Window stage = mainPane.getScene().getWindow();
        CsvBuilder csvBuilder = new CsvBuilder();
        try{
            File file = csvBuilder.writeToFile(fileChooser.showSaveDialog(stage), getTableData());
            fileChooser.setInitialDirectory(file.getParentFile());
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void initializeBindings() {
        Bindings.bindBidirectional(inputProperty(), textAreaInput.textProperty());
        Bindings.bindBidirectional(languageProperty(), choiceBoxLanguage.itemsProperty());
    }

    private void initializeGui() {
        setLanguages();
//        createCheckboxes();
        bindGuiElements();
    }

    private void createCheckboxes() {

        generalTaskCheckBoxItems = FXCollections.observableArrayList(Arrays.stream(GeneralItemCharacteristics.values())
                .map(textInformation -> new CheckBoxTreeItem<Task>(new Task(Translation.getInstance().getTranslation(textInformation.getId()), textInformation.getId()), null, true))
                .collect(Collectors.toList()));

        textTaskCheckBoxItems = FXCollections.observableArrayList(Arrays.stream(TextInformation.values())
                .map(textInformation -> new CheckBoxTreeItem<Task>(new Task(Translation.getInstance().getTranslation(textInformation.getId()), textInformation.getId()), null, true))
                .collect(Collectors.toList()));

        languageSpecificTaskCheckBoxItems = FXCollections.observableArrayList(Arrays.stream(GeneralLanguageItemCharacteristics.values())
                .map(generalLanguageItemCharacteristics -> new CheckBoxTreeItem<Task>(new Task(Translation.getInstance().getTranslation(generalLanguageItemCharacteristics.getId()), generalLanguageItemCharacteristics.getId()), null, true))
                .collect(Collectors.toList()));

        if (choiceBoxLanguage.getValue().equals(Locale.ENGLISH)) {
            languageSpecificTaskCheckBoxItems.addAll(FXCollections.observableArrayList(
                    Arrays.stream(EnglishItemCharacteristics.values())
                            .map(englishItemCharacteristic -> new CheckBoxTreeItem<Task>(new Task(Translation.getInstance().getTranslation(englishItemCharacteristic.getId()), englishItemCharacteristic.getId()), null, true))
                            .collect(Collectors.toList())
            ));
        }

        if (choiceBoxLanguage.getValue().equals(Locale.GERMAN)) {
            languageSpecificTaskCheckBoxItems.addAll(FXCollections.observableArrayList(
                    Arrays.stream(GermanItemCharacteristics.values())
                            .map(germanItemCharacteristic -> new CheckBoxTreeItem<Task>(new Task(Translation.getInstance().getTranslation(germanItemCharacteristic.getId()), germanItemCharacteristic.getId()), null, true))
                            .collect(Collectors.toList())
            ));
        }

        CheckBoxTreeItem<Task> generalRoot = new CheckBoxTreeItem<>(new Task("generalRoot", "generalRoot"), null, true);
        generalRoot.getChildren().addAll(generalTaskCheckBoxItems);
        generalRoot.setExpanded(true);

        CheckBoxTreeItem<Task> textRoot = new CheckBoxTreeItem<>(new Task("textRoot", "textRoot"), null, true);
        textRoot.getChildren().addAll(textTaskCheckBoxItems);
        textRoot.setExpanded(true);

        CheckBoxTreeItem<Task> languageSpecificRoot = new CheckBoxTreeItem<>(new Task("languageSpecificRoot", "languageSpecificRoot"), null, true);
        languageSpecificRoot.getChildren().addAll(languageSpecificTaskCheckBoxItems);
        languageSpecificRoot.setExpanded(true);

        CheckBoxTreeItem<Task> root = new CheckBoxTreeItem<>(new Task("root", "root"), null, true);
        root.getChildren().add(generalRoot);
        root.getChildren().add(textRoot);
        root.getChildren().add(languageSpecificRoot);

        treeView.setRoot(null);
        treeView.setRoot(root);

        root.setExpanded(true);
        treeView.setShowRoot(false);
        // set the cell factory
        treeView.setCellFactory(CheckBoxTreeCell.<Task>forTreeView());

    }

    public void changeLanguage() {
        primaryModel.setLanguage(choiceBoxLanguage.getValue());
        //TODO Cleaner solution
        Translation.getInstance().setLocale(choiceBoxLanguage.getValue());
        createCheckboxes();
    }

    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("SecondaryView");
    }

    public void AnalyzeText(ActionEvent actionEvent) {
        createColumns();

        var generalTasks = FXCollections.observableArrayList(
                generalTaskCheckBoxItems.stream()
                        .map(TreeItem::getValue)
                        .collect(Collectors.toList())
        );

        var textTasks = FXCollections.observableArrayList(
                textTaskCheckBoxItems.stream()
                        .map(TreeItem::getValue)
                        .collect(Collectors.toList())
        );

        var languageSpecificTasks = FXCollections.observableArrayList(
                languageSpecificTaskCheckBoxItems.stream()
                        .map(TreeItem::getValue)
                        .collect(Collectors.toList())
        );


        primaryModel.setParagraphs(textAreaInput.getParagraphs());
        primaryModel.initializeDocument();

        //TODO Only Anylyze Selected Items
        //TODO Dont duplicate tasks in textTasks
        textItemDataResults.add(primaryModel.analyzeItem(textTasks, generalTasks, languageSpecificTasks));

        addResults("Item:");
        textAreaInput.getParagraphs().forEach(charSequence -> addResults(charSequence.toString()));

        primaryModel.analyzeGeneralItemCharacteristics(generalTasks)
                .forEach(linguisticFeature -> addResults(
                        //linguisticFeature.getId() +  " : " +
                        linguisticFeature.getName() + " : " +
                        linguisticFeature.getValue()));

        log("---- selected Tasks ----");
        languageSpecificTasks.stream().forEach(task -> log(task.getId() + " : " + task.isSelected()));
        log("---- end selected Tasks -----");
        for (var linguisticFeature : primaryModel.wordClassesAsList(languageSpecificTasks))
        {
            if (languageSpecificTaskCheckBoxItems.stream().anyMatch(taskCheckBoxTreeItem -> taskCheckBoxTreeItem.getValue().getId().equals(linguisticFeature.getId()) && taskCheckBoxTreeItem.isSelected()))
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
            if (textTaskCheckBoxItems.stream().anyMatch(taskCheckBoxTreeItem -> taskCheckBoxTreeItem.getValue().getId().equals(linguisticFeature.getId()) && taskCheckBoxTreeItem.isSelected()))

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

    private void log(Object o) {
        Logger.getLogger("PrimaryViewModel").log(Level.DEBUG, o);
    }

    private void createColumns() {
        //TODO Bindings for Columns
        tableViewResults.getColumns().clear();
        var allTaskCheckBoxItems = FXCollections.concat(
                textTaskCheckBoxItems,
                generalTaskCheckBoxItems,
                languageSpecificTaskCheckBoxItems);

        for (var item : allTaskCheckBoxItems) {
            if (item.selectedProperty().get())
            {
                TableColumn<TextItemData, ?> column = new TableColumn<>();
                column.textProperty().bind(Translation.getInstance().createStringBinding(item.getValue().getId()));
                column.setId(item.getValue().getId());
                column.setCellValueFactory(new PropertyValueFactory<>(item.getValue().getId()));
                tableViewResults.getColumns().add(column);
            }
        }

//        for (var item : allTaskCheckBoxItems) {
//            if (item.selectedProperty().get())
//            {
//                TableColumn<TextItemData, ?> column = new TableColumn<>(item.getValue().getName());
//                column.setId(item.getValue().getId());
//                column.setCellValueFactory(new PropertyValueFactory<>(item.getValue().getId()));
//                tableViewResults.getColumns().add(column);
//            }
//        }
    }

}
