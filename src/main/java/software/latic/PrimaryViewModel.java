package software.latic;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import org.apache.log4j.Level;
import software.latic.item.*;
import software.latic.translation.Translation;
import software.latic.helper.TagMapper;
import software.latic.helper.CsvBuilder;
import software.latic.text_analyzer.NlpTextAnalyzer;
import software.latic.text_analyzer.SimpleTextAnalyzer;
import software.latic.word_class_service.TextFormattingService;
import software.latic.task.Task;
import software.latic.task.TaskLevel;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.util.StringConverter;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class PrimaryViewModel implements Initializable {

    @FXML private MenuItem menuItemSyllablesPerWordToCsv;
    @FXML private BorderPane mainPane;
    @FXML private Menu menuHelp;
    @FXML private Menu menuDebug;
    @FXML private MenuItem menuItemDocumentation;
    @FXML private MenuItem menuItemContact;
    @FXML private Button buttonDelete;
    @FXML private TableView<TextItemData> tableViewResults;
    @FXML private TextArea textAreaInput;
    @FXML private ChoiceBox<Locale> choiceBoxLanguage;
    @FXML private TreeView<Task> treeView;
    @FXML private Button buttonAnalyze;
    @FXML private Button buttonSaveFile;

    private PrimaryModel primaryModel;

    private ObservableList<TextItemData> textItemDataResults;

    private final ListProperty<Locale> languages = new SimpleListProperty<>();

    public void bindGuiElements() {
        menuHelp.textProperty().bind(Translation.getInstance().createStringBinding("help"));
        menuItemDocumentation.textProperty().bind(Translation.getInstance().createStringBinding("documentation"));
        menuItemContact.textProperty().bind(Translation.getInstance().createStringBinding("contact"));

        buttonAnalyze.textProperty().bind(Translation.getInstance().createStringBinding("analyze"));
        buttonSaveFile.textProperty().bind(Translation.getInstance().createStringBinding("saveFile"));
        buttonDelete.textProperty().bind(Translation.getInstance().createStringBinding("delete"));

        Label resultPlaceholder = new Label();
        resultPlaceholder.textProperty().bind(Translation.getInstance().createStringBinding("resultPlaceholder"));
        tableViewResults.setPlaceholder(resultPlaceholder);

        choiceBoxLanguage.disableProperty().bind(Bindings.isNotEmpty(textItemDataResults));
    }

    public void setLanguages() {
        languages.addAll(Translation.getInstance().getSupportedLocales());

        choiceBoxLanguage.setValue(Translation.getInstance().getLocale());
        choiceBoxLanguage.setConverter(new StringConverter<>() {
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
        initializeFileChooser();
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

    private void initializeFileChooser() {

        boolean isMac = System.getProperty("os.name").contains("Mac");
        //.equals("Mac OS X");
        boolean isWin = System.getProperty("os.name").contains("Win");

        String initialFilePath;
        if (isMac)
            initialFilePath = System.getProperty("user.home") + File.separator + "Documents";
        else if (isWin)
            initialFilePath = System.getProperty("user.home");
        else
            initialFilePath = System.getProperty("user.home") + File.separator + "Documents";


        fileChooser.setInitialDirectory(new File(initialFilePath));
        fileChooser.setTitle("Save File");
        fileChooser.setInitialFileName("table");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text file", "*.txt"),
                new FileChooser.ExtensionFilter("CSV table file", "*.csv"),
                new FileChooser.ExtensionFilter("CSV table file for excel", "*.csv"));

    }

    private List<String[]> getTableData() {
        var outList = new ArrayList<String[]>();

        var headerRowText = new ArrayList<String>();
        var headerRowIds = new ArrayList<String>();
        tableViewResults.getColumns()
                .forEach(textItemDataTableColumn -> {
                    headerRowText.add(textItemDataTableColumn.getText());
                    headerRowIds.add(textItemDataTableColumn.getId());
                });

        outList.add(headerRowText.toArray(String[]::new));

        for (var testItemDataResult : textItemDataResults) {
            var values = new ArrayList<String>();
            var map = testItemDataResult.getIdValueMap();
            headerRowIds.forEach(id -> values.add(map.get(id)));
            outList.add(values.toArray(String[]::new));
        }

        return outList;

    }

    @FXML
    private void handleSaveClicked() {
        Window stage = mainPane.getScene().getWindow();
        CsvBuilder csvBuilder = new CsvBuilder();
        try {
            File file = fileChooser.showSaveDialog(stage);
            if (file != null) {
                file = fileChooser.getSelectedExtensionFilter().getDescription().contains("excel")
                        ? csvBuilder.writeCsvForExcel(file, getTableData())
                        : csvBuilder.writeToFile(file, getTableData());
                fileChooser.setInitialDirectory(file.getParentFile());
            }
        } catch (IOException e) {
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
        if (App.loggingLevel.isGreaterOrEqual(Level.WARN)) {
            menuDebug.setVisible(false);
        }
    }

    private void addBoxToParent(List<CheckBoxTreeItem<Task>> rootBoxes, CheckBoxTreeItem<Task> subBox) {
        for (var rootBox : rootBoxes) {
            if (rootBox.getValue().getLevel().equals(subBox.getValue().getLevel())) {
                rootBox.getChildren().add(subBox);
                return;
            }
        }
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

        var boxSet = new HashSet<TaskLevel>();

        for (var checkBox : taskCheckBoxItems) {
            boxSet.add(checkBox.getValue().getLevel());
        }

        var boxes = boxSet.stream().map(taskLevel ->
                new CheckBoxTreeItem<Task>(new Task(taskLevel), null, true))
                .collect(Collectors.toList());

        for (var item : taskCheckBoxItems) {
            addBoxToParent(boxes, item);
        }
        var root = new CheckBoxTreeItem<Task>(new Task(TaskLevel.ROOT));

        structureBoxes(root, boxes);
        root.getChildren().sort(Comparator.comparing(taskTreeItem -> taskTreeItem.getValue().getLevel().getPriority()));
        expandTreeView(root);

        treeView.setRoot(null);
        treeView.setRoot(root);
        treeView.setShowRoot(false);

        treeView.setCellFactory(CheckBoxTreeCell.forTreeView());
    }

    private void structureBoxes(CheckBoxTreeItem<Task> node, List<CheckBoxTreeItem<Task>> boxes) {
        for (var item : boxes) {
            if (item.getValue().getLevel().getParent() == node.getValue().getLevel()) {
                node.getChildren().add(item);
                if (!item.isLeaf()) {
                    structureBoxes(item, boxes);
                    item.getChildren().sort(Comparator.comparing(taskTreeItem -> taskTreeItem.getValue().getName()));
                }
            }
        }
    }

    private void expandTreeView(TreeItem<?> item) {
        if (item != null && !item.isLeaf()) {
            item.setExpanded(true);
            for (TreeItem<?> child : item.getChildren()) {
                expandTreeView(child);
            }
        }
    }

    public void changeLanguage() {
        primaryModel.setLanguage(choiceBoxLanguage.getValue());
        Translation.getInstance().setLocale(choiceBoxLanguage.getValue());
        TagMapper.getInstance().loadInterjections();
        createCheckboxes();
    }

    public void AnalyzeText() {
        if (tableViewResults.getColumns().isEmpty()) {
            initColumns();
        }

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

        var wordLevelTasks = FXCollections.observableArrayList(
                languageSpecificTaskCheckBoxItems.stream()
                        .map(TreeItem::getValue)
                        .collect(Collectors.toList())
        );

        primaryModel.setParagraphs(textAreaInput.getParagraphs());
        primaryModel.initializeDocument();

        var currentItem = primaryModel.processTasks(textTasks, generalTasks, wordLevelTasks);
        textItemDataResults.add(currentItem);

        tableViewResults.setItems(textItemDataResults);
    }

    private void createColumns(CheckBoxTreeItem<Task> root) {
        for (TreeItem<Task> child : root.getChildren()) {
            if (child.getChildren().isEmpty()) {
                if (((CheckBoxTreeItem<Task>) child).selectedProperty().get()) {
                    addTreeItemColumn(child);
                }
            } else {
                createColumns((CheckBoxTreeItem<Task>) child);
            }
        }
    }

    private void addTreeItemColumn(TreeItem<Task> child) {
        TableColumn<TextItemData, ?> column = new TableColumn<>();
        column.textProperty().bind(Translation.getInstance().createStringBinding(child.getValue().getId()));
        column.setId(child.getValue().getId());
        column.setCellValueFactory(new PropertyValueFactory<>(child.getValue().getId()));
        tableViewResults.getColumns().add(column);
    }

    private void addItemColumn() {
        var itemCheckBox = new CheckBoxTreeItem<Task>(new Task("Text", "text", TaskLevel.ROOT), null, true);
        addTreeItemColumn(itemCheckBox);
    }

    private void initColumns() {
        //TODO Bindings for Columns
        tableViewResults.getColumns().clear();
        addItemColumn();
        createColumns((CheckBoxTreeItem<Task>) treeView.getRoot());
    }

    public void handleDeleteClicked() {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(Translation.getInstance().getTranslation("deleteAllResults"));
        alert.setHeaderText(Translation.getInstance().getTranslation("cannotRestoreDeleted"));
        alert.setContentText(Translation.getInstance().getTranslation("confirmationMessage"));

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            textItemDataResults.clear();
            tableViewResults.getColumns().clear();
        }
    }

    public void handleDocumentationClicked() {
        if (Desktop.isDesktopSupported()) {
            new Thread(() -> {
                try {
                    var tempOutput = Files.createTempFile("latic-documentation", ".pdf");
                    tempOutput.toFile().deleteOnExit();

                    Files.copy(Objects.requireNonNull(
                            App.class.getResourceAsStream(String.format("documentation_%s.pdf", Translation.getInstance().getLanguageTag())),
                            "Documentation is not available"),
                            tempOutput, StandardCopyOption.REPLACE_EXISTING);

                    File userManual = new File(tempOutput.toFile().getPath());
                    if (userManual.exists()) {
                        Desktop.getDesktop().open(userManual);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }, "Documentation-Thread").start();
        }
    }

    public void handleContactClicked() {
        if (Desktop.isDesktopSupported()) {
            new Thread(() -> {
                try {
                    Desktop.getDesktop().browse(new URI("mailto:hello@latic.software?subject=LATIC"));
                } catch (IOException | URISyntaxException e1) {
                    e1.printStackTrace();
                }
            }, "E-Mail-Thread").start();
        }
    }

    public void handleSyllablesPerWordToCsv() {
        primaryModel.setParagraphs(textAreaInput.getParagraphs());
        primaryModel.initializeDocument();
        var syllableResult = primaryModel.syllableTest();

        Window stage = mainPane.getScene().getWindow();
        CsvBuilder csvBuilder = new CsvBuilder();
        try {
            File file = fileChooser.showSaveDialog(stage);
            if (file != null) {
                file = fileChooser.getSelectedExtensionFilter().getDescription().contains("excel")
                        ? csvBuilder.writeCsvForExcel(file, syllableResult)
                        : csvBuilder.writeToFile(file, syllableResult);
                fileChooser.setInitialDirectory(file.getParentFile());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
