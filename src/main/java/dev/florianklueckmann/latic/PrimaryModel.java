package dev.florianklueckmann.latic;

import dev.florianklueckmann.latic.Translation.Translation;
import dev.florianklueckmann.latic.services.*;
import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.simple.Document;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

public class PrimaryModel {

    private Locale language = Translation.getInstance().getLocale(); //TODO: Maybe use ENUM
    private Properties props;
    SimpleTextAnalyzer simpleTextAnalyzer;
    TextFormattingService textFormattingService;
    NlpTextAnalyzer nlp;

    public List<CharSequence> getParagraphs() {
        return paragraphs;
    }

    public void setParagraphs(List<CharSequence> paragraphs) {
        this.paragraphs = paragraphs;
    }

    List<CharSequence> paragraphs;

    public Document getDoc() {
        return doc;
    }

    Document doc;


    public PrimaryModel(SimpleTextAnalyzer simpleTextAnalyzer, TextFormattingService textFormattingService, NlpTextAnalyzer nlp) {
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(App.loggingLevel);
        props = new Properties();

        this.simpleTextAnalyzer = simpleTextAnalyzer;
        this.textFormattingService = textFormattingService;
        this.nlp = nlp;
    }

    //TODO: Throw Exception if paragraphs not set?
    public void initializeDocument() {
        this.doc = new Document(props, this.paragraphs.stream()
                .map(charSequence -> charSequence.toString().trim())
                .collect(Collectors.joining(" ")));
    }

    public void initializeDocument(String text) {
        this.doc = new Document(props, text);
    }

    public Locale getLanguage() {
        return language;
    }

    public void setLanguage(Locale language) {
        //TODO: Lookup Option<String>
//        if (language) {
            this.language = language;
            if (!language.getLanguage().equalsIgnoreCase("en")) {
                try {
                    props.load(IOUtils.readerFromString("StanfordCoreNLP-" + language.getDisplayLanguage(Locale.ENGLISH).toLowerCase(Locale.ROOT) + ".properties"));
                } catch (IOException e) {
                    //TODO: Error Handling
                    e.printStackTrace();
                }
            }
            //TODO Find a better way to set language
            if (language.getLanguage().equalsIgnoreCase("en")) {
                props = new Properties();
            }
//        }
    }

    public void printSentences() {
        for (var sent : doc.sentences()) {
            System.out.println(" ");
            System.out.println(sent);
            System.out.println(" ");
        }
    }

    public String wordClassesAsString() {
        var sb = new StringBuilder();

        WordClassService wordClassCounter;

        //TODO
        if (language.equals(Locale.GERMAN))
            wordClassCounter = new GermanWordClassService();
        else
            wordClassCounter = new EnglishWordClassService();

        wordClassCounter.analyzeWordClasses(doc.sentences()).stream()
                .filter(linguisticFeature -> linguisticFeature.getValue() > 0)
                .forEach(linguisticFeature ->
                        sb.append(linguisticFeature.getName()).append(": ").append(linguisticFeature.getValue()).append("\n"));

        return sb.toString();
    }

    public List<IntegerLinguisticFeature> wordClassesAsList(ObservableList<Task> languageSpecificTasks) {

        List<IntegerLinguisticFeature> featureList = new ArrayList<>();

        WordClassService wordClassCounter;

        //TODO
        if (language.equals(Locale.GERMAN))
            wordClassCounter = new GermanWordClassService();
        else
            wordClassCounter = new EnglishWordClassService();

        for (var linguisticFeature : wordClassCounter.analyzeWordClasses(doc.sentences())) {
            if (languageSpecificTasks.stream().anyMatch(task -> task.getId().equals(linguisticFeature.getId()) && task.selectedProperty().get()))
                featureList.add(linguisticFeature);
        }

        return featureList;
    }

    public ObservableMap<String, IntegerLinguisticFeature> wordClassesAsMap(ObservableList<Task> languageSpecificTasks) {

        ObservableMap<String, IntegerLinguisticFeature> featureMap = FXCollections.observableHashMap();

        WordClassService wordClassCounter;

        //TODO
        if (language.equals(Locale.GERMAN))
            wordClassCounter = new GermanWordClassService();
        else
            wordClassCounter = new EnglishWordClassService();

        for (var linguisticFeature : wordClassCounter.analyzeWordClasses(doc.sentences())) {
            if (languageSpecificTasks.stream().anyMatch(task -> task.getId().equals(linguisticFeature.getId()) && task.selectedProperty().get()))
                featureMap.put(linguisticFeature.getId(), linguisticFeature);
        }

        return featureMap;
    }

    protected int getAverageSentenceLengthSyllables() {
        return 0;
    }

    protected String analyze() {

        simpleTextAnalyzer.setDoc(doc);

        var sb = new StringBuilder()
                .append(appendResultLine("Word count", simpleTextAnalyzer.wordCount()))
                .append(appendResultLine("Sentence count", simpleTextAnalyzer.sentenceCount()))
                .append(appendResultLine("Average sentence length without whitespaces", simpleTextAnalyzer.averageSentenceLengthCharactersWithoutWhitespaces()))
                .append(appendResultLine("Average sentence length", simpleTextAnalyzer.averageSentenceLengthCharacters()))
                .append(wordClassesAsString());

        doc.sentences().forEach(sentence -> sb.append(sentence).append("\n").append(sentence.posTags()).append("\n"));

        return sb.toString();

    }

    protected List<String> sentencesAndPosTags() {
        List<String> results = new ArrayList<>();
        doc.sentences().forEach(sentence -> results.add(sentence + "\n" + sentence.posTags()));
        return results;
    }

    protected List<LinguisticFeature> analyzeGeneralItemCharacteristics(ObservableList<Task> tasks) {
        simpleTextAnalyzer.setDoc(doc);

        ObservableList<LinguisticFeature> featureList = FXCollections.observableArrayList();
        List<String> errorList = new ArrayList<>();

        for (var task : tasks) {
            if (task.selectedProperty().get()) {
                java.lang.reflect.Method method;
                try {
                    method = simpleTextAnalyzer.getClass().getMethod(task.getId());
                    if (task.getId().contains("average"))
                        featureList.add(new DoubleLinguisticFeature(
                                task.getName(),
                                task.getId(),
                                (double) method.invoke(simpleTextAnalyzer)));
                    else if (task.getId().contains("count"))
                        featureList.add(new IntegerLinguisticFeature(
                                task.getName(),
                                task.getId(),
                                (int) method.invoke(simpleTextAnalyzer)));
                    else
                        featureList.add(new StringLinguisticFeature(
                                task.getName(),
                                task.getId(),
                                String.valueOf(method.invoke(simpleTextAnalyzer))));

                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                    featureList.add(new StringLinguisticFeature(task.getName(), task.getId(), "NoSuchMethodException"));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    featureList.add(new StringLinguisticFeature(task.getName(), task.getId(), "IllegalAccessException"));
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                    featureList.add(new StringLinguisticFeature(task.getName(), task.getId(), "InvocationTargetException"));
                }
            }
        }

        return featureList;
    }

    protected void analyzeGeneralItemCharacteristics(TextItemData textItemData, ObservableList<Task> tasks) {
        simpleTextAnalyzer.setDoc(doc);

        ObservableList<LinguisticFeature> featureList = FXCollections.observableArrayList();
        List<String> errorList = new ArrayList<>();

        for (var task : tasks) {
            if (task.selectedProperty().get()) {
                java.lang.reflect.Method setter;
                java.lang.reflect.Method simpleTextAnalyzerMethod;

                var setterName = task.getId();
                setterName = setterName.substring(0, 1).toUpperCase() + setterName.substring(1);
                setterName = "set" + setterName;
                log("methodName: " + setterName + " - " + "task.getId(): " + task.getId());

                try {
                    simpleTextAnalyzerMethod = simpleTextAnalyzer.getClass().getMethod(task.getId());

                    if (task.getId().toLowerCase().contains("average")
                            || task.getId().toLowerCase().contains("score")
                            || task.getId().toLowerCase().equals("lexicaldiversity")) {
                        setter = textItemData.getClass().getMethod(setterName, double.class);
                        setter.invoke(textItemData, (double) simpleTextAnalyzerMethod.invoke(simpleTextAnalyzer));
                    } else if (task.getId().toLowerCase().contains("count")) {
                        setter = textItemData.getClass().getMethod(setterName, int.class);
                        setter.invoke(textItemData, (int) simpleTextAnalyzerMethod.invoke(simpleTextAnalyzer));
                    } else {
                        setter = textItemData.getClass().getMethod(setterName, String.class);
                        setter.invoke(textItemData, String.valueOf(simpleTextAnalyzerMethod.invoke(simpleTextAnalyzer)));
                    }
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
//                    featureList.add(new StringLinguisticFeature(task.getName(), task.getId(), "NoSuchMethodException"));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
//                    featureList.add(new StringLinguisticFeature(task.getName(), task.getId(), "IllegalAccessException"));
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
//                    featureList.add(new StringLinguisticFeature(task.getName(), task.getId(), "InvocationTargetException"));
                }
            }
        }
    }

    protected List<LinguisticFeature> analyzeTextInformation(ObservableList<Task> tasks) {
        nlp.setDoc(doc);

        ObservableList<LinguisticFeature> featureList = FXCollections.observableArrayList();
        List<String> errorList = new ArrayList<>();

        for (var task : tasks) {
            if (task.selectedProperty().get()) {
                java.lang.reflect.Method method;
                try {
                    method = nlp.getClass().getMethod(task.getId());
                    featureList.add(new StringLinguisticFeature(
                            task.getName(),
                            task.getId(),
                            String.valueOf(method.invoke(nlp))));

                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                    featureList.add(new StringLinguisticFeature(task.getName(), task.getId(), "NoSuchMethodException"));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    featureList.add(new StringLinguisticFeature(task.getName(), task.getId(), "IllegalAccessException"));
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                    featureList.add(new StringLinguisticFeature(task.getName(), task.getId(), "InvocationTargetException"));
                }
            }
        }

        return featureList;
    }

    protected void analyzeTextInformation(TextItemData textItemData, ObservableList<Task> tasks) {
        nlp.setDoc(doc);

        ObservableList<LinguisticFeature> featureList = FXCollections.observableArrayList();
        List<String> errorList = new ArrayList<>();

        for (var task : tasks) {
            if (task.selectedProperty().get()) {
                java.lang.reflect.Method nlpMethod;
                java.lang.reflect.Method setter;
                try {
                    nlpMethod = nlp.getClass().getMethod(task.getId());

                    var setterName = task.getId();
                    setterName = setterName.substring(0, 1).toUpperCase() + setterName.substring(1);
                    setterName = "set" + setterName;
                    log("methodName: " + setterName + " - " + "task.getId(): " + task.getId());

                    setter = textItemData.getClass().getMethod(setterName, String.class);

                    setter.invoke(textItemData, String.valueOf(nlpMethod.invoke(nlp)));

                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                    featureList.add(new StringLinguisticFeature(task.getName(), task.getId(), "NoSuchMethodException"));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    featureList.add(new StringLinguisticFeature(task.getName(), task.getId(), "IllegalAccessException"));
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                    featureList.add(new StringLinguisticFeature(task.getName(), task.getId(), "InvocationTargetException"));
                }
            }
        }
    }

    protected String appendResultLine(String label, double result) {
        return label + ": " + result + "\n";
    }

    protected String appendResultLine(String label, int result) {
        return label + ": " + result + "\n";
    }

    protected String appendResultLine(String label, String result) {
        return label + ": " + result + "\n";
    }

    private void log(Object o) {
        Logger.getLogger("PrimaryModel").log(Level.DEBUG, o);
    }

    protected TextItemData analyzeItem(ObservableList<Task> textTasks, ObservableList<Task> generalTasks, ObservableList<Task> languageTasks) {
        log("analyzeItem");
        simpleTextAnalyzer.setDoc(doc);
        nlp.setDoc(doc);

        ObservableList<LinguisticFeature> featureList = FXCollections.observableArrayList();
        List<String> errorList = new ArrayList<>();

        TextItemData textItemData;

        if (language.equals(Locale.GERMAN))
            textItemData = new GermanTextItemData(doc.text());
        else
            textItemData = new EnglishTextItemData(doc.text());

        analyzeTextInformation(textItemData, textTasks);
        analyzeGeneralItemCharacteristics(textItemData, generalTasks);


        for (var task : languageTasks) {
            if (task.selectedProperty().get()) {
                java.lang.reflect.Method method;
                java.lang.reflect.Field field;
                try {
                    var methodName = task.getId();
                    methodName = methodName.substring(0, 1).toUpperCase() + methodName.substring(1);
                    methodName = "set" + methodName;
                    log("methodName: " + methodName + " - " + "task.getId(): " + task.getId());

                    method = textItemData.getClass().getMethod(methodName, int.class);
                    method.invoke(textItemData, wordClassesAsMap(languageTasks).getOrDefault(task.getId(), new IntegerLinguisticFeature("default", "default", 123)).getValue());

                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                    featureList.add(new StringLinguisticFeature(task.getName(), task.getId(), "NoSuchMethodException"));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    featureList.add(new StringLinguisticFeature(task.getName(), task.getId(), "IllegalAccessException"));
                } catch (InvocationTargetException e) {
//                e.printStackTrace();
                    featureList.add(new StringLinguisticFeature(task.getName(), task.getId(), "InvocationTargetException"));
                }
            }
        }

        return textItemData;
    }
}
