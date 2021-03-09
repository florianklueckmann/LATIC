package dev.florianklueckmann.latic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
//    public Label labelLanguage;
    public Menu menuFile;
    public Menu menuHelp;
    public MenuItem menuItemDocumentation;
    public MenuItem menuItemOpen;
    public MenuItem menuItemSave;
    public MenuItem menuItemClose;
    public ImageView logo;

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

    public void bindGuiElements() {
        menuFile.textProperty().bind(Translation.getInstance().createStringBinding("file"));
        menuHelp.textProperty().bind(Translation.getInstance().createStringBinding("help"));
        menuItemDocumentation.textProperty().bind(Translation.getInstance().createStringBinding("documentation"));
        menuItemOpen.textProperty().bind(Translation.getInstance().createStringBinding("open"));
        menuItemSave.textProperty().bind(Translation.getInstance().createStringBinding("save"));
        menuItemClose.textProperty().bind(Translation.getInstance().createStringBinding("close"));
//        labelLanguage.textProperty().bind(Translation.getInstance().createStringBinding("language"));
        buttonAnalyze.textProperty().bind(Translation.getInstance().createStringBinding("analyze"));
        buttonSaveFile.textProperty().bind(Translation.getInstance().createStringBinding("saveFile"));

        Label resultPlaceholder = new Label();
        resultPlaceholder.textProperty().bind(Translation.getInstance().createStringBinding("resultPlaceholder"));
        tableViewResults.setPlaceholder(resultPlaceholder);

        try {
            logo.setImage(new Image(new FileInputStream("src/main/resources/dev/florianklueckmann/latic/latic_text_white_60px.png")));
        } catch (FileNotFoundException e) {
            logo.setVisible(false);
            e.printStackTrace();
        }
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

    public void addResultsToTextArea(String results) {
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
    ObservableList<CheckBoxTreeItem<Task>> languageSpecificTaskCheckBoxItems;
    ObservableList<CheckBoxTreeItem<Task>> generalTaskCheckBoxItems;

    ObservableList<CheckBoxTreeItem<Task>> taskCheckBoxItems;

    FileChooser fileChooser = new FileChooser();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        var textFormatter = new TextFormattingService();
        var simpleTextAnalyzer = new SimpleTextAnalyzer(textFormatter);
        var nlp = new NlpTextAnalyzer(textFormatter);

        primaryModel = new PrimaryModel(simpleTextAnalyzer, textFormatter, nlp);

        textItemDataResults = FXCollections.observableArrayList();
        taskCheckBoxItems = FXCollections.observableArrayList();


        initializeBindings();
        initialzeFileChooser();
        setupTaskLevelStructure();
        initializeGui();
    }

    private void setupTaskLevelStructure() {
        TaskLevel.ROOT.setParent(TaskLevel.ROOT);
        TaskLevel.WORD.setParent(TaskLevel.ROOT);
        TaskLevel.WORD_CLASS.setParent(TaskLevel.WORD);
        TaskLevel.WORD_CLASS_VERBS.setParent(TaskLevel.WORD_CLASS);
        TaskLevel.SENTENCE.setParent(TaskLevel.ROOT);
        TaskLevel.SENTENCE_LENGTH.setParent(TaskLevel.SENTENCE);
        TaskLevel.TEXT.setParent(TaskLevel.ROOT);
        TaskLevel.TEXT_READABILITY.setParent(TaskLevel.TEXT);
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
        bindGuiElements();
    }

    private void addBoxToParent(List<CheckBoxTreeItem<Task>> rootBoxes, CheckBoxTreeItem<Task> subBox) {
        for (var rootBox : rootBoxes) {
            if(rootBox.getValue().getLevel().equals(subBox.getValue().getLevel())) {
//                System.out.println(rootBox.getChildren().size());
                rootBox.getChildren().add(subBox);
                return;
            }
        }
    }

    private boolean mainCheckBoxItemTaskContainsSubTaskMainLevel(CheckBoxTreeItem<Task> main, CheckBoxTreeItem<Task> sub) {
        return main.getValue()
                .getLevel().toString()
                .contains(getMainLevel(sub.getValue().getLevel()));
    }

    private String getSubLevel(TaskLevel taskLevel) {
        return taskLevel.toString().substring(taskLevel.toString().indexOf("_")+1);
    }

    private String getMainLevel(TaskLevel subTaskLevel) {
        var index = subTaskLevel.toString().indexOf("_");
        if (index >= 0)
            return subTaskLevel.toString().substring(0, index);
        else
            return "ROOT";
    }

    private CheckBoxTreeItem<Task> getRoot(CheckBoxTreeItem<Task> item) {
        CheckBoxTreeItem<Task> root = item;

        while(item.getParent() != null) {
            root = (CheckBoxTreeItem<Task>) item.getParent();
        }
        return root;
    }

    private void createCheckboxes() {


        generalTaskCheckBoxItems = FXCollections.observableArrayList(Arrays.stream(GeneralItemCharacteristics.values())
                .map(textInformation -> new CheckBoxTreeItem<Task>(new Task(Translation.getInstance().getTranslation(textInformation.getId()), textInformation.getId(), textInformation.getLevel()), null, true))
                .collect(Collectors.toList()));

        textTaskCheckBoxItems = FXCollections.observableArrayList(Arrays.stream(TextInformationItemCharacteristics.values())
                .map(textInformationItemCharacteristics -> new CheckBoxTreeItem<Task>(new Task(Translation.getInstance().getTranslation(textInformationItemCharacteristics.getId()), textInformationItemCharacteristics.getId(), textInformationItemCharacteristics.getLevel()), null, true))
                .collect(Collectors.toList()));

        languageSpecificTaskCheckBoxItems = FXCollections.observableArrayList(Arrays.stream(GeneralLanguageItemCharacteristics.values())
                .map(generalLanguageItemCharacteristics -> new CheckBoxTreeItem<Task>(new Task(Translation.getInstance().getTranslation(generalLanguageItemCharacteristics.getId()), generalLanguageItemCharacteristics.getId(), generalLanguageItemCharacteristics.getLevel()), null, true))
                .collect(Collectors.toList()));

        if (choiceBoxLanguage.getValue().equals(Locale.ENGLISH)) {
            languageSpecificTaskCheckBoxItems.addAll(FXCollections.observableArrayList(
                    Arrays.stream(EnglishItemCharacteristics.values())
                            .map(englishItemCharacteristic -> new CheckBoxTreeItem<Task>(new Task(Translation.getInstance().getTranslation(englishItemCharacteristic.getId()), englishItemCharacteristic.getId(), englishItemCharacteristic.getLevel()), null, true))
                            .collect(Collectors.toList())
            ));
        }

        if (choiceBoxLanguage.getValue().equals(Locale.GERMAN)) {
            languageSpecificTaskCheckBoxItems.addAll(FXCollections.observableArrayList(
                    Arrays.stream(GermanItemCharacteristics.values())
                            .map(germanItemCharacteristic -> new CheckBoxTreeItem<Task>(new Task(Translation.getInstance().getTranslation(germanItemCharacteristic.getId()), germanItemCharacteristic.getId(), germanItemCharacteristic.getLevel()), null, true))
                            .collect(Collectors.toList())
            ));
        }

        taskCheckBoxItems = FXCollections.concat(generalTaskCheckBoxItems, textTaskCheckBoxItems, languageSpecificTaskCheckBoxItems);
        taskCheckBoxItems.add(new CheckBoxTreeItem<Task>(new Task("Text", "text", TaskLevel.TEXT), null, true));

        var boxSet = new HashSet<TaskLevel>();

        for(var checkBox : taskCheckBoxItems) {
            boxSet.add(checkBox.getValue().getLevel());
        }

        var boxes = boxSet.stream().map(taskLevel ->
                new CheckBoxTreeItem<Task>(new Task(taskLevel), null, true))
                .collect(Collectors.toList());

        for(var item : taskCheckBoxItems) {
            addBoxToParent(boxes, item);
        }
        var root = new CheckBoxTreeItem<Task>(new Task(TaskLevel.ROOT));

        structureBoxes(root, boxes);
        root.getChildren().sort(Comparator.comparing(taskTreeItem -> taskTreeItem.getValue().getLevel().getPriority()));
        expandTreeView(root);

        treeView.setRoot(null);
        treeView.setRoot(root);
        treeView.setShowRoot(false);

        // set the cell factory
        treeView.setCellFactory(CheckBoxTreeCell.<Task>forTreeView());
    }

    private void structureBoxes(CheckBoxTreeItem<Task> node, List<CheckBoxTreeItem<Task>> boxes) {
        for (var item : boxes) {
            if (item.getValue().getLevel().getParent() == node.getValue().getLevel()) {
                node.getChildren().add(item);
                if (item != null && !item.isLeaf()) {
                    structureBoxes(item, boxes);
                }
            }
        }
    }

    private void expandTreeView(TreeItem<?> item){
        if(item != null && !item.isLeaf()){
            item.setExpanded(true);
            for(TreeItem<?> child:item.getChildren()){
                expandTreeView(child);
            }
        }
    }

    public void changeLanguage() {
        primaryModel.setLanguage(choiceBoxLanguage.getValue());
        //TODO Cleaner solution
        Translation.getInstance().setLocale(choiceBoxLanguage.getValue());
        TagMapper.getInstance().loadInterjections();
        createCheckboxes();
    }

    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("SecondaryView");
    }

    public void AnalyzeText(ActionEvent actionEvent) {
        initColumns();

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
//
        var wordLevelTasks = FXCollections.observableArrayList(
                languageSpecificTaskCheckBoxItems.stream()
                        .map(TreeItem::getValue)
                        .collect(Collectors.toList())
        );

        primaryModel.setParagraphs(textAreaInput.getParagraphs());
        primaryModel.initializeDocument();

        //TODO Only Anylyze Selected Items
        //TODO Dont duplicate tasks in textTasks
        var currentItem = primaryModel.processTasks(textTasks, generalTasks, wordLevelTasks);
        textItemDataResults.add(currentItem);
        //TODO: Add Option to Disable TextAreaOutput
        if (false) {
            printResultToTextArea(currentItem);
        }

        tableViewResults.setItems(textItemDataResults);
    }

    private void printResultToTextArea(TextItemData textItemData, int... position) {
            addResultsToTextArea(String.format("Item %s:", position.length == 1 ? position[0] : ""));
        textItemData.getIdValueMap().forEach((key, value) -> addResultsToTextArea(
                    String.format("%s:\n%s",
                            Translation.getInstance().getTranslation(key), value)));
            addResultsToTextArea("--------");
    }

    private void log(Object o) {
        Logger.getLogger("PrimaryViewModel").log(Level.DEBUG, o);
    }

    private void createColumns(CheckBoxTreeItem<Task> root){
//        System.out.println("Current Parent :" + root.getValue());
        for(TreeItem<Task> child: root.getChildren()){
            if(child.getChildren().isEmpty()){
//                System.out.println(child.getValue());
                if (((CheckBoxTreeItem<Task>) child).selectedProperty().get())
                {
//                    System.out.println(child.getValue().getName());
                    TableColumn<TextItemData, ?> column = new TableColumn<>();
                    column.textProperty().bind(Translation.getInstance().createStringBinding(child.getValue().getId()));
                    column.setId(child.getValue().getId());
                    column.setCellValueFactory(new PropertyValueFactory<>(child.getValue().getId()));
                    tableViewResults.getColumns().add(column);
                }
            } else {
                createColumns((CheckBoxTreeItem<Task>) child);
            }
        }
    }

    private void initColumns() {
        //TODO Bindings for Columns
        tableViewResults.getColumns().clear();
        createColumns((CheckBoxTreeItem<Task>) treeView.getRoot());
    }

}
