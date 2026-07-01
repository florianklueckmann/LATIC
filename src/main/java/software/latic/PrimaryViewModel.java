package software.latic;

import javafx.beans.property.*;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.util.Duration;
import org.apache.commons.lang3.StringUtils;

import software.latic.brelix.BrelixAnalyzer;
import software.latic.connectives.BaseConnectives;
import software.latic.frequency.FrequencyCalculator;
import software.latic.frequency.FrequencyListCleaner;
import software.latic.helper.*;
import software.latic.item.*;
import software.latic.translation.Translation;
import software.latic.task.Task;
import software.latic.task.TaskLevel;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.util.StringConverter;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class PrimaryViewModel implements Initializable {

    @FXML public MenuItem menuItemConnectivesToTxt;
    @FXML private TextField filePathTextField;
    @FXML private CheckBox analyzeHeadersCheckbox;
    @FXML private CheckBox analyzeFootersCheckbox;
    @FXML private CheckBox lineBreakSentencesCheckbox;
    @FXML private CheckBox joinHyphenatedLineBreaksCheckbox;
    @FXML private CheckBox stripFrontMatterCheckbox;
    @FXML private CheckBox brelixCheckbox;
    @FXML private CheckBox countReportedSpeechCheckbox;
    @FXML private Spinner<Integer> pagesCountSpinner;
    @FXML private Spinner<Double> fontSizeMmSpinner;
    @FXML private Label labelPagesCount;
    @FXML private Label labelFontSizeMm;
    @FXML private Label labelBrelixHint;
    @FXML private Button buttonSelectFile;
    @FXML private Tab fileTab;
    @FXML private Tab textTab;
    @FXML private Tab brelixTab;
    @FXML private TabPane tabPane;
    @FXML private MenuItem menuItemImportTestFile;
    @FXML private MenuItem menuItemSyllablesPerWordToCsv;
    @FXML private BorderPane mainPane;
    @FXML private Menu menuHelp;
    @FXML private Menu menuDebug;
    @FXML private MenuItem menuItemDocumentation;
    @FXML private MenuItem menuItemContact;
    @FXML private MenuItem menuItemCheckForUpdate;
    @FXML private Button buttonDelete;
    @FXML private TableView<TextItemData> tableViewResults;
    @FXML private TextArea textAreaInput;
    @FXML private ChoiceBox<Locale> choiceBoxLanguage;
    @FXML private TreeView<Task> treeView;
    @FXML private Button buttonAnalyze;
    @FXML private Button buttonSaveFile;

    private ObservableList<TextItemData> textItemDataResults;

    private final ListProperty<Locale> languages = new SimpleListProperty<>();

    private final ListProperty<CharSequence>  importedDocumentContent = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final MapProperty<String, FileContent>  importedDocumentsContent = new SimpleMapProperty<>(FXCollections.observableMap(new HashMap<>()));
    private final ListProperty<File> importedFiles = new SimpleListProperty<>(FXCollections.observableArrayList());

    private final FileChooser.ExtensionFilter excelFilter = new FileChooser.ExtensionFilter("Excel 2007-365", "*.xlsx");
    private final FileChooser.ExtensionFilter csvFilter = new FileChooser.ExtensionFilter("CSV table file", "*.csv");
    private final FileChooser.ExtensionFilter supportedFilesFilter = new FileChooser.ExtensionFilter("Supported files (*.txt, *.docx, *.pdf)", "*.txt", "*.docx", "*.pdf");


    public void bindGuiElements() {
        menuHelp.textProperty().bind(Translation.getInstance().createStringBinding("help"));
        menuItemDocumentation.textProperty().bind(Translation.getInstance().createStringBinding("documentation"));
        menuItemContact.textProperty().bind(Translation.getInstance().createStringBinding("contact"));
        menuItemCheckForUpdate.textProperty().bind(Translation.getInstance().createStringBinding("checkForUpdate"));

        buttonAnalyze.textProperty().bind(Translation.getInstance().createStringBinding("analyze"));

        buttonSelectFile.textProperty().bind(Translation.getInstance().createStringBinding("selectFiles"));

        fileTab.textProperty().bind(Translation.getInstance().createStringBinding("file"));
        textTab.textProperty().bind(Translation.getInstance().createStringBinding("text"));

        analyzeHeadersCheckbox.textProperty().bind(Translation.getInstance().createStringBinding("analyzeHeaders"));
        analyzeFootersCheckbox.textProperty().bind(Translation.getInstance().createStringBinding("analyzeFooters"));
        lineBreakSentencesCheckbox.textProperty().bind(Translation.getInstance().createStringBinding("lineBreakSentences"));
        joinHyphenatedLineBreaksCheckbox.textProperty().bind(Translation.getInstance().createStringBinding("joinHyphenatedLineBreaks"));
        stripFrontMatterCheckbox.textProperty().bind(Translation.getInstance().createStringBinding("stripFrontMatter"));
        brelixCheckbox.textProperty().bind(Translation.getInstance().createStringBinding("brelixBeta"));
        countReportedSpeechCheckbox.textProperty().bind(Translation.getInstance().createStringBinding("countReportedSpeech"));

        labelPagesCount.textProperty().bind(Translation.getInstance().createStringBinding("pagesCount"));
        labelFontSizeMm.textProperty().bind(Translation.getInstance().createStringBinding("fontSizeMm"));
        labelBrelixHint.textProperty().bind(Translation.getInstance().createStringBinding("brelixHint"));

        buttonSaveFile.textProperty().bind(Translation.getInstance().createStringBinding("saveResults"));
        buttonDelete.textProperty().bind(Translation.getInstance().createStringBinding("deleteResults"));

        Label resultPlaceholder = new Label();
        resultPlaceholder.textProperty().bind(Translation.getInstance().createStringBinding("resultPlaceholder"));
        tableViewResults.setPlaceholder(resultPlaceholder);

        var isBrelixTabSelectedBinding = Bindings.createBooleanBinding(
                () -> tabPane.getSelectionModel().getSelectedItem() == brelixTab,
                tabPane.getSelectionModel().selectedItemProperty()
        );
        buttonAnalyze.disableProperty().bind(textAreaInput.textProperty().isEmpty().and(importedFiles.emptyProperty()).or(isBrelixTabSelectedBinding));
        buttonSaveFile.disableProperty().bind(isBrelixTabSelectedBinding);

        choiceBoxLanguage.disableProperty().bind(Bindings.isNotEmpty(textItemDataResults));

        var isDocxFileBinding = Bindings.createBooleanBinding(
                () -> !FileContentProvider.getFileTypeExtension(filePathTextField.getText()).equalsIgnoreCase("docx"),
                filePathTextField.textProperty()
        );
        analyzeHeadersCheckbox.disableProperty().bind(isDocxFileBinding);
        analyzeFootersCheckbox.disableProperty().bind(isDocxFileBinding);

        var canAnalyzeBrelixBinding = Bindings.createBooleanBinding(
                () -> Translation.getInstance().canAnalyzeBrelixForLocale(),
                Translation.getInstance().localeProperty()
        );
        brelixCheckbox.visibleProperty().bind(canAnalyzeBrelixBinding);
        brelixCheckbox.managedProperty().bind(canAnalyzeBrelixBinding);

        brelixTab.textProperty().bind(Translation.getInstance().createStringBinding("BRELIX"));
        pagesCountSpinner.disableProperty().bind(brelixCheckbox.selectedProperty().not());
        fontSizeMmSpinner.disableProperty().bind(brelixCheckbox.selectedProperty().not());
        labelPagesCount.disableProperty().bind(brelixCheckbox.selectedProperty().not());
        labelFontSizeMm.disableProperty().bind(brelixCheckbox.selectedProperty().not());
        labelBrelixHint.disableProperty().bind(brelixCheckbox.selectedProperty().not());
        countReportedSpeechCheckbox.disableProperty().bind(brelixCheckbox.selectedProperty().not());

        if (!canAnalyzeBrelixBinding.get()) {
            tabPane.getTabs().remove(brelixTab);
        }

        canAnalyzeBrelixBinding.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                if (!tabPane.getTabs().contains(brelixTab)) {
                    tabPane.getTabs().add(brelixTab);
                }
            } else {
                tabPane.getTabs().remove(brelixTab);
            }
        });

        if (App.loggingLevel.intValue() <= Level.FINEST.intValue()) {
            initializeBrelixDebugTab();
        }
    }

    private void initializeBrelixDebugTab() {
        TextArea debugTextArea = new TextArea();
        debugTextArea.setEditable(false);
        debugTextArea.setWrapText(true);
        debugTextArea.setStyle("-fx-font-family: monospace; -fx-font-size: 12;");

        Button exportButton = new Button("Export CSV");
        exportButton.setOnAction(e -> exportBrelixDebugToCsv());

        VBox content = new VBox(5, exportButton, debugTextArea);
        VBox.setVgrow(debugTextArea, Priority.ALWAYS);

        Tab debugTab = new Tab("BRELIX Debug");
        debugTab.setClosable(false);
        debugTab.setContent(content);
        tabPane.getTabs().add(debugTab);

        tableViewResults.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldItem, newItem) -> {
                if (newItem != null && newItem.getBrelixDebugInfo() != null) {
                    debugTextArea.setText(newItem.getBrelixDebugInfo());
                    tabPane.getSelectionModel().select(debugTab);
                } else {
                    debugTextArea.clear();
                }
            }
        );

        textItemDataResults.addListener((javafx.collections.ListChangeListener<TextItemData>) change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    TextItemData last = change.getAddedSubList().get(change.getAddedSize() - 1);
                    if (last.getBrelixDebugInfo() != null) {
                        tableViewResults.getSelectionModel().select(last);
                    }
                }
            }
        });
    }

    private void exportBrelixDebugToCsv() {
        var items = textItemDataResults.stream()
                .filter(item -> !item.getBrelixDebugMap().isEmpty())
                .collect(Collectors.toList());
        if (items.isEmpty()) return;

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export BRELIX Debug CSV");
        fileChooser.setInitialFileName("brelix_debug.csv");
        fileChooser.getExtensionFilters().add(csvFilter);
        File file = fileChooser.showSaveDialog(mainPane.getScene().getWindow());
        if (file == null) return;

        List<String> columns = new ArrayList<>();
        columns.add("identifier");
        columns.addAll(items.get(0).getBrelixDebugMap().keySet());

        try (var writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            writer.print('﻿'); // UTF-8 BOM for Excel
            writer.println(String.join(";", columns));
            for (var item : items) {
                var values = new ArrayList<String>();
                String id = item.getFileName() != null ? item.getFileName()
                        : item.getText().substring(0, Math.min(50, item.getText().length())).replace("\n", " ");
                values.add(csvEscape(id));
                for (String col : columns.subList(1, columns.size())) {
                    values.add(csvEscape(item.getBrelixDebugMap().getOrDefault(col, "")));
                }
                writer.println(String.join(";", values));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String csvEscape(String value) {
        if (value == null) return "";
        if (value.contains(";") || value.contains("\"") || value.contains("\n") || value.contains("\r")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    public void setLanguages() {
        languages.addAll(Translation.getInstance().getSupportedLocales());

        choiceBoxLanguage.setValue(Translation.getInstance().getLocale());
        choiceBoxLanguage.setConverter(new StringConverter<>() {
            @Override
            public String toString(Locale locale) {
                if (locale != null) {
                    return Translation.getInstance().getTranslation("language." + locale.getLanguage());
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

    FileChooser exportFileChooser = new FileChooser();
    FileChooser importFileChooser = new FileChooser();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        textItemDataResults = FXCollections.observableArrayList();
        taskCheckBoxItems = FXCollections.observableArrayList();


        initializeBindings();
        initializeFileChooser();
        setupTaskLevelStructure();
        initializeBrelixSpinners();
        initializeGui();
    }

    private void setupTaskLevelStructure() {
        TaskLevel.ROOT.setParent(TaskLevel.ROOT);
        TaskLevel.WORD.setParent(TaskLevel.ROOT);
        TaskLevel.WORD_LENGTH.setParent(TaskLevel.WORD);
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

        var initialFileName = "results";

        exportFileChooser.setInitialDirectory(new File(initialFilePath));
        exportFileChooser.titleProperty().bind(Translation.getInstance().createStringBinding("saveFile"));
        exportFileChooser.setInitialFileName(initialFileName);
        exportFileChooser.getExtensionFilters().addAll(csvFilter, excelFilter);

        importFileChooser.setInitialDirectory(new File(initialFilePath));
        importFileChooser.titleProperty().bind(Translation.getInstance().createStringBinding("selectFiles"));
        importFileChooser.getExtensionFilters().addAll(supportedFilesFilter);
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
        try {
            File file = exportFileChooser.showSaveDialog(stage); //TODO EXTENSION
            if (file != null) {
                var selectedFileExtension = exportFileChooser.getSelectedExtensionFilter().getExtensions().get(0).replace("*", "");
                if (!file.getPath().contains(selectedFileExtension)) {
                    file = new File(file.getPath() + selectedFileExtension);
                }

                file = exportFileChooser.getSelectedExtensionFilter().getDescription().toLowerCase().contains("excel")
                        ? XlsxBuilder.getInstance().writeToFile(file, tableViewResults)
                        : CsvBuilder.getInstance().writeToFile(file, getTableData());
                exportFileChooser.setInitialDirectory(file.getParentFile());
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
        applyPreferences();
        setLanguages();
        bindGuiElements();
        if (App.loggingLevel.intValue() >= Level.WARNING.intValue()) {
            menuDebug.setVisible(false);
        }
    }

    private void initializeBrelixSpinners() {
        pagesCountSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 9999, 1));
        var fontSizeFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(1.0, 50.0, 6.0, 0.5);
        fontSizeFactory.setConverter(new StringConverter<>() {
            @Override
            public String toString(Double value) {
                return value == null ? "" : value.toString();
            }

            @Override
            public Double fromString(String value) {
                if (value == null) {
                    return fontSizeFactory.getValue();
                }

                var sanitizedValue = value.trim().replace(',', '.');
                if (sanitizedValue.isEmpty()) {
                    return fontSizeFactory.getValue();
                }

                return Double.parseDouble(sanitizedValue);
            }
        });
        fontSizeMmSpinner.setValueFactory(fontSizeFactory);
        pagesCountSpinner.setEditable(true);
        fontSizeMmSpinner.setEditable(true);
    }

    private void applyPreferences() {
        analyzeHeadersCheckbox.setSelected(Boolean.parseBoolean(Settings.userPreferences.get("analyzeHeaders", "true")));
        analyzeFootersCheckbox.setSelected(Boolean.parseBoolean(Settings.userPreferences.get("analyzeFooters", "true")));
        lineBreakSentencesCheckbox.setSelected(Boolean.parseBoolean(Settings.userPreferences.get("lineBreakSentences", "false")));
        joinHyphenatedLineBreaksCheckbox.setSelected(Boolean.parseBoolean(Settings.userPreferences.get("joinHyphenatedLineBreaks", "false")));
        stripFrontMatterCheckbox.setSelected(Boolean.parseBoolean(Settings.userPreferences.get("stripFrontMatter", "false")));
        brelixCheckbox.setSelected(Boolean.parseBoolean(Settings.userPreferences.get("brelixEnabled", "false")));
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

        if (Translation.getInstance().canAnalyzeSyllablesForLocale()) {
            generalTaskCheckBoxItems.addAll(FXCollections.observableArrayList(Arrays.stream(SyllableItemCharacteristics.values())
                    .map(textInformation -> new CheckBoxTreeItem<Task>(new Task(Translation.getInstance().getTranslation(textInformation.getId()), textInformation.getId(), textInformation.getLevel()), null, true))
                    .collect(Collectors.toList())));
        }

        textTaskCheckBoxItems = FXCollections.observableArrayList(Arrays.stream(TextInformationItemCharacteristics.values())
                .map(textInformationItemCharacteristics -> new CheckBoxTreeItem<Task>(new Task(Translation.getInstance().getTranslation(textInformationItemCharacteristics.getId()), textInformationItemCharacteristics.getId(), textInformationItemCharacteristics.getLevel()), null, true))
                .collect(Collectors.toList()));

        languageSpecificTaskCheckBoxItems = FXCollections.observableArrayList(Arrays.stream(GeneralLanguageItemCharacteristics.values())
                .map(generalLanguageItemCharacteristics -> new CheckBoxTreeItem<Task>(new Task(Translation.getInstance().getTranslation(generalLanguageItemCharacteristics.getId()), generalLanguageItemCharacteristics.getId(), generalLanguageItemCharacteristics.getLevel()), null, true))
                .collect(Collectors.toList()));

        if (choiceBoxLanguage.getValue().equals(Locale.ENGLISH)) {
            generalTaskCheckBoxItems.addAll(FXCollections.observableArrayList(
                    Arrays.stream(EnglishGeneralItemCharacteristics.values())
                            .map(ic -> new CheckBoxTreeItem<Task>(
                                    new Task(Translation.getInstance().getTranslation(ic.getId()),
                                            ic.getId(),
                                            ic.getLevel(),
                                            ic.getIsBeta()
                                    ), null, true))
                            .collect(Collectors.toList())
            ));
            languageSpecificTaskCheckBoxItems.addAll(FXCollections.observableArrayList(
                    Arrays.stream(EnglishItemCharacteristics.values())
                            .map(ic -> new CheckBoxTreeItem<Task>(
                                    new Task(Translation.getInstance().getTranslation(ic.getId()),
                                            ic.getId(),
                                            ic.getLevel()
                                    ), null, true))
                            .collect(Collectors.toList())
            ));
        }

        if (choiceBoxLanguage.getValue().equals(Locale.GERMAN)) {
            generalTaskCheckBoxItems.addAll(FXCollections.observableArrayList(
                    Arrays.stream(GermanGeneralItemCharacteristics.values())
                            .map(ic -> new CheckBoxTreeItem<Task>(
                                    new Task(Translation.getInstance().getTranslation(ic.getId()),
                                            ic.getId(),
                                            ic.getLevel(),
                                            ic.getIsBeta()
                                    ), null, true))
                            .collect(Collectors.toList())
            ));
            languageSpecificTaskCheckBoxItems.addAll(FXCollections.observableArrayList(
                    Arrays.stream(GermanItemCharacteristics.values())
                            .map(ic -> new CheckBoxTreeItem<Task>(
                                    new Task(Translation.getInstance().getTranslation(ic.getId()),
                                            ic.getId(),
                                            ic.getLevel()
                                    ), null, true))
                            .collect(Collectors.toList())
            ));
        }

        if (brelixCheckbox.isSelected() && Translation.getInstance().canAnalyzeBrelixForLocale()) {
            generalTaskCheckBoxItems.addAll(FXCollections.observableArrayList(
                    Arrays.stream(BrelixItemCharacteristics.values())
                            .map(ic -> new CheckBoxTreeItem<Task>(
                                    new Task(Translation.getInstance().getTranslation(ic.getId()),
                                            ic.getId(),
                                            ic.getLevel()
                                    ), null, true))
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
        treeView.setCellFactory(tv -> {
            final Tooltip tooltip = new Tooltip("Beta");
            tooltip.setShowDelay(Duration.millis(100));
            CheckBoxTreeCell<Task> cell = new CheckBoxTreeCell<Task>() {
                @Override
                public void updateItem(Task item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item != null && item.getIsBeta()) {
                        setTooltip(tooltip);
                    }
                }
            };
            return cell;
        });
    }

    private void structureBoxes(CheckBoxTreeItem<Task> node, List<CheckBoxTreeItem<Task>> boxes) {
        for (var item : boxes) {
            if (item.getValue().getLevel().getParent() == node.getValue().getLevel()) {
                node.getChildren().add(item);
                if (!item.isLeaf()) {
                    structureBoxes(item, boxes);
                    item.getChildren().sort(Comparator.comparing(taskTreeItem -> taskTreeItem.getValue().getName().toLowerCase(Locale.ROOT)));
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
        Translation.getInstance().setLocale(choiceBoxLanguage.getValue());
        TagMapper.getInstance().loadInterjections();
        BaseConnectives.getInstance().initialize();
        FrequencyCalculator.getInstance().initialize();
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

        TextItemData currentItem = null;
        List<TextItemData> currentItems = FXCollections.observableArrayList();

        if (fileTab.isSelected() && !importedFiles.isEmpty()) {
            PDFReader.getInstance().setStripFrontMatter(stripFrontMatterCheckbox.isSelected());
            for (var importedFile : importedFiles) {
                try {
                    FileContent content = FileContentProvider.getContent(importedFile.getPath());
                    importedDocumentsContent.put(importedFile.getName(), content);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            for( var importedDocumentContent : importedDocumentsContent.entrySet()) {
                var model = new PrimaryModel();
                if (brelixCheckbox.isSelected() && Translation.getInstance().canAnalyzeBrelixForLocale()) {
                    model.setPages(pagesCountSpinner.getValue());
                    model.setFontSizeMm(fontSizeMmSpinner.getValue());
                }
                model.setCountLineBreaksAsSentences(lineBreakSentencesCheckbox.isSelected());
                model.setJoinHyphenatedLineBreaks(joinHyphenatedLineBreaksCheckbox.isSelected());
                currentItems.add(model
                        .initializeDocument(importedDocumentContent.getValue())
                        .processTasks(textTasks, generalTasks, wordLevelTasks, importedDocumentContent.getKey()));
            }
        } else if (textTab.isSelected()) {
            //TODO We could split the input in multiple items
            var model = new PrimaryModel();
            if (brelixCheckbox.isSelected() && Translation.getInstance().canAnalyzeBrelixForLocale()) {
                model.setPages(pagesCountSpinner.getValue());
                model.setFontSizeMm(fontSizeMmSpinner.getValue());
            }
            model.setCountLineBreaksAsSentences(lineBreakSentencesCheckbox.isSelected());
            model.setJoinHyphenatedLineBreaks(joinHyphenatedLineBreaksCheckbox.isSelected());
            currentItems.add(model
                    .initializeDocument(textAreaInput.getParagraphs())
                    .processTasks(textTasks, generalTasks, wordLevelTasks));

        }

        if (!currentItems.isEmpty()) {
            textItemDataResults.addAll(currentItems);

            tableViewResults.setItems(textItemDataResults);
        }
        importedFiles.clear();
        importedDocumentsContent.clear();
        filePathTextField.clear();
        //TODO is clearing a good idea?
        //TODO add filename to first column
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
        column.setPrefWidth(150.0);

        tableViewResults.getColumns().add(column);
    }

    private void addItemColumn() {
        var fileNameCheckBox = new CheckBoxTreeItem<Task>(new Task("FileName", "fileName", TaskLevel.ROOT), null, true);
        var itemCheckBox = new CheckBoxTreeItem<Task>(new Task("Text", "text", TaskLevel.ROOT), null, true);
        addTreeItemColumn(fileNameCheckBox);
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
                            App.class.getResourceAsStream(Translation.getInstance().getDocumentationFileName()),
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

    public void handleCheckForUpdateClicked() {
        UpdateHelper.getInstance().manualUpdateCheck();
    }

    public void handleSyllablesPerWordToCsv() {
        var syllableResult = new PrimaryModel().initializeDocument(textAreaInput.getParagraphs()).syllableTest();

        Window stage = mainPane.getScene().getWindow();
        CsvBuilder csvBuilder = new CsvBuilder();
        try {
            File file = exportFileChooser.showSaveDialog(stage);
            if (file != null) {
                file = exportFileChooser.getSelectedExtensionFilter().getDescription().contains("excel")
                        ? csvBuilder.writeCsvForExcel(file, syllableResult)
                        : csvBuilder.writeToFile(file, syllableResult);
                exportFileChooser.setInitialDirectory(file.getParentFile());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleButtonSelectFile() {
        Window stage = mainPane.getScene().getWindow();
        var files = importFileChooser.showOpenMultipleDialog(stage);

        if (files != null && !files.isEmpty()) {
            importedFiles.addAll(files);
            if (importedFiles.size() == 1) {
                importFileChooser.setInitialDirectory(importedFiles.get(0).getParentFile());
                filePathTextField.setText(importedFiles.get(0).getPath());
            } else {
                importFileChooser.setInitialDirectory(importedFiles.get(0).getParentFile());
                filePathTextField.setText(importedFiles.size() + " files selected");
            }
        }
    }

    public void handleAnalyzeHeadersCheckboxValueChanged(ActionEvent actionEvent) {
        Settings.userPreferences.put("analyzeHeaders", String.valueOf(((CheckBox) actionEvent.getTarget()).isSelected()));
    }

    public void handleAnalyzeFootersCheckboxValueChanged(ActionEvent actionEvent) {
        Settings.userPreferences.put("analyzeFooters", String.valueOf(((CheckBox) actionEvent.getTarget()).isSelected()));
    }

    public void handleLineBreakSentencesCheckboxValueChanged(ActionEvent actionEvent) {
        Settings.userPreferences.put("lineBreakSentences", String.valueOf(((CheckBox) actionEvent.getTarget()).isSelected()));
    }

    public void handleJoinHyphenatedLineBreaksCheckboxValueChanged(ActionEvent actionEvent) {
        Settings.userPreferences.put("joinHyphenatedLineBreaks", String.valueOf(((CheckBox) actionEvent.getTarget()).isSelected()));
    }

    public void handleStripFrontMatterCheckboxValueChanged(ActionEvent actionEvent) {
        Settings.userPreferences.put("stripFrontMatter", String.valueOf(((CheckBox) actionEvent.getTarget()).isSelected()));
    }

    public void handleBrelixCheckboxValueChanged(ActionEvent actionEvent) {
        Settings.userPreferences.put("brelixEnabled", String.valueOf(brelixCheckbox.isSelected()));
        createCheckboxes();
    }

    public void handleCountReportedSpeechCheckboxValueChanged(ActionEvent actionEvent) {
        BrelixAnalyzer.getInstance().setCountReportedSpeechAsSubordinate(countReportedSpeechCheckbox.isSelected());
    }

    public void cleanCsv(ActionEvent actionEvent) {
        Window stage = mainPane.getScene().getWindow();
        importFileChooser.getExtensionFilters().add(csvFilter);
        var file = importFileChooser.showOpenDialog(stage);

        var cleaner = new FrequencyListCleaner();

        var x = cleaner.cleanCleanedCsv(file);



        try {
            File saveFile = exportFileChooser.showSaveDialog(stage); //TODO EXTENSION
            if (saveFile != null) {
                var selectedFileExtension = exportFileChooser.getSelectedExtensionFilter().getExtensions().get(0).replace("*", "");
                if (!saveFile.getPath().contains(selectedFileExtension)) {
                    saveFile = new File(saveFile.getPath() + selectedFileExtension);
                }

                saveFile = CsvBuilder.getInstance().writeToFile(saveFile, x);
                exportFileChooser.setInitialDirectory(saveFile.getParentFile());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
